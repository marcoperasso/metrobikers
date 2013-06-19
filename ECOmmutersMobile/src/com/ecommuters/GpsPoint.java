package com.ecommuters;

import java.io.Serializable;

import android.graphics.Color;

import com.google.android.maps.GeoPoint;

public class GpsPoint extends GeoPoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1654731097649464268L;
	double ele;
	int color = Color.TRANSPARENT;

	GpsPoint(int lat, int lon, double ele) {
		super(lat, lon);
		this.ele = ele;
	}

	public GpsPoint() {
		super(0, 0);

	}

	public int getColor(double minEle, double maxEle) {
		if (color == Color.TRANSPARENT)
			color = ColorProvider.GetColor(ele, minEle, maxEle);
		return color;
	}

}
