package com.ecommuters;

import java.util.Calendar;
import java.util.Date;

class TimeInterval {

	/**
	 * 
	 */
	private Date start;
	private Date end;
	private Route route;
	private int weigth;
	
	public TimeInterval(Route r, long time, int weight) {
		assert (weight < GPSManager.MAX_TIMER_INTERVALS);
		this.route = r;
		this.weigth = weight;
		int before = GPSManager.minutesBeforeStart[weight];
		int after = GPSManager.minutesAfterStart[weight];
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time));
		cal.add(Calendar.MINUTE, -before);
		start = cal.getTime();
		cal.add(Calendar.MINUTE, before + after);
		end = cal.getTime();
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public boolean isActiveNow() {
		Date now = new Date(System.currentTimeMillis());
		return Helper.compare(now, start) > 0 && Helper.compare(now, end) < 0;
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
