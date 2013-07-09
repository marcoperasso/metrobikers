package com.ecommuters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class TracksActivity extends MapActivity {

	private MyMapView mMap;

	private MapController mController;

	private TracksOverlay mTracksOverlay;
	private boolean mTrackGPSPosition;
	private ArrayList<Track> mTracks;

	private MyLocationOverlay myLocationOverlay;
	MenuItem mMenuItemTrackGpsPosition;

	private GeoPoint upperLeft;
	private GeoPoint bottomRight;
	private boolean retrievingTracks;

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracks);
		mTrackGPSPosition = MySettings.getTrackGPSPosition(this);

		mMap = (MyMapView) this.findViewById(R.id.mapview1);
		mController = mMap.getController();
		mMap.setSatellite(true);
		mMap.displayZoomControls(true);
		int zoomLevel = 15;

		List<Overlay> mapOverlays = mMap.getOverlays();
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.ic_routemarker);
		mTracksOverlay = new TracksOverlay(drawable, this, mMap);
		mapOverlays.add(mTracksOverlay);

		myLocationOverlay = new ECOmmutersLocationOverlay(this, mMap,
				mController);
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mController.animateTo(myLocationOverlay.getMyLocation());
			}
		});

		mapOverlays.add(myLocationOverlay);
		if (savedInstanceState != null) {
			mTracks = (ArrayList<Track>) savedInstanceState
					.getSerializable(Const.TRACKS);
			if (mTracks != null)
				mTracksOverlay.addTracksOverlay(mTracks);

			mTracksOverlay.setCurrentTrack((GPSTrack) savedInstanceState
					.getSerializable(Const.TRACK),
					(TrackInfo) savedInstanceState
							.getSerializable(Const.TRACKINFO));

			mTracksOverlay.setActiveTrackName(savedInstanceState
					.getString(Const.ACTIVE_TRACK_NAME));
			int late6 = savedInstanceState.getInt(Const.MAPLATITUDE);
			int lone6 = savedInstanceState.getInt(Const.MAPLONGITUDE);
			mController.animateTo(new GeoPoint(late6, lone6));

			zoomLevel = savedInstanceState.getInt(Const.ZoomLevel, 15);
		}
		mController.setZoom(zoomLevel);

		// // Look up the AdView as a resource and load a request.
		// AdView adView = (com.google.ads.AdView) this.findViewById(R.id.ad);
		// adView.loadAd(new com.google.ads.AdRequest());

	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tracks_menu, menu);
		mMenuItemTrackGpsPosition = menu.findItem(R.id.itemTrackGpsPosition);
		mMenuItemTrackGpsPosition
				.setTitleCondensed(getString(mTrackGPSPosition ? R.string.hide_position_menu
						: R.string.show_position_menu));
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemTrackGpsPosition:
			setTrackGPSPosition(!mTrackGPSPosition);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setTrackGPSPosition(boolean b) {
		mTrackGPSPosition = b;

		MySettings.setTrackGPSPosition(this, mTrackGPSPosition);
		mMenuItemTrackGpsPosition
				.setTitleCondensed(getString(mTrackGPSPosition ? R.string.hide_position_menu
						: R.string.show_position_menu));

		if (mTrackGPSPosition)
			myLocationOverlay.enableMyLocation();
		else
			myLocationOverlay.disableMyLocation();
	}

	private void showTracks(GeoPoint ul, GeoPoint br) {
		try {

			retrievingTracks = true;
			class DownloadTracksTask extends
					AsyncTask<GeoPoint, Void, ArrayList<Track>> {
				protected void onPostExecute(ArrayList<Track> tracks) {
					mTracks = tracks;
					mTracksOverlay.addTracksOverlay(mTracks);
					retrievingTracks = false;
				}

				@Override
				protected ArrayList<Track> doInBackground(GeoPoint... params) {
					return null;// getVisibleTracks(params[0], params[1]);
				}

			}
			DownloadTracksTask task = new DownloadTracksTask();
			task.execute(ul, br);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}

	public void checkTracks() {
		if (!retrievingTracks) {
			GeoPoint ul = mMap.getProjection().fromPixels(0, 0);
			GeoPoint br = mMap.getProjection().fromPixels(mMap.getWidth(),
					mMap.getHeight());

			if (!ul.equals(upperLeft) || !br.equals(bottomRight)) {
				upperLeft = ul;
				bottomRight = br;
				showTracks(ul, br);
			}
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		try {
			outState.putSerializable(Const.TRACKS, mTracks);
			outState.putSerializable(Const.TRACK, mTracksOverlay.getGpsPoints());
			outState.putSerializable(Const.TRACKINFO,
					mTracksOverlay.getTrackInfo());
			outState.putString(Const.ACTIVE_TRACK_NAME,
					mTracksOverlay.getActiveTrackName());
			outState.putInt(Const.MAPLATITUDE, mMap.getMapCenter()
					.getLatitudeE6());
			outState.putInt(Const.MAPLONGITUDE, mMap.getMapCenter()
					.getLongitudeE6());
			outState.putInt(Const.ZoomLevel, mMap.getZoomLevel());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
		mTracksOverlay.recycle();
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (mTrackGPSPosition)
			myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void notifyMessage(int id) {
		TextView tv = (TextView) findViewById(R.id.textViewNotification);
		LinearLayout l = (LinearLayout) findViewById(R.id.layoutNotification);
		if (id == -1)
			l.setVisibility(View.GONE);
		else {
			l.setVisibility(View.VISIBLE);
			tv.setText(id);
		}
	}

}