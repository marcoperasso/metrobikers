package com.ecommuters;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
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
		final ProgressDialog progressBar = new ProgressDialog(this);
		progressBar.setCancelable(true);
		progressBar.setMessage(getString(R.string.searching));
		progressBar.setIndeterminate(true);
		progressBar.show();
		new AsyncTask<String, Void, SearchPositionResult>() {

			@Override
			protected SearchPositionResult doInBackground(String... params) {
				String query = params[0];
				return HttpManager.getPositions(query);
			}

			@Override
			protected void onPostExecute(SearchPositionResult result) {

				ArrayAdapter<ECommuterPosition> adapter = new ArrayAdapter<ECommuterPosition>(SearchActivity.this,
						android.R.layout.simple_list_item_1, result.positions);
				setListAdapter(adapter);
				
				TextView tv = (TextView) findViewById(R.id.textViewLabel);
				StringBuilder sb = new StringBuilder();
				if (result.positions.size() > 0)
					sb.append(getString(R.string.select_ecommuter));
				else
					sb.append(getString(R.string.no_ecommuter));
				
				if (result.total > result.positions.size())
				{
					sb.append("\r\n");
					sb.append(getString(R.string.refine_query, result.positions.size(), result.total));
				}
				tv.setText(sb.toString());
				progressBar.dismiss();
				super.onPostExecute(result);
			}
		}.execute(query);
		
	}

	public void onClick(View v) {
		if (v.getId() == R.id.ButtonCancel)
		{
			finish();
		}
		
	}

}
class SearchPositionResult
{
	public ArrayList<ECommuterPosition> positions = new ArrayList<ECommuterPosition>();
	public int total = 0;
	}
