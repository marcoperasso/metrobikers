package com.ecommuters;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.view.Menu;
import android.widget.ArrayAdapter;

public class SearchActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}

	}

	private void doSearch(String query) {
		List<ECOmmuter> results = HttpManager.getECOmmuters(query);
		ArrayAdapter<ECOmmuter> adapter = new ArrayAdapter<ECOmmuter>(this,
				android.R.layout.simple_list_item_1, results);
		setListAdapter(adapter);
	}

}
