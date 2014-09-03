package com.weather.view;



import com.smallweather.R;
import com.weather.adapter.MyAdapter;
import com.weather.util.FontManager;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelectPopupWindow extends PopupWindow {

	private ImageView share_friend, share_circle,share_weibo,xianshiImg,jiudianImg;
	private TextView cancel;
	private View mMenuView;
	private ListView listColor;
//	private RelativeLayout xianshi,jiudian;
	private TextView xianshiText,jiudianText;
	private NotificationManager nm;
	private SharedPreferences sp = null;
	private int duration = 5000;

	public SelectPopupWindow(Activity context, OnClickListener itemsOnClick,
			 OnItemClickListener ietmclicklistener,OnTouchListener itemsOnTouch,int flag) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (flag == 0) {
			mMenuView = inflater.inflate(R.layout.share_menu, null);
			share_friend = (ImageView) mMenuView.findViewById(R.id.share_friend);
			share_circle = (ImageView) mMenuView.findViewById(R.id.share_circle);
			share_weibo = (ImageView) mMenuView.findViewById(R.id.share_weibo);
			share_friend.setOnClickListener(itemsOnClick);
			share_circle.setOnClickListener(itemsOnClick);
			share_weibo.setOnClickListener(itemsOnClick);
		} else if(flag == 1){
			mMenuView = inflater.inflate(R.layout.color_menu, null);
			listColor = (ListView) mMenuView.findViewById(R.id.list_color);
			listColor.setAdapter(new MyAdapter(context, context.getResources().getStringArray(R.array.coloritem),0));
			listColor.setOnItemClickListener(ietmclicklistener);
		}else if(flag == 2){
			mMenuView = inflater.inflate(R.layout.tixing_menu, null);
			sp = context.getSharedPreferences("weather", Context.MODE_PRIVATE);
//			xianshi = (RelativeLayout)mMenuView.findViewById(R.id.xianshi);
//			jiudian = (RelativeLayout)mMenuView.findViewById(R.id.jiudian);
//			
//			xianshi.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
////					if(sp.getBoolean("show", false)){
////						nm.cancel(0);
////						Editor editor = sp.edit();
////						editor.putBoolean("show", false);
////						editor.commit();
//////						Toast.makeText(context.this, context.this.getResources().getString(
//////								R.string.bxianshi), duration).show();
////					}else{
////						
////						MainActivity.sendMessage(sp.getString("citys",""),sp.getString("message",""));
////						Editor editor = sp.edit();
////						editor.putBoolean("show", true);
////						editor.commit();
//////						Toast.makeText(TiXingSetting.this, TiXingSetting.this.getResources().getString(
//////								R.string.yxianshi), duration).show();
////					}
//				}
//			});
			xianshiText = (TextView)mMenuView.findViewById(R.id.xianshi_text);
			jiudianText = (TextView)mMenuView.findViewById(R.id.jiudian_text);
			xianshiText.setTypeface(Typeface.createFromAsset(context.getAssets(),
					"fonts/font.ttf"));
			jiudianText.setTypeface(Typeface.createFromAsset(context.getAssets(),
					"fonts/font.ttf"));
			xianshiImg = (ImageView)mMenuView.findViewById(R.id.xianshi_img);
			xianshiImg.setOnTouchListener(itemsOnTouch);
			if(sp.getBoolean("showxianshi", false)){
				xianshiImg.setImageDrawable(context.getResources().getDrawable(R.drawable.checked));
			}else{
				xianshiImg.setImageDrawable(context.getResources().getDrawable(R.drawable.unchecked));
			}
			jiudianImg = (ImageView)mMenuView.findViewById(R.id.jiudian_img);
			jiudianImg.setOnTouchListener(itemsOnTouch);
			if(sp.getBoolean("showjiudian", false)){
				jiudianImg.setImageDrawable(context.getResources().getDrawable(R.drawable.checked));
			}else{
				jiudianImg.setImageDrawable(context.getResources().getDrawable(R.drawable.unchecked));
			}
//			xianshi.setOnClickListener(itemsOnClick);
//			jiudian.setOnClickListener(itemsOnClick);
		}
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		cancel = (TextView) mMenuView.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dismiss();
			}
		});
		this.setContentView(mMenuView);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
//		mMenuView.setOnTouchListener(new OnTouchListener() {
//
//			public boolean onTouch(View v, MotionEvent event) {
//
//				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
//				int y = (int) event.getY();
//				if (event.getAction() == MotionEvent.ACTION_UP) {
//					if (y < height) {
//						Log.v("wangqinqin", "  height  = "+height+"  y = "+ y);
//						dismiss();
//					}
//				}
//				return true;
//			}
//		});
		
//		public  void sendMessage(String citys, String message) {
//			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//			// 构建一个通知对象(需要传递的参数有三个,分别是图标,标题和 时间)
//			Notification notification = new Notification(R.drawable.logo, message,
//					System.currentTimeMillis());
//			Intent intent = new Intent(context.this, TiXingSetting.class);
//			PendingIntent pendingIntent = PendingIntent.getActivity(
//					TiXingSetting.this, 0, intent, 0);
//			notification.setLatestEventInfo(getApplicationContext(), citys,message,
//					pendingIntent);
//			notification.flags = Notification.FLAG_ONGOING_EVENT;//消息不可取消
//			// notification.defaults = Notification.DEFAULT_SOUND;//声音默认
//			manager.notify(0, notification);
//
//		}
	}
	
	
}