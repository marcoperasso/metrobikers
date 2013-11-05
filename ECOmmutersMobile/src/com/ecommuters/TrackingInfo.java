package com.ecommuters;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
	private long time;
	
	public TrackingInfo(Route route) {
		this.routeId = route.getId();
		this.time = System.currentTimeMillis() / 1000;
	}

	public int getLatestIndex() {
		return indexes.isEmpty() ? 0 : indexes.get(indexes.size()-1);
	}

	public void reset() {
		indexes.clear();
		positions.clear();

	}

	public void addPosition(int index, ECommuterPosition position) {
		indexes.add(index);
		positions.add(new RoutePoint(positions.size(), position.lat,
				position.lon, position.time));
		this.time = position.time;

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
		List<Integer> distances = new ArrayList<Integer>();
		int sum = 0;
		int a = -1;
		for (int i : indexes)
		{
			int distance = i - a;
			sum += distance;
			distances.add(distance);
			a = i;
		}
		int distance = maxIdx - a;
		sum += distance;
		distances.add(distance);
		
		double med = sum / distances.size();
		
		double varSum = 0.0;
		
		for (int d : distances)
		{
			varSum += Math.pow((d - med), 2.0);
		}
		
		double variance = varSum / distances.size();
		double stdDev = Math.sqrt(variance);
		
		boolean b = stdDev < 2 * med;
		if (!b)
		{
			Log.d(Const.ECOMMUTERS_TAG, String.format("Recording not evenly distribuded; mean: %f; standard deviation: %f", med, stdDev));
		}
		return b;
	}

	public static TrackingInfo readTrackingInfo(Context context, String fileName) {
		return (TrackingInfo) Helper.readObject(context, fileName);
	}

	
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("routeid", routeId);
		obj.put("time", time);
		JSONArray arPoints = new JSONArray();
		obj.put("points", arPoints);
		for (RoutePoint pt : positions)
			arPoints.put(pt.toJson());
		return obj;
	}

	public int size() {
		return positions.size();
	}

}
