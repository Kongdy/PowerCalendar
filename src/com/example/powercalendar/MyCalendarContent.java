package com.example.powercalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.powercalendar.tools.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ����������ɻ�������
 * 
 * @author wangk
 *
 */
public class MyCalendarContent extends FrameLayout {

	private final static String TAG = "MyCalendarContent";
	
	private final static int MATHCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
	private final static int WARP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

	private MyCalendarLayout mCalendarM1; // ÿҳ�����ÿؼ�1
	private MyCalendarLayout mCalendarM2; // ÿҳ�����ÿؼ�2
	private MyCalendarLayout mCalendarM3; // ÿҳ�����ÿؼ�3
	private MyCalendarLayout mCalendarMCursor; // ÿҳ�����ÿؼ�4

	private CopyOnWriteArrayList<MyCalendarLayout> mCalendars; // �ؼ���

	private int MAX_ID;

	private LinearLayout mainContent; // ������

	private int MAX_TAB_WIDTH;

	private Context context;

	private int currentChangeMonth; // ��ǰ�ı��·�

	private int mSCROLL_STATU; // ����״̬

	private int minSubStractMonth;// ��С�·����
	
	private int lastMotionY; // ���´���y��
	
	private  int deltaY = 0;
	// �Ѿ��ı��y������
	private int changeY = 0;

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

	/**
	 * ˢ��/ע������,����������
	 */
	public void notifyDataSetChanged() {
		mCalendars = new CopyOnWriteArrayList<MyCalendarLayout>();
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

		mCalendars.add(mCalendarM1);
		mCalendars.add(mCalendarM2);
		mCalendars.add(mCalendarM3);
		mCalendars.add(mCalendarMCursor);

		minSubStractMonth = 0;
		currentChangeMonth = 0;
		MAX_ID = 1;

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				MATHCH_PARENT, WARP_CONTENT);
		int i = 0;
		while (i < mCalendars.size()) {
			createMonth(mCalendars.get(i), System.currentTimeMillis(), i - 1);
			mainContent.addView(mCalendars.get(i), params);
			i++;
		}
		mainContent.setBackgroundColor(Color.GREEN);
		addView(mainContent);
	}

	/**
	 * ���·��������
	 * 
	 * @param mCalendarM
	 * @param list
	 * @param timeMills
	 */
	private void fillData(MyCalendarLayout mCalendarM,
			List<Map<String, String>> list, long TimeMills, int changeMonth) {
		MyCalendarWeek mCalendarW = new MyCalendarWeek(context); // ÿ��
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(TimeMills);
		int month = c.get(Calendar.MONTH) + 1;

		if (minSubStractMonth > changeMonth) {
			minSubStractMonth = changeMonth;
		}
		if (currentChangeMonth < changeMonth) {
			currentChangeMonth = changeMonth;
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String, String> map = list.get(i);
			int weekday = Integer.parseInt(map.get("week"));
			// ���հ�,����·ݱ���
			if (i == 0) {
				int count = weekday - 1;
				if (month == 1) {
					int year = c.get(Calendar.YEAR);
					TextView mCalendarYear = new TextView(context);
					mCalendarYear.setText("  " + year + "��");
					mCalendarYear.setTextColor(getResources().getColor(
							R.color.red));
					// �Ӵ�
					TextPaint tp = mCalendarYear.getPaint();
					tp.setFakeBoldText(true);
					mCalendarYear.setTextSize(Utils.getRawSize(getContext(),
							TypedValue.COMPLEX_UNIT_SP, 8));
					mCalendarW.addView(mCalendarYear);
					mCalendarM.addView(mCalendarW);
					mCalendarW = new MyCalendarWeek(context);
				}
				for (int j = 0; j < count; j++) {
					MyCalendarDay mCalendarD = new MyCalendarDay(context);
					mCalendarD.setText("  ");
					mCalendarW.addView(mCalendarD);
				}
				MyCalendarDay mCalendarTitle = new MyCalendarDay(context);
				mCalendarTitle.setText(month + "��");
				mCalendarTitle.setTextColor(getResources().getColor(
						R.color.text_orange));
				mCalendarTitle.setTextSize(Utils.getRawSize(context,
						TypedValue.COMPLEX_UNIT_SP, 6));
				mCalendarW.addView(mCalendarTitle);
				mCalendarM.addView(mCalendarW);
				mCalendarW = new MyCalendarWeek(context);
				if (weekday != 1) {
					for (int j = 0; j < count; j++) {
						MyCalendarDay mCalendarD = new MyCalendarDay(context);
						mCalendarD.setText("  ");
						mCalendarW.addView(mCalendarD);
					}
				}
			}
			MyCalendarDay mCalendarD = new MyCalendarDay(context); // ÿ��
			if (weekday == 1 || weekday == 7) {
				mCalendarD.setTextColor(getResources().getColor(
						R.color.title_weekend));
			}
			mCalendarD.setText(map.get("day"));
			mCalendarD.setTextSize(Utils.getRawSize(context,
					TypedValue.COMPLEX_UNIT_SP, 7));
			mCalendarW.addView(mCalendarD);
			if (weekday % 7 == 0 || i >= list.size() - 1) {
				mCalendarM.addView(mCalendarW);
				mCalendarW = new MyCalendarWeek(context);
			}
		}
	}

	/**
	 * ͨ��ʱ�����ȡĳ���µĴ������ڵ�����
	 * 
	 * @param TimeMills
	 *            ʱ�����
	 * @param changeMonth
	 *            �·ݱ仯���� -1Ϊ���ʱ����ϸ��£�1Ϊ���ʱ����¸���
	 * @return
	 */
	private List<Map<String, String>> createMonth(MyCalendarLayout mCalendarM,
			long TimeMills, int changeMonth) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Calendar c = Calendar.getInstance();
		if (TimeMills >= 0l) {
			c.setTimeInMillis(TimeMills);
		}
		if (changeMonth != 0) {
			c.add(Calendar.MONTH, changeMonth);
		}
		mCalendarM.setId(MAX_ID + 1);
		int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		int firstWeekDay = getFirstWeekDayOfMonth(c.getTimeInMillis());
		for (int i = 0; i < maxDay; i++) {
			Map<String, String> map = new HashMap<String, String>();
			int day = i + 1;
			map.put("day", "" + day);
			map.put("week", "" + firstWeekDay);
			list.add(map);
			++firstWeekDay;
			if (firstWeekDay > 7) {
				firstWeekDay = 1;
			}
		}
		if (list != null && list.size() > 0) {
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
	public boolean onTouchEvent(MotionEvent event) {
		  int y = (int) event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				deltaY = y+changeY;
				break;
			}
			case MotionEvent.ACTION_MOVE:
				ViewParent parent = getParent();
				if(parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
				lastMotionY = deltaY-y;
				super.scrollTo(0, lastMotionY);
				//offsetTopAndBottom(deltaY);
				changeY = lastMotionY;
				invalidate();
//				mainContent.requestLayout();
//				mainContent.invalidate();
//				for (int i = 0; i < mainContent.getChildCount(); i++) {
//					mainContent.getChildAt(i).invalidate();
//				}
				
				break;
			case MotionEvent.ACTION_UP:
				break;
			default:
				break;
			}
		return true;
	}


	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (t > oldt) {
			mSCROLL_STATU = SCROLL_STATE.STATE_SCROLLED_UP.nativeInt;
		} else {
			mSCROLL_STATU = SCROLL_STATE.STATE_SCROLLED_DONW.nativeInt;
		}

	}

	// TODO
	private void refreshView() {

		int[] location = new int[2];
		int[] location2 = new int[2];
		int i = mCalendars.size() - 1;
		MyCalendarLayout endM = (MyCalendarLayout) mainContent.getChildAt(i);
		endM.getLocationOnScreen(location);
		MyCalendarLayout firstM = (MyCalendarLayout) mainContent.getChildAt(0);
		firstM.getLocationOnScreen(location2);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				MATHCH_PARENT, WARP_CONTENT);
		if (mSCROLL_STATU == SCROLL_STATE.STATE_SCROLLED_UP.nativeInt) {
			MyCalendarLayout m = mCalendars.get(i);
			m.getLocationOnScreen(location);
			if ((location2[1] + firstM.getHeight()) < 0
					&& location[1] < getHeight()) {
				int id = mCalendars.get(mCalendars.size() - 1).getId();
				MyCalendarLayout newM = new MyCalendarLayout(getContext());
				params.addRule(RelativeLayout.BELOW, id);
				createMonth(newM, System.currentTimeMillis(),
						currentChangeMonth + 1);
				mainContent.addView(newM, params);
				mCalendars.add(newM);
				System.out.println("add foot");
			}
		} else if (mSCROLL_STATU == SCROLL_STATE.STATE_SCROLLED_DONW.nativeInt) {
			if (location[1] > getHeight()
					&& location2[1] + firstM.getHeight() > 0) {
				// System.out.println("getHeight():"+getHeight()+"location[1]:"+location[1]+",i==="+i);
				int id = mCalendars.get(0).getId();
				params.addRule(RelativeLayout.ABOVE, id);
				MyCalendarLayout newM = new MyCalendarLayout(getContext());
				createMonth(newM, System.currentTimeMillis(),
						minSubStractMonth - 1);
				mainContent.addView(newM, 0, params);
				mCalendars.add(0, newM);
				System.out.println("add head");
			}
		}
		mSCROLL_STATU = SCROLL_STATE.STATE_STATIC.nativeInt;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//		measureChildren(widthMeasureSpec, heightMeasureSpec);
