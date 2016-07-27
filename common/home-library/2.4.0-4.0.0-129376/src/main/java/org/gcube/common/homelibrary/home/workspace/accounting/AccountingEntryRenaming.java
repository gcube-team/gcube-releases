/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.accounting;

/**
 * @author gioia
 *
 */
public interface AccountingEntryRenaming extends AccountingEntry {
	
	String getOldItemName();
	
	String getNewItemName();
}
