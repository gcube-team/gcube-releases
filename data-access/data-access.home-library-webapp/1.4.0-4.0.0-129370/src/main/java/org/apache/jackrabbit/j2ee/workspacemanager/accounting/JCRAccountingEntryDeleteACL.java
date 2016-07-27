/**
 * 
 */
package org.apache.jackrabbit.j2ee.workspacemanager.accounting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;


import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * @author Valentina Marioli valentina.marioli@isti.cnr.it
 *
 */
public class JCRAccountingEntryDeleteACL extends JCRAccountingEntry{

	protected String itemName;
	protected List<String> members;

	
	protected static Logger logger = LoggerFactory.getLogger(JCRAccountingEntryDeleteACL.class);
	
	/**
	 * @param node
	 * @throws RepositoryException
	 */
	public JCRAccountingEntryDeleteACL(Node node) throws RepositoryException {

		super(node);
		
//		item.setEntryType(AccountingEntryType.DELETE_ACL);
		
		String itemName = node.getProperty(AccountingProperty.ITEM_NAME.toString()).getString();	
		ArrayList<String> members = new ArrayList<String>();
		
		for(Value v : node.getProperty(AccountingProperty.MEMBERS.toString()).getValues()) {
			   if(v!=null){
				   members.add(v.getString());
			   }
		}  
		
		HashMap<AccountingProperty, String> map = new HashMap<AccountingProperty, String>();
		map.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));
		map.put(AccountingProperty.MEMBERS, new XStream().toXML(members));
		 
		item.setAccountingProperties(map); 
		
 
	}


}
