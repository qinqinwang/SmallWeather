package com.weather.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.smallweather.R;
import com.weather.adapter.MyAdapter;
import com.weather.util.HttpUtil;

public class SettingActivity extends Activity{
	private ListView listColor;
	private ListView listCity;
	private List<String> listColors = new ArrayList<String>();
	private List<String> listCitys = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		listColor = (ListView)findViewById(R.id.list_color);
		listColor.setAdapter(new MyAdapter(this, getColor()));
		listCity = (ListView)findViewById(R.id.list_city);
		listCity.setAdapter(new MyAdapter(this, getCity()));
		

	}


	private List<String> getColor() {
		// TODO Auto-generated method stub
		listColors.add("白色");
		listColors.add("紫色");
		listColors.add("红色");
		return listColors;
	}


	private List<String> getCity() {
		// TODO Auto-generated method stub

		listCitys.add("嘉兴");
		listCitys.add("嘉善");
		listCitys.add("平湖");
		listCitys.add("桐乡");
		listCitys.add("海盐");
		listCitys.add("海宁");
		return listCitys;
	}

	

}
