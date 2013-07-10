package com.ecommuters;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisteredRoute implements IJsonSerializable{
	private String name;
	private List<GpsPoint> points = new ArrayList<GpsPoint>();
	public String getName() {
		return name;
	}
	public void setName(String routeName) {
		this.name = routeName;
	}
	public List<GpsPoint> getPoints() {
		return points;
	}
	public boolean isEmpty() {
		return points.isEmpty();
	}
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		JSONArray arPoints = new JSONArray();
		obj.put("points", arPoints);
		for (GpsPoint pt : points)
			arPoints.put(pt.toJson());
		return obj;
	}
	
}
