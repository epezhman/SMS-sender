package com.aps.safirsms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

//@SuppressLint("Wakelock")
public class UpdateSMSStatus extends BroadcastReceiver {

	//String UpdateMessageStatuseURL = "UpdateSMSStatus";

	String userNumner = "";
	String password = "";

	PendingResult maybe = null;

	PowerManager pm = null;
	PowerManager.WakeLock wl = null;

	// @SuppressLint("NewApi")
	@SuppressLint("Wakelock")
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			DatabaseHandler db = new DatabaseHandler(context);
			 pm = (PowerManager)
			 context.getSystemService(Context.POWER_SERVICE);
			 wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
			
			 wl.acquire();


			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String action = intent.getAction();
				if (action.equals("SMS_SENT_HOORAY")) {
					switch (getResultCode()) {
					case Activity.RESULT_OK:
						//db.addSmsStatIfNoExist(bundle.getString("MessageId"), 6);
						Log.i("sendNOEE", bundle.getString("MessageId") + " 6 "
								+ bundle.getString("Number"));
						break;
					}

				} else if (action.equals("SMS_DELIVERED_HOORAY")) {
					switch (getResultCode()) {
					case Activity.RESULT_OK:
						db.updateSmsStatWithoutId(bundle.getString("MessageId"), 1);

						Intent i = new Intent(context, OneSMSUpdateIntentService.class);
						i.putExtra("stat", 1);
						i.putExtra("smsId", bundle.getString("MessageId"));
						context.startService(i);
						Log.i("RECSS", bundle.getString("MessageId") + " 1 "
								+ bundle.getString("Number"));
						break;
					case Activity.RESULT_CANCELED:
						db.updateSmsStatWithoutId(bundle.getString("MessageId"), 3);
						Intent j = new Intent(context, OneSMSUpdateIntentService.class);
						j.putExtra("stat", 3);
						j.putExtra("smsId", bundle.getString("MessageId"));
						context.startService(j);
						Log.i("RECSS", bundle.getString("MessageId") + " 3 "
								+ bundle.getString("Number"));
						break;
					}

				}

			}

			 wl.release();
		} catch (Exception e) {
			Log.i("myError", e.getMessage());
		}

	}

}
