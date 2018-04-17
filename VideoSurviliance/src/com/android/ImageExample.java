package com.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImageExample extends Activity {
	EditText inputUrl;
	Button image_btn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageex);

		inputUrl = ((EditText) findViewById(R.id.imageUrl));
		inputUrl.setText(R.string.ipaddress
				+ getSharedPreferences("filename", Context.MODE_PRIVATE)
						.getString("filename", ""));
		inputUrl.setSingleLine();
		inputUrl.setTextSize(18);

		image_btn = (Button) findViewById(R.id.getImageButton);

	}

	private Drawable ImageOperations(Context ctx, String url) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object fetch(String address) throws MalformedURLException,
			IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
}
