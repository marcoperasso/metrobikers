package com.ecommuters;

import java.io.File;

import com.ecommuters.RecordRouteService.RecordRouteBinder;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LocationManager mlocManager;
	private Button btnNewRoute;
	private ServiceConnection mConnection;
	private RecordRouteService mRecordService = null;

	private void toggleRegister() {
		if (isRecordingServiceRunning()) {
			stopRegister();
		} else {
			askRouteName(new OnRouteSelected() {
				public void select(String routeName) {
					doRegister(routeName);
				}
			});

		}
	}

	private void stopRegister() {
		Helper.dialogMessage(this, R.string.stop_recording_question,
				R.string.app_name, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent myIntent = new Intent(getBaseContext(),
								RecordRouteService.class);
						stopService(myIntent);
						if (mRecordService != null)
					    	unbindService(mConnection);
						btnNewRoute.setText(R.string.btn_new_route);

					}
				}, null);
	}

	private void doRegister(final String routeName) {
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
	 @Override
	  protected void onResume() {
	    super.onResume();
	    if (isRecordingServiceRunning())
		{
			btnNewRoute.setText(R.string.stop_recording);
			Intent myIntent = new Intent(this, RecordRouteService.class);
			bindService(myIntent, mConnection, Context.BIND_AUTO_CREATE);
		}
	  }

	  @Override
	  protected void onPause() {
	    super.onPause();
	    if (mRecordService != null && mRecordService.isWorking())
	    	unbindService(mConnection);
	  }
	  
	private void startRecordingService(String routeName) {

		Intent myIntent = new Intent(this, RecordRouteService.class);
		myIntent.putExtra(Const.ROUTE_NAME, routeName);
		btnNewRoute.setText(R.string.stop_recording);
		startService(myIntent);
		bindService(myIntent, mConnection, Context.BIND_AUTO_CREATE);
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
				if (!Helper.isNullOrEmpty(routeName)) {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		enableGPS();
		mConnection = new ServiceConnection() {

			
			public void onServiceDisconnected(ComponentName name) {
				mRecordService = null;

			}

			public void onServiceConnected(ComponentName name, IBinder service) {
				mRecordService = ((RecordRouteBinder)service).getService();

			}
		};
		// prima di tutto testo la versione (solo se sono online)
		if (!testVersion()) {
			finish();
			return;
		}
		if (!isConnectorServiceRunning()) {
			Intent myIntent = new Intent(this, ConnectorService.class);
			startService(myIntent);
		}
		Button btnRoutes = (Button) findViewById(R.id.btn_routes);
		btnRoutes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(),
						RoutesActivity.class);
				startActivity(myIntent);

			}
		});
		btnNewRoute = (Button) findViewById(R.id.btn_new_route);
		btnNewRoute.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggleRegister();
			}
		});
		
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

		// Look up the AdView as a resource and load a request.
		// AdView adView = (com.google.ads.AdView)this.findViewById(R.id.ad);
		// adView.loadAd(new com.google.ads.AdRequest());
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

	protected void writeUserInfo() {
		runOnUiThread(new Runnable() {
			public void run() {
				TextView textView = (TextView) findViewById(R.id.textViewUser);
				Credentials c = MySettings.CurrentCredentials;
				if (c != null) {
					textView.setText(String.format(
							getString(R.string.welcome_s), c));
				}
			}
		});

	}

	private void showCredentialsDialog(boolean compulsory) {
		Intent intent = new Intent(this, CredentialsActivity.class);
		if (compulsory)
			startActivityForResult(intent, Const.CREDENTIALS_RESULT);
		else
			startActivity(intent);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.itemCredentials :
				showCredentialsDialog(false);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean isRecordingServiceRunning() {
		return isServiceRunning(RecordRouteService.class);
	}

	private boolean isConnectorServiceRunning() {
		return isServiceRunning(ConnectorService.class);
	}

	private boolean isServiceRunning(
			@SuppressWarnings("rawtypes") Class serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
