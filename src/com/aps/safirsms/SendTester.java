package com.aps.safirsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SendTester extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {

		try {
			Intent i = new Intent(context, SenderTesterService.class);
			context.stopService(i);
			context.startService(i);
		} catch (Exception e) {
			Log.i("myError", e.getMessage());
		}

	}
}
