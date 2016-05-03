package com.example.powercalendar;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

/**
 * 表示日历上的每行
 * @author wangk
 */
public class MyCalendarLayout extends LinearLayout {
	
	private int postion = -1;
	
	private int preRow = -1;
	
	private LayoutTransition layoutTransition;

	public MyCalendarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public MyCalendarLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		setOrientation(VERTICAL);
		layoutTransition = new LayoutTransition();
//		AlphaAnimation alphaAnimal = new AlphaAnimation(0.0f, 1.0f);
//		ScaleAnimation scaleAnimal = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f
//				,Animation.RELATIVE_TO_SELF,0.5f);
//		alphaAnimal.setDuration(2000);
//		scaleAnimal.setDuration(2000);
//		scaleAnimal.start();
//		ObjectAnimator animator = ObjectAnimator.ofFloat(null, "scaleY", 0.0f,1.0f);
//		animator.setDuration(1000);
//		layoutTransition.setAnimator(LayoutTransition.APPEARING, animator);
		this.setLayoutTransition(layoutTransition);
	}

	@Override
	public ViewPropertyAnimator animate() {
		return super.animate();
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
