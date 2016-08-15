package com.aps.safirsms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("Wakelock")
public class Sender extends Service {

	static final String KEY_ITEM_SMSList = "SMS";
	static final String KEY_Id = "Id";
	static final String KEY_Message = "Message";
	static final String KEY_Number = "Number";

	String SmsURL = "GetSMS/";

	ConnectionDetector cd;
	String userNumner = "";
	String password = "";

	String smsPerSend = "20";
	
	String filter = "";

	boolean rapidSend = false;

	boolean rightel = false;

	Context thisContext = null;

	DatabaseHandler db = null;

	ExecutorService _executorService;
	ScheduledExecutorService _scheduledStopService;

	SharedPreferences pref = null;

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			_executorService = Executors.newSingleThreadExecutor();
			_scheduledStopService = Executors
					.newSingleThreadScheduledExecutor();

			thisContext = getApplicationContext();
			// PowerManager pm = (PowerManager) getApplicationContext()
			// .getSystemService(Context.POWER_SERVICE);
			// PowerManager.WakeLock wl = pm.newWakeLock(
			// PowerManager.PARTIAL_WAKE_LOCK, "");

			pref = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			if (pref.getString("smsPerSend", null) != null) {
				smsPerSend = pref.getString("smsPerSend", "");
			}
			if (pref.getBoolean("rapidSend", false)) {
				rapidSend = pref.getBoolean("rapidSend", false);
			}
			if (pref.getBoolean("rightellimit", false)) {
				rightel = pref.getBoolean("rightellimit", false);
			}
			if (pref.getString("operatorNum", null ) != null) {
				filter = pref.getString("operatorNum", "" );
			}
			db = new DatabaseHandler(thisContext);
			cd = new ConnectionDetector(getApplicationContext());

			String BaseUrl = pref.getString("baseUrl", "");
			SmsURL = BaseUrl + SmsURL;
			// ////////wl.acquire();

