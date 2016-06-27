package com.abook23.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MyDraw extends View {

	public boolean isTouch = false;
	private Paint paint = new Paint();
	private Path path;
	public Button btnEraseAll;
	public LinearLayout layout;

	public Bitmap bitmap;
	

	/**
	 * 要画图形，最起码要有三个对象： 1.颜色对象 Color 2.画笔对象 Paint 3.画布对象 Canvas
	 */
	public MyDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	//	paint.setAntiAlias(true);// 第一个函数是用来防止边缘的锯齿
		paint.setDither(true);
		paint.setColor(Color.BLACK);// 设置颜色
		paint.setStyle(Paint.Style.STROKE);// 风格 让画出的图形是空心的
		paint.setStrokeJoin(Paint.Join.ROUND);// 
		paint.setStrokeWidth(10f);//设置画出的线的 粗细程度
		path = new Path();
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		isTouch = true;
		float x = event.getX();// 获取触摸坐标
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:// 停止触摸
			//path = new Path();
			path.moveTo(x, y);
			
			break;
		case MotionEvent.ACTION_MOVE:// 触摸
			path.lineTo(x, y);
			break;
		default:
			break;
		}
		postInvalidate();
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub	
		canvas.drawColor(Color.WHITE);
		canvas.drawPath(path, paint);
		super.onDraw(canvas);
	}
	
	public void onReset(){
		path.reset();
		postInvalidate();// 刷新界面
	}
}
