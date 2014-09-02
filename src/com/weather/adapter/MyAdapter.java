package com.weather.adapter;

import com.smallweather.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private Context mContext;
	private String[] list;
	private SharedPreferences sp;
	private int flag;

	public MyAdapter(Context mContext, String[] list, int flag) {
		this.mContext = mContext;
		this.list = list;
		this.flag = flag;
		sp = mContext.getSharedPreferences("weather", Context.MODE_PRIVATE);
	}

	public int getCount() {
		return list.length;
	}

	public Object getItem(int position) {
		return this.list[position];
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		String lists = list[position];
		if (convertView == null) {
			convertView = ((LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.list_item, null);
		}
		TextView weatherInfo = (TextView) convertView
				.findViewById(R.id.list_item);
		ImageView img = (ImageView) convertView.findViewById(R.id.img);
		if(flag  == 0){
			if(sp.getInt("colorPosition", 0) == position){
				img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.checked));
			}else{
				img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unchecked));
			}
			
		}else if(flag == 1){
				img.setVisibility(View.GONE);
		}
		weatherInfo.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
				"fonts/font.ttf"));
		weatherInfo.setText(lists);

		return convertView;
	}
}