/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager.shared.data;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public enum AccountingType {
	STORAGE,
	SERVICE,
	PORTLET,
	TASK,
	JOB;
	
	public static AccountingType getTypeFromString(String value){
		for(AccountingType a:values()){
			if(a.name().compareToIgnoreCase(value)==0){
				return a;
			} 
		}
		return null;
	}
	
		
}
