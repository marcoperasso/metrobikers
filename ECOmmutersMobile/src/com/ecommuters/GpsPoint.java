package com.ecommuters;


public class GpsPoint {
	int lat;
	int lon;
	double ele;
	long unixTime;

	GpsPoint(int lat, int lon, double ele, long unixTime) {
		this.lat = lat;
		this.lon = lon;
		this.ele = ele;
		this.unixTime = unixTime;
	}

	public GpsPoint() {

	}

	
	

}
