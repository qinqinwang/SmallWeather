package com.weather.view;

import com.smallweather.R;
import com.weather.util.Config;
import com.weather.util.FontManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
						ArticleActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		});
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
		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		FontManager.changeFonts(viewGroup, this);
//		webView.setWebViewClient(new WebViewClient() {
//			@SuppressWarnings("unused")
//			public void onPageStarted(WebView view, String url) {
//				// TODO Auto-generated method stub
//				super.onPageStarted(view, url, null);
////				progressBar.setVisibility(android.view.View.VISIBLE);
//				 Toast.makeText(WebViewActivity.this, "onPageStarted", 2).show();
//			}
//
//			// 网页加载完成时调用，隐藏加载提示旋转进度条
//			@Override
//			public void onPageFinished(WebView view, String url) {
//				// TODO Auto-generated method stub
//				super.onPageFinished(view, url);
////				progressBar.setVisibility(android.view.View.GONE);
//				 Toast.makeText(WebViewActivity.this, "onPageFinished", 2).show();
//			}
//
//			// 网页加载失败时调用，隐藏加载提示旋转进度条
//			@Override
//			public void onReceivedError(WebView view, int errorCode,
//					String description, String failingUrl) {
//				// TODO Auto-generated method stub
//				super.onReceivedError(view, errorCode, description, failingUrl);
////				progressBar.setVisibility(android.view.View.GONE);
//				 Toast.makeText(WebViewActivity.this, "onReceivedError", 2).show();
//			}
//
//		});

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
//			webView.loadData(webView.loadUrl(Config.newsUrls + id), "text/html", "utf-8");
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
