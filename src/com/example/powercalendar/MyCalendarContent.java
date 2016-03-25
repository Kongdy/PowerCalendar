package com.example.powercalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.powercalendar.tools.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ����������ɻ�������
 * @author wangk
 *
 */
public class MyCalendarContent extends ScrollView {
	
	private final static int MATHCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
	private final static int WARP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
	
	private MyCalendarLayout mCalendarM1; // ÿҳ�����ÿؼ�1
	private MyCalendarLayout mCalendarM2; // ÿҳ�����ÿؼ�2
	private MyCalendarLayout mCalendarM3; // ÿҳ�����ÿؼ�3
	private MyCalendarLayout mCalendarMCursor; // ÿҳ�����ÿؼ�4
	
	private List<MyCalendarLayout> mCalendars; // �ؼ���
	
	private final int mCalendarM1_ID = 1;
	private final int mCalendarM2_ID = 2;
	private final int mCalendarM3_ID = 3;
	private final int mCalendarMCursor_ID = 4;
	
	private LinearLayout mainContent; // ������

	private  int MAX_TAB_WIDTH;

	private Context context;
	
	public MyCalendarContent(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		notifyDataSetChanged();
	}
	
	
	public MyCalendarContent(Context context) {
		super(context);
		this.context = context;
		notifyDataSetChanged();
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	/**
	 * ˢ��/ע������,����������
	 */
	public void notifyDataSetChanged() {
		mCalendars = new ArrayList<MyCalendarLayout>();
		mainContent = new LinearLayout(context);
		mainContent.setOrientation(LinearLayout.VERTICAL);
		setVerticalScrollBarEnabled(false);// ����ʾ������(��ֱ����)
		setBackgroundColor(Color.WHITE);
		createRecycleMonth();
		
	}
	
	/**
	 * ���������·ݣ����������㣬ÿ���ڻ����Ĺ����и�������������
	 */
	private void createRecycleMonth() {
		mCalendarM1 = new MyCalendarLayout(context);
		mCalendarM2 = new MyCalendarLayout(context);
		mCalendarM3 = new MyCalendarLayout(context);
		mCalendarMCursor = new MyCalendarLayout(context);
		
		mCalendarM1.setId(mCalendarM1_ID);
		mCalendarM2.setId(mCalendarM2_ID);
		mCalendarM3.setId(mCalendarM3_ID);
		mCalendarMCursor.setId(mCalendarMCursor_ID);
		
		mCalendars.add(mCalendarM1);
		mCalendars.add(mCalendarM2);
		mCalendars.add(mCalendarM3);
		mCalendars.add(mCalendarMCursor);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATHCH_PARENT, WARP_CONTENT);
		int i = 0;
		while(i < mCalendars.size()) {
			createMonth(mCalendars.get(i),System.currentTimeMillis(),i-1);
			mainContent.addView(mCalendars.get(i),params);
			i++;
		}
		addView(mainContent);
	}
	
