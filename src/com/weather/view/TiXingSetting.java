package com.weather.view;

import com.smallweather.R;
import com.weather.util.FontManager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TiXingSetting extends Activity {
//	private ImageView back;
	private TextView xianshi;
	private TextView tianqi;
	private NotificationManager nm;
	private SharedPreferences sp = null;
	private int duration = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tixing_setting);
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);  
		sp = getSharedPreferences("weather", Context.MODE_PRIVATE);
		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		FontManager.changeFonts(viewGroup, this);
//		back = (ImageView) findViewById(R.id.back);
//		back.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(TiXingSetting.this, MainActivity.class);
//				startActivity(intent);
//				overridePendingTransition(R.anim.push_left_in,
//						R.anim.push_left_out);
//				finish();
//			}
//		});
//		tianqi = (TextView) findViewById(R.id.tianqi);
//		tianqi.setText(sp.getString("citys","")+sp.getString("message",""));
		xianshi = (TextView) findViewById(R.id.xianshi);
		xianshi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sp.getBoolean("show", false)){
					nm.cancel(0);
					Editor editor = sp.edit();
					editor.putBoolean("show", false);
					editor.commit();
					Toast.makeText(TiXingSetting.this, TiXingSetting.this.getResources().getString(
							R.string.bxianshi), duration).show();
				}else{
					sendMessage(sp.getString("citys",""),sp.getString("message",""));
					Editor editor = sp.edit();
					editor.putBoolean("show", true);
					editor.commit();
					Toast.makeText(TiXingSetting.this, TiXingSetting.this.getResources().getString(
							R.string.yxianshi), duration).show();
				}
				
			}
		});
//		shengyin = (TextView) findViewById(R.id.shengyin);
//		shengyin.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//			}
//		});
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(TiXingSetting.this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	public  void sendMessage(String citys, String message) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 构建一个通知对象(需要传递的参数有三个,分别是图标,标题和 时间)
		Notification notification = new Notification(R.drawable.logo, message,
				System.currentTimeMillis());
		Intent intent = new Intent(TiXingSetting.this, TiXingSetting.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				TiXingSetting.this, 0, intent, 0);
		notification.setLatestEventInfo(getApplicationContext(), citys,message,
				pendingIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT;//消息不可取消
		// notification.defaults = Notification.DEFAULT_SOUND;//声音默认
		manager.notify(0, notification);

	}
}
