package org.gcube.datatransfer.scheduler.db.test;

import java.util.ArrayList;
import java.util.List;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestStr {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		
		int size = list.size();
		System.out.println(size);
		
		while(list.size()>0)list.remove(0);
		
		size = list.size();
		System.out.println(size);
		
		list.add("1");
		list.add("2");
		
		size = list.size();
		System.out.println(size);
		
		String[] tobestored;
		tobestored=list.toArray(new String[list.size()]);
		System.out.println(tobestored);
	}

}
