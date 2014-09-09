package com.weather.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smallweather.R;
import com.weather.adapter.ZiXunAdapter;
import com.weather.util.Constant;
import com.weather.util.FontManager;
import com.weather.util.HttpUtil;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

public class ZiXunActivity extends Activity implements OnTouchListener,
		OnGestureListener {
	private GestureDetector gestureDetector;
	private ListView listzixun;
	private LinearLayout viewZiXun;
	private int verticalMinDistance = 10;
	private int minVelocity = 0;
	private int number;
	private ImageView back;
	private String json;

	private MyHandler handler = new MyHandler();
	private String result;
	private JSONArray Jsonarr;
	private List<String> listTitle = new ArrayList<String>();
	private List<String> listId = new ArrayList<String>();
	private SharedPreferences sp = null;
	private RelativeLayout zixuncolor;
	private HttpUtil httpUtil;
	private int showtime = 5000;

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
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ZiXunActivity.this, WebViewActivity.class);
				intent.putExtra("id", listId.get(position));
				Log.v("wangqinqin", "   zixunId " + listId.get(position));
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});
		// listzixun.setOnTouchListener(this);
		// listzixun.setLongClickable(true);
		viewZiXun.setOnTouchListener(this);
		viewZiXun.setLongClickable(true);

		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		FontManager.changeFonts(viewGroup, this);
		httpUtil = new HttpUtil(ZiXunActivity.this);
		hasNet();

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

	private void hasNet() {
		// TODO Auto-generated method stub
		if (isNetworkAvailable(ZiXunActivity.this)) {
			getData();
		} else {
			if ("".equals(httpUtil.readFile(Constant.NEWS_FILE_NAME))
					|| httpUtil.readFile(Constant.NEWS_FILE_NAME) == null) {
				Toast.makeText(
						ZiXunActivity.this,
						ZiXunActivity.this.getResources().getString(
								R.string.nonet), showtime).show();
			} else {
				Toast.makeText(
						ZiXunActivity.this,
						ZiXunActivity.this.getResources().getString(
								R.string.linknet), showtime).show();
				Message msg = new Message();
				msg.what = 0;
				msg.obj = httpUtil.readFile(Constant.NEWS_FILE_NAME);
				handler.sendMessage(msg);
			}
		}
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

	private void getData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpUtil httpUtil = new HttpUtil(ZiXunActivity.this);
				result = httpUtil.getJsonContent(Constant.newsUrl);
				if(result != null&&!("".equals(result))){
					Log.v("wangqiniqn"," http.save");
					httpUtil.saveFile(result, Constant.NEWS_FILE_NAME);
				}
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
					for (int i = 0; i < Jsonarr.length(); i++) {
						JSONObject obj = (JSONObject) Jsonarr.get(i);
						listTitle.add(obj.getString("title"));
						listId.add(obj.getString("id"));
						if (i == Jsonarr.length() - 1) {
							Message msg = new Message();
							msg.what = 1;
							msg.obj = listTitle;
							handler.sendMessage(msg);
						}
					}
					if(Jsonarr.length()>0){
						number = new Random().nextInt(Jsonarr.length()) + 1;
						JSONObject objs = (JSONObject) Jsonarr.get(number);
						Editor editor = sp.edit();
						editor.putString("title", objs.getString("title"));
						editor.putString("id", objs.getString("id"));
						editor.commit();
						if (sp.getBoolean("showxianshi", false)) {
							ZiXunActivity.this.sendMessage(sp.getString("citys", "")
									+ sp.getString("message", ""),
									objs.getString("title"));
						}
					}
					
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			case 1:
				listzixun.setAdapter(new ZiXunAdapter(ZiXunActivity.this,
						listTitle, listId));
				break;
			}
		}
	}

	public void sendMessage(String citys, String message) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(R.drawable.logo, message,
				System.currentTimeMillis());
		Intent intent = new Intent(ZiXunActivity.this, WebViewActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				ZiXunActivity.this, 0, intent, 0);
		notification.setLatestEventInfo(getApplicationContext(), citys,
				message, pendingIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT;// 消息不可取消
		// notification.defaults = Notification.DEFAULT_SOUND;//声音默认
		manager.notify(0, notification);

	}

}
