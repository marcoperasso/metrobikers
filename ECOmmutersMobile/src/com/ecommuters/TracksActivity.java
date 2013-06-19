package com.ecommuters;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class TracksActivity extends MapActivity {

	private MyMapView mMap;

	private MapController mController;
	private LocationManager mlocManager;

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
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		enableGPS();
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

		myLocationOverlay = new MtbMyLocationOverlay(this, mMap, mController);
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

			mTracksOverlay.setCurrentTrack(
					(GPSTrack) savedInstanceState
					.getSerializable(Const.TRACK), 
					(TrackInfo) savedInstanceState
					.getSerializable(Const.TRACKINFO));
			
			mTracksOverlay.setActiveTrackName(savedInstanceState.getString(Const.ACTIVE_TRACK_NAME));
			int late6 = savedInstanceState.getInt(Const.MAPLATITUDE);
			int lone6 = savedInstanceState.getInt(Const.MAPLONGITUDE);
			mController.animateTo(new GeoPoint(late6, lone6));

			zoomLevel = savedInstanceState.getInt(Const.ZoomLevel, 15);
		}
		mController.setZoom(zoomLevel);

//		// Look up the AdView as a resource and load a request.
//		AdView adView = (com.google.ads.AdView) this.findViewById(R.id.ad);
//		adView.loadAd(new com.google.ads.AdRequest());

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
					return getVisibleTracks(params[0], params[1]);
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

	private ArrayList<Track> getVisibleTracks(GeoPoint ul, GeoPoint br) {

		int minlat = br.getLatitudeE6();
		int maxlat = ul.getLatitudeE6();
		int minlon = ul.getLongitudeE6();
		int maxlon = br.getLongitudeE6();
		String reqString = RequestBuilder.getDownloadTracksRequest(minlat,
				maxlat, minlon, maxlon);
		ArrayList<Track> tracks = new ArrayList<Track>();
		StringBuilder result = new StringBuilder();
		if (!Helper.sendRequest(reqString, result))
			return tracks;

		try {
			// this will break the JSON messages into an array
			JSONArray aryJSONTracks = new JSONArray(result.toString());
			// loop through the array
			for (int i = 0; i < aryJSONTracks.length(); i++) {
				Track msgSum = new Track();
				JSONObject obj = aryJSONTracks.getJSONObject(i);
				msgSum.setName(obj.getString("name"));
				msgSum.setLat(obj.getInt("lat"));
				msgSum.setLon(obj.getInt("lon"));
				tracks.add(msgSum);
			}
		} catch (JSONException e) {
			Log.e("json", e.toString());
		}
		return tracks;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Const.ACTIVATE_GPS) {
			Toast.makeText(this, R.string.gps_enabled, Toast.LENGTH_SHORT)
					.show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void enableGPS() {
		if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.need_gps)
					.setPositiveButton(R.string.yes, new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							Intent myIntent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(myIntent, Const.ACTIVATE_GPS);
							return;

						}
					}).setNegativeButton(R.string.no, new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							mTrackGPSPosition = false;
						}
					}).show();
			return;
		}
	}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		try {
			outState.putSerializable(Const.TRACKS, mTracks);
			outState.putSerializable(Const.TRACK, mTracksOverlay.getGpsPoints());
			outState.putSerializable(Const.TRACKINFO, mTracksOverlay.getTrackInfo());
			outState.putString(Const.ACTIVE_TRACK_NAME, mTracksOverlay.getActiveTrackName());
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
		TextView tv = (TextView)findViewById(R.id.textViewNotification);
		LinearLayout l = (LinearLayout)findViewById(R.id.layoutNotification);
		if (id == -1)
			l.setVisibility(View.GONE);
		else
		{
			l.setVisibility(View.VISIBLE);
			tv.setText(id);
		}
	}

}