			Log.i("ggg", "eeee");
			// wl.release();
		} catch (Exception e) {
			Log.i("myError", e.getMessage());
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("ggg", "ssss");
		if (pref.getString("phonenumber", null) == null
				|| pref.getString("password", null) == null) {

		} else {
			userNumner = pref.getString("phonenumber", "");
			password = pref.getString("password", "");
			if (!cd.isConnectingToInternet()) {
				Toast.makeText(getApplicationContext(),
						getApplicationContext().getString(R.string.noInternet),
						Toast.LENGTH_LONG).show();
			} else {
				if (rightel) {
					if (db.getSmsStatsCountWhereNotDeleivered() < 20) {
						Runnable work2Run = new serviceRunnable(this, startId);
						_executorService.execute(work2Run);

					}
				} else {
					Runnable work2Run = new serviceRunnable(this, startId);
					_executorService.execute(work2Run);

				}
			}
		}

		return Service.START_STICKY;
	}

	class serviceRunnable implements Runnable {
		Sender _service;
		int _startId;

		public serviceRunnable(Sender service, int startId) {
			_service = service;
			_startId = startId;
		}

		public void run() {
			List<Message> messages = new ArrayList<Message>();

			try {
				Random rn = new Random();
				// XMLParser parser = new XMLParser();
				// String xml = null;
				// if (rapidSend) {
				// xml = parser.getXmlFromUrl(SmsURL + "?number=" + userNumner
				// + "&password=" + password + "&filter=" + filter+ "&count="
				// + (20 - db.getSmsStatsCountWhereNotDeleivered())
				// + "&rapid=1");
				// // Log.i("rapidxml", xml);
				// } else {
				// xml = parser.getXmlFromUrl(SmsURL + "?number=" + userNumner
				// + "&password=" + password+ "&filter=" + filter + "&count=" + smsPerSend);
				// // + (20 - db.getSmsStatsCountWhereNotDeleivered()));
				// Log.i("rapidxml", xml);
				// }
				// try {
				// Document doc = parser.getDomElement(xml);
				//
				// NodeList nl = doc.getElementsByTagName(KEY_ITEM_SMSList);
				// for (int i = 0; i < nl.getLength(); i++) {
				// Element e = (Element) nl.item(i);
				//
				// String tempId = parser.getValue(e, KEY_Id);
				// String tempMessage = parser.getValue(e, KEY_Message);
				// String tempNumber = parser.getValue(e, KEY_Number);
				// Message tempSMS = new Message();
				// tempSMS.Id = tempId;
				// tempSMS.Message = tempMessage;
				// tempSMS.Number = tempNumber;
				// messages.add(tempSMS);
				// }
				//
				// } catch (Exception e) {
				// Log.i("myError", e.getMessage());
				// }

				Message tempSMS = new Message();
				tempSMS.Id = Integer.toString(rn.nextInt());
				tempSMS.Message = "تتعالمللی 21-24شهریور تلفن84346";
				tempSMS.Number = "09125365895";
				messages.add(tempSMS);
				tempSMS = new Message();
				tempSMS.Id = Integer.toString(rn.nextInt());
				tempSMS.Message = "تعیین سطح رایگان سفیر سالن38نمایشگاه بین المللی 21-24شهریور تلفن84346";
				tempSMS.Number = "09125365895";
				messages.add(tempSMS);

				if (rapidSend) {
					for (Message item : messages) {
						if (item.Number.trim().length() != 0) {
							if (item.Message.trim().length() != 0) {

								final String phoneNumber = item.Number.trim();
								String message = item.Message.trim();
								String DELIVERED = "SMS_DELIVERED_HOORAY_RAPID";
								Intent sendInt = null;
								sendInt = new Intent(DELIVERED);
								sendInt.putExtra("MessageId", item.Id);
								sendInt.putExtra("Number", phoneNumber);
								PendingIntent sendResult = PendingIntent
										.getBroadcast(thisContext,
												rn.nextInt(), sendInt,
												PendingIntent.FLAG_ONE_SHOT);

								String SENT = "SMS_SENT_HOORAY_RAPID";
								Intent sendInt2 = null;
								sendInt2 = new Intent(SENT);
								sendInt2.putExtra("MessageId", item.Id);
								sendInt2.putExtra("Number", phoneNumber);
								PendingIntent sent = PendingIntent
										.getBroadcast(thisContext,
												rn.nextInt(), sendInt2,
												PendingIntent.FLAG_ONE_SHOT);

								SmsManager sms = SmsManager.getDefault();

								ArrayList<String> parts = sms
										.divideMessage(message);

								if (parts.size() > 1) {
									sms.sendTextMessage(phoneNumber, null,
											parts.get(0), sent, sendResult);
									Log.i("rapidPARTS", phoneNumber);

								} else {
									sms.sendTextMessage(phoneNumber, null,
											message, sent, sendResult);
									Log.i("rapidMessage", phoneNumber);

								}

								Thread.sleep(4500);

							}
						}
					}

				} else {
					for (Message item : messages) {
						if (item.Number.trim().length() != 0) {
							if (item.Message.trim().length() != 0) {

								final String phoneNumber = item.Number.trim();
								String message = item.Message.trim();

								String SENT = "SMS_SENT_HOORAY";
								String DELIVERED = "SMS_DELIVERED_HOORAY";

								SmsManager sms = SmsManager.getDefault();

								ArrayList<String> parts = sms
										.divideMessage(message);

								ArrayList<PendingIntent> sentPis = new ArrayList<PendingIntent>();

								Intent sendInt = null;
								for (int i = 0; i < parts.size(); i++) {
									sendInt = new Intent(SENT);
									sendInt.putExtra("MessageId", item.Id);
									sendInt.putExtra("Partei", i);
									sendInt.putExtra("Number", phoneNumber);
									PendingIntent sendResult = PendingIntent
											.getBroadcast(thisContext, i,
													sendInt,
													PendingIntent.FLAG_ONE_SHOT);
									sentPis.add(sendResult);
								}
								ArrayList<PendingIntent> deliverPis = new ArrayList<PendingIntent>();
								sendInt = new Intent(DELIVERED);
								sendInt.putExtra("MessageId", item.Id);
								sendInt.putExtra("Number", phoneNumber);
								PendingIntent sendResult = PendingIntent
										.getBroadcast(thisContext,
												rn.nextInt(), sendInt,
												PendingIntent.FLAG_ONE_SHOT);
								for (int i = 0; i < parts.size(); i++) {
									deliverPis.add(sendResult);
								}
								sms.sendMultipartTextMessage(phoneNumber, null,
										parts, sentPis, deliverPis);

								Thread.sleep(5000);

							}
						}
					}

				}

			} catch (Exception e) {
				Log.i("myError", e.getMessage());
			}

			DelayedStopRequest stopRequest = new DelayedStopRequest(_service,
					_startId);
			_service._scheduledStopService.schedule(stopRequest, 11,
					TimeUnit.SECONDS);
		}
	}

	class DelayedStopRequest implements Runnable {
		Sender _service;
		int _startId;

		public DelayedStopRequest(Sender service, int startId) {
			_service = service;
			_startId = startId;
		}

		public void run() {
			_service.stopSelf(_startId);
		}
	}

	@Override
	public void onDestroy() {

		try {
			db.close();
		} catch (Exception e) {
		}

	}

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	private class Message {
		public String Id;
		public String Message;
		public String Number;
	}

}
