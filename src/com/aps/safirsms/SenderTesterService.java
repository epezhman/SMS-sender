package com.aps.safirsms;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class SenderTesterService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	DatabaseHandler db = null;

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			db = new DatabaseHandler(getApplicationContext());
			db.deleteAll();
			Log.i("sender", db.getSmsStatsCount() + " ");

		} catch (Exception e) {
		} finally {

			db.close();
			stopSelf();
		}
		// new SendSMS().execute();

	}

	private class SendSMS extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				// SmsManager sms = SmsManager.getDefault();
				//
				// sms.sendTextMessage("09125365895", null,
				// "test", null, null);
				// Thread.sleep(5000);
				// sms.sendTextMessage("09212089519", null,
				// "test", null, null);
				// Thread.sleep(5000);

			} catch (Exception e) {
				Log.i("myError", e.getMessage());
			} finally {
				stopSelf();
			}
			return null;
		}

	}

}
