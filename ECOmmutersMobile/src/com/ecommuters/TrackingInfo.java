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
	private long startTime;
	private long endTime;
	
	public TrackingInfo(Route route) {
		this.routeId = route.getId();
		this.startTime = System.currentTimeMillis() / 1000;
		this.endTime = System.currentTimeMillis() / 1000;
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
		positions.add(new RoutePoint(position.lat,
				position.lon, position.time));
		this.endTime = position.time;

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
		return obj;
	}

	public int size() {
		return positions.size();
	}

}
