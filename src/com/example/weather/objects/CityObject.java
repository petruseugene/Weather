package com.example.weather.objects;


public class CityObject {
	
	private Integer serverCityId;
	private String name;
	private String country;
	private Boolean favourite;
	
	public CityObject(){
		serverCityId = 0;
		name		 = "Name";
		country		 = "Country";
		favourite	 = false;
	}
	
	public CityObject(	int serverId, String name, String country, boolean favourite){
		this.serverCityId	= serverId;
		this.name		= name;
		this.country	= country;
		this.favourite	= favourite;
	}
	
	public Integer getServerCityId(){
		return this.serverCityId;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getCountry(){
		return this.country;
	}
	
	public boolean isFavourite(){
		return this.favourite;
	}

    public void setFavourite(boolean favourite){
        this.favourite = favourite;
    }
	
	public String getCityNameCountry(){
		return String.format("%s, %s", this.name, this.country);
	}
	
}
