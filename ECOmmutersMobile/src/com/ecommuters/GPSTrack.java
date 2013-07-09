package com.ecommuters;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class GPSTrack extends ArrayList<GpsPoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6679824025513000973L;

	double minEle = Double.MAX_VALUE;
	double maxEle = Double.MIN_VALUE;
	int minLat = Integer.MAX_VALUE;
	int minLon = Integer.MAX_VALUE;
	int maxLat = Integer.MIN_VALUE;
	int maxLon = Integer.MIN_VALUE;

	private GeoPoint bottomRight;

	private GeoPoint topLeft;

	@Override
	public boolean add(GpsPoint object) {
		minEle = Math.min(minEle, object.ele);
		maxEle = Math.max(maxEle, object.ele);
		minLat = Math.min(minLat, object.lat);
		maxLat = Math.max(maxLat, object.lat);
		minLon = Math.min(minLon, object.lon);
		maxLon = Math.max(maxLon, object.lon);
		return super.add(object);
	}

	public GeoPoint getBottomRight() {
		if (bottomRight == null)
			bottomRight = new GeoPoint(minLat, maxLon);
		return bottomRight;
	}

	public GeoPoint getTopLeft() {
		if (topLeft == null)
			topLeft = new GeoPoint(maxLat, minLon);
		return  topLeft;
	}

}
