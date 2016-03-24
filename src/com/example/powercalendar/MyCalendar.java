package com.example.powercalendar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.example.powercalendar.tools.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 我的日历
 * @author wangk
 *
 */
public class MyCalendar extends RelativeLayout {
	
	private final static int MATHCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
	private final static int WARP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
	
	private final static int M_TAB_ID = 1;
	private final static int M_CONTENT_ID = 2;
	
	public final static int WEEK_SIZE = 7; // 每周天数
	private MyTab mTab; // 顶部标签
	private MyCalendarContent mCalendarContent; // 日历主要显示区域
	private int mMaxTabWidth;// 每个tab的最大宽度
	private Context context;

	public MyCalendar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		notifySetDataChanged();
	}
	
	/**
	 * 设置数据源
	 */
	public void setDataSource() {
		notifySetDataChanged();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/**
		 * 适配大小
		 */
		final int tabChildCount = mTab.getChildCount();
		
		if(tabChildCount > 0) {
			mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec)/tabChildCount;
		} else {
			mMaxTabWidth = -1;
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	/**
	 * 刷新/注入输入源
	 * 设置顶部的星期
	 */
	public void notifySetDataChanged() {
		// create a tab
		createTab();
		// create content
		createContent();
	}
	
	/**
	 * 创建日历主体部分
	 */
	private void createContent() {
		mCalendarContent = new MyCalendarContent(context);
		mCalendarContent.setId(M_CONTENT_ID);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATHCH_PARENT, MATHCH_PARENT);
		params.addRule(BELOW,M_TAB_ID);
		//params.addRule(ALIGN_BOTTOM,mTab.getId());
		addView(mCalendarContent, params);
	}

	private void createTab() {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATHCH_PARENT, WARP_CONTENT);
		params.addRule(ALIGN_PARENT_TOP);
		mTab = new MyTab(context);
		mTab.setBackgroundResource(R.color.title_bg);
		mTab.setPadding(0,(int)Utils.getRawSize(context, TypedValue.COMPLEX_UNIT_DIP, 5), 0, 
				(int)Utils.getRawSize(context, TypedValue.COMPLEX_UNIT_DIP, 5));
		mTab.setId(M_TAB_ID);
		addView(mTab, params);
		ArrayList<Week_Day> tabDatas = (ArrayList<Week_Day>) Week_Day.values.values();
		int i = 0;
		while(i < tabDatas.size()) {
			Week_Day wd = tabDatas.get(i);
			if(wd != null) {
				TabView tabView = new TabView(context);
				tabView.setText(wd.name());
				tabView.setGravity(Gravity.CENTER);
				tabView.setTextSize(Utils.getRawSize(context, TypedValue.COMPLEX_UNIT_SP, 5));
				if(wd.name().equals(Week_Day.SUNDAY.name()) || 
						wd.name().equals(Week_Day.SATURDAY.name())) {
					tabView.setTextColor(getResources().getColor(R.color.title_weekend));
				}
				mTab.addView(tabView);
			}
			i++;
		}
	}

	public class TabView extends TextView {
		public TabView(Context context) {
			super(context);
		}

		public TabView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY), 
					heightMeasureSpec);
		}
	}
	
	public class MyTab extends LinearLayout {
		
		public MyTab(Context context) {
			super(context);
		}

		public MyTab(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

}
