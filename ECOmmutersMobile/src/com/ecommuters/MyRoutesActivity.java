package com.ecommuters;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MyRoutesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_routes);
		ListView lv = (ListView) findViewById(R.id.list_routes);
		ArrayList<Route> readAllRoutes = Route.readAllRoutes(this);
		Route[] listItems = (Route[]) readAllRoutes.toArray();
		ArrayAdapter<Route> adapter = new ArrayAdapter<Route>(this, android.R.layout.simple_list_item_1, listItems);
		lv.setAdapter(adapter);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_routes, menu);
		return true;
	}

}
