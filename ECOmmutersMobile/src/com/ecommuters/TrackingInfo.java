package com.ecommuters;

import java.util.Date;

public class TrackingInfo {
	private int latestIndex = 0;
	private Date startOfTracking;
	private Date endOfTracking;

	public int getLatestIndex() {

		return latestIndex;
	}

	public void setLatestIndex(int i) {
		latestIndex = i;
		endOfTracking = new Date();
		if (startOfTracking == null)
			startOfTracking = endOfTracking;

	}

	public void reset() {
		latestIndex = 0;
		startOfTracking = null;
		endOfTracking = null;

	}

}
