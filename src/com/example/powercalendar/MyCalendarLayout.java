package com.example.powercalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * ��ʾ�����ϵ�ÿ��
 * @author wangk
 */
public class MyCalendarLayout extends LinearLayout {

	public MyCalendarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
	}
	
	
	public MyCalendarLayout(Context context) {
		super(context);
		setOrientation(VERTICAL);
	}

	
}
