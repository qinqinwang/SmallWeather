package com.weather.adapter;
import java.util.List;

import com.smallweather.R;

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

	public MyAdapter(Context mContext, List<String> list ) {
		this.mContext = mContext;
		this.list = list;
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
		weatherInfo.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/font.ttf"));
		weatherInfo.setText(lists);

		return convertView;
	}
}