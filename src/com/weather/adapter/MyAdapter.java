package com.weather.adapter;

import java.util.List;

import com.smallweather.R;
import com.weather.util.FontManager;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private Context mContext;
	private List<String> list, listhref;
	private boolean flag;
	SpannableString ss;
	String listhrefs;

	public MyAdapter(Context mContext, List<String> list, boolean flag,
			List<String> listhref) {
		this.mContext = mContext;
		this.list = list;
		this.listhref = listhref;
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
			if (flag) {
				convertView = ((LayoutInflater) this.mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.list_item, null);
			} else {

				listhrefs = listhref.get(position);
				convertView = ((LayoutInflater) this.mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.zixun_item, null);
			}

		}
		if (flag) {
			TextView weatherInfo = (TextView) convertView
					.findViewById(R.id.list_item);
			weatherInfo.setTypeface(Typeface.createFromAsset(
					mContext.getAssets(), "fonts/font.ttf"));
			weatherInfo.setText(lists);
		} else {
			TextView title = (TextView) convertView.findViewById(R.id.title);
			Typeface typeface = Typeface.create("黑体", Typeface.NORMAL);
			title.setTypeface(typeface);
//			title.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
//					"fonts/font.ttf"));
//			TextView href = (TextView) convertView.findViewById(R.id.href);
			title.setText(lists);
//			ss = new SpannableString(listhrefs);
//			ss.setSpan(new URLSpan(listhrefs), 0, listhrefs.length(),
//					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//			href.setText(ss);
//			href.setMovementMethod(LinkMovementMethod.getInstance());

		}

		return convertView;
	}
}