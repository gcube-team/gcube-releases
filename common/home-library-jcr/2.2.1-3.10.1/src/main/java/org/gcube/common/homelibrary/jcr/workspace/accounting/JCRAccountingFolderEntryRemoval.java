/**
 * 
 */
package org.gcube.common.homelibrary.jcr.workspace.accounting;

import java.util.Calendar;
import java.util.Map;

import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRemoval;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;

import com.thoughtworks.xstream.XStream;

/**
 * @author Antonio Gioia antonio.gioia@isti.cnr.it
 *
 */
public class JCRAccountingFolderEntryRemoval extends JCRAccountingEntry implements
		AccountingEntryRemoval {
	
	protected final WorkspaceItemType itemType;
	protected final FolderItemType folderItemType;
	protected final String itemName;
	protected final String mimeType;
	
	
	public JCRAccountingFolderEntryRemoval(AccountingDelegate node) throws RepositoryException {
		super(node);
		
		this.itemName =  (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.ITEM_NAME));
		this.itemType =  (WorkspaceItemType) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.ITEM_TYPE));
		this.folderItemType = (FolderItemType) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.FOLDER_ITEM_TYPE));
		this.mimeType = (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.MIME_TYPE));
		
	}
	
	public JCRAccountingFolderEntryRemoval(String id, String user, Calendar date,
			WorkspaceItemType itemType, FolderItemType folderItemType,
			String itemName, String mimeType) {
		super(id, user, date);
		
		this.itemName = itemName;
		this.folderItemType = folderItemType;
		this.itemType = itemType;
		this.mimeType = mimeType;
		
		Map<AccountingProperty, String> properties = entryDelegate.getAccountingProperties();
		properties.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));
		properties.put(AccountingProperty.FOLDER_ITEM_TYPE, new XStream().toXML(folderItemType));
		properties.put(AccountingProperty.ITEM_TYPE, new XStream().toXML(itemType));
		properties.put(AccountingProperty.MIME_TYPE, new XStream().toXML(mimeType));
		
		entryDelegate.setEntryType(AccountingEntryType.REMOVAL);
	}

	@Override
	public AccountingEntryType getEntryType() {
		return AccountingEntryType.REMOVAL;
	}

	@Override
	public WorkspaceItemType getItemType() {
		return itemType;
	}

	@Override
	public FolderItemType getFolderItemType() {
		return folderItemType;
	}

	@Override
	public String getItemName() {
		return itemName;
	}

	@Override
	public String mimeType() {
		return mimeType;
	}
	
//	@Override
//	public void save() throws RepositoryException {
//		
////		Map<AccountingProperty, String> properties = entryDelegate.getAccountingProperties();
////		properties.put(AccountingProperty.ITEM_NAME, itemName);
////		properties.put(AccountingProperty.ITEM_TYPE, itemType.toString());
////		if (folderItemType != null)
////			properties.put(AccountingProperty.FOLDER_ITEM_TYPE, folderItemType.toString());
////		if (mimeType != null)
////			properties.put(AccountingProperty.MIME_TYPE, mimeType);
//		
//		super.save();
//
//	}
	
	@Override
	public String toString() {
		String parentValue = super.toString();
		return String.format("[%s [%s, itemName:%s, itemType:%s, folderItemType:%s, mimeType:%s]]",
				parentValue, AccountingEntryType.REMOVAL, itemName, itemType, folderItemType, mimeType);
	}

}
