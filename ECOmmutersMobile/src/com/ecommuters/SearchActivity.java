package com.ecommuters;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SearchActivity extends ListActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		Intent intent = getIntent();
		String query = intent.getStringExtra(SearchManager.QUERY);
		doSearch(query);
		findViewById(R.id.ButtonCancel).setOnClickListener(this);
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
		ArrayList<ECommuterPosition> results = new ArrayList<ECommuterPosition>();
		int total = HttpManager.getPositions(query, results);
		ArrayAdapter<ECommuterPosition> adapter = new ArrayAdapter<ECommuterPosition>(this,
				android.R.layout.simple_list_item_1, results);
		setListAdapter(adapter);
		
		TextView tv = (TextView) findViewById(R.id.textViewLabel);
		StringBuilder sb = new StringBuilder();
		if (results.size() > 0)
			sb.append(getString(R.string.select_ecommuter));
		else
			sb.append(getString(R.string.no_ecommuter));
		
		if (total > results.size())
		{
			sb.append("\r\n");
			sb.append(getString(R.string.refine_query, results.size(), total));
		}
		tv.setText(sb.toString());
	}

	public void onClick(View v) {
		if (v.getId() == R.id.ButtonCancel)
		{
			finish();
		}
		
	}

}
