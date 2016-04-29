package com.example.powercalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 表示日历上的每行
 * @author wangk
 */
public class MyCalendarLayout extends LinearLayout {
	
	private int postion = -1;
	
	private int preRow = -1;

	public MyCalendarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
	}
	
	
	public MyCalendarLayout(Context context) {
		super(context);
		setOrientation(VERTICAL);
	}

	public int getPostion() {
		return postion;
	}


	public void setPostion(int postion) {
		this.postion = postion;
	}


	public int getPreRow() {
		return preRow;
	}


	public void setPreRow(int preRow) {
		this.preRow = preRow;
	}
	
}
