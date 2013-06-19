package com.ecommuters;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class TrackOverlayItem  extends OverlayItem{
	private Track track;
	public TrackOverlayItem(GeoPoint point, String title, String snippet, Track track) {
		super(point, title, snippet);
		this.track = track;
	}
	Track getTrack() {
		return track;
	}
	void setTrack(Track track) {
		this.track = track;
	}
	public String getName() {
		
		return track.getName();
	}
}
