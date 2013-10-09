package com.ecommuters;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MyRoutesActivity extends Activity {

	private static final int menuDeleteLocal = 0;
	private static final int menuDetails = 1;

	protected static final String MY_ROUTES_ACTIVITY = "MYROUTES";

	private Route[] mRoutes;

	private Route mActiveRoute;

	private GenericEventHandler mRoutesChangedHandler;

	@Override
	protected void onStop() {
		MyApplication.getInstance().RouteChanged
				.removeHandler(mRoutesChangedHandler);
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_routes);
		Button btnDownload = (Button) findViewById(R.id.btn_download_routes);
		btnDownload.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				downloadRoutes();
			}
		});
		mRoutes = MyApplication.getInstance().getRoutes();
		mRoutesChangedHandler = new GenericEventHandler() {
			public void onEvent(Object sender, EventArgs args) {
				mRoutes = MyApplication.getInstance().getRoutes();
				populate();
			}
		};
		MyApplication.getInstance().RouteChanged
				.addHandler(mRoutesChangedHandler);
		ListView lv = populate();
		registerForContextMenu(lv);

	}

	private void downloadRoutes() {

		final ProgressDialog progressBar = new ProgressDialog(this);
		progressBar.setMessage(getString(R.string.downloading));
		progressBar.setCancelable(true);
		progressBar.setIndeterminate(true);
		progressBar.show();

		try {
			Credentials.testCredentials(MyRoutesActivity.this,
					new OnAsyncResponse() {
						public void response(boolean success, String message) {

							if (!success)
								return;

							AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

								@Override
								protected Void doInBackground(Void... params) {
									try {
										List<Route> rr = RequestBuilder
												.getRoutes(0);
										boolean saved = false;
										for (Route r : rr) {
											String routeFile = Helper
													.getRouteFile(r.getName());
											r.save(MyRoutesActivity.this,
													routeFile);
											saved = true;
										}

										if (saved)
											MyApplication.getInstance()
													.refreshRoutes();
									} catch (Exception e) {
										Log.e(MY_ROUTES_ACTIVITY, e.toString());
									} finally {
										progressBar.dismiss();
									}
									return null;
								}
							};

							task.execute((Void) null);

						}
					});

		} catch (Exception e) {
			Log.e(MY_ROUTES_ACTIVITY, e.toString());
		}

	}

	private ListView populate() {
		StringBuilder sb = new StringBuilder();

		if (mRoutes.length == 0)
			sb.append(getString(R.string.no_routes));

		TextView tvDescri = (TextView) findViewById(R.id.textViewDescription);
		tvDescri.setText(sb.toString());

		final ListView lv = (ListView) findViewById(R.id.list_routes);
		ArrayAdapter<Route> adapter = new ArrayAdapter<Route>(this,
				android.R.layout.simple_list_item_1, mRoutes);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Route r = (Route) lv.getItemAtPosition(position);
				showRouteDetails(r.getName());
			}
		});
		return lv;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list_routes) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			mActiveRoute = mRoutes[info.position];
			menu.setHeaderTitle(mActiveRoute.toString());

			menu.add(Menu.NONE, menuDeleteLocal, 0, R.string.delete_route);
			menu.add(Menu.NONE, menuDetails, 0, R.string.route_properties);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case menuDeleteLocal:
			Helper.dialogMessage(this, String.format(
					getString(R.string.confirm_delete_route),
					mActiveRoute.getName()),
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							deleteActiveRoute();
						}

					}, null);

			break;

		case menuDetails:
			showRouteDetails(mActiveRoute.getName());
			break;

		}

		return true;
	}

	private void showRouteDetails(String routename) {
		Intent intent = new Intent(this, RouteDetailActivity.class);
		intent.putExtra(Const.ROUTE_EXTRA, routename);
		startActivity(intent);
	}

	private void deleteActiveRoute() {

		MyApplication.getInstance().deleteRoute(mActiveRoute);

	}

}
