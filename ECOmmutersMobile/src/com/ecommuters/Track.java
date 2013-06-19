package com.ecommuters;

import java.io.Serializable;


public class Track implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1341195211024511383L;
	private String name;
	private int lat;
	private int lon;


	int getLat() {
		return lat;
	}

	void setLat(int lat) {
		this.lat = lat;
	}

	int getLon() {
		return lon;
	}

	void setLon(int lon) {
		this.lon = lon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
