package com.android;



import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	HttpClient httpClient;
	HttpPost httpPost;
	HttpResponse httpResponse;
	private Thread thread;

	public GCMIntentService() {
		super(Integer.toString(R.string.registrationno));
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.d("Registration error", "Registration error" + arg1);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		Log.d("Gcm message", "" + arg1.getStringExtra("filename"));
		arg0.getSharedPreferences("filename", Context.MODE_PRIVATE).edit()
				.putString("filename", arg1.getStringExtra("filename"))
				.commit();
		NotificationManager manager = (NotificationManager) arg0
				.getSystemService(NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(getApplicationContext(),
				Welcome.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, notificationIntent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				arg0).setWhen(System.currentTimeMillis())
				.setContentText("Motion has been detected. Click to view")
				.setContentTitle("Video Survilliance")
				.setSmallIcon(R.drawable.alert).setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_SOUND)
				.setTicker("Motion detected").setContentIntent(pendingIntent);
		Notification notification = builder.build();
		manager.notify((int) System.currentTimeMillis(), notification);
	}

	@Override
	protected void onRegistered(Context arg0, final String arg1) {
		Log.d("Registration received", "Registration received " + arg1);

		
		Gcmpreference.saveString(GCMIntentService.this, Gcmpreference.GCM_ID, arg1);
		
		Log.i("reg",""+Gcmpreference.getString(GCMIntentService.this, Gcmpreference.GCM_ID, ""));
		/*thread = new Thread() {
			public void run() {

				try {

					httpClient = new DefaultHttpClient();
					httpPost = new HttpPost("http://192.168.1.6:8080/ChatServer/Login");
					List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
					nameValuePair.add(new BasicNameValuePair("flag",
							"2"));
					nameValuePair.add(new BasicNameValuePair("gcm_id", arg1));
					
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
					httpResponse = httpClient.execute(httpPost);

				} catch (Exception e) {
					Log.d("Exception in updating location", "" + e.toString());
				}

			}
		};

		thread.start();*/

	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub

	}

}
