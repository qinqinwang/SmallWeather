package com.weather.view;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.LightingColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.smallweather.R;
import com.weather.adapter.MyAdapter;
import com.weather.util.FontManager;
import com.weather.util.HttpUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity {
	private TextView date, city, type, temperature, wind;
	private ImageButton setting;
	private String result;
	private String reads;
	private String dates;
	private MyHandler handler = new MyHandler();
	private String json;
	private String fileResult;
	private SharedPreferences sp = null;
	private ListView listColor;
	private ListView listCity;
	private List<String> listColors = new ArrayList<String>();
	private List<String> listCitys = new ArrayList<String>();
	private RelativeLayout viewMain;
	private SlidingMenu menu;

	private final static String FILE_NAME = "weather.txt";

	JSONArray jsonArr;
	JSONObject obj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.activity_setting);
		sp = getSharedPreferences("weather", Context.MODE_PRIVATE);
		setView();
		hasNet();
		getDate();
		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		FontManager.changeFonts(viewGroup, this);
	}

	private void hasNet() {
		// TODO Auto-generated method stub
		if(isNetworkAvailable(MainActivity.this)){
//			getDate();
			getData();
		}else{
			if("".equals(readFile())||readFile() ==null){
				Toast.makeText(MainActivity.this, "无网络，无法获取数据", 8000).show();
				Message msg = new Message();
				msg.what = 4;
				handler.sendMessage(msg);
			}else{
				Toast.makeText(MainActivity.this, "连接网络，可更新数据", 8000).show();
				Message msg = new Message();
				msg.what = 0;
				msg.obj = readFile();
				handler.sendMessage(msg);	
			}
		}
	}

	private void setView() {
		// TODO Auto-generated method stub
		viewMain = (RelativeLayout) findViewById(R.id.viewMain);
		date = (TextView) findViewById(R.id.date);
		city = (TextView) findViewById(R.id.city);
		type = (TextView) findViewById(R.id.type);
		temperature = (TextView) findViewById(R.id.temperature);
		wind = (TextView) findViewById(R.id.wind);
		setting = (ImageButton) findViewById(R.id.setting);
		setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.this.getMenu().toggle();

			}
		});

		listColor = (ListView) findViewById(R.id.list_color);
		listColor.setAdapter(new MyAdapter(this, getColor()));
		listColor.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Editor editor = sp.edit();
				editor.putInt("colorPosition", position);
				editor.commit();
				Message msg = new Message();
				msg.what = 2;
				msg.arg1 = position;
				Log.v("wang", "onItemClick" + position);
				handler.sendMessage(msg);

			}
		});
		listCity = (ListView) findViewById(R.id.list_city);
		listCity.setAdapter(new MyAdapter(this, getCity()));
		listCity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(isNetworkAvailable(MainActivity.this)){
					Editor editor = sp.edit();
					editor.putInt("cityPosition", position);
					editor.commit();
					Message msg = new Message();
					msg.what = 3;
					msg.arg1 = position;
					handler.sendMessage(msg);
				}else{
					if("".equals(readFile())||readFile() ==null){
						Toast.makeText(MainActivity.this, "请连接网络", 8000).show();
						Message msg = new Message();
						msg.what = 4;
						handler.sendMessage(msg);
					}else{
						Editor editor = sp.edit();
						editor.putInt("cityPosition", position);
						editor.commit();
						Message msg = new Message();
						msg.what = 3;
						msg.arg1 = position;
						handler.sendMessage(msg);	
					}
				}
			}
		});
	}

	public SlidingMenu getMenu() {
		return menu;
	}

	private void getDate() {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DATE);
		dates = month + "." + day;
		Editor editor = sp.edit();
		editor.putString("date", dates);
		editor.commit();
		date.setText(dates);
	}

	private void getData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				    
					HttpUtil httpUtil = new HttpUtil(MainActivity.this);
					result = httpUtil
							.getJsonContent("http://dev.365jinbi.com/weather/");
					saveFile(result);
					
					Calendar calendar = Calendar.getInstance();
					int year = calendar.get(Calendar.YEAR);
					int month = calendar.get(Calendar.MONTH) + 1;
					int day = calendar.get(Calendar.DATE);
					dates = month + "." + day;
					if (sp.getInt("tag", 0) == 0) {
						Editor editor = sp.edit();
						editor.putString("date", dates);
						editor.putInt("tag", 1);
						editor.commit();
						Message msg = new Message();
						msg.what = 1;
						msg.obj = result;
						handler.sendMessage(msg);
					} else {
						Message msg = new Message();
						msg.what = 0;
						msg.obj = result;
						handler.sendMessage(msg);
					}
					Editor editors = sp.edit();
					editors.putString("date", dates);
					editors.commit();
					

				}

			
		}).start();
	}

	public class MyHandler extends Handler {
		public void handleMessage(Message m) {
			switch (m.what) {
			case 0:
				json = (String) m.obj;
				int cityPo = sp.getInt("cityPosition", 0);
				setCity(cityPo);
				int colorPo = sp.getInt("colorPosition", 0);
				setColor(colorPo);
				break;
			case 1:
				viewMain.setBackgroundColor(MainActivity.this.getResources()
						.getColor(R.color.green));
				json = (String) m.obj;
				setCity(2);
				break;

			case 2:
				int colorPosition = m.arg1;
				setColor(colorPosition);
				break;
			case 3:
				int cityPosition = m.arg1;
				setCity(cityPosition);
				break;
			case 4:
				int colorPos = sp.getInt("colorPosition", 0);
				setColor(colorPos);
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

	private List<String> getColor() {
		// TODO Auto-generated method stub

		listColors.add("绿");
		listColors.add("红");
		listColors.add("蓝");
		listColors.add("紫");
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
	
	
	public static boolean isNetworkAvailable(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        if (cm == null) {   
        } else {  
            NetworkInfo[] info = cm.getAllNetworkInfo();   
            if (info != null) {   
                for (int i = 0; i < info.length; i++) {   
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
                        return true;   
                    }   
                }   
            }   
        }   
        return false;   
    }
	private void setColor(int colorPosition) {
		if (colorPosition == 0) {
			viewMain.setBackgroundColor(MainActivity.this
					.getResources().getColor(R.color.green));
		} else if (colorPosition == 1) {
			viewMain.setBackgroundColor(MainActivity.this
					.getResources().getColor(R.color.red));
		} else if (colorPosition == 2) {
			viewMain.setBackgroundColor(MainActivity.this
					.getResources().getColor(R.color.blue));
		} else if (colorPosition == 3) {
			viewMain.setBackgroundColor(MainActivity.this
					.getResources().getColor(R.color.purple));
		}
	}
	private void setCity(int cityPosotion){
		try {
			jsonArr = new JSONArray(json);
			obj = (JSONObject) jsonArr.get(cityPosotion);
			city.setText(obj.getString("city"));
			type.setText(obj.getString("type"));
			temperature.setText(obj.getString("temperature"));
			wind.setText(obj.getString("wind_direction") + " "
					+ obj.getString("wind_force"));

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	

	private void saveFile(String str) {
		FileOutputStream fos;
		try {
			fos = this.openFileOutput(FILE_NAME, MODE_PRIVATE);
			fos.write(str.getBytes());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String readFile(){
		try {
			FileInputStream fis =openFileInput(FILE_NAME);
			byte[] b = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while(fis.read(b)!= -1){
				baos.write(b,0,b.length);
			}
			baos.close();
			fis.close();
			reads = baos.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reads;
		
	}
}
