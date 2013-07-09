package com.ecommuters;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LocationManager mlocManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Activity context = this;
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		enableGPS();

		// prima di tutto testo la versione (solo se sono online)
		if (!testVersion()) {
			finish();
			return;
		}

		Button btnRoutes = (Button) findViewById(R.id.btn_routes);
		btnRoutes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(),
						TracksActivity.class);
				startActivity(myIntent);

			}
		});
		final Button btnNewRoute = (Button) findViewById(R.id.btn_new_route);
		btnNewRoute.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				final Intent myIntent = new Intent(v.getContext(),
						RegisterRouteService.class);

				if (isRegisterServiceRunning()) {
					Helper.dialogMessage(context,
							getString(R.string.stop_recording_question), "",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									stopService(myIntent);
									btnNewRoute.setText(R.string.btn_new_route);

								}
							}, null);

				}

				else {
					btnNewRoute.setText(R.string.stop_recording);
					myIntent.putExtra(Const.ROUTE_NAME, "myroute");
					startService(myIntent);
				}

			}

		});
		// testo le credenziali
		Credentials credential = MySettings.readCredentials(this);
		if (credential.isEmpty()) {
			// non ho le credenziali: le chiedo e contestualmente le valido (se
			// sono online
			// facendo una login, altrimenti controllando che non siano vuote),
			// se non sono buone esco
			showCredentialsDialog(new OnAsyncResponse() {
				public void response(boolean success, String message) {
					if (success)
						writeUserInfo();
					else
						finish();

				}
			});
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
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									Intent myIntent = new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									startActivityForResult(myIntent,
											Const.ACTIVATE_GPS);
									return;

								}
							}).show();
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

	private void showCredentialsDialog(final OnAsyncResponse onResponse) {
		final CredentialsDialog dialog = new CredentialsDialog(this);
		if (onResponse != null) {
			dialog.setOnDismissListener(new Dialog.OnDismissListener() {
				public void onDismiss(DialogInterface di) {
					if (!dialog.isCredentialsSet()) {
						onResponse.response(false, null);
					}
				}
			});
		}
		dialog.show();
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
		case R.id.itemCredentials:
			showCredentialsDialog(null);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean isRegisterServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (RegisterRouteService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
