package com.ht.weibo.model.weather;

import java.util.List;

public class CityWeather {
	private String currentCity;
	private String pm25;
	private List<Live> index;
	private List<DayData> weather_data;
	public String getCurrentCity() {
		return currentCity;
	}
	public void setCurrentCity(String currentCity) {
		this.currentCity = currentCity;
	}
	public String getPm25() {
		return pm25;
	}
	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}
	public List<Live> getIndex() {
		return index;
	}
	public void setIndex(List<Live> index) {
		this.index = index;
	}
	public List<DayData> getWeather_data() {
		return weather_data;
	}
	public void setWeather_data(List<DayData> weather_data) {
		this.weather_data = weather_data;
	}

	
}
