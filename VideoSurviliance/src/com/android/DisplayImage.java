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
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class DisplayImage extends Activity {
	Button image_btn;
	EditText inputUrl;
	ImageView image_view;
	Bitmap b = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageex);
		
		inputUrl = ((EditText)findViewById(R.id.imageUrl));
		inputUrl.setText(DisplayImage.this.getResources().getString(R.string.ipaddress)+getSharedPreferences("filename", Context.MODE_PRIVATE).getString("filename", ""));
		inputUrl.setSingleLine();
		inputUrl.setTextSize(18);
		
		image_view = (ImageView)findViewById(R.id.imageView1);
		
		image_btn = (Button) findViewById(R.id.getImageButton);
		image_btn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (TextUtils.isEmpty(inputUrl.getText().toString())) {
					
				}
				else {
					
					new Thread(new Runnable() {
						
						public void run() {
							try {
								b = BitmapFactory.decodeStream((InputStream)new URL(inputUrl.getText().toString()).getContent());
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							handler.sendEmptyMessage(1);
						}
						
					}).start();
				}
			}
		});
	}
	
	Handler handler = new Handler() {
		
		public void handleMessage(Message msg) {
			image_view.setImageBitmap(b);
		}
	};
	
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

	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}

}










