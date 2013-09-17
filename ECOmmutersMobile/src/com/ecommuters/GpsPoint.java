package com.ecommuters;

import java.io.Serializable;


public class GpsPoint implements  Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3295827706008323983L;
	int lat;
	int lon;
	long unixTime;

	GpsPoint(int lat, int lon, long unixTime) {
		this.lat = lat;
		this.lon = lon;
		this.unixTime = unixTime;
	}

	public GpsPoint() {

	}

	
	

}
