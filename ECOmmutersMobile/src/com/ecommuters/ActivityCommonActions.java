package com.ecommuters;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ActivityCommonActions {

	private Activity mActivity;

	public ActivityCommonActions(Activity activity) {
		mActivity = activity;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = mActivity.getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.itemCredentials :
				showCredentialsDialog(false);
				return true;
			case R.id.itemDownloadRoutes :
				downloadRoutes();
				return true;

		}
		return false;
	}
	void showCredentialsDialog(boolean compulsory) {
		Intent intent = new Intent(mActivity, CredentialsActivity.class);
		if (compulsory)
			mActivity.startActivityForResult(intent, Const.CREDENTIALS_RESULT);
		else
			mActivity.startActivity(intent);
	}
	private void downloadRoutes() {

		if (!Helper.isOnline(mActivity)) {
			Toast.makeText(mActivity, R.string.internet_unavailable,
					Toast.LENGTH_LONG).show();
			return;
		}
		final ProgressDialog pd = ProgressDialog.show(mActivity, "",
				mActivity.getString(R.string.downloading));

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
						if (mActivity.getFileStreamPath(routeFile).exists()) {
							Route existing = Route.readRoute(mActivity,
									routeFile);
							if (existing != null
									&& existing.getLatestUpdate() >= r
											.getLatestUpdate()) {
								message.append(String.format(
										mActivity
												.getString(R.string.route_already_existing),
										r.getName()));
								continue;
							}
						}
						r.save(mActivity, routeFile);
						saved++;
					}
					message.append(String.format(mActivity
							.getString(R.string.route_succesfully_downloaded),
							saved));
					return message.toString();
				} catch (Exception e) {
					return e.getLocalizedMessage();
				}
			}
			@Override
			protected void onPostExecute(String result) {
				Toast.makeText(mActivity, result, Toast.LENGTH_LONG).show();
				pd.dismiss();
				super.onPostExecute(result);
			}

		}
		new DownloadOperation().execute();

	}
}
