package com.ecommuters;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class PositionsDownloader implements Runnable{
	 final class DownloadPositionsAsyncTask extends
			AsyncTask<GeoPoint, Void, ArrayList<ECommuterPosition>> {
		private final int userId;

		private DownloadPositionsAsyncTask(int userId) {
			this.userId = userId;
		}

		@Override
		protected ArrayList<ECommuterPosition> doInBackground(
				GeoPoint... params) {
			Thread.currentThread().setName("Positions Downloader Worker");
			
			final GeoPoint ul = params[0];
			final GeoPoint br = params[1];

			return HttpManager.getPositions(ul.getLatitudeE6(),
					ul.getLongitudeE6(), br.getLatitudeE6(),
					br.getLongitudeE6(), userId);
		}

		@Override
		protected void onPostExecute(
				ArrayList<ECommuterPosition> positions) {
			mRoutesOverlay.setPositions(positions);
			downloadPositionsTask = null;
			if (mHandler != null) {
				mHandler.postDelayed(PositionsDownloader.this, downloadPositionsInterval);
			}
			super.onPostExecute(positions);
		}
	}

	private static final int downloadPositionsInterval = 5000;

	private Handler mHandler;

	private AsyncTask<GeoPoint, Void, ArrayList<ECommuterPosition>> downloadPositionsTask;

	private RoutesOverlay mRoutesOverlay;

	private MapView mMap;

	private MyMapActivity activity;

	public PositionsDownloader(MapView map, RoutesOverlay overlay,
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
			downloadPositionsTask = new DownloadPositionsAsyncTask(userId).execute(ul, br);
		}
		else
		{
			if (mHandler != null) {
				mHandler.postDelayed(PositionsDownloader.this, downloadPositionsInterval);
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
