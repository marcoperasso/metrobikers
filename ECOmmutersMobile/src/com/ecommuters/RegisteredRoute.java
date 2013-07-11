package com.ecommuters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisteredRoute implements IJsonSerializable, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8299537479740651539L;
	private String name;
	private List<RegisteredPoint> points = new ArrayList<RegisteredPoint>();
	public String getName() {
		return name;
	}
	public void setName(String routeName) {
		this.name = routeName;
	}
	public List<RegisteredPoint> getPoints() {
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
		for (RegisteredPoint pt : points)
			arPoints.put(pt.toJson());
		return obj;
	}
}
