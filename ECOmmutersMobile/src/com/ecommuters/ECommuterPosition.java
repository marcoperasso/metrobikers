package com.ecommuters;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class ECommuterPosition implements IJsonSerializable, Serializable {

	int lat;
	int lon;
	String name;
	String surname;
	String mail;
	private static final long serialVersionUID = -5703092633640293472L;

	public ECommuterPosition(int lat, int lon, String name,
			String surname, String mail) {
		this.lat = lat;
		this.lon = lon;
		this.name = name;
		this.surname = surname;
		this.mail = mail;
	}

	public JSONObject toJson() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}

	public static ECommuterPosition parseJSON(JSONObject jsonObject) throws JSONException {
		return new ECommuterPosition(jsonObject.getInt("lat"),
				jsonObject.getInt("lon"), jsonObject.getString("name"),
				jsonObject.getString("surname"), jsonObject.getString("mail"));
	}

}
