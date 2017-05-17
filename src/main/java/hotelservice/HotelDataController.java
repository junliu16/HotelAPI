package hotelservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HotelDataController {

	private static final Logger logger = LoggerFactory.getLogger(HotelDataController.class);
    private static final String dataSourceFile = "hotelData.filePath";

    @Autowired
    private Environment env;
    
    private static String hotelDataFilePath;
    
    private static List<HotelData> hotelList = new ArrayList<>();
    
    @RequestMapping("/getHotel")
    public ResponsePOJO getHotel(@RequestParam(value="cityid", defaultValue="all") String name, @RequestParam(value="sort", defaultValue="asc") String sort) {
    	logger.debug("getHotel() request. cityid: [" + name + "] sort: [" + sort + "]");
    	hotelDataFilePath = env.getProperty(dataSourceFile);
    	 
    	ResponsePOJO res = new ResponsePOJO();
    	List<HotelData> list = getHotelData(name);
    	
    	if ( sort != null ) {
    		if ( sort.toLowerCase().indexOf("asc") >= 0 || sort.toLowerCase().indexOf("desc") >= 0) {
    			Collections.sort(list);
    			if ( sort.toLowerCase().indexOf("desc") >= 0) {
    				Collections.reverse(list);
    			}
    		}
    	}
    	
    	res.hotelInfo = list.toArray(new HotelData[list.size()]);
    	
    	logger.debug("getHotel() returning " + (res.hotelInfo==null? 0 : res.hotelInfo.length) + " data sets");
        return res;
    }//getHotel
    
    /**
     * Get Hotel data for a given city
     * @param cityName
     * @return
     */
    private List<HotelData> getHotelData(String cityName) {
    	if ( hotelList.size() == 0 ) {
    		hotelList = loadHotelData();
    	}
	if ( cityName != null && cityName.equalsIgnoreCase("all") ) {
		return hotelList;
	}
	else {
    		return hotelList.stream().filter(e -> e.city.equalsIgnoreCase(cityName)).collect(Collectors.toList());
	}
    }//getHotelData
    
    /**
     * Load all hotel data from data source. 
     * hotelData.filePath is configured in application.properties.
     * @return
     */
    private static List<HotelData> loadHotelData() {
    	logger.debug("Loading data from datasource...");
    	List<HotelData> list = new ArrayList<>();
    	BufferedReader br = null;
    	try {
    		br = new BufferedReader(new FileReader(new File(hotelDataFilePath)));
    		String line = br.readLine();
    		while ( (line = br.readLine()) != null ) {
    			try {
    				String[] words = line.split(",");
    				HotelData hotel = new HotelData(words[0],Integer.parseInt(words[1]), words[2],Integer.parseInt(words[3]));
    				list.add(hotel);
    			}
    			catch(Exception e) {
    				logger.error("Wrong data format:" + line, e);
    			}
    		}
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if ( br != null ) {
    				br.close();
    			}
    		}
    		catch(Exception e) {
    			logger.error("error when closing bufferedReader", e);
    		}
    	}
    	logger.debug("Done loading data from datasource.");
    	return list;
    }//loadHotelData
}
