package com.ecommuters;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class TracksDialog extends Dialog {

	private TrackInfo info;

	public TracksDialog(Context context, TrackInfo info) {
		super(context);
		this.info = info;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.track_dialog);
		setTitle(getContext().getString(R.string.routeDetail));
		TextView tv = (TextView) findViewById(R.id.textViewTitle);
		if (info == null)
		{
			tv.setText(R.string.no_track_info);
			return;
		}
		tv.setText(info.getTitle());
		tv = (TextView) findViewById(R.id.textViewLength);
		tv.setText(String.valueOf(info.getLength()) + " Km");
		tv = (TextView) findViewById(R.id.textViewCycle);
		tv.setText(String.valueOf(info.getCycling()) + "%");
		tv = (TextView) findViewById(R.id.textViewDifficulty);
		tv.setText(info.getDifficulty());
		tv = (TextView) findViewById(R.id.textViewMaxHeight);
		tv.setText(String.valueOf(info.getMaxHeight()) + " m");
		tv = (TextView) findViewById(R.id.textViewMinHeight);
		tv.setText(String.valueOf(info.getMinHeight()) + " m");
		RatingBar rb = (RatingBar) findViewById(R.id.ratingBar);
		rb.setRating((float) info.getRating());

		Button b = (Button)findViewById(android.R.id.button1);
		b.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				cancel();
				
			}
		});
	}
}
