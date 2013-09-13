package com.ecommuters;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MyRoutesActivity extends Activity {

	private static final int menuDeleteLocal = 0;


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
		
	}
	private ListView populate() {
		StringBuilder sb = new StringBuilder();

		if (mRoutes.length == 0)
			sb.append(getString(R.string.no_routes));
		List<File> files = Helper.getFiles(this, Const.TOSENDEXT);
		
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
	
}
