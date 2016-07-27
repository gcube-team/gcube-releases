/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared.util;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 21, 2014
 *
 */
public class CutStringUtil {
	
	
	public static String cutString(String value, int maxSize){
		
		if(value==null || value.isEmpty())
			return "";
		
		if(value.length()>maxSize)
			return value.substring(0, maxSize) +"...";
		else
			return value;
	}
	
	public static String stringPurgeSuffix(String value, String suffix){
		if(value==null)
			return null;
		
		if(value.endsWith(suffix)){
			return value.substring(0, value.indexOf(suffix));
		}
		
		return value;
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(cutString("tests", 5));
	}

}
