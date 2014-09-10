package com.weather.view;

import com.smallweather.R;
import com.weather.util.Config;
import com.weather.util.FontManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class WebViewActivity extends Activity {
	private WebView webView;
	private ImageView back;
	private SharedPreferences sp = null;
	private RelativeLayout webcolor;
	private String NewsId;
	private int showtime = 8000;
	private MyHandler handler = new MyHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		sp = getSharedPreferences("weather", Context.MODE_PRIVATE);
		initWebView();
		hasNet();
		initData();
		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		FontManager.changeFonts(viewGroup, this);
	}

	private void initWebView() {
		// TODO Auto-generated method stub
		webView = (WebView) findViewById(R.id.webView);
		WebSettings s = webView.getSettings();  
		//设置支持JavaScript脚本
		s.setJavaScriptEnabled(true); 
		back = (ImageView) findViewById(R.id.web_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WebViewActivity.this,
						ArticleActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		});
		int colorPo = sp.getInt("colorPosition", 0);
		webcolor = (RelativeLayout) findViewById(R.id.webcolor);
		setColor(colorPo);
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
			webView.loadUrl(Config.newsUrls + id);
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

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
		} else {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	private void hasNet() {
		// TODO Auto-generated method stub
		if (!isNetworkAvailable(WebViewActivity.this)) {
			Toast.makeText(
					WebViewActivity.this,
					WebViewActivity.this.getResources().getString(
							R.string.nowebnet), showtime).show();
		}
	}
	
	private void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		Log.v("wangqinqin", "   webId " + intent.getStringExtra("id")
				+ Config.newsUrls + intent.getStringExtra("id"));
		if (intent.getStringExtra("id") == null) {
			NewsId = sp.getString("id", "");
		} else {
			NewsId = intent.getStringExtra("id");
		}
		Message msg = new Message();
		msg.what = 0;
		msg.obj = NewsId;
		handler.sendMessage(msg);
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

}
