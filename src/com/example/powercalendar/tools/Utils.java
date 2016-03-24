package com.example.powercalendar.tools;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * 工具类
 * @author wangk
 */
public class Utils {
	
	 /**
     * 代码动态通过特定的dip值返回一个大小
     * @param context
     * @param unit
     * @param value
     * @return
     */
    public static float getRawSize(Context context,int unit,float value) {
        Resources res = context.getResources();
        return TypedValue.applyDimension(unit,value,res.getDisplayMetrics());
    }
	
}
