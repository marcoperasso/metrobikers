package com.ecommuters;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class ECommuterPosition implements IJsonSerializable, Serializable {

	int lat;
	int lon;
	long time;
	String name;
	String surname;
	String mail;
	private static final long serialVersionUID = -5703092633640293472L;

	public ECommuterPosition(int lat, int lon, String name,
			String surname, String mail, long time) {
		this.lat = lat;
		this.lon = lon;
		this.name = name;
		this.surname = surname;
		this.mail = mail;
		this.time = time;
	}

	public ECommuterPosition(int lat, int lon, long time) {
		this.lat = lat;
		this.lon = lon;
		this.name = null;
		this.surname = null;
		this.mail = null;
		this.time = time;
	}
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("lat", lat);
		obj.put("lon", lon);
		obj.put("time", time);
		if (name != null)
			obj.put("name", name);
		if (surname != null)
			obj.put("surname", surname);
		if (mail != null)
			obj.put("mail", mail);
		return obj;
	}

	public static ECommuterPosition parseJSON(JSONObject jsonObject) throws JSONException {
		return new ECommuterPosition(jsonObject.getInt("lat"),
				jsonObject.getInt("lon"), jsonObject.getString("name"),
				jsonObject.getString("surname"), jsonObject.getString("mail"), jsonObject.getLong("time"));
	}

}