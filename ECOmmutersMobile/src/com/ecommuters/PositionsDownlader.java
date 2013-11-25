package com.ecommuters;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class PositionsDownlader implements Runnable{
	private static final int downloadPositionsInterval = 5000;

	private Handler mHandler;

	private AsyncTask<GeoPoint, Void, ArrayList<ECommuterPosition>> downloadPositionsTask;

	private RoutesOverlay mRoutesOverlay;

	private MapView mMap;

	private MyMapActivity activity;

	public PositionsDownlader(MapView map, RoutesOverlay overlay,
			MyMapActivity activity) {
		this.mMap = map;
		this.mRoutesOverlay = overlay;
		this.activity = activity;
	}

	public void start() {
		mHandler = new Handler();
		downloadPositions();
	}

	public void stop() {
		if (downloadPositionsTask != null)
			downloadPositionsTask.cancel(false);
		mHandler.removeCallbacks(this);
		mHandler = null;
	}
	private void downloadPositions() {
		if (Helper.isOnline(activity)) {
			final int userId = getPinnedUserId();
			final GeoPoint ul = mMap.getProjection().fromPixels(0, 0);
			final GeoPoint br = mMap.getProjection().fromPixels(
					mMap.getWidth(), mMap.getHeight());
			downloadPositionsTask = new AsyncTask<GeoPoint, Void, ArrayList<ECommuterPosition>>() {

				@Override
				protected ArrayList<ECommuterPosition> doInBackground(
						GeoPoint... params) {
					final GeoPoint ul = params[0];
					final GeoPoint br = params[1];

					return HttpManager.getPositions(ul.getLatitudeE6(),
							ul.getLongitudeE6(), br.getLatitudeE6(),
							br.getLongitudeE6(), userId);
				}

				protected void onPostExecute(
						ArrayList<ECommuterPosition> positions) {
					mRoutesOverlay.setPositions(positions);
					if (mHandler != null) {
						mHandler.postDelayed(PositionsDownlader.this, downloadPositionsInterval);
					}
				};

			}.execute(ul, br);
		}
		else
		{
			if (mHandler != null) {
				mHandler.postDelayed(PositionsDownlader.this, downloadPositionsInterval);
			}
		}

	}

	private int getPinnedUserId() {
		ECommuterPosition pinnedPosition = mRoutesOverlay.getPinnedPosition();
		return pinnedPosition == null ? 0 : pinnedPosition.userId;
	}

	public void run() {
		downloadPositions();
	}
}
