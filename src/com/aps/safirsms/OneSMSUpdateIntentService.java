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

@SuppressLint("Wakelock")
public class OneSMSUpdateIntentService extends IntentService {

	
	String UpdateMessageStatuseURL = "UpdateSMSStatus";

	String userNumner = "";
	String password = "";

	Context context = null;

	
	public OneSMSUpdateIntentService() {
		super("OneSMSUpdateIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		PowerManager pm = (PowerManager) getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "");
		wl.acquire();
		context = getApplicationContext();

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);

		// String BaseUrl = context.getResources().getString(R.string.baseURl);
		String BaseUrl = pref.getString("baseUrl", "");
		UpdateMessageStatuseURL = BaseUrl + UpdateMessageStatuseURL;

		userNumner = pref.getString("phonenumber", "");
		password = pref.getString("password", "");
		XMLParser parser = new XMLParser();
		List<NameValuePair> IdsToSend = new ArrayList<NameValuePair>();
		
		String smsId = intent.getStringExtra("smsId");
		int stat = intent.getIntExtra("stat", 0);
		
		IdsToSend.add(new BasicNameValuePair("MessageIds",
				smsId));
		IdsToSend.add(new BasicNameValuePair("Status", Integer
				.toString(stat)));
		IdsToSend.add(new BasicNameValuePair("Number",
				userNumner));
		IdsToSend.add(new BasicNameValuePair("Password",
				password));
		parser.sendPostRequestWithoutResponce(
				UpdateMessageStatuseURL, IdsToSend);
		Log.i("oneIntent", "ddd");
		wl.release();
	}

}
