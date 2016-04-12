/**
 * 
 */
package org.apache.jackrabbit.j2ee.workspacemanager.accounting;

import java.util.HashMap;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.j2ee.workspacemanager.accounting.get.GetAccountingById;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;


public class JCRAccountingEntryRenaming extends JCRAccountingEntry {
	private static Logger logger = LoggerFactory.getLogger(JCRAccountingEntryRenaming.class);

	public JCRAccountingEntryRenaming(Node node) throws RepositoryException {
		super(node);

		//		item.setEntryType(AccountingEntryType.RENAMING);
		HashMap<AccountingProperty, String> map = new HashMap<AccountingProperty, String>();
		String oldItemName = node.getProperty(AccountingProperty.OLD_ITEM_NAME.toString()).getString();	
		map.put(AccountingProperty.OLD_ITEM_NAME, new XStream().toXML(oldItemName));

		try{
			String newItemName = node.getProperty(AccountingProperty.NEW_ITEM_NAME.toString()).getString();	
			map.put(AccountingProperty.NEW_ITEM_NAME, new XStream().toXML(newItemName));
		}catch (Exception e){
			logger.info(AccountingProperty.NEW_ITEM_NAME + " not found in " + node.getPath());
		}

		item.setAccountingProperties(map);

	}


}
