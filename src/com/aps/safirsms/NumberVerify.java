package com.aps.safirsms;

import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberVerify extends Activity {

	Button validateButton = null;
	TextView validateReport = null;
	EditText phoneNumberEditText = null;

	int validateDialogCode = 1;
	int noInternetDialog = 2;
	Dialog validateDialog = null;

	int tryCount = 0;

	private BroadcastReceiver smsIntentReceiver;
	private IntentFilter smsFilter;

	String serialNumber = "";
	String password = "";

	Intent thisOne = null;

	String updateValidationStatuseUrl = "ValidateNumber/";

	ConnectionDetector cd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {

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

			if (pref.getString("password", null) == null) {

			} else {
				password = pref.getString("password", "");
			}
			setContentView(R.layout.number_verify);

			//String BaseUrl = getResources().getString(R.string.baseURl);
			String BaseUrl = pref.getString("baseUrl", "");
			updateValidationStatuseUrl = BaseUrl + updateValidationStatuseUrl;

			validateButton = (Button) findViewById(R.id.validateNumberBtn);

			validateReport = (TextView) findViewById(R.id.validateReportNumberTextView);

			phoneNumberEditText = (EditText) findViewById(R.id.phonrNumberEditText);

			thisOne = getIntent();

			cd = new ConnectionDetector(getApplicationContext());
			if (!cd.isConnectingToInternet()) {
				showDialog(noInternetDialog);
			}

			smsFilter = new IntentFilter();
			smsFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

			smsIntentReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {

					TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					serialNumber = telemamanger.getSimSerialNumber();

					Bundle bundle = intent.getExtras();
					SmsMessage[] msgs = null;
					if (bundle != null) {
						Object[] pdus = (Object[]) bundle.get("pdus");
						msgs = new SmsMessage[pdus.length];
						for (int i = 0; i < msgs.length; i++) {
							msgs[i] = SmsMessage
									.createFromPdu((byte[]) pdus[i]);
							try {

								String msgBody = msgs[i].getMessageBody()
										.toString();
								if (msgBody.equals(StringCryptor.encrypt(
										"epezhman", serialNumber))) {
									abortBroadcast();
									Toast.makeText(NumberVerify.this,
											getString(R.string.gotandupdatedb),
											Toast.LENGTH_SHORT).show();
									new ValidateNumberStat().execute(msgs[i]
											.getOriginatingAddress());
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}

				}
			};

			validateButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {

					if (!cd.isConnectingToInternet()) {
						showDialog(noInternetDialog);
					} else {
						if (phoneNumberEditText.getText().toString() != null
								&& phoneNumberEditText.getText().toString()
										.length() == 11
								&& phoneNumberEditText.getText().toString()
										.startsWith("09")) {

							showDialog(validateDialogCode);
							sendValidation(phoneNumberEditText.getText()
									.toString());

						} else {
							Toast.makeText(NumberVerify.this,
									getString(R.string.validnumber),
									Toast.LENGTH_SHORT).show();
						}
					}

				}
			});
		} catch (Exception e) {
			Log.i("myError", e.getMessage());
		}

	}

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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		if (id == validateDialogCode) {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getString(R.string.wait));
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			validateDialog = dialog;
			return dialog;
		} else if (id == noInternetDialog) {
			AlertDialog alertDialog = new AlertDialog.Builder(NumberVerify.this)
					.create();
			alertDialog.setTitle(getString(R.string.noInternet));
			alertDialog.setMessage(getString(R.string.connectToInternet));
			alertDialog.setIcon(R.drawable.fail);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// System.exit(0);
				}
			});
			alertDialog.show();
		}
		return null;
	}

	private void sendValidation(final String phoneNumber) {
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					tryCount++;
					Toast.makeText(NumberVerify.this,
							getString(R.string.sentCode), Toast.LENGTH_SHORT)
							.show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					break;
				}
			}
		}, new IntentFilter(SENT));

		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					break;
				case Activity.RESULT_CANCELED:
					if (tryCount <= 2) {
						sendValidation(phoneNumber);
					} else {
						validateReport.setText(getString(R.string.invalidNumber));
						removeDialog(validateDialogCode);
					}
					break;
				}
			}
		}, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		try {
			sms.sendTextMessage(
					phoneNumber,
					null,
					StringCryptor.encrypt("epezhman",
							telemamanger.getSimSerialNumber()), sentPI,
					deliveredPI);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Intent intent = getIntent();
		finish();
		startActivity(intent);

	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(smsIntentReceiver, smsFilter);
		unregisterReceiver(smsIntentReceiver);
		registerReceiver(smsIntentReceiver, smsFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(smsIntentReceiver);

	}

	private class ValidateNumberStat extends AsyncTask<Object, Object, Object> {

		@Override
		protected Object doInBackground(Object... arg0) {

			String phoneNumber = (String) arg0[0];

			if (phoneNumber.startsWith("+")) {
				phoneNumber = "0" + phoneNumber.substring(3);
			}
			XMLParser parser = new XMLParser();
			String xml = parser.getXmlFromUrl(updateValidationStatuseUrl
					+ "?number=" + phoneNumber + "&password=" + password);
			Document doc = parser.getDomElement(xml);

			NodeList nl = doc.getElementsByTagName("Validation");
			Element e = (Element) nl.item(0);

			String numb = parser.getValue(e, "Number");

			if (!numb.equals("0")) {
				numb = numb + "_" + password;
			}

			return numb;
		}

		protected void onPostExecute(Object result) {

			String phoneNumber = (String) result;
			if (!phoneNumber.equals("0")) {
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				Editor prefEditor = prefs.edit();
				if (phoneNumber.startsWith("+98")) {
					phoneNumber = "0" + phoneNumber.substring(3);
				}
				String[] numberTokens = phoneNumber.split("_");
				// prefEditor.putString("phonenumber", numberTokens[0]);
				// prefEditor.putString("password", numberTokens[1]);
				prefEditor.putString("phonenumber", numberTokens[0]);
				// prefEditor.putString("phonenumber",
				// phoneNumber);

				prefEditor.commit();

				validateReport.setText(getString(R.string.validAndSubmitted));
				removeDialog(validateDialogCode);
				Toast.makeText(NumberVerify.this,
						getString(R.string.validAndSubmitted),
						Toast.LENGTH_SHORT).show();

				thisOne.putExtra("phoneNumber", phoneNumber);
				setResult(101, thisOne);
				finish();
			} else if (phoneNumber.equals("0")) {
				validateReport.setText(getString(R.string.doneValidation));
				removeDialog(validateDialogCode);
			} else {

				validateReport.setText(getString(R.string.invalidNumber));
				removeDialog(validateDialogCode);
			}
		}
	}

}
