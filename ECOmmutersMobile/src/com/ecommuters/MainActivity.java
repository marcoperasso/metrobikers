package com.ecommuters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private LocationManager mlocManager;
	private ActivityCommonActions mCommonActions;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		enableGPS();
		mCommonActions = new ActivityCommonActions(this);
		// prima di tutto testo la versione (solo se sono online)
		if (!testVersion()) {
			finish();
			return;
		}
		if (!Helper.isConnectorServiceRunning(this)) {
			Intent myIntent = new Intent(this, ConnectorService.class);
			startService(myIntent);
		}
		Button btnRoutes = (Button) findViewById(R.id.btn_routes);
		btnRoutes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(),
						MyMapActivity.class);
				startActivity(myIntent);

			}
		});
	

		Button btnMyRoutes = (Button) findViewById(R.id.btn_my_routes);
		btnMyRoutes.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(),
						MyRoutesActivity.class);
				startActivity(myIntent);

			}
		});

		// testo le credenziali
		Credentials credential = MySettings.readCredentials(this);
		if (credential.isEmpty()) {
			// non ho le credenziali: le chiedo e contestualmente le valido (se
			// sono online
			// facendo una login, altrimenti controllando che non siano vuote),
			// se non sono buone esco
			mCommonActions.showCredentialsDialog(true);
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
		return mCommonActions.onCreateOptionsMenu(menu);
		
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (mCommonActions.onOptionsItemSelected(item))
			return true;
		
		return super.onOptionsItemSelected(item);
	}

	

}
