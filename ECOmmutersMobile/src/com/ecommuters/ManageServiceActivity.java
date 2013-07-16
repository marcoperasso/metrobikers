package com.ecommuters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ManageServiceActivity extends Activity {

	private Button m_StopButton;
	private TextView m_Description;
	private Button m_CloseButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_manage_service);
		final Context context = this;
		m_Description = (TextView) findViewById(R.id.textViewDescription);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			m_Description.setText(extras.getString(Const.ServiceMessage));
		}
		m_StopButton = (Button) findViewById(R.id.btnStop);
		m_StopButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Helper.dialogMessage(context,
						R.string.stop_recording_question, R.string.app_name,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Intent myIntent = new Intent(getBaseContext(),
										RecordRouteService.class);
								stopService(myIntent);
								finish();

							}
						}, null);

			}
		});
		
		m_CloseButton = (Button) findViewById(R.id.btnClose);
		m_CloseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				
			}});
	}
}
