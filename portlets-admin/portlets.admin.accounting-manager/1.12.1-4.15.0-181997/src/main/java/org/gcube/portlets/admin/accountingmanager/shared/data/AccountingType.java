/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager.shared.data;

/**
 * 
  * @author Giancarlo Panichi 
 *
 *
 */
public enum AccountingType {
	STORAGE,
	SERVICE,
	PORTLET,
	TASK,
	JOB,
	SPACE;
	
	public static AccountingType getTypeFromString(String value){
		for(AccountingType a:values()){
			if(a.name().compareToIgnoreCase(value)==0){
				return a;
			} 
		}
		return null;
	}
	
		
}
