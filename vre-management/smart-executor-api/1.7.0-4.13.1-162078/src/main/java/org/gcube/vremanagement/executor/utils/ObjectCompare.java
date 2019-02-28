/**
 * 
 */
package org.gcube.vremanagement.executor.utils;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ObjectCompare<CO extends Comparable<CO>> {

	public int compare(CO first, CO second){
		if(first==null){
			if(second==null){
				return 0;
			}
			return -1;
		}
		
		if(second==null){
			return 1;
		}
		
		return first.compareTo(second);
	}
	
}
