package com.ecommuters;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;

public class Credentials {
	private int userId;
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

	public Credentials(int userId, String mail, String pwd) {
		this.userId = userId;
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
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
			boolean timeout = false;
			Timer timer = new Timer(true);

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
				if (timeout)
					return;

				timer.cancel();
				if (success) {
					int id = c.getUserId();
					HttpManager.fillCredentialsData(c);
					if (id != c.getUserId())
						MySettings.setCredentials(context, c);
				}
				onResponse.response(success, message);
			}

			public void startTimeoutTimer() {
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						timeout = true;
						timer.cancel();
						String message = "Timeout!";
						onResponse.response(false, message);
					}
				}, 30000);// 30 secondi
			}
		}
		webView.setVisibility(View.INVISIBLE);
		webView.getSettings().setJavaScriptEnabled(true);
		AutologinObject autologinObject = new AutologinObject();
		webView.addJavascriptInterface(autologinObject, "autologin");
		webView.loadUrl(HttpManager.HTTP_WWW_ECOMMUTERS_COM_LOGIN);
		autologinObject.startTimeoutTimer();
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

	public static void testCredentials(Context context,
			OnAsyncResponse testResponse) {
		if (HttpManager.isLogged()) {
			testResponse.response(true, "");
			return;
		}
		Credentials credential = MySettings.readCredentials(context);
		if (credential.isEmpty()) {
			testResponse.response(false, "");
			return;
		}
		credential.testLogin(context, testResponse);
	}

}
