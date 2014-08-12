package com.weather.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.smallweather.R;
import com.weather.adapter.MyAdapter;
import com.weather.util.HttpUtil;
import com.weather.util.MyApplication;

public class MainActivity extends Activity {
	private ListView listView;
	private TextView date;
	private Button setting;
	private String result;
	private List<String> list = new ArrayList<String>();
	private MyHandler handler = new MyHandler();
	private String json;
	private MyAdapter myAdapter;
	private SharedPreferences sp = null;
	private int colorP;
	private int cityP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.listView);
		listView.setEnabled(false);

		sp = getSharedPreferences("weather", Context.MODE_PRIVATE);

		if (((MyApplication) this.getApplication()).getFlag()) {
			Editor editor = sp.edit();
			editor.putInt("cityPosition", 2);
			editor.commit();
			getData(true);
			getWindow().setBackgroundDrawableResource(R.color.red);
			((MyApplication) MainActivity.this.getApplication()).setFlag(false);
		} else {
			getData(false);
			Intent intent = getIntent();
			boolean changeColor = intent.getBooleanExtra("changeColor", false);
			if (changeColor) {
				int colorPosition = intent.getIntExtra("colorPosition", 0);
				Editor editor = sp.edit();
				editor.putInt("colorPosition", colorPosition);
				editor.commit();
				if (colorPosition == 0) {
					getWindow().setBackgroundDrawableResource(R.color.red);
				} else if (colorPosition == 1) {
					getWindow().setBackgroundDrawableResource(R.color.green);
				} else if (colorPosition == 2) {
					getWindow().setBackgroundDrawableResource(R.color.blue);
				}else if (colorPosition == 3) {
					getWindow().setBackgroundDrawableResource(R.color.purple);
				} else if (colorPosition == 4) {
					getWindow().setBackgroundDrawableResource(R.color.rose);
				}
			} else {
				colorP = sp.getInt("colorPosition", 0);
				if (colorP == 0) {
					getWindow().setBackgroundDrawableResource(R.color.red);
				} else if (colorP == 1) {
					getWindow().setBackgroundDrawableResource(R.color.green);
				} else if (colorP == 2) {
					getWindow().setBackgroundDrawableResource(R.color.blue);
				} else if (colorP == 3) {
					getWindow().setBackgroundDrawableResource(R.color.purple);
				}else if (colorP == 4) {
					getWindow().setBackgroundDrawableResource(R.color.rose);
				}
			}
		}
		date = (TextView) findViewById(R.id.date);
		getDate();
		setting = (Button) findViewById(R.id.setting);
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intents = new Intent();
				intents.setClass(MainActivity.this, SettingActivity.class);
				MainActivity.this.finish();
				startActivity(intents);

			}
		});
		

	}

	private void getDate() {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DATE);
		date.setText(year + "年" + month + "月" + day + "日");
	}

	private void getData(final boolean f) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HttpUtil httpUtil = new HttpUtil(MainActivity.this);
				if (f) {
					result = httpUtil
							.getJsonContent("http://dev.365jinbi.com/weather/");
					json = result;
					// if (f) {
					Message msg = new Message();
					msg.what = 0;
					msg.obj = result;
					handler.sendMessage(msg);
				} else {
					Intent intent = getIntent();
					boolean changeCity = intent.getBooleanExtra("changeCity",
							false);
					try {
						result = httpUtil.getName();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (changeCity) {
						int cityPosition = intent
								.getIntExtra("cityPosition", 0);
						Editor editor = sp.edit();
						editor.putInt("cityPosition", cityPosition);
						editor.commit();
						Message msg = new Message();
						msg.what = 1;
						msg.arg1 = cityPosition;
						msg.obj = result;
						handler.sendMessage(msg);
					} else {
						cityP = sp.getInt("cityPosition", 0);
						Message msg = new Message();
						msg.what = 1;
						msg.arg1 = cityP;
						msg.obj = result;
						handler.sendMessage(msg);
					}

				}

			}
		}).start();
	}

	public class MyHandler extends Handler {
		public void handleMessage(Message m) {
			switch (m.what) {
			case 0:
				json = (String) m.obj;
				JSONArray jsonArr;
				JSONObject obj;
				List<String> list = new ArrayList<String>();
				try {
					jsonArr = new JSONArray(json);
					obj = (JSONObject) jsonArr.get(2);
					list.add(obj.getString("city"));
					list.add(obj.getString("type"));
					list.add(obj.getString("temperature"));
					list.add(obj.getString("wind_direction")+" "+obj.getString("wind_force"));
//					list.add(obj.getString("wind_force"));

					myAdapter = new MyAdapter(MainActivity.this, list, true);
					listView.setAdapter(myAdapter);
					myAdapter.notifyDataSetChanged();

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;

			case 1:
				json = (String) m.obj;
				int cityP = m.arg1;
				Log.v("wang", "   position  handler  =" + cityP);
				List<String> list1 = new ArrayList<String>();
				try {
					jsonArr = new JSONArray(json);
					obj = (JSONObject) jsonArr.get(cityP);
					list1.add(obj.getString("city"));
					list1.add(obj.getString("type"));
					list1.add(obj.getString("temperature"));
					list1.add(obj.getString("wind_direction")+"  "+obj.getString("wind_force"));
//					list1.add(obj.getString("wind_force"));
					myAdapter = new MyAdapter(MainActivity.this, list1, true);
					listView.setAdapter(myAdapter);
					myAdapter.notifyDataSetChanged();

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;

			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		MainActivity.this.finish();
		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
