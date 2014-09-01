package com.weather.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smallweather.R;
import com.weather.adapter.ZiXunAdapter;
import com.weather.util.Constant;
import com.weather.util.FontManager;
import com.weather.util.HttpUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ZiXunActivity extends Activity implements OnTouchListener,
		OnGestureListener {
	private GestureDetector gestureDetector;
	private ListView listzixun;
	private LinearLayout viewZiXun;
	private int verticalMinDistance = 10;
	private int minVelocity = 0;
	private ImageView back;
	private String json;

	private MyHandler handler = new MyHandler();
	private String result;
	private JSONArray Jsonarr;
	private List<String> listTitle = new ArrayList<String>();
	private List<String> listHref = new ArrayList<String>();
	private SharedPreferences sp = null;
	private RelativeLayout zixuncolor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zixun);
		sp = getSharedPreferences("weather", Context.MODE_PRIVATE);
		int colorPo = sp.getInt("colorPosition", 0);
		zixuncolor = (RelativeLayout) findViewById(R.id.zixuncolor);
		setColor(colorPo);
		gestureDetector = new GestureDetector(this);
		back = (ImageView) findViewById(R.id.zixun_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ZiXunActivity.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
			}
		});
		viewZiXun = (LinearLayout) findViewById(R.id.viewZiXun);
		listzixun = (ListView) findViewById(R.id.list_zixun);
		listzixun.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v("wangqoqon",
						"   listHref.get(position)  = "
								+ listHref.get(position));
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ZiXunActivity.this, WebViewActivity.class);
				intent.putExtra("href", listHref.get(position));
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});
		listzixun.setOnTouchListener(this);
		listzixun.setLongClickable(true);
		viewZiXun.setOnTouchListener(this);
		viewZiXun.setLongClickable(true);

		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		FontManager.changeFonts(viewGroup, this);
		getData();

	}

	private void setColor(int colorPosition) {
		if (colorPosition == 0) {
			zixuncolor.setBackgroundColor(ZiXunActivity.this.getResources()
					.getColor(R.color.green));
		} else if (colorPosition == 1) {
			zixuncolor.setBackgroundColor(ZiXunActivity.this.getResources()
					.getColor(R.color.red));
		} else if (colorPosition == 2) {
			zixuncolor.setBackgroundColor(ZiXunActivity.this.getResources()
					.getColor(R.color.blue));
		} else if (colorPosition == 3) {
			zixuncolor.setBackgroundColor(ZiXunActivity.this.getResources()
					.getColor(R.color.purple));

		}
	}

	private void getData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HttpUtil httpUtil = new HttpUtil(ZiXunActivity.this);
				result = httpUtil.getJsonContent(Constant.newsUrl);
				httpUtil.saveFile(result, Constant.NEWS_FILE_NAME);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = result;
				handler.sendMessage(msg);

			}

		}).start();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if (e2.getX() - e1.getX() > verticalMinDistance
				&& Math.abs(velocityX) > minVelocity) {
			Intent intent = new Intent(ZiXunActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
			// finish();
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}

	public class MyHandler extends Handler {
		public void handleMessage(Message m) {
			switch (m.what) {
			case 0:
				json = (String) m.obj;
				try {
					Jsonarr = new JSONArray(json);
					Log.v("wang    ", "   " + Jsonarr);
					for (int i = 0; i < Jsonarr.length(); i++) {
						JSONObject obj = (JSONObject) Jsonarr.get(i);
						listTitle.add(obj.getString("title"));
						listHref.add(obj.getString("href"));
						Log.v("wang ", "   i = " + i + "Jsonarr.length()  =  "
								+ Jsonarr.length());
						if (i == Jsonarr.length() - 1) {
							Message msg = new Message();
							msg.what = 1;
							msg.obj = listTitle;
							handler.sendMessage(msg);
						}
						Log.v("wangqinqin",
								"   i  =  " + i + "  title =      "
										+ obj.getString("title")
										+ "   href  = " + obj.getString("href"));
					}

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			case 1:
				listzixun.setAdapter(new ZiXunAdapter(ZiXunActivity.this,
						listTitle, listHref));
				break;

			}
		}
	}

}
