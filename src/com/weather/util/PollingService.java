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

public class PollingService extends Service {

	public static final String ACTION = "com.weather.util.PollingService";
	
	private Notification mNotification;
	private NotificationManager mManager;
	private SharedPreferences sp = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		sp = getSharedPreferences("weather", Context.MODE_PRIVATE);
//		initNotifiManager();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		showNotification();
//		new PollingThread().start();
	}

//	private void initNotifiManager() {
//		mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		int icon = R.drawable.logo;
//		mNotification = new Notification();
//		mNotification.icon = icon;
//		mNotification.tickerText = sp.getString("weather", "");
//		mNotification.defaults |= Notification.FLAG_ONGOING_EVENT;
////		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
//	}

	private void showNotification() {
		String title = sp.getString("title", "");
		String citys = sp.getString("citys", "");
		String message = sp.getString("weather", "");
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// Notification notification;
		// Intent intent;
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
		
		
		
		
		
		
//		
//		mNotification.when = System.currentTimeMillis();
//		//Navigator to the new activity when click the notification title
//		Intent i = new Intent(this, MainActivity.class);
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
//				Intent.FLAG_ACTIVITY_NEW_TASK);
//		mNotification.setLatestEventInfo(this,
//				getResources().getString(R.string.app_name), sp.getString("weather", ""), pendingIntent);
//		mManager.notify(0, mNotification);
	}


//	int count = 0;
//	class PollingThread extends Thread {
//		@Override
//		public void run() {
//			System.out.println("Polling...");
//			count ++;
//			if (count % 5 == 0) {
//				showNotification();
//				System.out.println("New message!");
//			}
//		}
//	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("Service:onDestroy");
	}

}
