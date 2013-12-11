package com.ecommuters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;

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
		TestIfLoggedAsyncTask testIfLoggedAsyncTask = new TestIfLoggedAsyncTask(context, testResponse);
		if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
			testIfLoggedAsyncTask.execute();
		}
		else
		{
			testIfLoggedAsyncTask.onPostExecute(testIfLoggedAsyncTask.doInBackground());
		}

	}

	public void testLogin(final Context context,
			final OnAsyncResponse onResponse) {

		if (!Helper.isOnline(context)) {
			onResponse.response(true, "");
		}
		LoginAsyncTask loginAsyncTask = new LoginAsyncTask(context, onResponse);
		if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
			loginAsyncTask.execute(this);
		}
		else
		{
			loginAsyncTask.onPostExecute(loginAsyncTask.doInBackground(this));
		}
			

	}

}

class TestIfLoggedAsyncTask extends
		AsyncTask<Void, Void, Boolean> {
	private final Context context;
	private final OnAsyncResponse testResponse;

	TestIfLoggedAsyncTask(Context context, OnAsyncResponse testResponse) {
		this.context = context;
		this.testResponse = testResponse;
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		return HttpManager.isLogged();
	}

	@Override
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
		super.onPostExecute(result);
	}
}

class LoginAsyncTask extends
		AsyncTask<Credentials, Void, LoginResult> {
	private final Context context;
	private final OnAsyncResponse onResponse;

	LoginAsyncTask(Context context, OnAsyncResponse onResponse) {
		this.context = context;
		this.onResponse = onResponse;
	}

	@Override
	protected LoginResult doInBackground(Credentials... params) {
		Credentials c = params[0];
		LoginResult res = HttpManager.login(c.getEmail(), c.getPassword());
		if (res.result) {
			int id = c.getUserId();
			HttpManager.fillCredentialsData(c);
			if (id != c.getUserId())
				MySettings.setCredentials(context, c);
		}
		return res;
	}

	@Override
	protected void onPostExecute(LoginResult result) {
		onResponse.response(result.result, result.message.toString());
		super.onPostExecute(result);
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