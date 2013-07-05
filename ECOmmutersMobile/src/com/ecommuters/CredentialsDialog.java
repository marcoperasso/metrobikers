package com.ecommuters;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CredentialsDialog extends Dialog {
	private Credentials credentials;
	private EditText mPassword;
	private EditText mEmail;

	public CredentialsDialog(Context context) {
		super(context);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.credentials);
		setTitle(R.string.insert_credential_title);
		TextView textView = (TextView) findViewById(R.id.textViewDescription);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		setCancelable(true);

		mPassword = (EditText) findViewById(R.id.editTextPassword);
		mEmail = (EditText) findViewById(R.id.editTextEMail);

		Credentials c = MySettings.getCredentials(getContext());
		mEmail.setText(c.getEmail());
		mPassword.setText(c.getPassword());

		Button btnOk = (Button) findViewById(R.id.ButtonOK);
		btnOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String mail = mEmail.getText().toString();
				String pwd = mPassword.getText().toString();
				if (Helper.isNullOrEmpty(mail) || Helper.isNullOrEmpty(pwd)) {
					return;
				}

				credentials = new Credentials(mail, pwd);
				credentials.testLogin(getContext(), new OnAsyncResponse() {

					public void response(boolean success, String message) {
						if (success) {
							MySettings
									.setCredentials(getContext(), credentials);
							dismiss();
						} 
					}
				});

			}
		});
		Button btnCancel = (Button) findViewById(R.id.ButtonCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dismiss();
			}
		});

		super.onCreate(savedInstanceState);
	}

	public boolean isCredentialsSet() {
		return credentials != null;
	}

	public Credentials getCredentials() {
		return credentials;
	}

}
