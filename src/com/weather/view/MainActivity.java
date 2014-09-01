package com.weather.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.smallweather.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.umeng.analytics.MobclickAgent;
import com.weather.adapter.MyAdapter;
import com.weather.bean.SDPATH;
import com.weather.util.AndroidUtil;
import com.weather.util.Constant;
import com.weather.util.FontManager;
import com.weather.util.HttpUtil;
import com.weather.util.TestUtils;

public class MainActivity extends Activity implements OnTouchListener,
		OnGestureListener {
	private TextView date, city, type, temperature, wind;
	// private ImageButton setting;
	private String result;
	private String dates;
	private MyHandler handler = new MyHandler();
	private String json;
	private SharedPreferences sp = null;
	private ListView listCity;

	private RelativeLayout viewMain;
	private SlidingMenu menu;

	private JSONArray jsonArr;
	private JSONObject obj;
	private NotificationManager nm;
	private final String APP_ID = "wxc1166aff17ba799b";
	private IWXAPI wxApi;
	private ImageButton share;
	private SelectPopupWindow menuWindow;

	private int vercode;

	private String appurl, fileName;
	private ProgressBar progress;
	private int width;
	private int height;

	private TextView beijing;
	private TextView tixing;

	private GestureDetector gestureDetector;
	private int verticalMinDistance = 10;
	private int minVelocity = 0;
	private HttpUtil httpUtil;
	private int showtime = 5000; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		wxApi = WXAPIFactory.createWXAPI(this, APP_ID, true);
		wxApi.registerApp(APP_ID);
		// 存放路径设置
		SDPATH.sdcardExit = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);// 判断SDCard是否存在
		SDPATH.sdCardPer = com.weather.util.SDPermission.checkFsWritable();// 判断SDCard权限
		if (SDPATH.sdcardExit) {
			if (!SDPATH.sdCardPer) {
				SDPATH.SD_PATH = this.getCacheDir().toString();
			} else {
				SDPATH.SD_PATH = Environment.getExternalStorageDirectory()
						.getPath() + "/cleaning/";
			}
		} else {
			SDPATH.SD_PATH = this.getCacheDir().toString();
		}
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// menu.setSecondaryMenu(R.layout.activity_zixun);
		menu.setMenu(R.layout.activity_setting);
		sp = getSharedPreferences("weather", Context.MODE_PRIVATE);

		httpUtil = new HttpUtil(MainActivity.this);
		gestureDetector = new GestureDetector(this);
		setView();
		hasNet();
		getDate();
		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		FontManager.changeFonts(viewGroup, this);

		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// 更新
		// Update();
		MobclickAgent.setDebugMode(true);
	}

	public void send(String citys, String s) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 构建一个通知对象(需要传递的参数有三个,分别是图标,标题和 时间)
		Notification notification = new Notification(R.drawable.logo, s,
				System.currentTimeMillis());
		Intent intent = new Intent(MainActivity.this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				MainActivity.this, 0, intent, 0);
		notification.setLatestEventInfo(getApplicationContext(), citys, s,
				pendingIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT;//消息不可取消
		// notification.defaults = Notification.DEFAULT_SOUND;//声音默认
		manager.notify(0, notification);

	}

	private void hasNet() {
		// TODO Auto-generated method stub
		if (isNetworkAvailable(MainActivity.this)) {
			// getDate();
			getData();
		} else {
			if ("".equals(httpUtil.readFile(Constant.WEATHER_FILE_NAME))
					|| httpUtil.readFile(Constant.WEATHER_FILE_NAME) == null) {
				Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(
						R.string.nonet), showtime).show();
				Message msg = new Message();
				msg.what = 4;
				handler.sendMessage(msg);
			} else {
				Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(
						R.string.linknet), showtime).show();
				Message msg = new Message();
				msg.what = 0;
				msg.obj = httpUtil.readFile(Constant.WEATHER_FILE_NAME);
				handler.sendMessage(msg);
			}
		}
	}

	private void setView() {
		// TODO Auto-generated method stub
		viewMain = (RelativeLayout) findViewById(R.id.viewMain);
		viewMain.setOnTouchListener(this);
		viewMain.setLongClickable(true);
		date = (TextView) findViewById(R.id.date);
		city = (TextView) findViewById(R.id.city);
		type = (TextView) findViewById(R.id.type);
		temperature = (TextView) findViewById(R.id.temperature);
		wind = (TextView) findViewById(R.id.wind);

		beijing = (TextView) findViewById(R.id.beijing);
		beijing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// showPopupWindow(false);
				menuWindow = new SelectPopupWindow(MainActivity.this, null,
						false, onitemsOnClicks);
				// 显示窗口
				menuWindow.showAtLocation(
						MainActivity.this.findViewById(R.id.main),
						Gravity.BOTTOM, 0, 0); //

			}
		});
		tixing = (TextView) findViewById(R.id.tixing);
		tixing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, Setting.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in,
						R.anim.push_right_out);
				finish();
			}
		});
		listCity = (ListView) findViewById(R.id.list_city);
		listCity.setAdapter(new MyAdapter(this, this.getResources()
				.getStringArray(R.array.cityitem)));
		listCity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				MainActivity.this.getMenu().toggle();
				if (isNetworkAvailable(MainActivity.this)) {
					Editor editor = sp.edit();
					editor.putInt("cityPosition", position);
					editor.commit();
					Message msg = new Message();
					msg.what = 3;
					msg.arg1 = position;
					handler.sendMessage(msg);
				} else {
					if ("".equals(httpUtil.readFile(Constant.WEATHER_FILE_NAME))
							|| httpUtil.readFile(Constant.WEATHER_FILE_NAME) == null) {
						Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(
								R.string.linknets), showtime).show();
						Message msg = new Message();
						msg.what = 4;
						handler.sendMessage(msg);
					} else {
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

		share = (ImageButton) findViewById(R.id.share);
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menuWindow = new SelectPopupWindow(MainActivity.this,
						itemsOnClick, true, null);
				// 显示窗口
				menuWindow.showAtLocation(
						MainActivity.this.findViewById(R.id.main),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //
			}
		});

		progress = (ProgressBar) findViewById(R.id.progress);
	}

	private OnItemClickListener onitemsOnClicks = new OnItemClickListener() {

		@SuppressLint("ResourceAsColor")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			MainActivity.this.getMenu().toggle();
			Editor editor = sp.edit();
			editor.putInt("colorPosition", position);
			editor.commit();
			Message msg = new Message();
			msg.what = 2;
			msg.arg1 = position;
			handler.sendMessage(msg);
			menuWindow.dismiss();
		}

	};

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
			Uri uri = shotScreen();
			switch (v.getId()) {
			case R.id.share_friend:

				if (isNetworkAvailable(MainActivity.this)) {
					if (checkApkExist(Constant.pkgweixin)) {
						share(uri,Constant.pkgweixin,Constant.weixinfriend);
					} else {
						Toast.makeText(
								MainActivity.this,
								MainActivity.this.getResources().getString(
										R.string.noweixin), showtime).show();
					}
				} else {
					if ("".equals(httpUtil.readFile(Constant.WEATHER_FILE_NAME))
							|| httpUtil.readFile(Constant.WEATHER_FILE_NAME) == null) {
						Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(
								R.string.linknets), showtime).show();
					} else {
						if (checkApkExist(Constant.pkgweixin)) {
							share(uri,Constant.pkgweixin,Constant.weixinfriend);
						} else {
							Toast.makeText(
									MainActivity.this,
									MainActivity.this.getResources().getString(
											R.string.noweixin), showtime).show();
						}
					}
				}
				break;
			case R.id.share_circle:
				if (isNetworkAvailable(MainActivity.this)) {
					if (checkApkExist(Constant.pkgweixin)) {
						share(uri,Constant.pkgweixin,Constant.weixincircle);
					} else {
						Toast.makeText(
								MainActivity.this,
								MainActivity.this.getResources().getString(
										R.string.noweixin), showtime).show();
					}

				} else {
					if ("".equals(httpUtil.readFile(Constant.WEATHER_FILE_NAME))
							|| httpUtil.readFile(Constant.WEATHER_FILE_NAME) == null) {
						Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(
								R.string.linknets), showtime).show();
					} else {
						if (checkApkExist(Constant.pkgweixin)) {
							share(uri,Constant.pkgweixin,Constant.weixincircle);
						} else {
							Toast.makeText(
									MainActivity.this,
									MainActivity.this.getResources().getString(
											R.string.noweixin), showtime).show();
						}
					}
				}

				break;
			case R.id.share_weibo:
				if (isNetworkAvailable(MainActivity.this)) {
					if (checkApkExist(Constant.pkgweibo)) {
						share(uri,Constant.pkgweibo,Constant.weibowhere);
					} else {
						Toast.makeText(
								MainActivity.this,
								MainActivity.this.getResources().getString(
										R.string.noweibo), showtime).show();
					}

				} else {
					if ("".equals(httpUtil.readFile(Constant.WEATHER_FILE_NAME))
							|| httpUtil.readFile(Constant.WEATHER_FILE_NAME) == null) {
						Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(
								R.string.linknets), showtime).show();
					} else {
						if (checkApkExist(Constant.pkgweibo)) {
							share(uri,Constant.pkgweibo,Constant.weibowhere);
						} else {
							Toast.makeText(
									MainActivity.this,
									MainActivity.this.getResources().getString(
											R.string.noweibo), showtime).show();
						}
					}
				}

				break;
			default:
				break;
			}

		}

	};

	private boolean checkApkExist(String packageName) {
		if (packageName == null || "".equals(packageName)) {
			return false;
		}
		try {
			ApplicationInfo info = this.getPackageManager().getApplicationInfo(
					packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}

	// 截图
	public Uri shotScreen() {
		// View是你需要截图的View
		View view = this.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		// 获取屏幕长和高
		width = this.getWindowManager().getDefaultDisplay().getWidth();
		height = this.getWindowManager().getDefaultDisplay().getHeight();
		// 去掉标题栏
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();

		FileOutputStream Os;
		try {
			Os = this.openFileOutput("Img" + ".jpg",
					Context.MODE_WORLD_READABLE);
			b.compress(Bitmap.CompressFormat.JPEG, 100, Os);
			Os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File F = this.getFileStreamPath("Img" + ".jpg");
		Uri u = Uri.fromFile(F);

		return u;
	}

	public File shotScree() {
		View view = this.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		width = this.getWindowManager().getDefaultDisplay().getWidth();
		height = this.getWindowManager().getDefaultDisplay().getHeight();

		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();

		FileOutputStream Os;
		try {
			Os = this.openFileOutput("Img" + ".png",
					Context.MODE_WORLD_READABLE);
			b.compress(Bitmap.CompressFormat.JPEG, 100, Os);
			Os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File F = this.getFileStreamPath("Img" + ".png");
		Uri u = Uri.fromFile(F);

		return F;
	}
	
	private void share(Uri u ,String pkgName,String where) {
		Intent intent = new Intent();
		ComponentName comp = new ComponentName(pkgName,
				where);
		intent.setComponent(comp);
		intent.setAction("android.intent.action.SEND");
		// intent.putExtra("Kdescription", weather);
		intent.setType("image/*");
		// intent.putExtra(Intent.EXTRA_TEXT, weather);
		intent.putExtra(Intent.EXTRA_STREAM, u);
		startActivity(intent);
	}

//	private void shareToFriend(Uri u) {
//		Intent intent = new Intent();
//		ComponentName comp = new ComponentName("com.tencent.mm",
//				"com.tencent.mm.ui.tools.ShareImgUI");
//		intent.setComponent(comp);
//		intent.setAction("android.intent.action.SEND");
//		// intent.putExtra("Kdescription", weather);
//		intent.setType("image/*");
//		// intent.putExtra(Intent.EXTRA_TEXT, weather);
//		intent.putExtra(Intent.EXTRA_STREAM, u);
//		startActivity(intent);
//	}
//
//	private void shareToTimeLine(Uri u) {
//
//		Intent intent = new Intent();
//		ComponentName comp = new ComponentName("com.tencent.mm",
//				"com.tencent.mm.ui.tools.ShareToTimeLineUI");
//		intent.setComponent(comp);
//		intent.setAction("android.intent.action.SEND");
//		intent.setType("image/jpg");
//		intent.putExtra(Intent.EXTRA_STREAM, u);
//		startActivity(intent);
//	}

//	private void shareToXinLang(Uri u) {
//		Intent intent = new Intent();
//		ComponentName comp = new ComponentName("com.sina.weibo",
//				"com.sina.weibo.EditActivity");
//		intent.setComponent(comp);
//		intent.setAction(Intent.ACTION_SEND);
//		intent.setType("image/jpg");
//		intent.putExtra(Intent.EXTRA_STREAM, u);
//		startActivity(intent);
//	}

	public SlidingMenu getMenu() {
		return menu;
	}

	private void getDate() {
		// TODO Auto-generated method stub
		String[] weekDays = this.getResources()
				.getStringArray(R.array.weekitem);
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DATE);
		int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0) {
			w = 0;
		}
		String week = weekDays[w];
		dates = month + "." + day + "/" + week;
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
				result = httpUtil.getJsonContent(Constant.weatherUrl);
				httpUtil.saveFile(result, Constant.WEATHER_FILE_NAME);

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
			case 5:

				JSONObject obj = (JSONObject) m.obj;

				// HashMap<String, String> map = new HashMap<String, String>();
				try {
					appurl = obj.getString("appurl");
					fileName = TestUtils.getFileName(appurl);
					int urlcode = Integer.parseInt(obj.getString("verCode"));
					Log.v("wangqinqin", "    " + (urlcode > getVersion()));
					// if(true){
					if (urlcode > getVersion()) {
						Dialog alertDialog = new AlertDialog.Builder(
								MainActivity.this)
								.setTitle(MainActivity.this.getResources().getString(
										R.string.update))
								.setMessage(MainActivity.this.getResources().getString(
										R.string.sure))
								.setPositiveButton(MainActivity.this.getResources().getString(
										R.string.ok),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												new Thread(new Runnable() {
													public void run() {
														loadFile(appurl,
																fileName);
													}
												}).start();
											}
										})
								.setNegativeButton(MainActivity.this.getResources().getString(
										R.string.no),
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
											}
										}).create();
						alertDialog.show();

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;
			case 6:

				progress.setVisibility(View.VISIBLE);
				int lMax,
				lNowSize;
				lMax = m.arg1;
				lNowSize = m.arg2;
				int progressPer = lNowSize / lMax * 100;
				progress.setProgress(progressPer);
				break;
			case 7:
				progress.setVisibility(View.GONE);
				AndroidUtil
						.install(MainActivity.this, fileName, SDPATH.SD_PATH);
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& (MainActivity.this.getMenu().isSecondaryMenuShowing() || MainActivity.this
						.getMenu().isMenuShowing())) {
			MainActivity.this.getMenu().toggle();
			return false;
		} else {
			finish();
		}

		return super.onKeyDown(keyCode, event);
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
			viewMain.setBackgroundColor(MainActivity.this.getResources()
					.getColor(R.color.green));
		} else if (colorPosition == 1) {
			viewMain.setBackgroundColor(MainActivity.this.getResources()
					.getColor(R.color.red));
		} else if (colorPosition == 2) {
			viewMain.setBackgroundColor(MainActivity.this.getResources()
					.getColor(R.color.blue));
		} else if (colorPosition == 3) {
			viewMain.setBackgroundColor(MainActivity.this.getResources()
					.getColor(R.color.purple));

		}
	}

	private void setCity(int cityPosotion) {
		String s = null;
		String citys = null;
		try {
			jsonArr = new JSONArray(json);
			obj = (JSONObject) jsonArr.get(cityPosotion);
			city.setText(obj.getString("city"));
			type.setText(obj.getString("type"));
			temperature.setText(obj.getString("temperature"));
			wind.setText(obj.getString("wind_direction") + " "
					+ obj.getString("wind_force"));
			citys = obj.getString("city");

			s = obj.getString("type") + "  " + obj.getString("temperature");
			String weather = citys + "  " + s;
			Editor editor = sp.edit();
			editor.putString("weather", weather);
			editor.commit();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		send(citys, s);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	private void Update() {
		new Thread(new Runnable() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpParams httpParams = client.getParams();
				HttpConnectionParams
						.setConnectionTimeout(httpParams, 30 * 1000);
				HttpConnectionParams.setSoTimeout(httpParams, 5000);

				HttpPost request = new HttpPost(Constant.updateUrl);
				try {
					HttpResponse response = new DefaultHttpClient()
							.execute(request);
					result = EntityUtils.toString(response.getEntity(), "utf-8");
				} catch (Exception e) {
					// TODO: handle exception
				}

				String json = result;

				JSONObject dataJson;
				try {
					dataJson = new JSONObject(json);
					Message msg = new Message();
					msg.what = 5;
					msg.obj = dataJson;
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}

	public int getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			vercode = info.versionCode;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vercode;
	}

	public void loadFile(String URL, String fileName) {
		InputStream is = null;
		try {
			// 创建、打开连接
			URL myUrl = new URL(URL);
			URLConnection connection = myUrl.openConnection();
			connection.connect();

			// 得到访问内容并保存在输入流中。
			is = connection.getInputStream();
			// 得到文件的总长度。注意这里有可能因得不到文件大小而抛出异常
			int len = connection.getContentLength();
			int nowSize = 0;
			if (is != null) {

				File file = new File(SDPATH.SD_PATH + fileName);
				// 如果文件存在，则删除该文件。
				if (file.exists()) {

					file.delete();
				}
				// 判断文件路径的文件夹是否存在，如果没有就创建一个。

				File p = new File(SDPATH.SD_PATH);

				if (!p.exists()) {

					p.mkdirs();
				}

				// RandomAccessFile随机访问的文件类，可以从指定访问位置，为以后实现断点下载提供支持
				RandomAccessFile randomAccessFile = new RandomAccessFile(
						SDPATH.SD_PATH + fileName, "rw");
				byte[] buffer = new byte[4096];
				int length = -1;
				int count = 0;
				if (!SDPATH.sdcardExit || !SDPATH.sdCardPer) {
					try {
						String command = "chmod " + "777" + " "
								+ (SDPATH.SD_PATH + fileName); // 777为permission
						Runtime runtime = Runtime.getRuntime();
						runtime.exec(command);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				while ((length = is.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, length);
					Message msg = new Message();
					msg.arg1 = len;
					msg.what = 6;
					nowSize += length;
					msg.arg2 = nowSize;
					if (count % 50 == 0 || nowSize == len) {
						handler.sendMessage(msg);
					}
					count++;
				}
				Message end = new Message();
				end.what = 7;
				handler.sendMessage(end);

				is.close();
				randomAccessFile.close();

			}
		} catch (Exception e) {
		} finally {

		}
	}

	private void wechatShare(int flag) {
		String text = "dffsgfdg";
		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		// msg.title = "Will be ignored";
		msg.description = text;
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		// String path = shotScree().toString();
		// WXImageObject imgObj = new WXImageObject();
		// imgObj.setImagePath(path);
		//
		// WXMediaMessage msg = new WXMediaMessage();
		// msg.mediaObject = imgObj;
		//
		// Bitmap bmp = BitmapFactory.decodeFile(path);
		// Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
		// THUMB_SIZE, true);
		// bmp.recycle();
		// msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
		//
		// SendMessageToWX.Req req = new SendMessageToWX.Req();
		// req.transaction = buildTransaction("img");
		// req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
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
		if (e1.getX() - e2.getX() > verticalMinDistance
				&& Math.abs(velocityX) > minVelocity) {

			Intent intent = new Intent(MainActivity.this, ZiXunActivity.class);
			startActivity(intent);
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}

}
