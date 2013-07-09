package com.ecommuters;

import java.util.ArrayList;
import java.util.List;

public class RegisteredPoints {
	private String routeName;
	private List<GpsPoint> points = new ArrayList<GpsPoint>();
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	public List<GpsPoint> getPoints() {
		return points;
	}
	public boolean isEmpty() {
		return points.isEmpty();
	}
	
}
