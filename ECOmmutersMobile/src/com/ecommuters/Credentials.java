package com.ecommuters;

import android.content.Context;
import android.os.AsyncTask;

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

	public static void testCredentials(final Context context,
			final OnAsyncResponse testResponse) {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				return HttpManager.isLogged();
			}

			protected void onPostExecute(Boolean result) {
				if (result) {
					testResponse.response(true, "");
					return;
				}
				Credentials credential = MySettings.readCredentials(context);
				if (credential.isEmpty()) {
					testResponse.response(false, "");
					return;
				}
				credential.testLogin(context, testResponse);
			};
		}.execute();

	}

	public void testLogin(final Context context,
			final OnAsyncResponse onResponse) {

		if (!Helper.isOnline(context)) {
			onResponse.response(true, "");
		}
		new AsyncTask<Credentials, Void, LoginResult>() {

			@Override
			protected LoginResult doInBackground(Credentials... params) {
				LoginResult res = HttpManager.login(getEmail(), getPassword());
				Credentials c = params[0];
				if (res.result) {
					int id = getUserId();
					HttpManager.fillCredentialsData(c);
					if (id != getUserId())
						MySettings.setCredentials(context, c);
				}
				return res;
			}

			protected void onPostExecute(LoginResult result) {
				onResponse.response(result.result, result.message.toString());
			};
		}.execute(this);

	}

}

class LoginResult {
	public LoginResult(boolean result, String message) {
		this.result = result;
		this.message = message;
	}

	public LoginResult() {
		this.result = false;
		this.message = "";
	}

	public boolean result;
	public String message;
}