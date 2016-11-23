package org.gcube.application.aquamaps.publisher.impl;

import java.util.ArrayList;
import java.util.Iterator;

public class Util {

	
	public static <T> ArrayList<T> iteratorToArrayList(Iterator<T> it){
		ArrayList<T> toReturn = new ArrayList<T>();
		while (it.hasNext())
			toReturn.add(it.next());
		return toReturn;
	}
}
