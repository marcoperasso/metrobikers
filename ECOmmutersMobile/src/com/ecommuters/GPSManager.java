package com.ecommuters;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import android.util.Log;

class GPSStatus {
	int[] levels = new int[Route.MAX_TIMER_INTERVALS + 2];// uno aggiuntivo per il
													// tracking
	// automatico, uno per il
	// tracking manuale
	int currentLevel = -1;
	public final static int[] minDistanceMetres = { 10, 5, 5 };
	public final static long[] minTimeSecs = { 10, 5, 5 };
	
	public static final int AUTOMATIC_TRACKING = Route.MAX_TIMER_INTERVALS;
	public static final int MANUAL_TRACKING = Route.MAX_TIMER_INTERVALS + 1;

	public boolean startGPS(int level) {
		levels[level]++;
		
		return updateCurrentLevel();
	}

	public boolean stopGPS(int level) {
		levels[level]--;

		return updateCurrentLevel();
	}

	private boolean updateCurrentLevel() {
		int l = activeLevel();
		if (currentLevel != l) {
			currentLevel = l;
			return true;
		}
		return false;
	}

	private int activeLevel() {
		for (int i = levels.length - 1; i >= 0; i--)
			if (levels[i] > 0)
				return i;
		return -1;
	}

	public boolean requestingLocation() {
		return currentLevel >= 0;
	}

	public int getMinDinstanceMeters() {
		if (currentLevel < 0 || currentLevel >= minDistanceMetres.length)
			return Integer.MAX_VALUE;
		return minDistanceMetres[currentLevel];
	}

	public long getMinTimeSeconds() {
		if (currentLevel < 0 || currentLevel >= minTimeSecs.length)
			return Long.MAX_VALUE;
		return minTimeSecs[currentLevel];
	}

	public boolean resetLevels() {
		for (int i = 0; i < Route.MAX_TIMER_INTERVALS; i++)
			levels[i] = 0;
		return updateCurrentLevel();
	}

}

class GPSManager {
	private Hashtable<Integer, GPSStatus> mGPSStatus = new Hashtable<Integer, GPSStatus>();

	private GPSStatus getGPSStatus(int routeId) {
		GPSStatus m = mGPSStatus.get(routeId);
		if (m == null) {
			m = new GPSStatus();
			mGPSStatus.put(routeId, m);
		}
		return m;
	}

	public boolean isManualTracking() {
		for (GPSStatus m : mGPSStatus.values())
			if (m.currentLevel == GPSStatus.MANUAL_TRACKING)
				return true;
		return false;
	}

	void resetLevels() {
		for (GPSStatus s : mGPSStatus.values()) {
			s.resetLevels();
		}

	}

	boolean requestingLocation() {
		for (GPSStatus m : mGPSStatus.values())
			if (m.requestingLocation())
				return true;
		return false;
	}

	boolean startGPS(int level, int routeId) {
		boolean b = getGPSStatus(routeId).startGPS(level);
		
		if (MyApplication.LogEnabled)
			Log.d(Const.ECOMMUTERS_TAG, String.format(
				"Raising GPS listening for route %d and level %d; current level status: %s",
						routeId, level, getLevelString()));
		return b;
	}

	boolean stopGPS(int level, int routeId) {

		boolean b = getGPSStatus(routeId).stopGPS(level);
		if (MyApplication.LogEnabled)
			Log.d(Const.ECOMMUTERS_TAG,
				String.format(
						"Lowering GPS listening for route %d and level %d; current level status: %s",
						routeId, level, getLevelString()));
		return b;
	}

	private String getLevelString() {
		StringBuilder sb = new StringBuilder();
		Enumeration<Integer> keys = mGPSStatus.keys();
		while (keys.hasMoreElements()) {
			int routeId = keys.nextElement();
			sb.append("Route id: ");
			sb.append(routeId);
			sb.append(" - levels: ");
			sb.append(Arrays.toString(mGPSStatus.get(routeId).levels));
			sb.append("; ");
		}
		return sb.toString();
	}

	public int getMinDinstanceMeters() {
		int d = Integer.MAX_VALUE;
		for (GPSStatus m : mGPSStatus.values())
			d = Math.min(d, m.getMinDinstanceMeters());
		return d;
	}

	public long getMinTimeSeconds() {
		long s = Long.MAX_VALUE;
		for (GPSStatus m : mGPSStatus.values())
			s = Math.min(s, m.getMinDinstanceMeters());
		return s;
	}

}
