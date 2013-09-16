package com.ecommuters;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
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
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class MyMapActivity extends MapActivity {

	private MyMapView mMap;

	private MapController mController;

	private RoutesOverlay mTracksOverlay;
	private boolean mTrackGPSPosition;

	private MyLocationOverlay myLocationOverlay;
	MenuItem mMenuItemTrackGpsPosition;

	private GeoPoint upperLeft;
	private GeoPoint bottomRight;
	private boolean retrievingTracks;

	private Route[] mRoutes;

	private GenericEvent mRoutesChangedHandler;
	private GenericEvent mUpdateRoutehandler = new GenericEvent() {

		@Override
		public void onEvent(Object sender, EventArgs args) {
			mMap.invalidate();

		}
	};
	private BroadcastReceiver recordingServiceStoppedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			final Route r = (Route) intent
					.getSerializableExtra(Const.ROUTE_EXTRA);
			if (r.getPoints().size() == 0)
				return;

			askRouteName(new OnRouteSelected() {
				public void select(String routeName) {
					try {
						r.setName(routeName);
						r.save(MyMapActivity.this,
								Helper.getRouteFile(routeName));
						r.save(MyMapActivity.this,
								Helper.getRouteFileToSend(routeName));

						final File recordingFile = getFileStreamPath(Const.RECORDING_ROUTE_FILE);
						recordingFile.delete();
						MyApplication.getInstance().refreshRoutes();
					} catch (IOException e) {
						Toast.makeText(MyMapActivity.this,
								e.getLocalizedMessage(), Toast.LENGTH_LONG)
								.show();
					}
					stopRecordingService();
				}

			});
		}
	};
	private LocationManager mlocManager;

	private Animation mAnimation;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymap);

		// prima di tutto testo la versione (solo se sono online)
		if (!testVersion()) {
			finish();
			return;
		}
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		enableGPS();

		MyApplication.getInstance().activateConnector(this);

		
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

		mMap = (MyMapView) this.findViewById(R.id.mapview1);
		mController = mMap.getController();
		mMap.setSatellite(false);
		mMap.displayZoomControls(true);
		int zoomLevel = 15;

		List<Overlay> mapOverlays = mMap.getOverlays();
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.ic_routemarker);
		mTracksOverlay = new RoutesOverlay(drawable, this, mMap);
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
		mRoutesChangedHandler = new GenericEvent() {
			public void onEvent(Object sender, EventArgs args) {
				mRoutes = MyApplication.getInstance().getRoutes();
				mTracksOverlay.setRoutes(mRoutes);

				mMap.invalidate();
			}
		};
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

		showStopRecordingButton(MyApplication.getInstance().isRecording());
		// mTracksOverlay.drawRoutes();
		// // Look up the AdView as a resource and load a request.
		// AdView adView = (com.google.ads.AdView) this.findViewById(R.id.ad);
		// adView.loadAd(new com.google.ads.AdRequest());

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

	private boolean testVersion() {
		if (Helper.isOnline(this) && !Helper.matchProtocolVersion()) {
			Toast t = Toast.makeText(this, R.string.wrong_version,
					Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
			return false;
		}
		return true;
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

		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mymap_menu, menu);
		mMenuItemTrackGpsPosition = menu.findItem(R.id.itemTrackGpsPosition);
		mMenuItemTrackGpsPosition.setTitleCondensed(getString(mTrackGPSPosition
				? R.string.hide_position_menu
				: R.string.show_position_menu));

		MenuItem menuItemRecordRoute = menu.findItem(R.id.itemRecordRoute);
		menuItemRecordRoute.setTitleCondensed(getString(MyApplication
				.getInstance().isRecording()
				? R.string.stop_recording
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
			case R.id.itemTrackGpsPosition :
				setTrackGPSPosition(!mTrackGPSPosition);
				break;
			case R.id.itemVisibleRoutes :
				chooseRoutes();
				return true;
			case R.id.itemRecordRoute :
				toggleRecording();
				return true;
			case R.id.itemCredentials :
				showCredentialsDialog(false);
				return true;
				/*
				 * case R.id.itemDownloadRoutes : downloadRoutes(); return true;
				 * case R.id.itemMyRoutes : Intent myIntent = new Intent(this,
				 * MyRoutesActivity.class); startActivity(myIntent); return
				 * true;
				 */
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
				final File f = getFileStreamPath(Const.RECORDING_ROUTE_FILE);
				if (!f.exists())
					return;
				Helper.dialogMessage(MyMapActivity.this,
						R.string.maintain_recording_question, R.string.app_name,
						null, new DialogInterface.OnClickListener() {

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
										onSelected.select(routeName);

									}
								}, null);
					} else {
						dialogRoute.dismiss();
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

	/*
	 * private void doRecord() { String routeFile =
	 * Helper.getRouteFile(routeName); final File file =
	 * getFileStreamPath(routeFile); if (file.exists()) { new
	 * AlertDialog.Builder(this) .setIcon(android.R.drawable.ic_dialog_alert)
	 * .setTitle(getString(R.string.app_name)) .setMessage(
	 * getString(R.string.existing_route_question, routeName))
	 * .setPositiveButton(R.string.overwrite, new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int which) { file.delete(); startRecordingService(routeName);
	 * 
	 * } }) .setNegativeButton(android.R.string.cancel, null)
	 * .setNeutralButton(R.string.append, new DialogInterface.OnClickListener()
	 * { public void onClick(DialogInterface dialog, int which) {
	 * startRecordingService(routeName); }
	 * 
	 * }).show();
	 * 
	 * } else { startRecordingService(routeName); }
	 * 
	 * }
	 */

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
		super.onPause();
		myLocationOverlay.disableMyLocation();
		// myLocationOverlay.disableCompass();
		MyApplication.getInstance().OnRecordingRouteUpdated
				.removeHandler(mUpdateRoutehandler);
		
		MyApplication.getInstance().RouteChanged.removeHandler(mRoutesChangedHandler);
		unregisterReceiver(recordingServiceStoppedReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mTrackGPSPosition)
			myLocationOverlay.enableMyLocation();
		// myLocationOverlay.enableCompass();
		MyApplication.getInstance().OnRecordingRouteUpdated
				.addHandler(mUpdateRoutehandler);
		IntentFilter intentFilter = new IntentFilter(Const.SERVICE_STOPPED);
		registerReceiver(recordingServiceStoppedReceiver, intentFilter);
		MyApplication.getInstance().RouteChanged.addHandler(mRoutesChangedHandler);

		showStopRecordingButton(MyApplication.getInstance().isRecording());
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

}