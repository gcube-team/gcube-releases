package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public enum AccountingPeriodMode {
	YEARLY("Yearly"), 
	MONTHLY("Monthly"), 
	DAILY("Daily"), 
	HOURLY("Hourly"), 
	MINUTELY("Minutely"); 
	//SECONDLY("Secondly"), 
	//MILLISECONDLY("Per Millisecond");
	
	
	/**
	 * @param text
	 */
	private AccountingPeriodMode(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}

	public String getLabel() {
		return id;
	}
	
	public String getId(){
		return id;
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static AccountingPeriodMode getAccountingPeriodModeFromId(String id) {
		if(id==null||id.isEmpty()) return null;
		
		for (AccountingPeriodMode columnDataType : values()) {
			if (columnDataType.id.compareToIgnoreCase(id) == 0) {
				return columnDataType;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static List<AccountingPeriodMode> asList() {
		List<AccountingPeriodMode> list=Arrays.asList(values());
		return list;
	}
	
	
}
