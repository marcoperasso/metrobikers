package com.ecommuters;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class GpsPoint implements Serializable, IJsonSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1654731097649464268L;
	int lat;
	int lon;
	double ele;
	Date time;

	GpsPoint(int lat, int lon, double ele, Date time) {
		this.lat = lat;
		this.lon = lon;
		this.ele = ele;
		this.time = time;
	}

	public GpsPoint() {

	}

	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("lat", lat);
		obj.put("lon", lon);
		obj.put("ele", ele);
		obj.put("time", time);
		return obj;
	}

	

}
