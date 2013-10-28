package com.ecommuters;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CredentialsActivity extends Activity implements
		OnEditorActionListener, OnClickListener {

	private EditText mPassword;
	private EditText mEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credentials);
		setTitle(R.string.insert_credential_title);
		TextView textView = (TextView) findViewById(R.id.textViewDescription);
		textView.setMovementMethod(LinkMovementMethod.getInstance());

		mPassword = (EditText) findViewById(R.id.editTextPassword);
		mEmail = (EditText) findViewById(R.id.editTextEMail);

		Credentials c = MySettings.readCredentials(this);
		mEmail.setText(c.getEmail());
		mPassword.setText(c.getPassword());
		mPassword.setOnEditorActionListener(this);
		mPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);
		
		Button btnOk = (Button) findViewById(R.id.ButtonOK);
		btnOk.setOnClickListener(this);
		
		Button btnCancel = (Button) findViewById(R.id.ButtonCancel);
		btnCancel.setOnClickListener(this);
	}

	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE
				|| (event.getAction() == KeyEvent.ACTION_DOWN && event
						.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
			doLogin();
			return true;
		}
		return false;
	}

	public void onClick(View v) {
		if (v.getId() == R.id.ButtonOK) {
			doLogin();

		} else if (v.getId() == R.id.ButtonCancel) {
			Intent returnIntent = new Intent();
			setResult(RESULT_CANCELED, returnIntent);
			finish();
		}

	}

	private void doLogin() {
		String mail = mEmail.getText().toString();
		String pwd = mPassword.getText().toString();
		if (Helper.isNullOrEmpty(mail) || Helper.isNullOrEmpty(pwd)) {
			return;
		}

		final Credentials credentials = new Credentials(0, mail, pwd);
		final ProgressDialog progressBar = new ProgressDialog(this);
		progressBar.setMessage(getString(R.string.verifying_credentials));
		progressBar.setCancelable(false);
		progressBar.setIndeterminate(true);
		progressBar.show();
		credentials.testLogin(this, new OnAsyncResponse() {

			public void response(boolean success, String message) {
				if (success) {
					MySettings.setCredentials(CredentialsActivity.this,
							credentials);
					Intent returnIntent = new Intent();
					setResult(RESULT_OK, returnIntent);
					finish();
				}
				progressBar.dismiss();
			}
		});
		
	}

}
