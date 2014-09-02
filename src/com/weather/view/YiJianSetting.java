package com.weather.view;

import com.smallweather.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class YiJianSetting extends Activity{
	private ImageView back;
	private TextView yijian;
	private EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yijian_setting);
		back =(ImageView)findViewById(R.id.back_yijian);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(YiJianSetting.this, MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				finish();
			}
		});
		yijian = (TextView)findViewById(R.id.yijian);
		edit = (EditText)findViewById(R.id.edit);
	}
}
