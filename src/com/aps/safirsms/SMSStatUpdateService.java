package com.aps.safirsms;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class SMSStatUpdateService extends Service {

	String UpdateMessageStatuseURL = "UpdateSMSStatus";

	String userNumner = "";
	String password = "";

	Context context = null;

	DatabaseHandler db = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		db = new DatabaseHandler(context);
		context = getApplicationContext();
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		
		
		//String BaseUrl = context.getResources().getString(R.string.baseURl);
		String BaseUrl = pref.getString("baseUrl", "");
		UpdateMessageStatuseURL = BaseUrl + UpdateMessageStatuseURL;

		
		userNumner = pref.getString("phonenumber", "");
		password = pref.getString("password", "");
		new UpdateSMSStat().execute();

	}

	private class UpdateSMSStat extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {

			try {
				db = new DatabaseHandler(context);
				Log.i("cntntnt", db.getSmsStatsCount() + " ");
				if (db.getSmsStatsCount() > 0) {

					List<SMSStat> stats = db.getAllSmsStats(100);

					for (SMSStat cn : stats) {

						String smsId = (String) cn.SMSId;
						int stat = (Integer) cn.Status;
						if (stat == 1 || stat == 3) {
							XMLParser parser = new XMLParser();
							List<NameValuePair> IdsToSend = new ArrayList<NameValuePair>();
							IdsToSend.add(new BasicNameValuePair("MessageIds",
									smsId));
							IdsToSend.add(new BasicNameValuePair("Status",
									Integer.toString(stat)));
							IdsToSend.add(new BasicNameValuePair("Number",
									userNumner));
							IdsToSend.add(new BasicNameValuePair("Password",
									password));
							parser.sendPostRequestWithoutResponce(
									UpdateMessageStatuseURL, IdsToSend);
							db.deleteSmsStat(cn.Id);
						}

					}
				}

			} catch (Exception e) {
				Log.i("myError", e.getMessage());
			} finally {

				db.close();
			}
			return null;
		}

	}

	protected void onPostExecute(Object result) {
		// db.close();
		stopSelf();
		return;
	}

}
