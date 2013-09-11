package com.ecommuters;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

	private Route[] mRoutes;

	private GenericEvent mRoutesChangedHandler;
	private GenericEvent updateRoutehandler = new GenericEvent() {

		@Override
		public void onEvent(Object sender, EventArgs args) {
			mMap.invalidate();

		}
	};

	private LocationManager mlocManager;
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
				} else {
					// se ci sono le credenziali e sono online, le testo
					credential.testLogin(this, new OnAsyncResponse() {
						public void response(boolean success, String message) {
							if (success)
								writeUserInfo();
							else
								finish();

						}
					});
				}
						
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
		
		adjustRecordingButtonVisibility();
		// mTracksOverlay.drawRoutes();
		// // Look up the AdView as a resource and load a request.
		// AdView adView = (com.google.ads.AdView) this.findViewById(R.id.ad);
		// adView.loadAd(new com.google.ads.AdRequest());

	}

	private void adjustRecordingButtonVisibility() {
		Button btn = (Button) findViewById(R.id.buttonRecord);
		if (Helper.isRecordingServiceRunning(this))
		{
			final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		    animation.setDuration(500); // duration - half a second
		    animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
		    animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		    animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
		    btn.startAnimation(animation);
		    btn.setOnClickListener(new OnClickListener() {
		        public void onClick(final View view) {
		           toggleRecording();
		        }
		    });
		}
		else
		{
			btn.setVisibility(View.GONE);
		}
	}
	
	protected void writeUserInfo() {
		runOnUiThread(new Runnable() {
			public void run() {
				Credentials c = MySettings.CurrentCredentials;
				if (c != null) {
					Toast.makeText(MyMapActivity.this, String.format(
							getString(R.string.welcome_s), c),
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
		MyApplication.getInstance().RouteChanged
				.removeHandler(mRoutesChangedHandler);
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
		return super.onCreateOptionsMenu(menu);
	}

	private void toggleRecording() {
		if (Helper.isRecordingServiceRunning(this)) {
			stopRecording();
		} else {
				doRecord(Const.DEFAULT_TRACK_NAME);
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
			case R.id.itemRecordRoute:
				toggleRecording();
				return true;
			case R.id.itemCredentials :
				showCredentialsDialog(false);
				return true;
			case R.id.itemDownloadRoutes :
				downloadRoutes();
				return true;
			case R.id.itemMyRoutes:
				Intent myIntent = new Intent(this, MyRoutesActivity.class);
				startActivity(myIntent);
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
	private void downloadRoutes() {

		if (!Helper.isOnline(this)) {
			Toast.makeText(this, R.string.internet_unavailable,
					Toast.LENGTH_LONG).show();
			return;
		}
		final ProgressDialog pd = ProgressDialog.show(this, "", getString(R.string.downloading));

		class DownloadOperation extends AsyncTask<Void, Void, String> {
			@Override
			protected String doInBackground(Void... params) {
				try {
					Looper.prepare();
					List<Route> rr = RequestBuilder.getRoutes();
					StringBuilder message = new StringBuilder();
					int saved = 0;
					for (Route r : rr) {
						String routeFile = Helper.getRouteFile(r.getName());
						if (getFileStreamPath(routeFile).exists()) {
							Route existing = Route.readRoute(MyMapActivity.this, routeFile);
							if (existing != null
									&& existing.getLatestUpdate() >= r
											.getLatestUpdate()) {
								message.append(String.format(
										getString(R.string.route_already_existing),
										r.getName()));
								continue;
							}
						}
						r.save(MyMapActivity.this, routeFile);
						saved++;
					}
					if (saved > 0)
						MyApplication.getInstance().refreshRoutes();
						
					message.append(String.format(getString(R.string.route_succesfully_downloaded),
							saved));
					return message.toString();
				} catch (Exception e) {
					return e.getLocalizedMessage();
				}
			}
			@Override
			protected void onPostExecute(String result) {
				Toast.makeText(MyMapActivity.this, result, Toast.LENGTH_LONG).show();
				pd.dismiss();
				super.onPostExecute(result);
			}

		}
		new DownloadOperation().execute();

	}
	private void stopRecording() {
		Helper.dialogMessage(this, R.string.stop_recording_question,
				R.string.app_name, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent myIntent = new Intent(getBaseContext(),
								RecordRouteService.class);
						stopService(myIntent);
						adjustRecordingButtonVisibility();
						if (mRecordService != null)
							unbindService(mConnection);
						
						askRouteName(new OnRouteSelected() {
							public void select(String routeName) {
								
							}
						});

					}
				}, null);
	}
	
	private void askRouteName(final OnRouteSelected onSelected) {

		// Set an EditText view to get user input
		final EditText input = new EditText(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.app_name)
				.setMessage(R.string.insert_route_name).setView(input)
				.setPositiveButton(android.R.string.ok, null)
				.setNegativeButton(android.R.string.cancel, null);
		final AlertDialog dialog = builder.create();
		dialog.show();

		Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
		theButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String routeName = input.getText().toString();
				if (Helper.isValidRouteName(routeName)) {
					dialog.dismiss();
					onSelected.select(routeName);
				} else {
					Toast.makeText(dialog.getContext(),
							R.string.insert_route_name, Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
	}

	private void doRecord(final String routeName) {
		String routeFile = Helper.getRouteFile(routeName);
		final File file = getFileStreamPath(routeFile);
		if (file.exists()) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(getString(R.string.app_name))
					.setMessage(
							getString(R.string.existing_route_question,
									routeName))
					.setPositiveButton(R.string.overwrite,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									file.delete();
									startRecordingService(routeName);

								}
							})
					.setNegativeButton(android.R.string.cancel, null)
					.setNeutralButton(R.string.append,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									startRecordingService(routeName);
								}

							}).show();

		} else {
			startRecordingService(routeName);
		}

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

	private void startRecordingService(String routeName) {

		Intent myIntent = new Intent(this, RecordRouteService.class);
		myIntent.putExtra(Const.ROUTE_NAME, routeName);
		startService(myIntent);
		
		myIntent = new Intent(this, MyMapActivity.class);
		startActivity(myIntent);
		
		adjustRecordingButtonVisibility();
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