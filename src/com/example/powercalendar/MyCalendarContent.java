package com.example.powercalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.powercalendar.tools.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.ViewConfigurationCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

/**
 * 日历的主题可滑动部分
 * 
 * @author wangk
 *
 */
public class MyCalendarContent extends FrameLayout {

	private final static String TAG = "MyCalendarContent";
	
	private final static int MATHCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
	private final static int WARP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

	private MyCalendarLayout mCalendarM1; // 每页，复用控件1
	private MyCalendarLayout mCalendarM2; // 每页，复用控件2
	private MyCalendarLayout mCalendarM3; // 每页，复用控件3
	private MyCalendarLayout mCalendarMCursor; // 每页，复用控件4

	private CopyOnWriteArrayList<MyCalendarLayout> mCalendars; // 控件组

	private int MAX_ID;

	private LinearLayout mainContent; // 主布局

	private int MAX_TAB_WIDTH;

	private Context context;

	private int currentChangeMonth; // 当前改变月份

	private int mSCROLL_STATU; // 滚动状态

	private int minSubStractMonth;// 最小月份相减
	
	private float lastMotionY; // 最新触摸y轴
	// 开始滑动
	private boolean isBeginDraged;
	
	private int mTouchSlop; // 滑动最小距离阀值
	
	private int mActivePointId;
	
	private int totalWidth; // 总宽度
	private int totalHeight; // 总高度
	
	private float scrollY; // 滑动y轴数据
	private float scrollX; // 滑动x轴数据
	
	private boolean mFillViewPort; // 强制填充
	
	private int mOverscrollDistance;
	
	public MyCalendarContent(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = configuration.getScaledPagingTouchSlop();
		mOverscrollDistance = configuration.getScaledOverscrollDistance();
		notifyDataSetChanged();
	}

	public MyCalendarContent(Context context) {
		super(context);
		this.context = context;
		notifyDataSetChanged();
	}
	

	/**
	 * 刷新/注入数据,生成万年历
	 */
	public void notifyDataSetChanged() {
		setWillNotDraw(false);
		//setClipChildren(false);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mOverscrollDistance = configuration.getScaledOverscrollDistance();
		mCalendars = new CopyOnWriteArrayList<MyCalendarLayout>();
		mainContent = new LinearLayout(context);
		mainContent.setOrientation(LinearLayout.VERTICAL);
		setVerticalScrollBarEnabled(false);// 不显示滚动条(垂直方向)
		setBackgroundColor(Color.WHITE);
		createRecycleMonth();
		totalWidth = 0;
		totalHeight = 0;
	}

