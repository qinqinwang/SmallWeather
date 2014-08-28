package com.weather.view;

import java.util.ArrayList;
import java.util.List;

import com.smallweather.R;
import com.weather.adapter.MyAdapter;
import com.weather.util.FontManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ZixunActivity extends Activity implements OnTouchListener,OnGestureListener{
	private GestureDetector gestureDetector;
	private List<String> listZixun = new ArrayList<String>();
	private ListView listzixun;
	private LinearLayout viewZiXun;
	private int verticalMinDistance = 10;
    private int minVelocity = 0; 
    private TextView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zixun);
		gestureDetector = new GestureDetector(this);
		back = (TextView)findViewById(R.id.right_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ZixunActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		viewZiXun = (LinearLayout)findViewById(R.id.viewZiXun);
		listzixun = (ListView) findViewById(R.id.list_zixun);
		listzixun.setAdapter(new MyAdapter(this,getzixun(),false));
		viewZiXun.setOnTouchListener(this);
		viewZiXun.setLongClickable(true);
		ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
		FontManager.changeFonts(viewGroup, this);

	}
	private List<String> getzixun() {
		// TODO Auto-generated method stub
		listZixun.add("女子为了照顾瘫痪男友花光积蓄 村民捐款资助");
		listZixun.add("90后白富美感情受挫：吞100片药微信直播感受 ");
		listZixun.add("河南一高中现八条禁令 男女拉手两次将被开除 ");
		listZixun.add("叔嫂孽情浮出水面：男方杀7岁私生子逃亡19年");
		listZixun.add("明星减肥食谱大曝光 杨丽萍20年坚持不吃米饭");
		listZixun.add("揭谢霆锋八亿身家：公司暂不上市 每年赚一亿");
		return listZixun;
	}


	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if(e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity){
			Intent intent = new Intent(ZixunActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//            finish();
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if(keyCode == KeyEvent.KEYCODE_BACK){
//			  finish();
//		}
////		Intent intent = new Intent(ZixunActivity.this, MainActivity.class);
////        startActivity(intent);
////        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
////      
//		return super.onKeyDown(keyCode, event);
//	}

}
