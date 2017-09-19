/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.shared;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 30, 2014
 *
 */
public class FieldValidator {
	
	
	public static boolean validateByClassName(Class<?> className, String[] valueConstraintsClassName){
		
		
		if(valueConstraintsClassName!=null && valueConstraintsClassName.length>0){
	
			for (String valueCs : valueConstraintsClassName) {
				if(valueCs.compareTo(className.getName())!=0)
					return false;
			}
		}
		
		return true;
		
	}

}
