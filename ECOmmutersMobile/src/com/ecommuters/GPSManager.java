package com.ecommuters;

public class GPSManager {
	int[] levels = new int[MAX_GPS_LEVELS + 1];
	int currentLevel = -1;
	public final static int[] minDistanceMetres = { 30, 20, 10, 5 };
	public final static long[] minTimeSecs = { 120, 90, 60, 30 };
	public final static int[] minutesBeforeStart = { 10, 5, 3 };// minuti
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
	public final static int[] minutesAfterStart = { 20, 10, 6 };// minuti
																			// dopo
																			// la
																			// partenza
																			// dopo
																			// i
																			// quali
																			// disattivo
																			// il
																			// GPS
	protected static final int MAX_GPS_LEVELS = minutesBeforeStart.length;

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
		return minDistanceMetres[currentLevel];
	}

	public long getMinTimeSeconds() {
		return minTimeSecs[currentLevel];
	}

	public boolean resetLevels() {
		for (int i = 0; i < MAX_GPS_LEVELS; i++)
			levels[i] = 0;
		return updateCurrentLevel(); 
	}

}
