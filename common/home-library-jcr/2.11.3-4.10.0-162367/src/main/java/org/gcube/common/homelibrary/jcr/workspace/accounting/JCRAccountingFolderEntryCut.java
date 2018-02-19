/**
 * 
 */
package org.gcube.common.homelibrary.jcr.workspace.accounting;

import java.util.Calendar;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;

import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryCut;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;

/**
 * @author Antonio Gioia antonio.gioia@isti.cnr.it
 *
 */
public class JCRAccountingFolderEntryCut extends
		JCRAccountingFolderEntryRemoval implements AccountingEntryCut {

	/**
	 * @param user
	 * @param date
	 * @param itemType
	 * @param folderItemType
	 * @param itemName
	 * @param mimeType
	 */
	public JCRAccountingFolderEntryCut(String id, String user, Calendar date,
			WorkspaceItemType itemType, FolderItemType folderItemType,
			String itemName, String mimeType) {
		super(id, user, date, itemType, folderItemType, itemName, mimeType);
		
		entryDelegate.setEntryType(AccountingEntryType.CUT);
	}
	
	
	public JCRAccountingFolderEntryCut(AccountingDelegate node) throws RepositoryException {
		super(node);
		
		
	}

	@Override
	public AccountingEntryType getEntryType() {
		return AccountingEntryType.CUT;
	}
	
	@Override
	public String toString() {
		String parentValue = super.toString();
		return String.format("[%s [%s, itemName:%s, itemType:%s, folderItemType:%s, mimeType:%s]]",
				parentValue, AccountingEntryType.CUT, itemName, itemType, folderItemType, mimeType);
	}
	

}
