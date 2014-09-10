package com.weather.util;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class ServiceUtils {
	public static void startWeatherService(Context context, Class<?> cls,
			String action) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		long time = 0;
		if (hour < 9) {
			time = 9 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - minute * 60
					* 1000 - second * 1000;
		} else {
			time = 33 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - minute * 60
					* 1000 - second * 1000;
		}
		long jiangeTime = 24 * 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + time;
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime,
				jiangeTime, pendingIntent);
	}

	public static void stopWeatherService(Context context, Class<?> cls,
			String action) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		manager.cancel(pendingIntent);
	}
}
