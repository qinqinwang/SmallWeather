package com.weather.view;

import java.util.ArrayList;
import java.util.List;

import com.smallweather.R;
import com.weather.adapter.MyAdapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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

	private List<String> listColors = new ArrayList<String>();

	public SelectPopupWindow(Activity context, OnClickListener itemsOnClick,
			boolean flag, OnItemClickListener ietmclicklistener) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (flag) {
			mMenuView = inflater.inflate(R.layout.menu, null);
			share_friend = (ImageView) mMenuView.findViewById(R.id.share_friend);
			share_circle = (ImageView) mMenuView.findViewById(R.id.share_circle);
			share_weibo = (ImageView) mMenuView.findViewById(R.id.share_weibo);
			share_friend.setOnClickListener(itemsOnClick);
			share_circle.setOnClickListener(itemsOnClick);
			share_weibo.setOnClickListener(itemsOnClick);

			this.setWidth(LayoutParams.MATCH_PARENT);
			this.setHeight(LayoutParams.WRAP_CONTENT);
		} else {
			mMenuView = inflater.inflate(R.layout.dialog_menu, null);
			listColor = (ListView) mMenuView.findViewById(R.id.list_color);
			listColor.setAdapter(new MyAdapter(context, getColor(), true,null));
			listColor.setOnItemClickListener(ietmclicklistener);
			this.setWidth(LayoutParams.MATCH_PARENT);
			this.setHeight(LayoutParams.WRAP_CONTENT);
		}

		cancel = (TextView) mMenuView.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 销毁弹出框
				dismiss();
			}
		});
		this.setContentView(mMenuView);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}



	private List<String> getColor() {
		// TODO Auto-generated method stub

		listColors.add("绿");
		listColors.add("红");
		listColors.add("蓝");
		listColors.add("紫");
		return listColors;
	}

}