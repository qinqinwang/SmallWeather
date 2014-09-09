package com.weather.layout;

import com.smallweather.R;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TextImage extends RelativeLayout {


	private ImageView imageView;
	private TextView textView;
	public TextImage(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	public TextImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 导入布局
		LayoutInflater.from(context).inflate(R.layout.textimage, this, true);
		imageView = (ImageView) findViewById(R.id.image);
		textView = (TextView) findViewById(R.id.text);
		textView.setTypeface(Typeface.createFromAsset(context.getAssets(),
				"fonts/font.ttf"));

	}

	/**
	 * 设置图片资源
	 */
	public void setImageDrawables(Drawable d) {
		imageView.setImageDrawable(d);
	}

	/**
	 * 设置显示的文字
	 */
	public void setTextViewText(int  text) {
		textView.setText(text);
	}


	
}
