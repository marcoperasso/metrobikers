package com.ecommuters;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

public class RouteDetailActivity extends Activity {

	private Route mRoute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_detail);

		String routename = null;
		if (savedInstanceState != null) {
			routename = savedInstanceState.getString(Const.ROUTE_EXTRA);
		} else {
			Intent i = getIntent();
			if (i != null && i.getExtras()!=null)
				routename = i.getExtras().getString(Const.ROUTE_EXTRA);
		}

		Route[] routes = MyApplication.getInstance().getRoutes();
		for (Route r : routes)
			if (r.getName().equals(routename)) {
				mRoute = r;
				break;
			}
		
		if (mRoute == null)
			return;
		
		java.text.DateFormat timeFormat = DateFormat.getTimeFormat(this);
		
		TextView tv =  (TextView) findViewById(R.id.textViewTitle);
		tv.setText(routename);
		
		
		Date df = new Date(mRoute.getStartingTimeSeconds()*1000);
		tv =  (TextView) findViewById(R.id.tvStartTimeValue);
		tv.setText(timeFormat.format(df));
		
		df = new Date(mRoute.getEndingTimeSeconds()*1000);
		tv =  (TextView) findViewById(R.id.tvEndTimeValue);
		tv.setText(timeFormat.format(df));
		
		tv =  (TextView) findViewById(R.id.tvIntervalsValue);
		
		Date[] d = new Date[mRoute.getIntervals().length*2];
		int[] ws = new int[mRoute.getIntervals().length*2];
		StringBuilder sb = new StringBuilder();
		
		int x = 0;
		for (int i = 0; i < mRoute.getIntervals().length; i++)
		{
			TimeInterval timeInterval = mRoute.getIntervals()[i];
			d[x] = timeInterval.getStart();
			ws[x] = timeInterval.getWeight();
			x++;
		}
		
		for (int i = mRoute.getIntervals().length-1; i>=0; i--)
		{
			TimeInterval timeInterval = mRoute.getIntervals()[i];
			d[x] = timeInterval.getEnd();
			ws[x-1] = timeInterval.getWeight();
			x++;
		}
		for (int i = 0; i < d.length-1; i++)
		{
			if (sb.length()>0)
				sb.append("\r\n");
			sb.append(String.format(
					"%s - %s: ", 
					timeFormat.format(d[i]),
					timeFormat.format(d[i+1]) 
					));
			sb.append("\r\n");
			sb.append(String.format(
					"ogni %d secondi o %d metri", 
			GPSManager.minTimeSecs[ws[i]],
			GPSManager.minDistanceMetres[ws[i]]
			));
			
					
		}
		tv.setText(sb.toString());
		
		tv =  (TextView) findViewById(R.id.tvDistanceValue);
		tv.setText(String.format("%.2f Km", mRoute.getDistanceMetres()/1000));
		
		tv =  (TextView) findViewById(R.id.tvTimeValue);
		
		tv.setText(Helper.formatElapsedTime(mRoute.getTotalTimeSeconds())); 
		
		tv =  (TextView) findViewById(R.id.tvAverageSpeedValue);
		tv.setText(String.format("%.2f Km/h", mRoute.getAverageSpeed()));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (mRoute != null)
			outState.putString(Const.ROUTE_EXTRA, mRoute.getName());
		super.onSaveInstanceState(outState);
	}
}
