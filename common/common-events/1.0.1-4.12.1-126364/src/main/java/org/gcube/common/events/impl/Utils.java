package org.gcube.common.events.impl;

import java.util.Arrays;
import java.util.Collection;


/**
 * Library-wide utilities.
 * 
 * @author Fabio Simeoni
 *
 */
public class Utils {

	
	public static void notNull(Object o) throws IllegalArgumentException {
		notNull("argument",o);
	}
	
	public static void notNull(String name, Object o) throws IllegalArgumentException {
		if (o==null)
			throw new IllegalArgumentException(name+" is null");
	}
	
	public static void notEmpty(String name, String o) throws IllegalArgumentException {
		if (o.isEmpty())
			throw new IllegalArgumentException(name+" is empty");
	}
	
	public static void valid(String name, String o) throws IllegalArgumentException {
		notNull(name, o);
		notEmpty(name,o);
	}
	
	public static void valid(String name, Collection<String> o) throws IllegalArgumentException {
		
		for (String s : o) {
			notNull(name+"'s element", s);
			notEmpty(name+"'s element",s);
		}
	}
	
	public static void valid(String name, String[] o) throws IllegalArgumentException {
		
		valid(name,Arrays.asList(o));
	}
}
