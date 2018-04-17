package com.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		if (arg1.equals("com.google.android.c2dm.intent.REGISTRATION")) {
			handleRegistration(arg0, arg1);
			Log.d("Registration received", "Registration received");
		}
		else if (arg1.equals("com.google.android.c2dm.intent.RECEIVE")) {
			handleMessage(arg0, arg1);
		}
	}

	private void handleMessage(Context arg0, Intent arg1) {
		
	}

	private void handleRegistration(Context arg0, Intent arg1) {
		String regis_id = arg1.getStringExtra("registration_id");
		if (regis_id != null) {
			Log.d("Registration Id", " "+regis_id);
		}
		else if (arg1.getStringExtra("error") != null) {
			Log.d("Registration Error", ""+arg1.getStringExtra("error"));
		}
	}

}
