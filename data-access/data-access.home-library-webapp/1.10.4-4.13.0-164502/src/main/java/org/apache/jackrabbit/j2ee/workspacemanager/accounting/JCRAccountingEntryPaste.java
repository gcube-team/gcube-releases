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



public class JCRAccountingEntryPaste extends JCRAccountingEntry{


	public JCRAccountingEntryPaste(Node node) throws RepositoryException {
		super(node);
		
//		item.setEntryType(AccountingEntryType.PASTE);
		
		String fromPath = node.getProperty(AccountingProperty.FROM_PATH.toString()).getString();	

		HashMap<AccountingProperty, String> map = new HashMap<AccountingProperty, String>();
		map.put(AccountingProperty.FROM_PATH, new XStream().toXML(fromPath));
		 
		item.setAccountingProperties(map);
		
		
	}



}
