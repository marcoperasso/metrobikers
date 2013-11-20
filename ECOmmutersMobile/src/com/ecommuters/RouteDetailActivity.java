package com.ecommuters;

import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class RouteDetailActivity extends Activity implements
		OnCheckedChangeListener {

	private Route mRoute;
	private boolean modified;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_detail);

		String routename = null;
		if (savedInstanceState != null) {
			routename = savedInstanceState.getString(Const.ROUTE_EXTRA);
		} else {
			Intent i = getIntent();
			if (i != null && i.getExtras() != null)
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

		TextView tv = (TextView) findViewById(R.id.textViewTitle);
		tv.setText(routename);

		updateIntervalDescription();

		tv = (TextView) findViewById(R.id.tvDistanceValue);
		tv.setText(String.format("%.2f Km", mRoute.getDistanceMetres() / 1000));

		tv = (TextView) findViewById(R.id.tvTimeValue);

		tv.setText(Helper.formatElapsedTime(mRoute.getTotalTimeSeconds()));

		tv = (TextView) findViewById(R.id.tvAverageSpeedValue);
		tv.setText(String.format("%.2f Km/h", mRoute.getAverageSpeed()));

		EditText et = (EditText) findViewById(R.id.etMinutesBeforeValue);
		et.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				modified = true;
				mRoute.setMinutesBeforeStart(safeParseInt(s));
				updateIntervalDescription();
			}

		});
		et.setText(Integer.toString(mRoute.getMinutesBeforeStart()));
		et = (EditText) findViewById(R.id.etMinutesAfterValue);
		et.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				modified = true;
				mRoute.setMinutesAfterStart(safeParseInt(s));
				updateIntervalDescription();

			}
		});
		et.setText(Integer.toString(mRoute.getMinutesAfterStart()));

		CheckBox cb = (CheckBox) findViewById(R.id.cbMonday);
		cb.setChecked(mRoute.isMonday());
		cb.setOnCheckedChangeListener(this);
		cb = (CheckBox) findViewById(R.id.cbTuesday);
		cb.setOnCheckedChangeListener(this);
		cb.setChecked(mRoute.isTuesday());
		cb = (CheckBox) findViewById(R.id.cbWednesday);
		cb.setOnCheckedChangeListener(this);
		cb.setChecked(mRoute.isWednesday());
		cb = (CheckBox) findViewById(R.id.cbThursday);
		cb.setOnCheckedChangeListener(this);
		cb.setChecked(mRoute.isThursday());
		cb = (CheckBox) findViewById(R.id.cbFriday);
		cb.setOnCheckedChangeListener(this);
		cb.setChecked(mRoute.isFriday());
		cb = (CheckBox) findViewById(R.id.cbSaturday);
		cb.setOnCheckedChangeListener(this);
		cb.setChecked(mRoute.isSaturday());
		cb = (CheckBox) findViewById(R.id.cbSunday);
		cb.setOnCheckedChangeListener(this);
		cb.setChecked(mRoute.isSunday());
	}

	private int safeParseInt(CharSequence s) {
		try {
			return Integer.parseInt(s.toString());
		} catch (Exception e) {
			return 0;
		}
	}

	private void updateIntervalDescription() {
		java.text.DateFormat timeFormat = DateFormat.getTimeFormat(this);

		TextView tv;
		Date df = new Date(mRoute.getStartingTimeSeconds() * 1000);
		tv = (TextView) findViewById(R.id.tvStartTimeValue);
		tv.setText(timeFormat.format(df));

		df = new Date(mRoute.getEndingTimeSeconds() * 1000);
		tv = (TextView) findViewById(R.id.tvEndTimeValue);
		tv.setText(timeFormat.format(df));

		tv = (TextView) findViewById(R.id.tvIntervalsValue);

		Date[] d = new Date[2];
		int[] ws = new int[2];
		StringBuilder sb = new StringBuilder();

		int x = 0;
		TimeInterval timeInterval = mRoute.getInterval();
		d[x] = timeInterval.getStart().getTime();
		ws[x] = timeInterval.getWeight();
		x++;

		d[x] = timeInterval.getEnd().getTime();
		ws[x - 1] = timeInterval.getWeight();
		x++;
		for (int i = 0; i < d.length - 1; i++) {
			if (sb.length() > 0)
				sb.append("\r\n");
			sb.append(String.format("%s - %s: ", timeFormat.format(d[i]),
					timeFormat.format(d[i + 1])));
			sb.append("\r\n");
			sb.append(String.format("ogni %d secondi o %d metri",
					GPSStatus.minTimeSecs[ws[i]],
					GPSStatus.minDistanceMetres[ws[i]]));

		}
		tv.setText(sb.toString());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (mRoute != null)
			outState.putString(Const.ROUTE_EXTRA, mRoute.getName());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		if (modified)
			try {
				mRoute.update();
				modified = false;
				// faccio partire il servizio che lo manda al server
				Intent service = new Intent(this, SyncService.class);
				startService(service);
			} catch (IOException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		super.onPause();
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.cbMonday:
			mRoute.setMonday(isChecked);
			modified = true;
			break;
		case R.id.cbTuesday:
			mRoute.setTuesday(isChecked);
			modified = true;
			break;
		case R.id.cbWednesday:
			mRoute.setWednesday(isChecked);
			modified = true;
			break;
		case R.id.cbThursday:
			mRoute.setThursday(isChecked);
			modified = true;
			break;
		case R.id.cbFriday:
			mRoute.setFriday(isChecked);
			modified = true;
			break;
		case R.id.cbSaturday:
			mRoute.setSaturday(isChecked);
			modified = true;
			break;
		case R.id.cbSunday:
			mRoute.setSunday(isChecked);
			modified = true;
			break;

		}

	}
}
