package com.ecommuters;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class ECOmmutersLocationOverlay extends MyLocationOverlay {

	private MapController mController;
	private RoutesOverlay mRoutesOverlay;

	public ECOmmutersLocationOverlay(Context context, MapView map,
			MapController controller, RoutesOverlay routesOverlay) {
		super(context, map);
		this.mController = controller;
		this.mRoutesOverlay = routesOverlay;
	}

	@Override
	public synchronized void onLocationChanged(Location loc) {
		// se ho il balloon aperto, seguo il balloon e non la mia posizione
		if (mRoutesOverlay.getFocus() == null && loc.getProvider().equals(LocationManager.GPS_PROVIDER)) {
			mController.animateTo(new GeoPoint((int) (loc.getLatitude() * 1e6),
					(int) (loc.getLongitude() * 1e6)));
		}
		super.onLocationChanged(loc);
	}
}
