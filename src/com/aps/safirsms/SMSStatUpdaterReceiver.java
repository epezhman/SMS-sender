package com.aps.safirsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SMSStatUpdaterReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			//Intent i = new Intent(context, SMSStatUpdateService.class);
			Intent i = new Intent(context, SMSesStautsIntentService.class);
			//context.stopService(i);
			context.startService(i);
		} catch (Exception e) {
			Log.i("myError", e.getMessage());
		}

	}

}
