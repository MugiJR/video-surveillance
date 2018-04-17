package com.android;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.android.gcm.GCMRegistrar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Welcome extends Activity {
	Button submit, reset, exit, signup;
	EditText txt_user, txt_pass;
	SQLiteDatabase db;
	boolean user = false, pass = false;
	ProgressDialog pd;
	HttpClient httpClient;
	HttpPost httpPost;
	HttpResponse httpResponse;
	private Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		
		createWidgetId();

		submit.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(View v) {
				if (((!txt_user.getText().toString().isEmpty()))
						&& ((!txt_pass.getText().toString().isEmpty()))) {
					pd = ProgressDialog.show(Welcome.this, "", "Logging in...",
							false, true);
					new Thread() {
						public void run() {
							try {
								httpClient = new DefaultHttpClient();
								httpPost = new HttpPost(
										Welcome.this
										.getResources().getString(
												R.string.loginip));
								List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
								
								nameValuePair.add(new BasicNameValuePair(
										"flag", "2"));
								
								GCMRegistrar.register(Welcome.this, Integer.toString(R.string.registrationno));
								
								nameValuePair
										.add(new BasicNameValuePair("email",
												txt_user.getText().toString()));
								nameValuePair.add(new BasicNameValuePair(
										"password", txt_pass.getText()
												.toString()));
								nameValuePair.add(new BasicNameValuePair(
										"gcm_id", Gcmpreference.getString(Welcome.this, Gcmpreference.GCM_ID, "")));
								
								httpPost.setEntity(new UrlEncodedFormEntity(
										nameValuePair));
								httpResponse = httpClient.execute(httpPost);
								InputStream inputStream = httpResponse
										.getEntity().getContent();

								InputStreamReader inputStreamReader = new InputStreamReader(
										inputStream);

								BufferedReader bufferedReader = new BufferedReader(
										inputStreamReader);

								StringBuilder stringBuilder = new StringBuilder();

								String bufferedStrChunk = null;

								while ((bufferedStrChunk = bufferedReader
										.readLine()) != null) {
									stringBuilder.append(bufferedStrChunk);
								}
								System.out.println("Login :"
										+ stringBuilder.toString());
								if (stringBuilder.toString().trim()
										.equals("yes")) {
									handler.sendEmptyMessage(1);
								} else {
									handler.sendEmptyMessage(2);
								}

							} catch (Exception e) {
								handler.sendEmptyMessage(3);
							}
						}
					}.start();

				} else {
					Toast.makeText(getApplicationContext(),
							"Username or Password fields can't be empty",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		reset.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				txt_user.setText("");
				txt_pass.setText("");
			}
		});

		signup.setOnClickListener(new OnClickListener() 
		{

			public void onClick(View v) 
			{
				startActivity(new Intent(Welcome.this, Signup.class));
			}

		});

		exit.setOnClickListener(new OnClickListener() 
		{

			public void onClick(View v) {
				finish();
			}
		});

	}

	private Handler handler = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case 1:
				pd.dismiss();
				//GCMRegistrar.register(Welcome.this, Integer.toString(R.string.registrationno));703793047540
				GCMRegistrar.register(Welcome.this, "703793047540");
				Intent intent = new Intent(Welcome.this, DisplayImage.class);
				startActivity(intent);
				break;
			case 2:
				pd.dismiss();
				Toast.makeText(Welcome.this,
						"Your username and password is wrong !",
						Toast.LENGTH_LONG).show();
				break;
			case 3:
				pd.dismiss();
				Toast.makeText(Welcome.this,
						"Please check your internet connection or URL!",
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	private void createWidgetId() {
		submit = (Button) findViewById(R.id.submit);
		signup = (Button) findViewById(R.id.singup);
		exit = (Button) findViewById(R.id.exit);
		reset = (Button) findViewById(R.id.reset);
		txt_user = (EditText) findViewById(R.id.txt_userName);
		txt_pass = (EditText) findViewById(R.id.txt_pass);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}