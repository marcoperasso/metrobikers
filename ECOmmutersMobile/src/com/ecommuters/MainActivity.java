package com.ecommuters;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

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

		// testo le credenziali
		Credentials credential = MySettings.getCredentials(this);
		if (credential.isEmpty()) {
			// non ho le credenziali: le chiedo e contestualmente le valido (se
			// sono online
			// facendo una login, altrimenti controllando che non siano vuote),
			// se non sono buone esco
			showCredentialsDialog(new OnAsyncResponse() {
				public void response(boolean success, String message) {
					if (!success)
						finish();

				}
			});
		} else {
			// se ci sono le credenziali e sono online, le testo
			credential.testLogin(this, new OnAsyncResponse() {
				public void response(boolean success, String message) {
					if (!success)
						finish();

				}
			});
		}

		// Look up the AdView as a resource and load a request.
		// AdView adView = (com.google.ads.AdView)this.findViewById(R.id.ad);
		// adView.loadAd(new com.google.ads.AdRequest());
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

}
