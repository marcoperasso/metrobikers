package com.ecommuters;

import java.util.List;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SearchActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		Intent intent = getIntent();
		String query = intent.getStringExtra(SearchManager.QUERY);
		doSearch(query);
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		ECommuterPosition e = (ECommuterPosition) l.getItemAtPosition(position);
		Intent intent = new Intent();
		intent.putExtra(Const.ECOMMUTERPOS, e);
		setResult(RESULT_OK, intent);
		finish();
		super.onListItemClick(l, v, position, id);
	}

	private void doSearch(String query) {
		List<ECommuterPosition> results = HttpManager.getPositions(query);
		ArrayAdapter<ECommuterPosition> adapter = new ArrayAdapter<ECommuterPosition>(this,
				android.R.layout.simple_list_item_1, results);
		setListAdapter(adapter);

	}

}
