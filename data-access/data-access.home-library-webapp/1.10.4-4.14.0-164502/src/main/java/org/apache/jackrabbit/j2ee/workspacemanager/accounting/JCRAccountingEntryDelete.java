/**
 * 
 */
package org.apache.jackrabbit.j2ee.workspacemanager.accounting;

import java.util.HashMap;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;

import com.thoughtworks.xstream.XStream;



/**
 * @author Valentina Marioli valentina.marioli@isti.cnr.it
 *
 */
public class JCRAccountingEntryDelete extends JCRAccountingEntry {
	

	protected String itemName;

	/**
	 * @param node
	 * @throws RepositoryException
	 */
	public JCRAccountingEntryDelete(Node node) throws RepositoryException {
		
		super(node);
		
//		item.setEntryType(AccountingEntryType.UNSHARE);
		
		String itemName = node.getProperty(AccountingProperty.ITEM_NAME.toString()).getString();	
		String from = node.getProperty(AccountingProperty.FROM_PATH.toString()).getString();

		HashMap<AccountingProperty, String> map = new HashMap<AccountingProperty, String>();
		map.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));
		map.put(AccountingProperty.FROM_PATH, new XStream().toXML(from));
		
		item.setAccountingProperties(map);

	}
	
}
