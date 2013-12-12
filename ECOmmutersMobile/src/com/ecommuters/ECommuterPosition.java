package com.ecommuters;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ECommuterPosition extends GpsPoint implements IJsonSerializable,
		Serializable {
	@Override
	public String toString() {
		return name + " " + surname;
	}

	int userId;
	String name;
	String surname;
	float accuracy;
	private ArrayList<Integer> routeIds = new ArrayList<Integer>();

	private static final long serialVersionUID = -5703092633640293472L;

	public ECommuterPosition(int userId, int lat, int lon, String name,
			String surname, long time) {
		super(lat, lon, time);
		this.userId = userId;
		this.name = name;
		this.surname = surname;
	}

	public ECommuterPosition(int userId, int lat, int lon, long time) {
		super(lat, lon, time);
		this.userId = userId;
		this.name = null;
		this.surname = null;
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

		JSONArray arRoutes = new JSONArray();
		obj.put("routes", arRoutes);
		for (Integer id : routeIds)
			arRoutes.put(id);
		return obj;
	}

	public static ECommuterPosition parseJSON(JSONObject jsonObject)
			throws JSONException {
		ECommuterPosition position = new ECommuterPosition(
				jsonObject.getInt("userid"), jsonObject.getInt("lat"),
				jsonObject.getInt("lon"), jsonObject.getString("name"),
				jsonObject.getString("surname"), 
				jsonObject.getLong("time"));
		JSONArray arRoutes = jsonObject.getJSONArray("routes");
		int length = arRoutes.length();
		for (int i = 0; i < length; i++) {
			position.routeIds.add(arRoutes.getInt(i));
		}
		return position;
	}

	public void addRoute(int id) {
		routeIds.add(id);

	}

	public ArrayList<Integer> getRoutes() {
		return routeIds;
	}

}
