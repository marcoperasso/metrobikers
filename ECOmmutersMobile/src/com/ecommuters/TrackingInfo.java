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
	private int latestIndex = 0;
	private List<RoutePoint> positions = new ArrayList<RoutePoint>();
	private int routeId;
	private long time;
	
	public TrackingInfo(Route route) {
		this.routeId = route.getId();
		this.time = System.currentTimeMillis() / 1000;
	}

	public int getLatestIndex() {

		return latestIndex;
	}

	public void reset() {
		latestIndex = 0;
		positions.clear();

	}

	public void addPosition(int index, ECommuterPosition position) {
		latestIndex = index;
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

}
