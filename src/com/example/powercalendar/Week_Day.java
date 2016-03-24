package com.example.powercalendar;

import java.util.List;
import java.util.Set;


/**
 * 星期枚举
 * @author wangk
 */
public class Week_Day extends SafeEnum<Week_Day> {
	public static SafeEnumCollection<Week_Day> values = new SafeEnumCollection<Week_Day>();
	
	public static Week_Day SUNDAY = new Week_Day("日", "星期日");
	public static Week_Day MONDAY = new Week_Day("一", "星期一");
	public static Week_Day TUESDDAY = new Week_Day("二", "星期二");
	public static Week_Day WNDESDAY = new Week_Day("三", "星期三");
	public static Week_Day THURSDAY = new Week_Day("四", "星期四");
	public static Week_Day FRIDAY = new Week_Day("五", "星期五");
	public static Week_Day SATURDAY = new Week_Day("六", "星期六");

	public Week_Day(String name, String decription) {
		super(values, name, decription);
	}
	
	public  List<Week_Day> values() {
		return values.values();
	}

}
