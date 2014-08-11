package com.weather.util;

import android.app.Application;

public class MyApplication extends Application {
	public static boolean flag = true; 

	public void setFlag(boolean f) {
		this.flag = f;
	}

	public boolean getFlag() {
		return this.flag;
	}
}
