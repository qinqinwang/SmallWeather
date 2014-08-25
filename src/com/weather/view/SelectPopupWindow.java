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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;  
import android.widget.ListView;
import android.widget.PopupWindow; 
import android.widget.TextView;


public class SelectPopupWindow extends PopupWindow {  


  private TextView share_friend, share_circle, cancel;  
  private View mMenuView;  
  private ListView listColor;

	private List<String> listColors = new ArrayList<String>();

  public SelectPopupWindow(Activity context,OnClickListener itemsOnClick,boolean flag,OnItemClickListener ietmclicklistener) {  
      super(context);  
      LayoutInflater inflater = (LayoutInflater) context  
              .getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
      if(flag){
    	  mMenuView = inflater.inflate(R.layout.menu, null);  
          share_friend = (TextView) mMenuView.findViewById(R.id.share_friend);  
          share_circle = (TextView) mMenuView.findViewById(R.id.share_circle);   
          share_friend.setOnClickListener(itemsOnClick);  
          share_circle.setOnClickListener(itemsOnClick);  
      
          this.setWidth(LayoutParams.MATCH_PARENT); 
      }else{
    	  mMenuView = inflater.inflate(R.layout.dialog_menu, null);  
    	 
    	  listColor =(ListView)mMenuView.findViewById(R.id.list_color);
    	  listColor.setAdapter(new MyAdapter(context, getColor(),true));
    	  listColor.setOnItemClickListener(ietmclicklistener);
    	
          this.setWidth(300); 
      }
     
      cancel = (TextView) mMenuView.findViewById(R.id.cancel);
     cancel.setOnClickListener(new OnClickListener() {  

          public void onClick(View v) {  
              //销毁弹出框  
              dismiss();  
          }  
      });  
      //设置按钮监听  
      
      //设置SelectPicPopupWindow的View  
      this.setContentView(mMenuView);   
      //设置SelectPicPopupWindow弹出窗体的高  
      this.setHeight(LayoutParams.WRAP_CONTENT);  
      //设置SelectPicPopupWindow弹出窗体可点击  
      this.setFocusable(true);  
      //设置SelectPicPopupWindow弹出窗体动画效果  
      this.setAnimationStyle(R.style.AnimBottom);  
      //实例化一个ColorDrawable颜色为半透明  
      ColorDrawable dw = new ColorDrawable(0xb0000000);  
      //设置SelectPicPopupWindow弹出窗体的背景  
      this.setBackgroundDrawable(dw);  
      //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框  
      mMenuView.setOnTouchListener(new OnTouchListener() {  
            
          public boolean onTouch(View v, MotionEvent event) {  
                
              int height = mMenuView.findViewById(R.id.pop_layout).getTop();  
              int y=(int) event.getY();  
              if(event.getAction()==MotionEvent.ACTION_UP){  
                  if(y<height){  
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