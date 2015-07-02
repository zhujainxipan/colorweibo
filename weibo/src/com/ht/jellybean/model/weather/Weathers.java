package com.ht.jellybean.model.weather;

import java.util.List;

public class Weathers {
	private String error;
	private String status;
	private String date;
	private List<CityWeather> results;
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<CityWeather> getResults() {
		return results;
	}
	public void setResults(List<CityWeather> results) {
		this.results = results;
	}
	
	
	

}
