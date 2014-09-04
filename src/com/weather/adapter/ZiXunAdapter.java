package com.weather.adapter;

import java.util.List;

import com.smallweather.R;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ZiXunAdapter extends BaseAdapter {

	private Context mContext;
	private List<String> list, listhref;
	SpannableString ss;

	public ZiXunAdapter(Context mContext, List<String> list,
			List<String> listhref) {
		this.mContext = mContext;
		this.list = list;
		this.listhref = listhref;
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
		String listhrefs = listhref.get(position);
		if (convertView == null) {
			convertView = ((LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.zixun_item, null);

		}
		TextView title = (TextView) convertView.findViewById(R.id.title);
		Typeface typeface = Typeface.create(mContext.getResources().getString(R.string.font), Typeface.NORMAL);
		title.setTypeface(typeface);
		title.setText(lists);
//		TextView content = (TextView)convertView.findViewById(R.id.content);
//		content.setText(lists);

		return convertView;
	}
}