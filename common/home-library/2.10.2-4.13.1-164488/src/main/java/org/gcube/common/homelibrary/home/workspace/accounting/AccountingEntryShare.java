/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.accounting;

import java.util.List;

/**
 * @author Valentina Marioli
 *
 */
public interface AccountingEntryShare extends AccountingEntry{

	String getItemName();

	/**
	 * @return
	 */
	List<String> getMembers();
	
}
