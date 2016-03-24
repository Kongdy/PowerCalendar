package com.example.powercalendar;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 加强枚举基类
 * @author wangk
 */
public class SafeEnum<T extends SafeEnum<T>> {
		private String name;
		private String description;
		private static List<Map<Type, SafeEnumCollection<?>>> enumslist = new LinkedList<Map<Type,SafeEnumCollection<?>>>();
				
		@SuppressWarnings("unchecked")
		public SafeEnum(SafeEnumCollection<T> values,String name,String decription) {
			Map<Type, SafeEnumCollection<?>> enums = new HashMap<Type,SafeEnumCollection<?>>();
			this.name = name;
			this.description = decription;
			values.add((T)this);
			enums.put(this.getClass(), values);
			if(!enumslist.contains(enums)) {
				enumslist.add(enums);
			}
		}
		
		public String name() {
			return name;
		}
		
		public String description() {
			return description;
		}
		
		public boolean equals(Object obj) {
			if(obj == null) {
				return false;
			}
			if(obj == this) {
				return true;
			}
			
			if(mEquals(this.getClass(),obj.getClass() )) {
				@SuppressWarnings("unchecked")
				final T other = (T)obj;
				
				return mEquals(name, other.name()) &&
						mEquals(description, other.description());
			}
			return false;
		}
		
		/**
		 * 枚举元素总数量
		 * @return
		 */
		public int size() {
			return enumslist.size();
		}
		
		/**
		 * 为了兼容api不够，不能使用google 的Objects.equals();
		 * @return
		 */
		public boolean mEquals(Object a, Object b) {
			return (a == null) ? (b == null) : a.equals(b);
		}

		public static class  SafeEnumCollection<T extends SafeEnum<T>> {
			
			//private Map<String, T> values = new HashMap<String,T>();
			private List<T> values = new ArrayList<T>();
			
			void add(T value) {
				values.add(value);
			//	this.values.put(value.name, value);
			}
			
			public List<T> values() {
				return values;
			}
		}
}
