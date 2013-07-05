package com.ecommuters;

import android.content.Context;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class ECOmmutersLocationOverlay extends MyLocationOverlay {

	private MapController mController;

	public ECOmmutersLocationOverlay(Context context, MapView map,
			MapController controller) {
		super(context, map);
		this.mController = controller;
	}

	@Override
	public synchronized void onLocationChanged(Location loc) {
		mController.animateTo(new GeoPoint((int) (loc.getLatitude() * 1e6),
				(int) (loc.getLongitude() * 1e6)));
		super.onLocationChanged(loc);
	}
}
