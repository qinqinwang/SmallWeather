package com.weather.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smallweather.R;
import com.weather.adapter.MyAdapter;
import com.weather.bean.SDPATH;
import com.weather.util.AndroidUtil;
import com.weather.util.Constant;
import com.weather.util.FontManager;
import com.weather.util.HttpUtil;
import com.weather.util.TestUtils;
import com.weather.view.MainActivity.MyHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ZixunActivity extends Activity implements OnTouchListener,
		OnGestureListener {
	private GestureDetector gestureDetector;
	private List<String> listZixun = new ArrayList<String>();
	private ListView listzixun;
	private LinearLayout viewZiXun;
	private int verticalMinDistance = 10;
	private int minVelocity = 0;
	private ImageView back;
	private String json;

	private MyHandler handler = new MyHandler();
	private String result;
	private JSONArray Jsonarr;
	private SpannableString ss;
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
		zixuncolor = (RelativeLayout)findViewById(R.id.zixuncolor);
		setColor(colorPo);
		gestureDetector = new GestureDetector(this);
		back = (ImageView) findViewById(R.id.zixun_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ZixunActivity.this,
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
				Log.v("wangqoqon", "   listHref.get(position)  = "+listHref.get(position));
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ZixunActivity.this, WebViewActivity.class);
//				Bundle b = new Bundle();
//				b.putString("href", listHref.get(position));
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
			zixuncolor.setBackgroundColor(ZixunActivity.this.getResources()
					.getColor(R.color.green));
		} else if (colorPosition == 1) {
			zixuncolor.setBackgroundColor(ZixunActivity.this.getResources()
					.getColor(R.color.red));
		} else if (colorPosition == 2) {
			zixuncolor.setBackgroundColor(ZixunActivity.this.getResources()
					.getColor(R.color.blue));
		} else if (colorPosition == 3) {
			zixuncolor.setBackgroundColor(ZixunActivity.this.getResources()
					.getColor(R.color.purple));

		}
	}
	private void getData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HttpUtil httpUtil = new HttpUtil(ZixunActivity.this);
				result = httpUtil
						.getJsonContent(Constant.newsUrl);
				httpUtil.saveFile(result,Constant.NEWS_FILE_NAME);
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
			Intent intent = new Intent(ZixunActivity.this, MainActivity.class);
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
						// StrikethroughSpan span = new StrikethroughSpan();
						// ss = new SpannableString(obj.getString("title"));
						//
						// // ss.setSpan(new StrikethroughSpan(), 0,
						// // obj.getString("title").length(),
						// // Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						// // ss.setSpan(new BackgroundColorSpan(Color.WHITE),
						// 0,
						// // obj.getString("title").length(),
						// // Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						// ss.setSpan(new MyURLSpan(obj.getString("href")), 0,
						// obj
						// .getString("title").length(),
						// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						// ss.setSpan(new URLSpan(obj.getString("href")), 0, obj
						// .getString("title").length(),
						// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						// zixun.append(ss);
						// zixun.setText(ss);
						// zixun.setMovementMethod(LinkMovementMethod
						// .getInstance());
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
				listzixun.setAdapter(new MyAdapter(ZixunActivity.this,
						listTitle, false,listHref));
				break;

			}
		}
	}

	private class MyURLSpan extends ClickableSpan {
		private String mUrl;

		MyURLSpan(String url) {
			mUrl = url;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(Color.parseColor("#034198")); // 设置超链接颜色
			ds.setUnderlineText(false); // 超链接去掉下划线
		}

		@Override
		public void onClick(View widget) {
			// 判断是否热门话题
			// if (mUrl.startsWith("#") && mUrl.endsWith("#")) {
			// openHotTopic(mUrl.substring(1, mUrl.length()-1));
			// }else { //@用户名
			// openOtherUserInfoByName(mUrl);
			// }
		}
	}
	// private class NoLineClickSpan extends ClickableSpan {
	// String text;
	//
	// public NoLineClickSpan(String text) {
	// super();
	// this.text = text;
	// }
	//
	// @Override
	// public void updateDrawState(TextPaint ds) {
	// ds.setColor(ds.linkColor);
	// ds.setUnderlineText(false); //去掉下划线
	// }
	//
	// @Override
	// public void onClick(View widget) {
	// this.processHyperLinkClick(text); //点击超链接时调用
	// }
	// }
	// private void setZixun() {
	// try {
	// jsonArr = new JSONArray(json);
	// for(int i= 0;i<jsonArr.length();i++){
	//
	//
	// obj = (JSONObject) jsonArr.get(i);
	// Log.v("wangqinqin",
	// "     "+obj.getString("title")+"   href  = "+obj.getString("href"));
	//
	//
	// }
	// } catch (JSONException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// }

}
