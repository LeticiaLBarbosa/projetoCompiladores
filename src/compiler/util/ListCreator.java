package compiler.util;

import java.util.ArrayList;

public class ListCreator {
	
	
	public static <T> ArrayList<T> newList(T... elements) {
		ArrayList<T> list = new ArrayList<T>();
		for (int i = 0 ; i < elements.length ; i++) {
			list.add(elements[i]);
		}
		return list;
	}
}
