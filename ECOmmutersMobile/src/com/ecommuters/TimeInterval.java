package com.ecommuters;

import java.util.Calendar;
import java.util.Date;

class TimeInterval {

	/**
	 * 
	 */
	private Calendar start;
	private Calendar end;
	private Route route;
	private int weigth;
	
	public TimeInterval(Route r, long time, int weight) {
		assert (weight < GPSManager.MAX_TIMER_INTERVALS);
		this.route = r;
		this.weigth = weight;
		int before = GPSManager.minutesBeforeStart[weight];
		int after = GPSManager.minutesAfterStart[weight];
		start = Calendar.getInstance();
		start.setTimeInMillis(time);
		start.add(Calendar.MINUTE, -before);
		
		end = Calendar.getInstance();
		end.setTimeInMillis(time);
		end.add(Calendar.MINUTE, after);
	}

	public Calendar getStart() {
		return start;
	}

	

	public Calendar getEnd() {
		return end;
	}



	public boolean isActiveNow() {
		Date now = new Date(System.currentTimeMillis());
		return Helper.compare(now, start.getTime()) > 0 && Helper.compare(now, end.getTime()) < 0;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public int getWeight() {
		return weigth;
	}

}
