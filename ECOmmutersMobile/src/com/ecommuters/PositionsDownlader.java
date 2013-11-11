package com.ecommuters;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class PositionsDownlader {
	private Handler mHandler;

	private AsyncTask<GeoPoint, Void, ArrayList<ECommuterPosition>> downloadPositionsTask;

	private Runnable downloadPositionsRunnable;

	private RoutesOverlay mRoutesOverlay;

	private MapView mMap;

	private MyMapActivity activity;

	public PositionsDownlader(MapView map, RoutesOverlay overlay,
			MyMapActivity activity) {
		this.mMap = map;
		this.mRoutesOverlay = overlay;
		this.activity = activity;
		downloadPositionsRunnable = new Runnable() {
			public void run() {
				downloadPositions();
			}
		};

	}

	public void start() {
		mHandler = new Handler();
		downloadPositions();
	}

	public void stop() {
		if (downloadPositionsTask != null)
			downloadPositionsTask.cancel(false);
		mHandler.removeCallbacks(downloadPositionsRunnable);
		mHandler = null;
	}
	private void downloadPositions() {
		if (Helper.isOnline(activity)) {
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
							br.getLongitudeE6());
				}

				protected void onPostExecute(
						ArrayList<ECommuterPosition> positions) {
					mRoutesOverlay.setPositions(positions);

				};

			}.execute(ul, br);
		}
		if (mHandler != null) {

			mHandler.postDelayed(downloadPositionsRunnable, 5000);
		}

	}
}
