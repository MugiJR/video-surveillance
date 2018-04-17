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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signup extends Activity {
	Button signup_back_btn, signup_submit;
	EditText signup_username, signup_password, signup_reenter_password,
			email_edit, phone_edit;
	boolean checkUser = false;
	private Context context;
	ProgressDialog pd;
	HttpClient httpClient;
	HttpPost httpPost;
	HttpResponse httpResponse;
	
	int len;
	int i;
	int num=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);

		createWidgetId();

		signup_back_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}

		});

		signup_submit.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				if (signup_username.getText().toString().isEmpty()) {
					Toast.makeText(Signup.this, "Please Enter your username",
							Toast.LENGTH_LONG).show();
				} 
				
				else if (signup_password.getText().toString().isEmpty()) {
					Toast.makeText(Signup.this, "Please Enter your password",
							Toast.LENGTH_LONG).show();
				} 
				
				
				else if (signup_reenter_password.getText().toString()
						.isEmpty()) {
					Toast.makeText(Signup.this,
							"Please Re-Enter your password", Toast.LENGTH_LONG)
							.show();
				} 
				
				
				else if (email_edit.getText().toString().isEmpty()) {
				
					Toast.makeText(Signup.this,
							"Please Enter your email-address",
							Toast.LENGTH_LONG).show();
				} 
				
				

				else if(!email_edit.getText().toString().isEmpty())
				{
					
					len=email_edit.getText().toString().length();
					
					for(i=0;i<len;i++)
					{
						
						if(email_edit.getText().toString().charAt(i)=='@')
						
						{
							num++;
							
						}
						
					}
				
				 if(num==0)
					{
						Toast.makeText(getApplicationContext(), "Enter the valid mail.ID", Toast.LENGTH_LONG).show();
					}
				
				
				
				
				
				
				
				
				
				
				else if (phone_edit.getText().toString().isEmpty()) {
					Toast.makeText(Signup.this,
							"Please Enter your phone number", Toast.LENGTH_LONG)
							.show();
				}
				
				else if(!phone_edit.getText().toString().isEmpty()&&phone_edit.getText().toString().length()!=10)
				{
					
					
						Toast.makeText(getApplicationContext(), "Enter The Valid Phone Numbere", Toast.LENGTH_LONG).show();
					
					
				}
				
			
				
				
				
				
				
				
				else {
					if (signup_password
							.getText()
							.toString()
							.equals(signup_reenter_password.getText()
									.toString())) {
						pd = ProgressDialog.show(Signup.this, "",
								"Logging in...", false, true);
						new Thread() {
							public void run() {
								try {
									httpClient = new DefaultHttpClient();
									httpPost = new HttpPost(Signup.this
											.getResources().getString(
													R.string.loginip));
									List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
									nameValuePair.add(new BasicNameValuePair(
											"flag", "1"));
									nameValuePair.add(new BasicNameValuePair(
											"email", email_edit.getText()
													.toString()));
									nameValuePair.add(new BasicNameValuePair(
											"password", signup_password
													.getText().toString()));
									nameValuePair.add(new BasicNameValuePair(
											"username", signup_username
													.getText().toString()));
									nameValuePair.add(new BasicNameValuePair(
											"phone", phone_edit.getText()
													.toString()));
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
						Toast.makeText(Signup.this,
								"Re-entered password is wrong!",
								Toast.LENGTH_LONG).show();
					}
				}}
			
			}
		});
			
		
	}

	private void createWidgetId() {
		signup_submit = (Button) findViewById(R.id.signup_submit);
		signup_back_btn = (Button) findViewById(R.id.signup_back_btn);
		signup_username = (EditText) findViewById(R.id.signup_username);
		signup_password = (EditText) findViewById(R.id.signup_password);
		signup_reenter_password = (EditText) findViewById(R.id.signup_reenter_password);
		email_edit = (EditText) findViewById(R.id.email_edit);
		phone_edit = (EditText) findViewById(R.id.phone_edit);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				pd.dismiss();
				AlertDialog.Builder alert = new AlertDialog.Builder(Signup.this);
				alert.setCancelable(false);
				alert.setTitle("Registration successfull !");
				alert.setMessage("You have been successfully registered with Email");
				alert.setPositiveButton("Done",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								//GCMRegistrar.register(Signup.this,Integer.toString(R.string.registrationno));
								GCMRegistrar.register(Signup.this, "703793047540");
								finish();
							}
						});
				alert.show();
				break;
			case 2:
				pd.dismiss();
				Toast.makeText(Signup.this,
						"Your username and password is wrong !",
						Toast.LENGTH_LONG).show();
				break;
			case 3:
				pd.dismiss();
				Toast.makeText(Signup.this,
						"Please check your internet connection or URL!",
						Toast.LENGTH_LONG).show();
				break;
			}
		}

	};

}