	/**
	 * ���·��������
	 * @param mCalendarM
	 * @param list
	 * @param timeMills
	 */
	private void fillData(MyCalendarLayout mCalendarM,List<Map<String, String>> list,long TimeMills,int changeMonth) {
		MyCalendarWeek mCalendarW = new MyCalendarWeek(context); // ÿ��
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(TimeMills);
		int month = c.get(Calendar.MONTH)+1;
		for (int i = 0; i < list.size(); i++) {
			Map<String, String> map = list.get(i);
			int weekday = Integer.parseInt(map.get("week"));
			// ���հ�,����·ݱ���
			if( i == 0 ) {
				int count = weekday-1;
				for (int j = 0; j < count; j++) {
					MyCalendarDay mCalendarD = new MyCalendarDay(context);
					mCalendarD.setText("  ");
					mCalendarW.addView(mCalendarD);
				}
				MyCalendarDay mCalendarTitle = new MyCalendarDay(context);
				mCalendarTitle.setText(month+"��");
				mCalendarTitle.setTextColor(getResources().getColor(R.color.text_orange));
				mCalendarTitle.setTextSize(Utils.getRawSize(context, TypedValue.COMPLEX_UNIT_SP, 6));
				mCalendarW.addView(mCalendarTitle);
				mCalendarM.addView(mCalendarW);
				mCalendarW = new MyCalendarWeek(context); 
				if(weekday!=1) {
					for (int j = 0; j < count; j++) {
						MyCalendarDay mCalendarD = new MyCalendarDay(context);
						mCalendarD.setText("  ");
						mCalendarW.addView(mCalendarD);
					}
				}
			}
			MyCalendarDay mCalendarD = new MyCalendarDay(context); // ÿ��
			if(weekday == 1 || weekday == 7) {
				mCalendarD.setTextColor(getResources().getColor(R.color.title_weekend));
			}
			mCalendarD.setText(map.get("day"));
			mCalendarD.setTextSize(Utils.getRawSize(context, TypedValue.COMPLEX_UNIT_SP,7 ));
			mCalendarW.addView(mCalendarD);
			if( weekday%7 == 0 || i >= list.size()-1 ) {
				mCalendarM.addView(mCalendarW);
				mCalendarW = new MyCalendarWeek(context);
			}
		}
	}
	
	/**
	 *  ͨ��ʱ�����ȡĳ���µĴ������ڵ�����
	 * @param TimeMills ʱ�����
	 * @param changeMonth �·ݱ仯���� -1Ϊ���ʱ����ϸ��£�1Ϊ���ʱ����¸���
	 * @return
	 */
	private List<Map<String, String>> createMonth(MyCalendarLayout mCalendarM,long TimeMills,int changeMonth) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Calendar c = Calendar.getInstance();
		if(TimeMills >= 0l) {
			c.setTimeInMillis(TimeMills);
		}
		if(changeMonth != 0) {
			c.add(Calendar.MONTH, changeMonth);
		}
		int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		int firstWeekDay = getFirstWeekDayOfMonth(c.getTimeInMillis());
		for (int i = 0; i < maxDay; i++) {
			Map<String, String> map = new HashMap<String, String>();
			int day = i+1;
			map.put("day", ""+day);
			map.put("week", ""+firstWeekDay);
			list.add(map);
			++firstWeekDay;
			if(firstWeekDay > 7) {
				firstWeekDay = 1;
			}
		}
		if(list != null && list.size() > 0) {
			fillData(mCalendarM, list, c.getTimeInMillis(), changeMonth);
		}
		return list;
	}
	
	private int getFirstWeekDayOfMonth(long TimeMills) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(TimeMills);
		c.set(Calendar.DATE, 1);
		return c.get(Calendar.DAY_OF_WEEK);
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		boolean fillViewFlag = widthMode == MeasureSpec.EXACTLY;
		setFillViewport(fillViewFlag);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		
		
	}

	/**
	 * ÿ������
	 * @author wangk
	 *
	 */
	public class MyCalendarWeek extends LinearLayout {

		public MyCalendarWeek(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}

		public MyCalendarWeek(Context context) {
			super(context);
			init();
		}
		
		private void init() {
			setOrientation(LinearLayout.HORIZONTAL);
			setPadding(0, (int)Utils.getRawSize(context, TypedValue.COMPLEX_UNIT_DIP, 15), 0, 
					(int)Utils.getRawSize(context, (int)TypedValue.COMPLEX_UNIT_DIP, 15));
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			MAX_TAB_WIDTH = MeasureSpec.getSize(widthMeasureSpec)/7;
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
		
	}
	
	/**
	 * ÿ������
	 * @author wangk
	 */
	public class MyCalendarDay extends TextView {

		public MyCalendarDay(Context context, AttributeSet attrs) {
			super(context, attrs);
			setGravity(Gravity.CENTER);
		}

		public MyCalendarDay(Context context) {
			super(context);
			setGravity(Gravity.CENTER);
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(MeasureSpec.makeMeasureSpec(MAX_TAB_WIDTH, MeasureSpec.EXACTLY),
					heightMeasureSpec);
		}
		
	}
}
