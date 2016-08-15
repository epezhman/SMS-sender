package com.aps.safirsms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class UpdateSMSStatusRapid extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		DatabaseHandler db = new DatabaseHandler(context);
		
		try {
			

			// Log.i("RECSS","sss");
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String action = intent.getAction();
				if (action.equals("SMS_DELIVERED_HOORAY_RAPID")) {
					switch (getResultCode()) {
					case Activity.RESULT_OK:
						//db.addSmsStat(bundle.getString("MessageId"), 1);
						db.updateSmsStatWithoutId(bundle.getString("MessageId"), 1);
						Log.i("RECSS", bundle.getString("MessageId") + " 1 "
								+ bundle.getString("Number"));
						break;
					case Activity.RESULT_CANCELED:
						//db.addSmsStat(bundle.getString("MessageId"), 3);
						db.updateSmsStatWithoutId(bundle.getString("MessageId"), 1);

						Log.i("RECSS", bundle.getString("MessageId") + " 3 "
								+ bundle.getString("Number"));
						break;
					}

				} else if (action.equals("SMS_SENT_HOORAY_RAPID")) {
					switch (getResultCode()) {
					case Activity.RESULT_OK:
						db.addSmsStat(bundle.getString("MessageId"), 6);
						Log.i("SENTSSS", bundle.getString("MessageId") + " 6 "
								+ bundle.getString("Number"));
						break;
					}

				}

			}
		} catch (Exception e) {
			Log.i("myError", e.getMessage());
		} finally {

			db.close();
		}

	}

}
