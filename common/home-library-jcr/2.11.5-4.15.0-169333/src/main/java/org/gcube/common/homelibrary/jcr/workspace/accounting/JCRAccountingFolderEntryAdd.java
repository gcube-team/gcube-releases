/**
 * 
 */
package org.gcube.common.homelibrary.jcr.workspace.accounting;

import java.util.Calendar;
import java.util.Map;

import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryAdd;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;

import com.thoughtworks.xstream.XStream;

/**
 * @author Valentina Marioli valentina.marioli@isti.cnr.it
 *
 */
public class JCRAccountingFolderEntryAdd extends JCRAccountingEntry implements
AccountingEntryAdd{

	/**
	 * @param user
	 * @param date
	 * @param itemName
	 */
	
	private final WorkspaceItemType itemType;
	private final FolderItemType folderItemType;
	private final String itemName;
	private final String mimeType;
	
	
	public JCRAccountingFolderEntryAdd(AccountingDelegate node) throws RepositoryException {
		super(node);
		
		this.itemName =  (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.ITEM_NAME));
		this.itemType =  (WorkspaceItemType) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.ITEM_TYPE));
		this.folderItemType = (FolderItemType) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.FOLDER_ITEM_TYPE));
		this.mimeType = (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.MIME_TYPE));
				
	}
	
	public JCRAccountingFolderEntryAdd(String id, String user, Calendar date,
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
		
		entryDelegate.setEntryType(AccountingEntryType.ADD);
	}

	@Override
	public AccountingEntryType getEntryType() {
		return AccountingEntryType.ADD;
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
	
	@Override
	public String toString() {
		String parentValue = super.toString();
		return String.format("[%s [%s, itemName:%s, itemType:%s, folderItemType:%s, mimeType:%s]]",
				parentValue, AccountingEntryType.ADD, itemName, itemType, folderItemType, mimeType);
	}

}
