package com.weather.view;

import com.smallweather.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class FeedBackSetting extends Activity {
	private ImageView back;
	private TextView yijian;
	private EditText edit;
	
	private InputMethodManager imm ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_setting);
		imm = (InputMethodManager) getApplicationContext()
                .getSystemService(this.INPUT_METHOD_SERVICE);
		back =(ImageView)findViewById(R.id.back_yijian);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(FeedBackSetting.this, MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				finish();
			}
		});
		yijian = (TextView)findViewById(R.id.yijian);
		edit = (EditText)findViewById(R.id.edit);
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(FeedBackSetting.this.getWindow().getAttributes().softInputMode ==WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED){
					   

//               imm.so;
				}
			}
		});
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(FeedBackSetting.this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
