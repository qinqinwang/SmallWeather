package com.weather.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.smallweather.R;
import com.weather.adapter.MyAdapter;

public class SettingActivity extends Activity {
	private ListView listColor;
	private ListView listCity;
	private List<String> listColors = new ArrayList<String>();
	private List<String> listCitys = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		listColor = (ListView) findViewById(R.id.list_color);
		listColor.setAdapter(new MyAdapter(this, getColor(),false));
		listCity = (ListView) findViewById(R.id.list_city);
		listCity.setAdapter(new MyAdapter(this, getCity(),false));
		listColor.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingActivity.this,
						MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("colorPosition", position);
				bundle.putBoolean("changeColor", true);
				bundle.putBoolean("changeCity", false);
				intent.putExtras(bundle);
				SettingActivity.this.finish();
				startActivity(intent);
			}
		});
		listCity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(SettingActivity.this,
						MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("cityPosition", position);
				bundle.putBoolean("changeCity", true);
				bundle.putBoolean("changeColor", false);
				intent.putExtras(bundle);
				SettingActivity.this.finish();
				startActivity(intent);
			}
		});

	}

	private List<String> getColor() {
		// TODO Auto-generated method stub
		listColors.add("黑色");
		listColors.add("蓝色");
		listColors.add("紫色");
		listColors.add("红色");
		return listColors;
	}

	private List<String> getCity() {
		// TODO Auto-generated method stub

		listCitys.add("嘉善");
		listCitys.add("平湖");
		listCitys.add("嘉兴");
		listCitys.add("桐乡");
		listCitys.add("海盐");
		listCitys.add("海宁");
		return listCitys;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(SettingActivity.this,
				MainActivity.class);
		Intent intents = new Intent();
		intents.setClass(SettingActivity.this, MainActivity.class);
		startActivity(intents);
		SettingActivity.this.finish();
		return super.onKeyDown(keyCode, event);
	}

}
