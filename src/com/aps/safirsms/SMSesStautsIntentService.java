package com.aps.safirsms;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class SMSesStautsIntentService extends IntentService {

	String UpdateMessageStatuseURL = "UpdateSMSStatus";

	String userNumner = "";
	String password = "";

	Context context = null;

	DatabaseHandler db = null;

	public SMSesStautsIntentService() {
		super("SMSesStautsIntentService");
	}

	@SuppressLint("Wakelock")
	@Override
	protected void onHandleIntent(Intent arg0) {

		PowerManager pm = (PowerManager) getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();
		db = new DatabaseHandler(context);
		context = getApplicationContext();
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);

		// String BaseUrl = context.getResources().getString(R.string.baseURl);
		String BaseUrl = pref.getString("baseUrl", "");
		UpdateMessageStatuseURL = BaseUrl + UpdateMessageStatuseURL;

		userNumner = pref.getString("phonenumber", "");
		password = pref.getString("password", "");
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
						IdsToSend.add(new BasicNameValuePair("Status", Integer
								.toString(stat)));
						IdsToSend.add(new BasicNameValuePair("Number",
								userNumner));
						IdsToSend.add(new BasicNameValuePair("Password",
								password));
//						parser.sendPostRequestWithoutResponce(
//								UpdateMessageStatuseURL, IdsToSend);
						db.deleteSmsStat(cn.Id);
					}

				}
			}

		} catch (Exception e) {
			Log.i("myError", e.getMessage());
		} finally {

			wl.release();
			db.close();
		}

	}

}
