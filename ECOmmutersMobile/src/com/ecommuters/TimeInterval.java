package com.ecommuters;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class TimeInterval implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4252274440166137504L;
	private Date start;
	private Date end;
	private Route route;
	public TimeInterval(Route r, long time) {
		this.route = r;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time));
		cal.add(Calendar.MINUTE, -15);
		start = cal.getTime();
		cal.add(Calendar.MINUTE, 30);
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
	

}
