/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client.util;

import java.util.ArrayList;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class Util {
	
	public static <T> ArrayList<T> asList(T ... array)
	{
		ArrayList<T> list = new ArrayList<T>();
		if (array!=null) for (T item:array) list.add(item);
		return list;
	}

}
