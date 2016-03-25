package com.example.powercalendar.tools;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * ������
 * @author wangk
 */
public class Utils {
	
	 /**
     * ���붯̬ͨ���ض���dipֵ����һ����С
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
