package com.ecommuters;

import java.util.Calendar;
import java.util.Date;

class TimeInterval  {

	protected static final int MAX_WEIGHT = 10;
	/**
	 * 
	 */
	private Date start;
	private Date end;
	private Route route;
	private int weigth;
	final int maxTimeBefore = 10;
	final int maxTimeAfter = 20;

	public TimeInterval(Route r, long time, int weight) {

		this.route = r;
		this.weigth = weight;
		int before = maxTimeBefore - weight * 2;
		int after = maxTimeAfter - weight * 2;
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
