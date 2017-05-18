/*
 * Jun Liu
 * 05-16-2017
 */
package hotelservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

	private static final String maxRowsAll = "hotelData.maxRowsAll";//max number of rows to be returned when all cities are retrieved
	@Autowired
	private Environment env;

	private static String hotelDataFilePath;
	private static int maxRowsVal = 20;

	private static List<HotelData> hotelList = new ArrayList<>();

	@RequestMapping("/getHotel")
	public ResponsePOJO getHotel(@RequestParam(value="cityid", defaultValue="all") String name, @RequestParam(value="sort", defaultValue="null") String sort, @RequestParam(value="reload", defaultValue="n") String reload) {
		logger.debug("getHotel() request. cityid: [" + name + "] sort: [" + sort + "] reload: [" + reload + "]");
		hotelDataFilePath = env.getProperty(dataSourceFile);
		String tmp = env.getProperty(maxRowsAll);

		if ( tmp != null ) {
			try {
				maxRowsVal = Integer.parseInt(tmp.trim());
			}
			catch(Exception e) {
				logger.debug("Invalid maxRows property: " + tmp);
			}
		}

		ResponsePOJO res = new ResponsePOJO();
		List<HotelData> list = getHotelData(name, reload);

		if ( !sort.equalsIgnoreCase("null") ) {
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
	private List<HotelData> getHotelData(String cityName, String reload) {
		//returned list will be a copy of the original hotelList or a portion of it
		if ( hotelList.size() == 0 || (reload.toLowerCase().startsWith("y")|| reload.toLowerCase().startsWith("true"))) {
			hotelList = loadHotelData();
		}
		if ( cityName != null && cityName.equalsIgnoreCase("all") ) {
			if ( hotelList.size() <= maxRowsVal) {
				return new ArrayList<HotelData> (hotelList);
			}
			else {
				return new ArrayList<HotelData> (hotelList.subList(0, maxRowsVal) );
			}
		}
		else {
			return new ArrayList<HotelData> (hotelList.stream().filter(e -> e.city.equalsIgnoreCase(cityName)).collect(Collectors.toList()));
		}
	}//getHotelData

	/**
	 * Load all hotel data from data source. 
	 * hotelData.filePath is configured in application.properties.
	 * @return
	 */
	private static List <HotelData> loadHotelData() {
		logger.debug("Loading data from datasource...");
		int count = 0;
		Set<HotelData> set = new LinkedHashSet<>();//Use a set to eliminate duplicates.
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(hotelDataFilePath)));
			String line;
			while ( (line = br.readLine()) != null ) {
				count++;
				try {
					String[] words = line.split(",");
					HotelData hotel = new HotelData(words[0],Integer.parseInt(words[1]), words[2],Integer.parseInt(words[3]));
					set.add(hotel);
				}
				catch(Exception e) {
					logger.info("Wrong data format:" + line );
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
		logger.debug("Done loading data from datasource. Total # of lines processed: " + count);
		return new ArrayList<>(set);
	}//loadHotelData
}
