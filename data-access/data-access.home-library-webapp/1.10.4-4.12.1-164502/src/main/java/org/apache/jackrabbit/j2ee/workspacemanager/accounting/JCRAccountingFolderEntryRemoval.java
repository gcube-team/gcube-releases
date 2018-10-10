/**
 * 
 */
package org.apache.jackrabbit.j2ee.workspacemanager.accounting;

import java.util.HashMap;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;

import com.thoughtworks.xstream.XStream;


public class JCRAccountingFolderEntryRemoval extends JCRAccountingEntry{

	
	public JCRAccountingFolderEntryRemoval(Node node) throws RepositoryException {
		super(node);
		
//		item.setEntryType(AccountingEntryType.REMOVAL);
		
		String itemName =  node.getProperty(AccountingProperty.ITEM_NAME.toString()).getString();
		WorkspaceItemType itemType =  WorkspaceItemType.valueOf(node.getProperty(AccountingProperty.ITEM_TYPE.toString()).getString());
		
		FolderItemType folderItemType = (node.hasProperty(AccountingProperty.FOLDER_ITEM_TYPE.toString()))? FolderItemType.valueOf(node.getProperty(AccountingProperty.FOLDER_ITEM_TYPE.toString()).getString()):null;
		String mimeType = (node.hasProperty(AccountingProperty.MIME_TYPE.toString())) ? node.getProperty(AccountingProperty.MIME_TYPE.toString()).getString() : null;
	
		HashMap<AccountingProperty, String> map = new HashMap<AccountingProperty, String>();
		map.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));
		map.put(AccountingProperty.ITEM_TYPE, new XStream().toXML(itemType));
		map.put(AccountingProperty.FOLDER_ITEM_TYPE, new XStream().toXML(folderItemType));
		map.put(AccountingProperty.MIME_TYPE, new XStream().toXML(mimeType));

		item.setAccountingProperties(map);
		
	
	}




}
