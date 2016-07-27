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
public class JCRAccountingEntryRestore extends JCRAccountingEntry {
	

	protected String itemName;

	/**
	 * @param node
	 * @throws RepositoryException
	 */
	public JCRAccountingEntryRestore(Node node) throws RepositoryException {
		
		super(node);
		
//		item.setEntryType(AccountingEntryType.UNSHARE);
		
		String itemName = node.getProperty(AccountingProperty.ITEM_NAME.toString()).getString();	

		HashMap<AccountingProperty, String> map = new HashMap<AccountingProperty, String>();
		map.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));

		item.setAccountingProperties(map);

	}
	
}
