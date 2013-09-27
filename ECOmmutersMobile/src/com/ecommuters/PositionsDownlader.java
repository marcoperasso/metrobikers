package com.ecommuters;

import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.maps.GeoPoint;

public class PositionsDownlader {
	private Handler mHandler;

	private AsyncTask<GeoPoint, Void, PositionList> downloadPositionsTask;

	private Runnable downloadPositionsRunnable;

	private RoutesOverlay mRoutesOverlay;

	private MyMapView mMap;

	private MyMapActivity activity;

	public PositionsDownlader(MyMapView map, RoutesOverlay overlay,
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
			downloadPositionsTask = new AsyncTask<GeoPoint, Void, PositionList>() {

				@Override
				protected PositionList doInBackground(
						GeoPoint... params) {
					final GeoPoint ul = params[0];
					final GeoPoint br = params[1];

					return RequestBuilder.getPositions(ul.getLatitudeE6(),
							ul.getLongitudeE6(), br.getLatitudeE6(),
							br.getLongitudeE6());
				}

				protected void onPostExecute(
						PositionList positions) {
					mRoutesOverlay.setPositions(positions);

				};

			}.execute(ul, br);
		}
		if (mHandler != null) {

			mHandler.postDelayed(downloadPositionsRunnable, 5000);
		}

	}
}
