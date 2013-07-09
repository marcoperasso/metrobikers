package com.ecommuters;

import java.io.Serializable;
import java.util.Date;

public class GpsPoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1654731097649464268L;
	double ele;
	Date date;
	int lat;
	int lon;

	GpsPoint(int lat, int lon, double ele, Date date) {
		this.lat = lat;
		this.lon = lon;
		this.ele = ele;
		this.date = date;
	}

	public GpsPoint() {

	}

	

}
