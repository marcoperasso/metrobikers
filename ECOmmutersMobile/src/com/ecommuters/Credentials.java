package com.ecommuters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

public class Credentials {

	private String email;
	private String password;
	private String name;
	private String surname;

	@Override
	public String toString() {
		if (Helper.isNullOrEmpty(name) || Helper.isNullOrEmpty(surname))
			return email;
		return name + " " + surname;
	}

	public Credentials(String mail, String pwd) {
		this.email = mail;
		this.password = pwd;
	}

	public boolean isEmpty() {
		return Helper.isNullOrEmpty(email) || Helper.isNullOrEmpty(password);
	}

	String getPassword() {
		return password;
	}

	void setPassword(String password) {
		this.password = password;
	}

	String getEmail() {
		return email;
	}

	void setEmail(String email) {
		this.email = email;
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void testLogin(final Context context,
			final OnAsyncResponse onResponse) {

		if (!Helper.isOnline(context)) {
			onResponse.response(true, "");
		}
		final Credentials c = this;
		final WebView webView = new WebView(context);
		class AutologinObject {
			@SuppressWarnings("unused")
			public String getUser() {
				return c.getEmail();
			}

			@SuppressWarnings("unused")
			public String getPassword() {
				return c.getPassword();
			}

			@SuppressWarnings("unused")
			public void completed(boolean success, String message) {
				if (!success) {
					Toast t = Toast.makeText(context, message,
							Toast.LENGTH_LONG);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
					return;
				}

				RequestBuilder.fillCredentialsData(c);
				onResponse.response(success, message);
			}
		}
		webView.setVisibility(View.INVISIBLE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new AutologinObject(), "autologin");

		webView.loadUrl(RequestBuilder.HTTP_WWW_ECOMMUTERS_COM_LOGIN);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
}
