/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.accounting;

import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;

/**
 * @author gioia
 *
 */
public interface AccountingEntryRemoval extends AccountingEntry {

	WorkspaceItemType getItemType();
	
	FolderItemType getFolderItemType();
	
	String getItemName();
	
	String mimeType();
}
