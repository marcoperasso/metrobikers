package com.ecommuters;

import java.util.Arrays;
import java.util.Hashtable;

import android.util.Log;

class GPSStatus {
	int[] levels = new int[MAX_TIMER_INTERVALS + 2];// uno aggiuntivo per il
													// tracking
	// automatico, uno per il
	// tracking manuale
	int currentLevel = -1;
	public final static int[] minDistanceMetres = { 10, 5, 5 };
	public final static long[] minTimeSecs = { 10, 5, 5 };
	public final static int[] minutesBeforeStart = { 10 };// minuti
															// prima
															// della
															// partenza
															// della
															// traccia
															// a
															// partire
															// dai
															// quali
															// attivo
															// il
															// GPS
	public final static int[] minutesAfterStart = { 20 };// minuti
															// dopo
															// la
															// partenza
															// dopo
															// i
															// quali
															// disattivo
															// il
															// GPS
	public static final int MAX_TIMER_INTERVALS = minutesBeforeStart.length;
	public static final int AUTOMATIC_TRACKING = MAX_TIMER_INTERVALS;
	public static final int MANUAL_TRACKING = MAX_TIMER_INTERVALS + 1;

	public boolean startGPS(int level) {
		levels[level]++;
		Log.d(Const.ECOMMUTERS_TAG, String.format(
				"Raising GPS listening level %d; current level status: %s",
				level, Arrays.toString(levels)));
		return updateCurrentLevel();
	}

	public boolean stopGPS(int level) {
		levels[level]--;
		Log.d(Const.ECOMMUTERS_TAG, String.format(
				"Lowering GPS listening level %d; current level status: %s",
				level, Arrays.toString(levels)));
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
		for (int i = 0; i < MAX_TIMER_INTERVALS; i++)
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
		return getGPSStatus(routeId).startGPS(level);
	}
	boolean stopGPS(int level, int routeId) {
		return getGPSStatus(routeId).stopGPS(level);
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