//
//		setMeasuredDimension(widthSize, heightSize);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * ÿ������
	 * 
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
			setPadding(0, (int) Utils.getRawSize(context,
					TypedValue.COMPLEX_UNIT_DIP, 15), 0,
					(int) Utils.getRawSize(context,
							(int) TypedValue.COMPLEX_UNIT_DIP, 15));
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			MAX_TAB_WIDTH = MeasureSpec.getSize(widthMeasureSpec) / 7;
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

	}

	/**
	 * ÿ������
	 * 
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

			super.onMeasure(MeasureSpec.makeMeasureSpec(MAX_TAB_WIDTH,
					MeasureSpec.EXACTLY), heightMeasureSpec);
		}

	}

	/**
	 * scrollview����״̬ö��
	 * 
	 * @author wangk
	 *
	 */
	static enum SCROLL_STATE {
		/**
		 * ��ֹ
		 */
		STATE_STATIC(0),
		/**
		 * ��Ļ���£�����ָ���ϲ����Ļ���
		 */
		STATE_SCROLLED_UP(1),
		/**
		 * ��Ļ���ϣ�����ָ���²����Ļ���
		 */
		STATE_SCROLLED_DONW(2);

		private int nativeInt;

		private SCROLL_STATE(int nativeInt) {
			this.nativeInt = nativeInt;
		}
	}
	
}
