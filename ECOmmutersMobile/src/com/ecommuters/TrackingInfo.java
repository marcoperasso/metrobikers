package com.ecommuters;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class TrackingInfo implements Serializable, IJsonSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2748465926568698621L;
	private List<RoutePoint> positions = new ArrayList<RoutePoint>();
	private List<Integer> indexes = new ArrayList<Integer>();
	private int routeId;
	private long startTime;
	private long endTime;
	
	private boolean valid = false;
	public TrackingInfo(Route route) {
		this.routeId = route.getId();
	}

	public int getLatestIndex() {
		return indexes.isEmpty() ? 0 : indexes.get(indexes.size()-1);
	}

	public void reset() {
		indexes.clear();
		positions.clear();
		valid = false;
	}

	public void addPosition(int index, ECommuterPosition position) {
		indexes.add(index);
		positions.add(new RoutePoint(position.lat,
				position.lon, position.time));
		if (positions.size() == 1)
			this.startTime = position.time;
		this.endTime = position.time;

	}

	private int calculateDistanceMetres() {
		Float d = 0.0f;
		for (int i = 1; i < positions.size(); i++)
			d = (float) (d + positions.get(i).distance(positions.get(i - 1)));

		return d.intValue();
	}
	public boolean save() {
		if (positions.size() == 0)
			return false;
		String routeFile = Long.toString(System.currentTimeMillis())
				+ Const.TRACKINGEXT;
		try {
			Helper.saveObject(MyApplication.getInstance(), routeFile, this);
			return true;
		} catch (IOException e) {
			Log.e(Const.ECOMMUTERS_TAG, Log.getStackTraceString(e)); 
		}
		return false;
	}

	boolean isEqualDistribution(int maxIdx) {
		int segmentLength = maxIdx / 4; 
		int lower = 0;
		int upper = segmentLength;
		for (int i : indexes)
		{
			if (i > lower && i < upper)
			{
				lower = upper;
				upper = upper + segmentLength;
			}
			if (i > upper)
				return false;
			
			if (upper > maxIdx)
				return true;
		}
		
		return false;
	}

	public static TrackingInfo readTrackingInfo(Context context, String fileName) {
		return (TrackingInfo) Helper.readObject(context, fileName);
	}

	
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("routeid", routeId);
		obj.put("start", startTime);
		obj.put("end", endTime);
		obj.put("distance", calculateDistanceMetres());
		obj.put("points", positions.size());
		return obj;
	}

	public int size() {
		return positions.size();
	}


	public boolean isValid(Route route) {
		if (valid)
			return true;
		if (indexes.size()<2)
			return false;
		int distance = 0;
		RoutePoint p1 = route.getPoints().get(indexes.get(0));
		int j = ConnectorService.DISTANCE_METERS*2;
		for (int i = 1; i < indexes.size(); i++)
		{
			RoutePoint p2 = route.getPoints().get(indexes.get(i));
			distance += p2.distance(p1);
			p1 = p2;
			
			if (distance > j)
			{
				valid = true;
				return true;
			}
		}
		return false;
	}

}
