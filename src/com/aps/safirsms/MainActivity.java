package com.aps.safirsms;

import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	Button checkButton = null;
	Button sendButton = null;
	TextView reportText = null;

	String CheckURL = "Check/";
	String SmsURL = "GetSMS/";
	String UpdateMessageStatuseURL = "UpdateSMSStatus";

	Dialog checkDialog = null;
	Dialog sendDialog = null;
	int waitDialog = 1;
	int noInternetDialog = 3;

	static final String KEY_ITEM_SMSCheck = "Available";
	static final String KEY_Count = "Count";

	static final String KEY_ITEM_SMSList = "SMS";
	static final String KEY_Id = "Id";
	static final String KEY_Message = "Message";
	static final String KEY_Number = "Number";

	String userNumner = "";
	String password = "";

	String smsCount = null;
	int smsSent = 0;
	int smsSentContr = 0;
	int smsListCnt = 0;
	String smsPerSend = "20";

	ConnectionDetector cd;
	Boolean isInternetPresent = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (pref.getString("lang", null) != null) {

			Locale.setDefault(new Locale(pref.getString("lang", "")));
			Resources res = getResources();
			DisplayMetrics dm = res.getDisplayMetrics();
			android.content.res.Configuration conf = res.getConfiguration();
			conf.locale = new Locale(pref.getString("lang", ""));
			res.updateConfiguration(conf, dm);
		}
		if (pref.getString("smsPerSend", null) != null) {
			smsPerSend = pref.getString("smsPerSend", "");
		}

		setContentView(R.layout.activity_main);

		checkButton = (Button) findViewById(R.id.checkServerBtn);
		reportText = (TextView) findViewById(R.id.reportTextView);

		//String BaseUrl = getResources().getString(R.string.baseURl);
		String BaseUrl = pref.getString("baseUrl", "");
		CheckURL = BaseUrl + CheckURL;
		SmsURL = BaseUrl + SmsURL;
		UpdateMessageStatuseURL = BaseUrl + UpdateMessageStatuseURL;

		cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
			showDialog(noInternetDialog);
		}

		if (pref.getString("phonenumber", null) == null
				|| pref.getString("password", null) == null) {
			Intent i = new Intent(MainActivity.this, NumberVerify.class);
			startActivityForResult(i, 101);

		} else {
			userNumner = pref.getString("phonenumber", "");
			password = pref.getString("password", "");

		}

		checkButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!cd.isConnectingToInternet()) {
					showDialog(noInternetDialog);
				} else {
					showDialog(waitDialog);
					new CheckSms().execute();
				}

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent i = new Intent(this, MainPreferenceActivity.class);
			startActivityForResult(i, 206);
			break;
		}

		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		if (id == waitDialog) {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getString(R.string.wait));
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			checkDialog = dialog;
			return dialog;
		} else if (id == noInternetDialog) {
			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
					.create();
			alertDialog.setTitle(getString(R.string.noInternet));
			alertDialog.setMessage(getString(R.string.connectToInternet));
			alertDialog.setIcon(R.drawable.fail);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialog.show();
		}
		return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (pref.getString("phonenumber", null) == null
				|| pref.getString("password", null) == null) {
			Intent i = new Intent(MainActivity.this, NumberVerify.class);
			startActivityForResult(i, 101);

		} else {
			userNumner = pref.getString("phonenumber", "");
			password = pref.getString("password", "");
		}

		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}

	private class CheckSms extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {

			try {

				XMLParser parser = new XMLParser();
				String xml;
				xml = parser.getXmlFromUrl(CheckURL + "?number=" + userNumner
						+ "&password=" + password);
				Log.i("url", xml);
				Document doc = parser.getDomElement(xml);

				NodeList nl = doc.getElementsByTagName(KEY_ITEM_SMSCheck);
				Element e = (Element) nl.item(0);

				smsCount = parser.getValue(e, KEY_Count);

			} catch (Exception e) {
				Log.e("myError", e.getMessage());
			}
			return null;

		}

		protected void onPostExecute(Object result) {
			reportText = (TextView) findViewById(R.id.reportTextView);
			reportText.setText(getString(R.string.messageReadytoSend)
					+ smsCount);
			removeDialog(waitDialog);
		}
	}

}
