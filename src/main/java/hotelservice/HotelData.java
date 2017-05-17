package hotelservice;

import java.io.Serializable;

public class HotelData implements Serializable, Comparable<HotelData>{

	private static final long serialVersionUID = 7526472295622776147L;
	
	String city;
	int hotelId;
	String room;
	int price;
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getHotelId() {
		return hotelId;
	}
	public void setHotelId(int hotelId) {
		this.hotelId = hotelId;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public HotelData() {}
	public HotelData(String city, int hotelId, String room, int price) {
		this.city = city;
		this.hotelId = hotelId;
		this.room = room;
		this.price = price;
	}

	@Override
	public String toString() {
		return "HotelData [city=" + city + ", hotelId=" + hotelId + ", room="
				+ room + ", price=" + price + "]";
	}
	@Override
	public int compareTo(HotelData o) {
		return this.price - o.price;
	}
}
