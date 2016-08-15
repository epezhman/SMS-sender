package com.aps.safirsms;

import java.util.Locale;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class MainPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (prefs.getString("lang", null) != null) {
			Locale.setDefault(new Locale(prefs.getString("lang", "")));
			Resources res = getResources();
			DisplayMetrics dm = res.getDisplayMetrics();
			android.content.res.Configuration conf = res.getConfiguration();
			conf.locale = new Locale(prefs.getString("lang", ""));
			res.updateConfiguration(conf, dm);
		}

		addPreferencesFromResource(R.xml.main_preferences);

		Preference p = (ListPreference) findPreference("lang");

		p.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				Intent intent = getIntent();
				finish();
				startActivity(intent);
				return true;
			}
		});

		Preference intr = (ListPreference) findPreference("smsTimeInterval");

		intr.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				String interval = newValue.toString();
				if (prefs.getBoolean("autoSend", false)) {
					AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					Intent i = new Intent(getApplicationContext(),
							AutoSender.class);
					PendingIntent pi = PendingIntent.getBroadcast(
							getApplicationContext(), 0, i, 0);
					am.cancel(pi);
					am.setRepeating(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis(),
							1000 * 60 * Integer.parseInt(interval), pi);
					i = new Intent(getApplicationContext(),
							SMSStatUpdaterReceiver.class);
					pi = PendingIntent.getBroadcast(getApplicationContext(), 0,
							i, 0);
					am.cancel(pi);
					am.setRepeating(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis(),
							1000 * 65 * Integer.parseInt(interval), pi);
					i = new Intent(getApplicationContext(), SendTester.class);
					pi = PendingIntent.getBroadcast(getApplicationContext(), 0,
							i, 0);
					am.cancel(pi);
					am.setRepeating(AlarmManager.RTC_WAKEUP,
							System.currentTimeMillis(), 1000 * 62 * 60 * 1, pi);
				}
				return true;
			}
		});

		CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager()
				.findPreference("autoSend");

		checkboxPref
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if (newValue.toString().equals("true")) {
							SharedPreferences prefs = PreferenceManager
									.getDefaultSharedPreferences(getApplicationContext());
							String interval = "5";
							if (prefs.getString("smsTimeInterval", null) != null) {
								interval = prefs.getString("smsTimeInterval",
										"");
							}
							startNotif();
							AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
							Intent i = new Intent(getApplicationContext(),
									AutoSender.class);
							PendingIntent pi = PendingIntent.getBroadcast(
									getApplicationContext(), 0, i, 0);

							am.setRepeating(AlarmManager.RTC_WAKEUP,
									System.currentTimeMillis(),
									1000 * 60 * Integer.parseInt(interval), pi);

							i = new Intent(getApplicationContext(),
									SMSStatUpdaterReceiver.class);
							pi = PendingIntent.getBroadcast(
									getApplicationContext(), 0, i, 0);

							am.setRepeating(AlarmManager.RTC_WAKEUP,
									System.currentTimeMillis(),
									1000 * 65 * Integer.parseInt(interval), pi);

							i = new Intent(getApplicationContext(),
									SendTester.class);
							pi = PendingIntent.getBroadcast(
									getApplicationContext(), 0, i, 0);

							am.setRepeating(AlarmManager.RTC_WAKEUP,
									System.currentTimeMillis(),
									1000 * 62 * 60 * 1, pi);

							Toast.makeText(getApplicationContext(),
									getString(R.string.autoSendWarning),
									Toast.LENGTH_LONG).show();
						} else {

							Intent intent = new Intent(getApplicationContext(),
									AutoSender.class);
							PendingIntent pi = PendingIntent.getBroadcast(
									getApplicationContext(), 0, intent, 0);
							AlarmManager alarmManager = (AlarmManager) getApplicationContext()
									.getSystemService(Context.ALARM_SERVICE);
							alarmManager.cancel(pi);

							
							
							intent = new Intent(getApplicationContext(),
									SMSStatUpdaterReceiver.class);
							pi = PendingIntent.getBroadcast(
									getApplicationContext(), 0, intent, 0);
							alarmManager.cancel(pi);

							intent = new Intent(getApplicationContext(),
									SendTester.class);
							pi = PendingIntent.getBroadcast(
									getApplicationContext(), 0, intent, 0);
							alarmManager.cancel(pi);

							cancelNoti();
						}
						return true;
					}
				});

	}

	@SuppressWarnings("deprecation")
	private void startNotif() {

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		int notifId = R.drawable.notif;
		long timeStamp = System.currentTimeMillis();
		String notifText = "Safir SMS Running";

		Context context = getApplicationContext();
		CharSequence contentTitle = "Safir SMS Running";
		CharSequence contentText = "Safir SMS Running";
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		Notification noti = new Notification(notifId, notifText, timeStamp);

		noti.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		noti.flags |= Notification.FLAG_NO_CLEAR; 

		mNotificationManager.notify(1, noti);

	}
	
	private void cancelNoti()
	{
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);
	}
}
