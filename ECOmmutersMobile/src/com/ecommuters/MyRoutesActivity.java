package com.ecommuters;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ecommuters.RecordRouteService.RecordRouteBinder;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyRoutesActivity extends Activity {

	private static final int menuDeleteLocal = 0;
	private ArrayList<Route> mRoutes;
	private Route mActiveRoute;

	private Button btnNewRoute;
	private ServiceConnection mConnection;
	private RecordRouteService mRecordService = null;
	private ActivityCommonActions mCommonActions;

	private void toggleRegister() {
		if (Helper.isRecordingServiceRunning(this)) {
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_routes);
		mRoutes = Route.readAllRoutes(this);

		mCommonActions = new ActivityCommonActions(this);
		ListView lv = populate();
		registerForContextMenu(lv);
		mConnection = new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
				mRecordService = null;
			}
			public void onServiceConnected(ComponentName name, IBinder service) {
				mRecordService = ((RecordRouteBinder) service).getService();
			}
		};
		btnNewRoute = (Button) findViewById(R.id.btn_new_route);
		btnNewRoute.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggleRegister();
			}
		});
	}
	private ListView populate() {
		StringBuilder sb = new StringBuilder();
		if (mRoutes.size() == 0)
			sb.append("Non ci sono itinerari\r\n");
		List<File> files = Helper.getFiles(this, Const.TOSENDEXT);
		if (files.size() > 0) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			for (File f : files) {
				String name = Helper.getRouteNameFromFileToSend(f.getName());
				Integer i = map.get(name);
				if (i == null)
					i = 1;
				else
					i++;

				map.put(name, i);
			}
			for (String key : map.keySet()) {
				sb.append(String
						.format("Ci sono %d pacchetti in sospeso per l'itinerario %s da mandare al server\r\n",
								map.get(key), key));
			}
		}

		TextView tvDescri = (TextView) findViewById(R.id.textViewDescription);
		tvDescri.setText(sb.toString());

		ListView lv = (ListView) findViewById(R.id.list_routes);
		Route[] listItems = new Route[mRoutes.size()];
		mRoutes.toArray(listItems);
		ArrayAdapter<Route> adapter = new ArrayAdapter<Route>(this,
				android.R.layout.simple_list_item_1, listItems);
		lv.setAdapter(adapter);
		return lv;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return mCommonActions.onCreateOptionsMenu(menu);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list_routes) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			mActiveRoute = mRoutes.get(info.position);
			menu.setHeaderTitle(mActiveRoute.toString());

			menu.add(Menu.NONE, menuDeleteLocal, 0, "Elimina");
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mCommonActions.onOptionsItemSelected(item))
			return true;

		return super.onOptionsItemSelected(item);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case menuDeleteLocal :
				Helper.dialogMessage(
						this,
						String.format(
								"Confermi la cancellazione dell'itinerario %s? Potrai comunque scaricarlo nuovamente dal server.",
								mActiveRoute.getName()), null,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								removeActiveRoute();
							}

						}, null);

				break;

		}
		return true;
	}
	private void removeActiveRoute() {
		String routeFile = Helper.getRouteFile(mActiveRoute.getName());
		final File file = getFileStreamPath(routeFile);
		file.delete();
		mRoutes.remove(mActiveRoute);
		for (File f : Helper.getRoutePacketFiles(this, mActiveRoute.getName()))
			f.delete();
		populate();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Helper.isRecordingServiceRunning(this)) {
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
}
