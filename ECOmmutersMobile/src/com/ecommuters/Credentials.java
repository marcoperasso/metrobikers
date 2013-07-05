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
	public void testLogin(final Context context, final OnAsyncResponse onResponse) {
		
		if (!Helper.isOnline(context))
		{
			onResponse.response(true, "");
		}
		final Credentials c = this;
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
					Toast t = Toast.makeText(context, message, Toast.LENGTH_LONG);
					t.setGravity(Gravity.CENTER, 0, 0);
					t.show();
				}
				onResponse.response(success, message);
			}
		}
		WebView webView = new WebView(context);
		webView.setVisibility(View.INVISIBLE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new AutologinObject(), "autologin");
		
		webView.loadUrl("http://www.ecommuters.com/login");
	}
}
