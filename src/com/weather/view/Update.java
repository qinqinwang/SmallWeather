package com.weather.view;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

import com.smallweather.R;
import com.weather.bean.SDPATH;
import com.weather.util.AndroidUtil;
import com.weather.util.TestUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class Update extends Activity{



	private TextView tv_update_myself, 
					 update_progress_bar_text;
	private Button bt_update_now, 
				   bt_update_later, 
				   update_bt_install;
	private String appurl, 
	               appdes;
	String fileName;
	private LinearLayout update_Module_download, 
	                     update_Module_install;
	private RelativeLayout update_progress_rl;
	private ProgressBar update_progress_bar;
	private DownHandler downHandler = new DownHandler();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.updata_dialog);
		
		update_Module_download = (LinearLayout)findViewById(R.id.update_Module_download);
		update_Module_download.setVisibility(View.VISIBLE);
		update_Module_install = (LinearLayout)findViewById(R.id.update_Module_install);
		update_Module_install.setVisibility(View.GONE);
		update_progress_rl = (RelativeLayout)findViewById(R.id.update_progress_rl);
		update_progress_rl.setVisibility(View.GONE);
		tv_update_myself = (TextView)findViewById(R.id.tv_update_myself);
		bt_update_now = (Button)findViewById(R.id.bt_update_now);
		bt_update_now.requestFocus();
		bt_update_later = (Button)findViewById(R.id.bt_update_later);
		update_bt_install = (Button)findViewById(R.id.update_bt_install);
		update_progress_bar = (ProgressBar)findViewById(R.id.update_progress_bar);
		update_progress_bar_text = (TextView)findViewById(R.id.update_progress_bar_text);
		
		Bundle bundle=this.getIntent().getExtras();
		ArrayList list = bundle.getParcelableArrayList("listUpdate");
		Map map = (Map) list.get(0);
		appurl = (String) map.get("appurl");
		appdes = (String) map.get("appdes");
		tv_update_myself.setText(appdes);
		fileName = TestUtils.getFileName(appurl);
		
				
		//立即更新
		bt_update_now.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				update_Module_download.setVisibility(View.GONE);
				update_progress_rl.setVisibility(View.VISIBLE);
				new Thread(new Runnable() {
					public void run() {
						loadFile(appurl, fileName);
						
					}
				}).start();
			}
		});
		
		//稍后再说
		bt_update_later.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		update_bt_install.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AndroidUtil.install(Update.this, fileName, SDPATH.SD_PATH);				
			}
		});
	}
	
	
	
	public void loadFile(String URL, String fileName)
	{		
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
				//判断文件路径的文件夹是否存在，如果没有就创建一个。
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
				//储存内存时，修改APK的权限
				if(!SDPATH.sdcardExit||!SDPATH.sdCardPer){
					try {
		                String command = "chmod " + "777" + " " + (SDPATH.SD_PATH + fileName); //777为permission
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
					msg.what = 1;
					nowSize += length;
					msg.arg2 = nowSize;
					if (count % 50 == 0 || nowSize == len) {
						downHandler.sendMessage(msg);
					}
					count++;
				}
				Message end = new Message();
				end.what = 2;
				downHandler.sendMessage(end);
				
				is.close();
				randomAccessFile.close();
				
			}
		} catch (Exception e) {

		} finally {

		}
	}
	
	class DownHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				int lMax,lNowSize;
				lMax = msg.arg1;
				lNowSize = msg.arg2;
				update_progress_bar.setMax(lMax);
				update_progress_bar.setProgress(lNowSize);
				int progressPer = lNowSize/(lMax/100);
				update_progress_bar_text.setText(""+ progressPer+"%");
				break;
			case 2:
				update_Module_install.setVisibility(View.VISIBLE);
				update_Module_download.setVisibility(View.GONE);
				update_progress_rl.setVisibility(View.GONE);
				update_bt_install.requestFocus();

				AndroidUtil.install(Update.this, fileName, SDPATH.SD_PATH);
				break;
			}
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//MainActivity.flag = false;
			Update.this.finish();
//			MainActivity.StopActivity();
		} 
		return super.onKeyDown(keyCode, event);
	}

}
