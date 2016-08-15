package com.aps.safirsms;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		try {

			if ("android.intent.action.BOOT_COMPLETED".equals(intent
					.getAction())
					|| "android.intent.action.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE"
							.equals(intent.getAction())) {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(context);
				String interval = "5";
				if (prefs.getString("smsTimeInterval", null) != null) {
					interval = prefs.getString("smsTimeInterval", "");
				}
				if (prefs.getBoolean("autoSend", false)) {
					AlarmManager am = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);
					Intent i = new Intent(context, AutoSender.class);
					PendingIntent pi = PendingIntent.getBroadcast(context, 0,
							i, 0);
					am.setRepeating(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis(),
							1000 * 60 * Integer.parseInt(interval), pi);

					i = new Intent(context, SMSStatUpdaterReceiver.class);
					pi = PendingIntent.getBroadcast(context, 0, i, 0);
					am.setRepeating(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis(),
							1000 * 65 * Integer.parseInt(interval), pi);

					i = new Intent(context, SendTester.class);
					pi = PendingIntent.getBroadcast(context, 0, i, 0);
					am.setRepeating(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis(),
							1000 * 62 * 60 * 1, pi);
					
					NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

					int notifId = R.drawable.notif;
					long timeStamp = System.currentTimeMillis();
					String notifText = "Safir SMS Running";

					CharSequence contentTitle = "Safir SMS Running";
					CharSequence contentText = "Safir SMS Running";
					Intent notificationIntent = new Intent(context, MainActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
							notificationIntent, 0);
					Notification noti = new Notification(notifId, notifText, timeStamp);

					noti.setLatestEventInfo(context, contentTitle, contentText,
							contentIntent);
					noti.flags |= Notification.FLAG_NO_CLEAR; 

					mNotificationManager.notify(1, noti);

					Toast.makeText(context, "SafirSMS started!",
							Toast.LENGTH_LONG).show();

				}
			}

		} catch (Exception e) {
			Log.i("myError", e.getMessage());
		}
	}
}