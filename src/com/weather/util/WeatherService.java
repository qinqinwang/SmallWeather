package com.weather.util;

import com.smallweather.R;
import com.weather.view.MainActivity;
import com.weather.view.WebViewActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class WeatherService extends Service {
	private SharedPreferences sp = null;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		sp = getSharedPreferences("weather", Context.MODE_PRIVATE);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		showNotification();
	}
	private void showNotification() {
		String title = sp.getString("title", "");
		String citys = sp.getString("citys", "");
		String message = sp.getString("weather", "");
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (title == null || "".equals(title)) {
			Notification notification = new Notification(R.drawable.logo,
					message, System.currentTimeMillis());
			Intent intent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(
					this, 0, intent, 0);
			notification.setLatestEventInfo(getApplicationContext(), citys,
					message, pendingIntent);
			notification.flags = Notification.FLAG_ONGOING_EVENT;// 消息不可取消
			// notification.defaults = Notification.DEFAULT_SOUND;//声音默认
			manager.notify(0, notification);
		} else {
			Notification notification = new Notification(R.drawable.logo,
					title, System.currentTimeMillis());
			Intent intent = new Intent(this, WebViewActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(
					this, 0, intent, 0);
			notification.setLatestEventInfo(getApplicationContext(), citys
					+ message, title, pendingIntent);
			notification.flags = Notification.FLAG_ONGOING_EVENT;// 消息不可取消
			// notification.defaults = Notification.DEFAULT_SOUND;//声音默认
			manager.notify(0, notification);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
