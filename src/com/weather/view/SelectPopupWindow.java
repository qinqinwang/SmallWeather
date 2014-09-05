package com.weather.view;

import com.smallweather.R;
import com.weather.adapter.MyAdapter;
import com.weather.util.TextImage;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SelectPopupWindow extends PopupWindow {

	private ImageView share_friend, share_circle,share_weibo;
	private TextView cancel;
	private View mMenuView;
	private ListView listColor;
	private TextImage jiudian_img,xianshi_img;
	private NotificationManager nm;
	private SharedPreferences sp = null;

	public SelectPopupWindow(Activity context, OnClickListener itemsOnClick,
			 OnItemClickListener ietmclicklistener,int flag) {
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
			jiudian_img = (TextImage)mMenuView.findViewById(R.id.jiudian_img);
			xianshi_img = (TextImage)mMenuView.findViewById(R.id.xianshi_img);
			xianshi_img.setOnClickListener(itemsOnClick);
			xianshi_img.setTextViewText(R.string.xianshi);
			if(sp.getBoolean("showxianshi", false)){
				xianshi_img.setImageDrawables(context.getResources().getDrawable(R.drawable.checked));
			}else{
				xianshi_img.setImageDrawables(context.getResources().getDrawable(R.drawable.unchecked));
			}
			jiudian_img.setOnClickListener(itemsOnClick);
			jiudian_img.setTextViewText(R.string.jiudian);
			if(sp.getBoolean("showjiudian", false)){
				jiudian_img.setImageDrawables(context.getResources().getDrawable(R.drawable.checked));
			}else{
				jiudian_img.setImageDrawables(context.getResources().getDrawable(R.drawable.unchecked));
			}
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
	}
	
	
}