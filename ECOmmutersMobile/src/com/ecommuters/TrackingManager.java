package com.ecommuters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class TrackingManager {
	private static final int distanceMeters = 2;
	private static final int toleranceMinutes = 15;
	private Hashtable<String, Integer> mIndexMap = new Hashtable<String, Integer>();
	// disposizione del
	// gps per
	// connettersi e
	// trovare la
	// posizione
	private List<Route> mTrackingRoutes;

	public boolean liveTracking() {
		return (mTrackingRoutes != null && mTrackingRoutes.size() > 0);
	}

	public void locationChanged(ECommuterPosition mLocation) {
		mTrackingRoutes = getRoutesByPosition(mLocation);

	}

	private List<Route> getRoutesByPosition(ECommuterPosition position) {
		List<Route> trackingRoutesByTime = getRoutesByTime();
		List<Route> trackingRoutes = new ArrayList<Route>();
		resetIndexMap(trackingRoutesByTime);
		for (Route r : trackingRoutesByTime) {
			boolean tracked = false;
			for (int i = getStartingIndex(r); i < r.getPoints().size(); i++) {
				RoutePoint pt = r.getPoints().get(i);
				if (position.distance(pt) < distanceMeters)// due metri
				{
					trackingRoutes.add(r);
					addToIndexMap(r, i);
					tracked = true;
					break;
				}
			}
			if (!tracked)
				removeFromIndexMap(r);
		}
		return trackingRoutes;

	}
	private void resetIndexMap(List<Route> trackingRoutesByTime) {
		for (String name : mIndexMap.keySet())
		{
			if(!contains(trackingRoutesByTime, name))
				mIndexMap.put(name, 0);
		}
	}

	private boolean contains(List<Route> trackingRoutesByTime, String name) {
		for (Route r : trackingRoutesByTime)
			if (r.getName().equals(name))
				return true;
		return false;
	}

	private void removeFromIndexMap(Route r) {
		mIndexMap.remove(r.getName());
	}

	private void addToIndexMap(Route r, int i) {
		mIndexMap.put(r.getName(), i);
	}

	private int getStartingIndex(Route r) {
		Integer index = mIndexMap.get(r.getName());
		return index == null ? 0 : index;
	}
	private List<Route> getRoutesByTime() {
		List<Route> trackingRoutes = new ArrayList<Route>();
		for (Route r : MyApplication.getInstance().getRoutes())
			trackingRoutes.add(r);
		return trackingRoutes;
	}
	private List<Route> getRoutesByTime1() {
		List<Route> trackingRoutes = new ArrayList<Route>();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, toleranceMinutes);
		Date nowForward = cal.getTime();
		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -toleranceMinutes);
		Date nowBackward = cal.getTime();
		for (Route r : MyApplication.getInstance().getRoutes()) {
			Date from = new Date((long) (r.getPoints().get(0).time * 1e3));
			Date to = new Date((long) (r.getPoints().get(
					r.getPoints().size() - 1).time * 1e3));
			if (Helper.compare(nowForward, from) > 0
					&& Helper.compare(nowBackward, to) < 0) {
				trackingRoutes.add(r);
			}
		}
		return trackingRoutes;

	}
	public boolean isTimeForTracking() {
		return getRoutesByTime().size() > 0;
	}
}
