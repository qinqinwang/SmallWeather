package com.weather.view;

import com.smallweather.R;
import com.weather.util.Constant;
import com.weather.util.FontManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class WebViewActivity extends Activity {
	private WebView webView;
	private ImageView back;
	private SharedPreferences sp = null;
	private RelativeLayout webcolor;
	private String NewsId;
	private MyHandler handler = new MyHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		sp = getSharedPreferences("weather", Context.MODE_PRIVATE);
		int colorPo = sp.getInt("colorPosition", 0);
		webcolor = (RelativeLayout) findViewById(R.id.webcolor);
		setColor(colorPo);
		webView = (WebView) findViewById(R.id.webView);
		back = (ImageView) findViewById(R.id.web_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WebViewActivity.this,
						ZiXunActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		});
		Intent intent = getIntent();
		Log.v("wangqinqin", "   webId " + intent.getStringExtra("id")
				+ Constant.newsUrls + intent.getStringExtra("id"));
		if (intent.getStringExtra("id") == null) {
			NewsId = sp.getString("id", "");
		} else {
			NewsId = intent.getStringExtra("id");
		}
		Message msg = new Message();
		msg.what = 0;
		msg.obj = NewsId;
		handler.sendMessage(msg);
		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		FontManager.changeFonts(viewGroup, this);
	}

	public class MyHandler extends Handler {
		public void handleMessage(Message m) {
			switch (m.what) {
			case 0:
				String id = (String) m.obj;
				LoadThread load = new LoadThread(id);
				load.start();
				break;

			}
		}
	}

	class LoadThread extends Thread {
		private String id;

		LoadThread(String id) {
			// TODO Auto-generated constructor stub
			this.id = id;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			webView.loadUrl(Constant.newsUrls + id);
		}

	}

	private void setColor(int colorPosition) {
		if (colorPosition == 0) {
			webcolor.setBackgroundColor(WebViewActivity.this.getResources()
					.getColor(R.color.green));
		} else if (colorPosition == 1) {
			webcolor.setBackgroundColor(WebViewActivity.this.getResources()
					.getColor(R.color.red));
		} else if (colorPosition == 2) {
			webcolor.setBackgroundColor(WebViewActivity.this.getResources()
					.getColor(R.color.blue));
		} else if (colorPosition == 3) {
			webcolor.setBackgroundColor(WebViewActivity.this.getResources()
					.getColor(R.color.purple));

		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		finish();
		super.onStop();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		finish();
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
