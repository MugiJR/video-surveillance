package com.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Gcmpreference {
	public static final String GCM_ID = "GCM_ID";
	private Context context;
	public static final String PREFERENCE_NAME = "VIDEO_SURV";
	public static final int MODE = Context.MODE_PRIVATE;

	public Gcmpreference(Context context) {
		this.context = context;
	}

	public static void saveString(Context context, String key, String value) {
		getEditor(context).putString(key, value).commit();

	}

	public static String getString(Context context, String key, String defValue) {
		return getPreference(context).getString(key, defValue);
	}

	public static SharedPreferences getPreference(Context context) {
		return context.getSharedPreferences(Gcmpreference.PREFERENCE_NAME,
				Gcmpreference.MODE);
	}

	public static Editor getEditor(Context context) {
		return getPreference(context).edit();
	}

}
