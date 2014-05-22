package com.example.weather;


public class CityObject {
	//TODO Objects for values that might be null
	public int serverId;
	public String name;
	public String country;
	public boolean favourite;
	
	public CityObject(){
		serverId = 0;
		name		 = "No data";
		country		 = "No data";
		favourite	 = false;
	}
	
	public CityObject(	int serverId,
						String name,
						String country,
						boolean favourite){
		this.serverId	= serverId;
		this.name		= name;
		this.country	= country;
		this.favourite	= favourite;
	}
	
	public String getCityNameCountry(){
		return this.name + ", " + this.country;
	}
	
}
