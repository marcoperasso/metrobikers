package com.ecommuters;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MyMapActivity extends MapActivity {

	private MapView mMap;

	private MapController mController;

	private RoutesOverlay mRoutesOverlay;
	private ECOmmutersLocationOverlay myLocationOverlay;
	private boolean mTrackGPSPosition;

	MenuItem mMenuItemTrackGpsPosition;

	private LocationManager mlocManager;
	private boolean askingRouteName;

	private Animation mAnimation;
	private GenericEventHandler mRoutesChangedHandler;
	private GenericEventHandler mConnectorServiceChangedHandler = new GenericEventHandler() {

		@Override
		public void onEvent(Object sender, EventArgs args) {
			showTrackingButton(isManualLiveTracking());
		}
	};

	private PositionsDownloader mPositionsDownloader;

	private GenericEventHandler mUpdateRoutehandler = new GenericEventHandler() {

		@Override
		public void onEvent(Object sender, EventArgs args) {
			mMap.invalidate();

		}
	};
	private GenericEventHandler mRecordingServiceChangedHandler = new GenericEventHandler() {

		@Override
		public void onEvent(Object sender, EventArgs args) {
			// solo se il servizio � stato stoppato chiedo il salvataggio.
			if (MyApplication.getInstance().getRecordingService() != null)
				return;
			saveRecordedRouteIfNeeded();

		}
	};

	private void saveRecordedRouteIfNeeded() {
		final Route r = Route.readRoute(this, Const.RECORDING_ROUTE_FILE);
		if (r == null || r.getPoints().size() == 0)
			return;
		askingRouteName = true;
		askRouteName(new OnRouteSelected() {
			public void select(String routeName) {
				try {
					r.setName(routeName);
					r.save(MyMapActivity.this, Helper.getRouteFile(routeName));

					final File recordingFile = getFileStreamPath(Const.RECORDING_ROUTE_FILE);
					recordingFile.delete();
					MyApplication.getInstance().addRoute(r, false);

					// faccio partire il servizio che lo manda al server
					Intent service = new Intent(MyMapActivity.this,
							SyncService.class);
					startService(service);
					
					Intent intent = new Intent(MyMapActivity.this, RouteDetailActivity.class);
					intent.putExtra(Const.ROUTE_EXTRA, routeName);
					startActivity(intent);

				} catch (IOException e) {
					Toast.makeText(MyMapActivity.this, e.getLocalizedMessage(),
							Toast.LENGTH_LONG).show();
				}
				stopRecordingService();
			}

		});
	}

	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymap);

		// prima di tutto testo la versione (solo se sono online)
		// if (!testVersion()) {
		// finish();
		// return;
		// }

		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		enableGPS();

		// testo le credenziali
		Credentials credential = MySettings.readCredentials(this);
		if (credential.isEmpty()) {
			// non ho le credenziali: le chiedo e contestualmente le valido (se
			// sono online
			// facendo una login, altrimenti controllando che non siano vuote),
			// se non sono buone esco
			showCredentialsDialog(true);
		}

		mTrackGPSPosition = MySettings.getTrackGPSPosition(this);

		mMap = (MapView) this.findViewById(R.id.mapview1);
		mController = mMap.getController();
		mMap.setSatellite(false);
		mMap.displayZoomControls(true);
		int zoomLevel = 15;

		List<Overlay> mapOverlays = mMap.getOverlays();
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.ic_routemarker);
		mRoutesOverlay = new RoutesOverlay(drawable, this, mMap);
		mapOverlays.add(mRoutesOverlay);

		myLocationOverlay = new ECOmmutersLocationOverlay(this, mMap,
				mController, mRoutesOverlay);
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				try {
					mController.animateTo(myLocationOverlay.getMyLocation());
				} catch (Exception ex) {
					Log.e(Const.ECOMMUTERS_TAG, Log.getStackTraceString(ex));
				}
			}

		});

		mapOverlays.add(myLocationOverlay);
		if (savedInstanceState != null) {

			int late6 = savedInstanceState.getInt(Const.MAPLATITUDE);
			int lone6 = savedInstanceState.getInt(Const.MAPLONGITUDE);
			mController.animateTo(new GeoPoint(late6, lone6));

			zoomLevel = savedInstanceState.getInt(Const.ZoomLevel, 15);

			Serializable positions = savedInstanceState
					.getSerializable(Const.POSITIONS);
			if (positions != null)
				mRoutesOverlay
						.setPositions((ArrayList<ECommuterPosition>) positions);
			askingRouteName = savedInstanceState.getBoolean(Const.ASKING_ROUTE,
					false);
			if (askingRouteName)
				saveRecordedRouteIfNeeded();
		} else {
			// testVersion();
			Helper.hideableMessage(this, R.string.warning_to_user);
			handleIntent(getIntent());
		}

		mRoutesChangedHandler = new GenericEventHandler() {
			public void onEvent(Object sender, EventArgs args) {
				mRoutesOverlay.setRoutes(MyApplication.getInstance()
						.getRoutes());
				mMap.invalidate();
			}
		};
		//scheduleForDebug();
		
		MyApplication.getInstance().RouteChanged
				.addHandler(mRoutesChangedHandler);
		MyApplication.getInstance().OnRecordingRouteUpdated
				.addHandler(mUpdateRoutehandler);
		MyApplication.getInstance().ConnectorServiceChanged
				.addHandler(mConnectorServiceChangedHandler);
		mController.setZoom(zoomLevel);

		Button btn = (Button) findViewById(R.id.buttonRecord);
		mAnimation = new AlphaAnimation(1, 0.5f);
		// from
		// fully
		// visible
		// to
		// invisible
		mAnimation.setDuration(300);
		mAnimation.setInterpolator(new LinearInterpolator()); // do not alter
																// animation
																// rate
		mAnimation.setRepeatCount(Animation.INFINITE); // Repeat animation
														// infinitely
		mAnimation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
														// end so the button
														// will
														// fade back in
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(final View view) {
				toggleRecording();
			}
		});

		btn = (Button) findViewById(R.id.buttonLiveTracking);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(final View view) {
				toggleLiveTracking();
			}

		});
		mPositionsDownloader = new PositionsDownloader(mMap, mRoutesOverlay,
				this);

		MyApplication.getInstance().requestRoutes(this,
				new OnRoutesAvailable() {
					public void gotRoutes(Route[] routes) {
						mRoutesOverlay.setRoutes(routes);
						mMap.invalidate();
					}
				});

		MyApplication.getInstance().RecordingServiceChanged
				.addHandler(mRecordingServiceChangedHandler);

	}

	@SuppressWarnings("unused")
	private void scheduleForDebug() {
		for (Route r : MyApplication.getInstance().getRoutes())
			r.scheduleDebug();
	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		// Get the intent, verify the action and get the query
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			// manually launch the real search activity
			final Intent searchIntent = new Intent(getApplicationContext(),
					SearchActivity.class);
			// add query to the Intent Extras
			searchIntent.putExtra(SearchManager.QUERY, query);
			startActivityForResult(searchIntent, Const.SEARCH_ACTIVITY_RESULT);
		}
	}

	@Override
	protected void onDestroy() {
		MyApplication.getInstance().RecordingServiceChanged
				.removeHandler(mRecordingServiceChangedHandler);
		MyApplication.getInstance().OnRecordingRouteUpdated
				.removeHandler(mUpdateRoutehandler);
		MyApplication.getInstance().RouteChanged
				.removeHandler(mRoutesChangedHandler);
		MyApplication.getInstance().ConnectorServiceChanged
				.removeHandler(mConnectorServiceChangedHandler);
		super.onDestroy();
	}

	private void toggleLiveTracking() {
		boolean b = !isManualLiveTracking();
		setLiveTracking(b);
		showTrackingButton(b);
		Toast.makeText(
				MyMapActivity.this,
				b ? R.string.manual_live_tracking_on
						: R.string.manual_live_tracking_off, Toast.LENGTH_LONG)
				.show();

	}

	public boolean isManualLiveTracking() {
		ConnectorService connectorService = MyApplication.getInstance().getConnectorService();
		return connectorService != null && connectorService.isManualLiveTracking();
	}

	public void setLiveTracking(boolean b) {
		ConnectorService.setManualLiveTracking(b);
	}

	private void showStopRecordingButton(Boolean show) {
		Button btn = (Button) findViewById(R.id.buttonRecord);
		final float scale = getResources().getDisplayMetrics().density;

		if (show) {
			btn.setAnimation(mAnimation);

			LayoutParams layoutParams = btn.getLayoutParams();
			layoutParams.width = (int) (100 * scale + 0.5f);
			layoutParams.height = (int) (100 * scale + 0.5f);
			btn.setLayoutParams(layoutParams);
			mAnimation.start();
		} else {
			LayoutParams layoutParams = btn.getLayoutParams();
			layoutParams.width = (int) (50 * scale + 0.5f);
			layoutParams.height = (int) (50 * scale + 0.5f);
			btn.setLayoutParams(layoutParams);
			btn.setAnimation(null);
			mAnimation.cancel();
		}

	}

	private void showTrackingButton(Boolean show) {
		Button btn = (Button) findViewById(R.id.buttonLiveTracking);
		final float scale = getResources().getDisplayMetrics().density;

		if (show) {
			btn.setAnimation(mAnimation);

			LayoutParams layoutParams = btn.getLayoutParams();
			layoutParams.width = (int) (100 * scale + 0.5f);
			layoutParams.height = (int) (100 * scale + 0.5f);
			btn.setLayoutParams(layoutParams);
			mAnimation.start();
		} else {
			LayoutParams layoutParams = btn.getLayoutParams();
			layoutParams.width = (int) (50 * scale + 0.5f);
			layoutParams.height = (int) (50 * scale + 0.5f);
			btn.setLayoutParams(layoutParams);
			btn.setAnimation(null);
			mAnimation.cancel();
		}

	}

	protected void writeUserInfo() {
		runOnUiThread(new Runnable() {
			public void run() {
				Credentials c = MySettings.CurrentCredentials;
				if (c != null) {
					Toast.makeText(MyMapActivity.this,
							String.format(getString(R.string.welcome_s), c),
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	private void enableGPS() {
		if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			Helper.dialogMessage(this, R.string.need_gps, R.string.app_name,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							Intent myIntent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(myIntent,
									Const.ACTIVATE_GPS_RESULT);
							return;

						}
					}, null);

			return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Const.ACTIVATE_GPS_RESULT) {
			Toast.makeText(this, R.string.gps_enabled, Toast.LENGTH_SHORT)
					.show();
		} else if (requestCode == Const.CREDENTIALS_RESULT) {
			if (resultCode == RESULT_OK)
				writeUserInfo();
			else
				finish();

		} else if (requestCode == Const.SEARCH_ACTIVITY_RESULT) {
			if (resultCode == RESULT_OK)
			{
				ECommuterPosition pos = (ECommuterPosition) data.getSerializableExtra(Const.ECOMMUTERPOS);
				mRoutesOverlay.pinTo(pos);
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mymap_menu, menu);
		mMenuItemTrackGpsPosition = menu.findItem(R.id.itemTrackGpsPosition);
		mMenuItemTrackGpsPosition
				.setTitleCondensed(getString(mTrackGPSPosition ? R.string.hide_position_menu
						: R.string.show_position_menu));

		MenuItem menuItemRecordRoute = menu.findItem(R.id.itemRecordRoute);
		menuItemRecordRoute.setTitleCondensed(getString(MyApplication
				.getInstance().isRecording() ? R.string.stop_recording
				: R.string.record_route));

		return super.onCreateOptionsMenu(menu);
	}

	private void toggleRecording() {
		if (MyApplication.getInstance().isRecording()) {
			stopRecordingService();
		} else {
			startRecordingService();
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemTrackGpsPosition:
			setTrackGPSPosition(!mTrackGPSPosition);
			break;
		case R.id.itemVisibleRoutes:
			chooseRoutes();
			return true;
		case R.id.itemRecordRoute:
			toggleRecording();
			return true;
		case R.id.itemCredentials:
			showCredentialsDialog(false);
			return true;

		case R.id.itemMyRoutes:
			Intent myIntent = new Intent(this, MyRoutesActivity.class);
			startActivity(myIntent);
			return true;
		case R.id.itemFind:
			onSearchRequested();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	void showCredentialsDialog(boolean compulsory) {
		Intent intent = new Intent(this, CredentialsActivity.class);
		if (compulsory)
			startActivityForResult(intent, Const.CREDENTIALS_RESULT);
		else
			startActivity(intent);
	}

	private void startRecordingService() {

		Intent myIntent = new Intent(this, RecordRouteService.class);
		startService(myIntent);

		showStopRecordingButton(true);
	}

	private void stopRecordingService() {
		Intent myIntent = new Intent(getBaseContext(), RecordRouteService.class);
		stopService(myIntent);
		showStopRecordingButton(false);
	}

	private void askRouteName(final OnRouteSelected onSelected) {

		// Set an EditText view to get user input
		final EditText input = new EditText(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.app_name)
				.setMessage(R.string.insert_route_name).setView(input)
				.setPositiveButton(android.R.string.ok, null)
				.setNegativeButton(android.R.string.cancel, null);
		final AlertDialog dialogRoute = builder.create();
		dialogRoute.show();

		Button cancelButton = dialogRoute
				.getButton(DialogInterface.BUTTON_NEGATIVE);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dialogRoute.dismiss();
				askingRouteName = false;
				final File f = getFileStreamPath(Const.RECORDING_ROUTE_FILE);
				if (!f.exists())
					return;
				Helper.dialogMessage(MyMapActivity.this,
						R.string.maintain_recording_question,
						R.string.app_name, null,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								f.delete();

							}
						});

			}
		});

		Button okButton = dialogRoute
				.getButton(DialogInterface.BUTTON_POSITIVE);
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final String routeName = input.getText().toString();
				if (Helper.isValidRouteName(routeName)) {
					final File fileStreamPath = getFileStreamPath(Helper
							.getRouteFile(routeName));
					if (fileStreamPath.exists()) {
						Helper.dialogMessage(MyMapActivity.this,
								R.string.overwrite_route_question,
								R.string.app_name,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										fileStreamPath.delete();
										dialogRoute.dismiss();
										askingRouteName = false;
										onSelected.select(routeName);

									}
								}, null);
					} else {
						dialogRoute.dismiss();
						askingRouteName = false;
						onSelected.select(routeName);
					}
				} else {
					Toast.makeText(dialogRoute.getContext(),
							R.string.insert_route_name, Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		try {
			outState.putInt(Const.MAPLATITUDE, mMap.getMapCenter()
					.getLatitudeE6());
			outState.putInt(Const.MAPLONGITUDE, mMap.getMapCenter()
					.getLongitudeE6());
			outState.putInt(Const.ZoomLevel, mMap.getZoomLevel());
			outState.putSerializable(Const.POSITIONS,
					mRoutesOverlay.getPositions());
			outState.putBoolean(Const.ASKING_ROUTE, askingRouteName);
		} catch (Exception e) {
			Log.e(Const.ECOMMUTERS_TAG, Log.getStackTraceString(e));
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		super.onPause();
		myLocationOverlay.disableMyLocation();
		// myLocationOverlay.disableCompass();

		mPositionsDownloader.stop();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTrackGPSPosition)
			myLocationOverlay.enableMyLocation();
		// myLocationOverlay.enableCompass();

		showStopRecordingButton(MyApplication.getInstance().isRecording());
		showTrackingButton(isManualLiveTracking());
		mPositionsDownloader.start();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void chooseRoutes() {

		Route[] routes = MyApplication.getInstance().getRoutes();
		final CharSequence[] items = new CharSequence[routes.length];
		boolean[] checkedItems = new boolean[routes.length];
		int i = 0;
		for (Route r : routes) {
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

}