	/**
	 * 创建三个月份，上中下三层，每次在滑动的过程中复用这三个布局
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
		android.view.ViewGroup.LayoutParams params2 = new android.view.ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 
				FrameLayout.LayoutParams.WRAP_CONTENT);
		addView(mainContent,params2);
	}
	

	/**
	 * 给月份填充数据
	 * @param mCalendarM
	 * @param list
	 * @param timeMills
	 */
	private void fillData(final MyCalendarLayout mCalendarM,
			List<Map<String, String>> list, long TimeMills, int changeMonth) {
		MyCalendarWeek mCalendarW = new MyCalendarWeek(context); // 每行
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(TimeMills);
		int row = 0;
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
			// 填充空白,添加月份标题
			if (i == 0) {
				int count = weekday - 1;
				if (month == 1) {
					int year = c.get(Calendar.YEAR);
					TextView mCalendarYear = new TextView(context);
					mCalendarYear.setText("  " + year + "年");
					mCalendarYear.setTextColor(getResources().getColor(
							R.color.red));
					// 加粗
					TextPaint tp = mCalendarYear.getPaint();
					tp.setFakeBoldText(true);
					mCalendarYear.setTextSize(Utils.getRawSize(getContext(),
							TypedValue.COMPLEX_UNIT_SP, 8));
					mCalendarW.addView(mCalendarYear);
					mCalendarM.addView(mCalendarW);
					mCalendarW = new MyCalendarWeek(context);
					row++;
				}
				for (int j = 0; j < count; j++) {
					MyCalendarDay mCalendarD = new MyCalendarDay(context);
					mCalendarD.setText("  ");
					mCalendarW.addView(mCalendarD);
				}
				MyCalendarDay mCalendarTitle = new MyCalendarDay(context);
				mCalendarTitle.setText(month + "月");
				mCalendarTitle.setTextColor(getResources().getColor(
						R.color.text_orange));
				mCalendarTitle.setTextSize(Utils.getRawSize(context,
						TypedValue.COMPLEX_UNIT_SP, 6));
				mCalendarW.addView(mCalendarTitle);
				mCalendarM.addView(mCalendarW);
				mCalendarW = new MyCalendarWeek(context);
				row++;
				if (weekday != 1) {
					for (int j = 0; j < count; j++) {
						MyCalendarDay mCalendarD = new MyCalendarDay(context);
						mCalendarD.setText("  ");
						mCalendarW.addView(mCalendarD);
					}
				}
			}
			MyCalendarDay mCalendarD = new MyCalendarDay(context); // 每天
			if (weekday == 1 || weekday == 7) {
				mCalendarD.setTextColor(getResources().getColor(
						R.color.title_weekend));
			}
			final int index = row;
			final int postion = i;
			mCalendarD.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(postion != -1 && mCalendarM.getPreRow() != -1) {
						mCalendarM.removeViewAt(mCalendarM.getPreRow());
					}
					if(postion != mCalendarM.getPostion()) {
						mCalendarM.setPostion(postion);
						MyCalendarWeek mCalendarData = new MyCalendarWeek(context);
						mCalendarData.setBackgroundColor(Color.RED);
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATHCH_PARENT,
								WARP_CONTENT);
						params.height = 200;
						mCalendarM.addView(mCalendarData,index+1, params);
						mCalendarM.setPreRow(index+1);
					} else {
						mCalendarM.setPreRow(-1);
						mCalendarM.setPostion(-1);
					}
				}
			});
			mCalendarD.setText(map.get("day"));
			mCalendarD.setTextSize(Utils.getRawSize(context,
					TypedValue.COMPLEX_UNIT_SP, 7));
			mCalendarW.addView(mCalendarD);
			if (weekday % 7 == 0 || i >= list.size() - 1) {
				mCalendarM.addView(mCalendarW);
				mCalendarW = new MyCalendarWeek(context);
				row++;
			}
		}
	}

	/**
	 * 通过时间戳获取某个月的带有星期的天数
	 * @param TimeMills
	 *            时间毫秒
	 * @param changeMonth
	 *            月份变化，如 -1为这个时间的上个月，1为这个时间的下个月
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
	
	/**
	 * 滑动关键
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: {
				isBeginDraged = getChildCount() != 0;
				if(!isBeginDraged) {
					return false;
				}
				lastMotionY = event.getY();
				mActivePointId = event.getPointerId(0);
				break;
			}
			case MotionEvent.ACTION_MOVE:
				final int activePointerIndex = event.findPointerIndex(mActivePointId);
				final float y = event.getY(activePointerIndex);
				final int deltaY2 = (int) (lastMotionY-y);
				
				if(!isBeginDraged) {
					if(Math.abs(deltaY2) > mTouchSlop) {
						isBeginDraged = true;
					}
				}
				if(isBeginDraged) {
				  final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
					lastMotionY = y;
					float oldScrollY = getScrollY();
					scrollY = oldScrollY+deltaY2;
					scrollX = getScrollX();
				
				overScrollBy(0, deltaY2, 0, (int) scrollY, 0, getScrollRangeY(), 0, mOverscrollDistance, true);
				invalidate();
				}
		/*		
		 *      预留
		 *		 deltaY-y;
				((View)parent).invalidate();
				overScrollBy(0, lastMotionY, 0, getScrollY(), 0, getScrollRange(), 0, mOverscrollDistance, true);
				super.scrollTo(0, lastMotionY);
				offsetTopAndBottom(deltaY);
				changeY = lastMotionY;
				invalidate();
				requestLayout();
				computeScroll();
				int[] location = new int[2];
				Rect rect = new Rect();
				invalidateChildInParent(location, rect);
				computeVerticalScrollOffset();
				mainContent.requestLayout();
				mainContent.invalidate();
				for (int i = 0; i < mainContent.getChildCount(); i++) {
					mainContent.getChildAt(i).invalidate();
				}
				*/
				break;
			case MotionEvent.ACTION_UP:
				isBeginDraged = false;
				break;
			default:
				break;
			}
		return true;
	}
	
	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
			boolean clampedY) {
	//	super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
		super.scrollTo(scrollX, scrollY);
	}
	

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (t > oldt) {
			mSCROLL_STATU = SCROLL_STATE.STATE_SCROLLED_UP.nativeInt;
		} else {
			mSCROLL_STATU = SCROLL_STATE.STATE_SCROLLED_DONW.nativeInt;
		}
		if(isBeginDraged) {
			refreshView();
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
		
		// 如果是向下滚动的状态，即手指向上滑动，并且如果第一个控件的Y值+第一个空间的高度小于0，即在屏幕之外，
		//底部控件的顶部Y值小于屏幕高度,即可以看到底部控件的时候，则底部增加控件
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
				newM.setEnabled(false);	
				mainContent.addView(newM, params);
				mCalendars.add(newM);
				System.out.println("add foot");
			}
			// 如果是向上滚动状态，即手指向下，并且底部控件的Y值在屏幕之外，顶部控件的Y值加上他的高度
			// 大于0，即可以看到上面的第一个控件的时候，顶部增加一个控件
		} else if (mSCROLL_STATU == SCROLL_STATE.STATE_SCROLLED_DONW.nativeInt) {
			if (location[1] > getHeight()
					&& location2[1] + firstM.getHeight() > 0) {
				int id = mCalendars.get(0).getId();
				params.addRule(RelativeLayout.ABOVE, id);
				MyCalendarLayout newM = new MyCalendarLayout(getContext());
				createMonth(newM, System.currentTimeMillis(),
						minSubStractMonth - 1);
				mainContent.addView(newM, 0,params);
				// 向上滑动，顶部增加控件，但不把向下滚动的关键
				int aa = (int) (scrollY+mainContent.getMeasuredHeight()/mainContent.getChildCount());
				super.scrollTo(0, aa);
				mCalendars.add(0, newM);
				System.out.println("add head");
			}
		}
		mSCROLL_STATU = SCROLL_STATE.STATE_STATIC.nativeInt;
	}
	
	   @Override
	    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
	        ViewGroup.LayoutParams lp = child.getLayoutParams();

	        int childWidthMeasureSpec;
	        int childHeightMeasureSpec;

	        childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft()
	                + getPaddingRight(), lp.width);

	        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	        
	        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
	    }
	   
	   /**
	    * 关键性作用，重写这里，来达到对子控件的适应
	    */
	   @Override
	    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
	            int parentHeightMeasureSpec, int heightUsed) {
	        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

	        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
	        		getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin
	                        + widthUsed, lp.width);
	        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
	                lp.topMargin + lp.bottomMargin, MeasureSpec.UNSPECIFIED);
	        totalHeight = MeasureSpec.getSize(childHeightMeasureSpec);
	        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
	    }
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		if(!mFillViewPort) {
			return;
		}
		// fillviewPort
		 final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	        if (heightMode == MeasureSpec.UNSPECIFIED) {
	            return;
	        }

	        if (getChildCount() > 0) {
	            final View child = getChildAt(0);
	            int height = getMeasuredHeight();
	            if (child.getMeasuredHeight() < height) {
	            	 System.out.println("child.getMeasuredHeight():"+child.getMeasuredHeight()+"height:"+height);
	                final FrameLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();

	                int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
	                        getPaddingLeft() + getPaddingRight(), lp.width);
	                height -= getPaddingTop();
	                height -= getPaddingBottom();
	                int childHeightMeasureSpec =
	                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

	                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
	            }
	        }
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
	//	super.onLayout(changed, left, top, right, bottom);
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if(child.getVisibility() != GONE) {
				final LayoutParams params = (LayoutParams) child.getLayoutParams();
				
				final int width = child.getMeasuredWidth();
				final int height = child.getMeasuredHeight();
				
				int childleft = 0;
				int childtop = getPaddingTop()+params.topMargin;
				
				child.layout(childleft, childtop, childleft+width, childtop+height);
			}
		}
	}
	
	/**
	 * 设置是否强制填充
	 * @param flag
	 */
	public void setFillViewPort(boolean flag) {
		if(this.mFillViewPort != flag) {
			this.mFillViewPort = flag;
			requestLayout();
		}
	}
	
	/**
	 * 是否强制填充
	 * @return
	 */
	public boolean isFillViewPort() {
		return mFillViewPort;
	}
	
	   @Override
    public void addView(View child) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }

        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }

        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }

        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }

        super.addView(child, index, params);
    }
    
    /**
     * 返回子控件的bottom减去父控件的大小，即判断是否到最底部
     * @return
     */
    private int getScrollRangeY() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            scrollRange = Math.max(0,
                    child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
            System.out.println(" child.getHeight():"+ child.getHeight());
        }
        return scrollRange;
    }

	    
	    
	 /******************   内部类/枚举   *********/
	/**
	 * 每行日期
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
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			MAX_TAB_WIDTH = MeasureSpec.getSize(widthMeasureSpec) / 7;
		}

	}

	/**
	 * 每天日期
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
	 * scrollview滚动状态枚举
	 * 
	 * @author wangk
	 *
	 */
	static enum SCROLL_STATE {
		/**
		 * 静止
		 */
		STATE_STATIC(0),
		/**
		 * 屏幕向下，即手指向上产生的滑动
		 */
		STATE_SCROLLED_UP(1),
		/**
		 * 屏幕向上，即手指向下产生的滑动
		 */
		STATE_SCROLLED_DONW(2);

		private int nativeInt;

		private SCROLL_STATE(int nativeInt) {
			this.nativeInt = nativeInt;
		}
	}
	
	

	    
}
