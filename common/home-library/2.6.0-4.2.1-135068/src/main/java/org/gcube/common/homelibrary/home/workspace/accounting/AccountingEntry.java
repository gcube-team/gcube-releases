/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.accounting;

import java.util.Calendar;

import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;



/**
 * @author gioia
 *
 */
public interface AccountingEntry {
	
	String getUser();
	
	Calendar getDate();
	
	AccountingEntryType getEntryType();


}
