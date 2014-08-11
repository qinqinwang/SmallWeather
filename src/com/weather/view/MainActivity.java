package com.weather.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.smallweather.R;
import com.weather.adapter.MyAdapter;
import com.weather.util.HttpUtil;

public class MainActivity extends Activity{
	private ListView listView;
	private TextView date;
	private Button setting;
	private String result;
	private List<String> list = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setBackgroundDrawableResource(R.color.blue);
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(new MyAdapter(this, getData()));
		date = (TextView) findViewById(R.id.date);
		getDate();
		setting = (Button) findViewById(R.id.setting);
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, SettingActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_out,
						R.anim.push_left_in);
				MainActivity.this.finish();

			}
		});

	}

	private void getDate() {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		date.setText(month + "." + day);
	}

	private List<String> getData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpUtil httpUtil = new HttpUtil(MainActivity.this);
				result = httpUtil
						.getJsonContent("http://dev.365jinbi.com/weather/");
				String json = result;
				Log.v("wang", "   result  =" + result);
				JSONArray jsonArr;
				JSONObject obj;

				try {
					jsonArr = new JSONArray(json);
					obj = (JSONObject) jsonArr.get(2);
					list.add(obj.getString("city"));
					list.add(obj.getString("type"));
					list.add(obj.getString("temperature"));
					list.add(obj.getString("wind_direction"));
					list.add(obj.getString("wind_force"));
					Log.v("wang", "   city0  =" + ((JSONObject) jsonArr.get(0)).getString("city"));
					Log.v("wang", "   city1  =" + ((JSONObject) jsonArr.get(1)).getString("city"));
					Log.v("wang", "   city2  =" + ((JSONObject) jsonArr.get(2)).getString("city"));
					Log.v("wang", "   city3  =" + ((JSONObject) jsonArr.get(3)).getString("city"));
					Log.v("wang", "   city4  =" + ((JSONObject) jsonArr.get(4)).getString("city"));
					Log.v("wang", "   city5  =" + ((JSONObject) jsonArr.get(5)).getString("city"));
					// for (int i = 0; i < jsonArr.length(); i++) {
					// obj = (JSONObject) jsonArr.getssss(i);
					// list.add(obj.getString("city"));
					// list.add(obj.getString("type"));
					// list.add(obj.getString("temperature"));
					// list.add(obj.getString("wind_direction"));
					// list.add(obj.getString("wind_force"));
					// }

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}).start();

		return list;
	}

	
}
