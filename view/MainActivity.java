package com.weather.view;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.weather.adapter.MyAdapter;
import com.weather.bean.SDPATH;
import com.weather.util.AndroidUtil;
import com.weather.util.FontManager;
import com.weather.util.HttpUtil;
import com.weather.util.TestUtils;

public class MainActivity extends Activity {
	private TextView date, city, type, temperature, wind;
	private ImageButton setting;
	private String result;
	private String reads;
	private String dates;
	private MyHandler handler = new MyHandler();
	private String json;
	private SharedPreferences sp = null;
	private ListView listColor;
	private ListView listCity;
	private ListView listshare;
	private List<String> listColors = new ArrayList<String>();
	private List<String> listCitys = new ArrayList<String>();
	private List<String> listShare = new ArrayList<String>();

	private RelativeLayout viewMain;
	private SlidingMenu menu;

	private final static String FILE_NAME = "weather.txt";

	private JSONArray jsonArr;
	private JSONObject obj;
	private NotificationManager nm;
	private final String APP_ID = "wxc1166aff17ba799b";
	private IWXAPI wxApi;
	//实例化
	
	// private ImageButton share;

	private int vercode;
	private ArrayList<HashMap<String, String>> applist = new ArrayList<HashMap<String, String>>();

	private String appurl, fileName;
	private ProgressBar progress;
	int width;
	int height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		wxApi = WXAPIFactory.createWXAPI(this, APP_ID);
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

		nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// 更新
		// Update();
		MobclickAgent.setDebugMode(true);
	}

	public void send(String citys, String s) {
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 构建一个通知对象(需要传递的参数有三个,分别是图标,标题和 时间)
		Notification notification = new Notification(R.drawable.logo, null,
				System.currentTimeMillis());
		Intent intent = new Intent(MainActivity.this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				MainActivity.this, 0, intent, 0);
		notification.setLatestEventInfo(getApplicationContext(), citys, s,
				pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击后自动消失
		// notification.defaults = Notification.DEFAULT_SOUND;//声音默认
		manager.notify(0, notification);

	}

	private void hasNet() {
		// TODO Auto-generated method stub
		if (isNetworkAvailable(MainActivity.this)) {
			// getDate();
			getData();
		} else {
			if ("".equals(readFile()) || readFile() == null) {
				Toast.makeText(MainActivity.this, "无网络，无法获取数据", 8000).show();
				Message msg = new Message();
				msg.what = 4;
				handler.sendMessage(msg);
			} else {
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
				MainActivity.this.getMenu().toggle();
				Editor editor = sp.edit();
				editor.putInt("colorPosition", position);
				editor.commit();
				Message msg = new Message();
				msg.what = 2;
				msg.arg1 = position;
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
					if ("".equals(readFile()) || readFile() == null) {
						Toast.makeText(MainActivity.this, "请连接网络", 8000).show();
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
		listshare = (ListView) findViewById(R.id.list_share);
		listshare.setAdapter(new MyAdapter(this, getShare()));
		listshare.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				MainActivity.this.getMenu().toggle();
				
				if (isNetworkAvailable(MainActivity.this)) {
					TimerTask task = new TimerTask() {

						public void run() {
							Message message = new Message();
							message.what = 8;
							message.arg1 = position;
							handler.sendMessage(message);
						}

					};
					Timer timer = new Timer();
					timer.schedule(task, 1000);
				}else {
					if ("".equals(readFile()) || readFile() == null) {
						Toast.makeText(MainActivity.this, "请连接网络", 8000).show();
					} else {
						TimerTask task = new TimerTask() {

							public void run() {
								Message message = new Message();
								message.what = 8;
								message.arg1 = position;
								handler.sendMessage(message);
							}

						};
						Timer timer = new Timer();
						timer.schedule(task, 1000);
					}
				}
				
				

			}
		});

		progress = (ProgressBar) findViewById(R.id.progress);
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

	public Bitmap shotScree() {
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
		int width = this.getWindowManager().getDefaultDisplay().getWidth();
		int height = this.getWindowManager().getDefaultDisplay().getHeight();
		// 去掉标题栏
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);

		return b;
	}

	private void shareToFriend(String weather, Uri u) {
		Intent intent = new Intent();
		ComponentName comp = new ComponentName("com.tencent.mm",
				"com.tencent.mm.ui.tools.ShareImgUI");
		intent.setComponent(comp);
		intent.setAction("android.intent.action.SEND");
		// intent.putExtra("Kdescription", weather);
		intent.setType("image/jpg");
		// intent.putExtra(Intent.EXTRA_TEXT, weather);
		intent.putExtra(Intent.EXTRA_STREAM, u);
		startActivity(intent);
	}

	private void shareToTimeLine(Uri u) {

		Intent intent = new Intent();
		ComponentName comp = new ComponentName("com.tencent.mm",
				"com.tencent.mm.ui.tools.ShareToTimeLineUI");
		intent.setComponent(comp);
		intent.setAction("android.intent.action.SEND");
		intent.setType("image/jpg");
		intent.putExtra(Intent.EXTRA_STREAM, u);
		startActivity(intent);
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
						.getJsonContent("http://tianqi.iyoo.me/weather/");
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
						Log.w("sh", "需要更新");

						Dialog alertDialog = new AlertDialog.Builder(
								MainActivity.this)
								.setTitle("更新")
								.setMessage("您确定更新微天气吗？")
								.setPositiveButton("确定",
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
								.setNegativeButton("取消",
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
			case 8:
				int position = m.arg1;
				Uri uri = shotScreen();
				if (position == 0) {
					shareToFriend(sp.getString("weather", null), uri);
				} else {
					shareToTimeLine(uri);
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

	private List<String> getShare() {
		// TODO Auto-generated method stub

		listShare.add("朋友");
		listShare.add("朋友圈");
		return listShare;
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

	public String readFile() {
		try {
			FileInputStream fis = openFileInput(FILE_NAME);
			byte[] b = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while (fis.read(b) != -1) {
				baos.write(b, 0, b.length);
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

				HttpPost request = new HttpPost(
						"http://down.znds.com/apinew/cleanupdate.php");
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
				// 储存内存时，修改APK的权限
				Log.v("wqang@@@@@@@@@", "  3333333333  "
						+ (!SDPATH.sdcardExit || !SDPATH.sdCardPer));
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
	
	
	private void wechatShare(int flag){
	    WXWebpageObject webpage = new WXWebpageObject();
	    webpage.webpageUrl = 这里填写链接url;
	    WXMediaMessage msg = new WXMediaMessage(webpage);
	    msg.title = 这里填写标题;
	    msg.description = 这里填写内容;
	    //这里替换一张自己工程里的图片资源
	    Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.share_logo);
	    msg.setThumbImage(thumb);
	     
	    SendMessageToWX.Req req = new SendMessageToWX.Req();
	    req.transaction = String.valueOf(System.currentTimeMillis());
	    req.message = msg;
	    req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
	    wxApi.sendReq(req);
	}

}
