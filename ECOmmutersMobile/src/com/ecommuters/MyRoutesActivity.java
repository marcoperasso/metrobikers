package com.ecommuters;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.ecommuters.RecordRouteService.RecordRouteBinder;

public class MyRoutesActivity extends Activity {

	private static final int menuDeleteLocal = 0;

	private Button btnNewRoute;
	private ServiceConnection mConnection;
	private RecordRouteService mRecordService = null;

	private Route[] mRoutes;

	private Route mActiveRoute;

	private GenericEvent mRoutesChangedHandler;
	private GenericEvent updateRoutehandler = new GenericEvent() {

		@Override
		public void onEvent(Object sender, EventArgs args) {
			populate();
		}
	};
	
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

		mRoutes = MyApplication.getInstance().getRoutes();
		mRoutesChangedHandler = new GenericEvent() {
			public void onEvent(Object sender, EventArgs args) {
				mRoutes = MyApplication.getInstance().getRoutes();
				populate();
			}
		};
		MyApplication.getInstance().RouteChanged
				.addHandler(mRoutesChangedHandler);
		ListView lv = populate();
		registerForContextMenu(lv);
		mConnection = new ServiceConnection() {
			public void onServiceDisconnected(ComponentName name) {
				mRecordService.OnRecordingRouteUpdated.removeHandler(updateRoutehandler);
				mRecordService = null;
			}
			public void onServiceConnected(ComponentName name, IBinder service) {
				mRecordService = ((RecordRouteBinder) service).getService();
				mRecordService.OnRecordingRouteUpdated.addHandler(updateRoutehandler);
				
			}
		};
		
	}
	private ListView populate() {
		StringBuilder sb = new StringBuilder();

		if (mRoutes.length == 0)
			sb.append(getString(R.string.no_routes));
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
						.format(getString(R.string.pending_packages),
								map.get(key), key));
			}
		}

		TextView tvDescri = (TextView) findViewById(R.id.textViewDescription);
		tvDescri.setText(sb.toString());

		ListView lv = (ListView) findViewById(R.id.list_routes);
		ArrayAdapter<Route> adapter = new ArrayAdapter<Route>(this,
				android.R.layout.simple_list_item_1, mRoutes);
		lv.setAdapter(adapter);
		return lv;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list_routes) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			mActiveRoute = mRoutes[info.position];
			menu.setHeaderTitle(mActiveRoute.toString());

			menu.add(Menu.NONE, menuDeleteLocal, 0, "Elimina");
		}
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

		MyApplication.getInstance().removeRoute(mActiveRoute);

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
		if (mRecordService != null) {
			if (mRecordService.isWorking())
				unbindService(mConnection);
			mRecordService.OnRecordingRouteUpdated.removeHandler(updateRoutehandler);
		}super.onPause();
		
	}
	
	
}
