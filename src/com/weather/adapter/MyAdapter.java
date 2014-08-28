package com.weather.adapter;
import java.util.List;

import com.smallweather.R;
import com.weather.util.FontManager;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter
{
	
	private Context mContext;
	private List<String> list;
	private boolean flag;

	public MyAdapter(Context mContext, List<String> list ,boolean flag) {
		this.mContext = mContext;
		this.list = list;
		this.flag = flag;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return this.list.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		String lists = list.get(position);
		if (convertView == null) {
			convertView = ((LayoutInflater) this.mContext
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
			        .inflate(R.layout.list_item, null);
		}
		TextView weatherInfo = (TextView) convertView.findViewById(R.id.list_item);
		if(flag){
			weatherInfo.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/font.ttf"));
		}
//		weatherInfo.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "font.ttf"));
		weatherInfo.setText(lists);

		return convertView;
	}
}