package com.volvo.congestion_tax.model;

import java.util.List;

public class TaxRequest {
	
	private String vehicleType;
    private String city;
    private List<String> timestamps;
    
    // Getters and setters
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public List<String> getTimestamps() {
		return timestamps;
	}
	public void setTimestamps(List<String> timestamps) {
		this.timestamps = timestamps;
	}
        
}