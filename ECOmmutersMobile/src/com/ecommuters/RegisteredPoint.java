package com.ecommuters;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisteredPoint extends GpsPoint implements IJsonSerializable,
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4639726905468751875L;
	int id;

	public RegisteredPoint(int id, int lat, int lon, double ele, long unixTime) {
		super(lat, lon, ele, unixTime);
		this.id = id;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("lat", lat);
		obj.put("lon", lon);
		obj.put("ele", ele);
		obj.put("time", unixTime);
		return obj;
	}

}
