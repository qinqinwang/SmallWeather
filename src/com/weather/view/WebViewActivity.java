package com.weather.view;

import com.smallweather.R;
import com.weather.util.FontManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class WebViewActivity extends Activity {
	private WebView webView;
	private TextView back;
	private SharedPreferences sp = null;
	private RelativeLayout webcolor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		sp = getSharedPreferences("weather", Context.MODE_PRIVATE);
		int colorPo = sp.getInt("colorPosition", 0);
		webcolor = (RelativeLayout)findViewById(R.id.webcolor);
		setColor(colorPo);
		webView = (WebView)findViewById(R.id.webView);
		back = (TextView)findViewById(R.id.web_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WebViewActivity.this,
						ZixunActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		});
		Intent intent = getIntent();
		webView.loadUrl(intent.getStringExtra("href"));
		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		FontManager.changeFonts(viewGroup, this);
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

}
