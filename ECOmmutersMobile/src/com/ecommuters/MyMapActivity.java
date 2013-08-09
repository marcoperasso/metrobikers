package com.ecommuters;

import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecommuters.RecordRouteService.RecordRouteBinder;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class MyMapActivity extends MapActivity {

	private MyMapView mMap;

	private MapController mController;

	private TracksOverlay mTracksOverlay;
	private boolean mTrackGPSPosition;

	private MyLocationOverlay myLocationOverlay;
	MenuItem mMenuItemTrackGpsPosition;

	private GeoPoint upperLeft;
	private GeoPoint bottomRight;
	private boolean retrievingTracks;

	private ServiceConnection mConnection;
	private RecordRouteService mRecordService;

	private ActivityCommonActions mCommonActions;

	private Route[] mRoutes;

	private GenericEvent mRoutesChangedHandler;
	private GenericEvent updateRoutehandler = new GenericEvent() {

		@Override
		public void onEvent(Object sender, EventArgs args) {
			mMap.invalidate();

		}
	};
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymap);
		mCommonActions = new ActivityCommonActions(this);
		mTrackGPSPosition = MySettings.getTrackGPSPosition(this);

		mMap = (MyMapView) this.findViewById(R.id.mapview1);
		mController = mMap.getController();
		mMap.setSatellite(false);
		mMap.displayZoomControls(true);
		int zoomLevel = 15;
		mConnection = new ServiceConnection() {

			public void onServiceDisconnected(ComponentName name) {
				mRecordService.OnRecordingRouteUpdated.removeHandler(updateRoutehandler);
				mRecordService = null;
				mTracksOverlay.setRecordService(mRecordService);
			}

			public void onServiceConnected(ComponentName name, IBinder service) {
				mRecordService = ((RecordRouteBinder) service).getService();

				mRecordService.OnRecordingRouteUpdated.addHandler(updateRoutehandler);
				mTracksOverlay.setRecordService(mRecordService);
			}
		};
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

			int late6 = savedInstanceState.getInt(Const.MAPLATITUDE);
			int lone6 = savedInstanceState.getInt(Const.MAPLONGITUDE);
			mController.animateTo(new GeoPoint(late6, lone6));

			zoomLevel = savedInstanceState.getInt(Const.ZoomLevel, 15);
		}

		mRoutes = MyApplication.getInstance().getRoutes();
		mTracksOverlay.setRoutes(mRoutes);
		mTracksOverlay.setRecordService(mRecordService);
		mRoutesChangedHandler = new GenericEvent() {
			public void onEvent(Object sender, EventArgs args) {
				mRoutes = MyApplication.getInstance().getRoutes();
				mTracksOverlay.setRoutes(mRoutes);

				mMap.invalidate();
			}
		};
		MyApplication.getInstance().RouteChanged
				.addHandler(mRoutesChangedHandler);
		mController.setZoom(zoomLevel);
		// mTracksOverlay.drawRoutes();
		// // Look up the AdView as a resource and load a request.
		// AdView adView = (com.google.ads.AdView) this.findViewById(R.id.ad);
		// adView.loadAd(new com.google.ads.AdRequest());

	}
	@Override
	protected void onStop() {
		MyApplication.getInstance().RouteChanged
				.removeHandler(mRoutesChangedHandler);
		super.onStop();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tracks_menu, menu);
		mMenuItemTrackGpsPosition = menu.findItem(R.id.itemTrackGpsPosition);
		mMenuItemTrackGpsPosition.setTitleCondensed(getString(mTrackGPSPosition
				? R.string.hide_position_menu
				: R.string.show_position_menu));
		return mCommonActions.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (mCommonActions.onOptionsItemSelected(item))
			return true;
		switch (item.getItemId()) {
			case R.id.itemTrackGpsPosition :
				setTrackGPSPosition(!mTrackGPSPosition);
				break;
			case R.id.itemVisibleRoutes :
				chooseRoutes();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setTrackGPSPosition(boolean b) {
		mTrackGPSPosition = b;

		MySettings.setTrackGPSPosition(this, mTrackGPSPosition);
		mMenuItemTrackGpsPosition.setTitleCondensed(getString(mTrackGPSPosition
				? R.string.hide_position_menu
				: R.string.show_position_menu));

		if (mTrackGPSPosition)
			myLocationOverlay.enableMyLocation();
		else
			myLocationOverlay.disableMyLocation();
	}

	public void checkTracks() {
		if (!retrievingTracks) {
			GeoPoint ul = mMap.getProjection().fromPixels(0, 0);
			GeoPoint br = mMap.getProjection().fromPixels(mMap.getWidth(),
					mMap.getHeight());

			if (!ul.equals(upperLeft) || !br.equals(bottomRight)) {
				upperLeft = ul;
				bottomRight = br;

			}
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		try {
			outState.putInt(Const.MAPLATITUDE, mMap.getMapCenter()
					.getLatitudeE6());
			outState.putInt(Const.MAPLONGITUDE, mMap.getMapCenter()
					.getLongitudeE6());
			outState.putInt(Const.ZoomLevel, mMap.getZoomLevel());
			outState.putSerializable(Const.ROUTES, mRoutes);
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

		if (mRecordService != null) {
			if (mRecordService.isWorking())
				unbindService(mConnection);
			mRecordService.OnRecordingRouteUpdated.removeHandler(updateRoutehandler);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (mTrackGPSPosition)
			myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();

		if (Helper.isRecordingServiceRunning(this)) {
			Intent myIntent = new Intent(this, RecordRouteService.class);
			bindService(myIntent, mConnection, Context.BIND_AUTO_CREATE);
		}

		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	public void chooseRoutes() {

		final CharSequence[] items = new CharSequence[mRoutes.length];
		boolean[] checkedItems = new boolean[mRoutes.length];
		int i = 0;
		for (Route r : mRoutes) {
			items[i] = r.getName();
			checkedItems[i] = !MySettings.isHiddenRoute(this, r.getName());
			i++;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.routes)
				.setMultiChoiceItems(items, checkedItems,
						new DialogInterface.OnMultiChoiceClickListener() {

							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								MySettings.setHiddenRoute(MyMapActivity.this,
										items[which].toString(), !isChecked);
								mMap.invalidate();
							}
						}).setPositiveButton(android.R.string.ok, null).show();

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