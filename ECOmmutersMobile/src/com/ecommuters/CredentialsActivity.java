package com.ecommuters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CredentialsActivity extends Activity {

	private EditText mPassword;
	private EditText mEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Activity context = this;
		setContentView(R.layout.activity_credentials);
		setTitle(R.string.insert_credential_title);
		TextView textView = (TextView) findViewById(R.id.textViewDescription);
		textView.setMovementMethod(LinkMovementMethod.getInstance());

		mPassword = (EditText) findViewById(R.id.editTextPassword);
		mEmail = (EditText) findViewById(R.id.editTextEMail);

		Credentials c = MySettings.readCredentials(this);
		mEmail.setText(c.getEmail());
		mPassword.setText(c.getPassword());

		Button btnOk = (Button) findViewById(R.id.ButtonOK);
		btnOk.setOnClickListener(new View.OnClickListener() {
			private Credentials credentials;

			public void onClick(View v) {
				String mail = mEmail.getText().toString();
				String pwd = mPassword.getText().toString();
				if (Helper.isNullOrEmpty(mail) || Helper.isNullOrEmpty(pwd)) {
					return;
				}

				credentials = new Credentials(mail, pwd);
				credentials.testLogin(context, new OnAsyncResponse() {

					public void response(boolean success, String message) {
						if (success) {
							MySettings.setCredentials(context, credentials);
							Intent returnIntent = new Intent();
							setResult(RESULT_OK, returnIntent);
							finish();
						}
					}
				});

			}
		});
		Button btnCancel = (Button) findViewById(R.id.ButtonCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED, returnIntent);
				
				finish();
			}
		});

	}

}
