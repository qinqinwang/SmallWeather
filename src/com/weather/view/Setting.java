package com.weather.view;

import com.smallweather.R;
import com.weather.util.FontManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Setting extends Activity{
	private TextView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tixing_setting);
		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		FontManager.changeFonts(viewGroup, this);
		back = (TextView)findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(Setting.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(Setting.this, MainActivity.class);
		startActivity(intent);
		finish();
		return super.onKeyDown(keyCode, event);
	}

}
