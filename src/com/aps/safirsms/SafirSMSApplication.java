package com.aps.safirsms;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SafirSMSApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		try {

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			Editor prefEditor = prefs.edit();
			TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

			if (prefs.getString("simserial", null) == null) {
				prefEditor.putString("simserial",
						telemamanger.getSimSerialNumber());
			} else {

				String serial = prefs.getString("simserial", "");

				if (!serial.equals(telemamanger.getSimSerialNumber())) {
					prefEditor.putString("phonenumber", null);
					prefEditor.putString("password", null);
					prefEditor.putString("simserial",
							telemamanger.getSimSerialNumber());
				}
			}
			if (prefs.getString("smsPerSend", null) == null) {
				prefEditor.putString("smsPerSend", "20");
			}
			if (prefs.getString("smsTimeInterval", null) == null) {
				prefEditor.putString("smsTimeInterval", "5");
			}
			prefEditor.putString("phonenumber", "09212089592");
			prefEditor.putString("password", "ePezhman");
			prefEditor.putString("rapidSend", "false");

			prefEditor.commit();
		} catch (Exception e) {
			Log.i("myError", e.getMessage());
		}

	}
}
