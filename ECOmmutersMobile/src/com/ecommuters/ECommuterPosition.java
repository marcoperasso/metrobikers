package com.ecommuters;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ECommuterPosition extends GpsPoint
		implements
			IJsonSerializable,
			Serializable {
	int userId;
	String name;
	String surname;
	String mail;
	float accuracy;
	private ArrayList<Integer> routeIds = new ArrayList<Integer>();
	
	private static final long serialVersionUID = -5703092633640293472L;

	public ECommuterPosition(int userId, int lat, int lon, String name,
			String surname, String mail, long time) {
		super(lat, lon, time);
		this.userId = userId;
		this.name = name;
		this.surname = surname;
		this.mail = mail;
	}

	public ECommuterPosition(int userId, int lat, int lon, float accuracy, long time) {
		super(lat, lon, time);
		this.accuracy = accuracy;
		this.userId = userId;
		this.name = null;
		this.surname = null;
		this.mail = null;
	}
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("userid", userId);
		obj.put("lat", lat);
		obj.put("lon", lon);
		obj.put("time", time);
		if (name != null)
			obj.put("name", name);
		if (surname != null)
			obj.put("surname", surname);
		if (mail != null)
			obj.put("mail", mail);
		
		JSONArray arRoutes = new JSONArray();
		obj.put("routes", arRoutes);
		for (Integer id : routeIds)
			arRoutes.put(id);
		return obj;
	}

	public static ECommuterPosition parseJSON(JSONObject jsonObject)
			throws JSONException {
		ECommuterPosition position = new ECommuterPosition(jsonObject.getInt("userid"),
				jsonObject.getInt("lat"), jsonObject.getInt("lon"),
				jsonObject.getString("name"), jsonObject.getString("surname"),
				jsonObject.getString("mail"), jsonObject.getLong("time"));
		JSONArray arRoutes = jsonObject.getJSONArray("routes");
		int length = arRoutes.length();
		for (int i = 0; i < length; i++)
		{
			position.routeIds.add(arRoutes.getInt(i));
		}
		return position;
	}

	public void addRoute(int id) {
		routeIds.add(id);
		
	}


}
