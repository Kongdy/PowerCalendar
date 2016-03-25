package com.example.powercalendar;

import java.util.List;
import java.util.Set;


/**
 * ����ö��
 * @author wangk
 */
public class Week_Day extends SafeEnum<Week_Day> {
	public static SafeEnumCollection<Week_Day> values = new SafeEnumCollection<Week_Day>();
	
	public static Week_Day SUNDAY = new Week_Day("��", "������");
	public static Week_Day MONDAY = new Week_Day("һ", "����һ");
	public static Week_Day TUESDDAY = new Week_Day("��", "���ڶ�");
	public static Week_Day WNDESDAY = new Week_Day("��", "������");
	public static Week_Day THURSDAY = new Week_Day("��", "������");
	public static Week_Day FRIDAY = new Week_Day("��", "������");
	public static Week_Day SATURDAY = new Week_Day("��", "������");

	public Week_Day(String name, String decription) {
		super(values, name, decription);
	}
	
	public  List<Week_Day> values() {
		return values.values();
	}

}
