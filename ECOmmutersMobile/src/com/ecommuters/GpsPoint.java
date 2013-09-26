package com.ecommuters;

import java.io.Serializable;


public class GpsPoint implements  Serializable{
	private static final double radius = 6378137.0;
	/**
	 * 
	 */
	private static final long serialVersionUID = -3295827706008323983L;
	int lat;
	int lon;
	long time;

	GpsPoint(int lat, int lon, long time) {
		this.lat = lat;
		this.lon = lon;
		this.time = time;
	}

	public GpsPoint() {

	}
	///distanza in metri
    public double distance(GpsPoint pt) {
		return calculateGreatCircleDistance(radians(lat), radians(lon), radians(pt.lat), radians(pt.lon));
	}
	public double radians(int degreesE6)
    {
        return degreesE6 * Math.PI / 180e6;
    }
	///distanza in metri
    public double calculateGreatCircleDistance(double lat1, double long1, double lat2, double long2)
    {
        return radius * Math.acos(
            Math.sin(lat1) * Math.sin(lat2)
            + Math.cos(lat1) * Math.cos(lat2) * Math.cos(long2 - long1));
    }

	
	

}
