/**
 * 
 */
package org.gcube.common.homelibrary.jcr.workspace.accounting;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryShare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * @author Valentina Marioli valentina.marioli@isti.cnr.it
 *
 */
public class JCRAccountingEntryDeleteACL extends JCRAccountingEntry implements AccountingEntryShare {


	protected static Logger logger = LoggerFactory.getLogger(JCRAccountingEntryDeleteACL.class);
	
	private final String itemName;
	private final List<String> members;
	
	/**
	 * @param node
	 * @throws RepositoryException
	 */
	@SuppressWarnings("unchecked")
	public JCRAccountingEntryDeleteACL(AccountingDelegate node) throws RepositoryException {
		super(node);
		
		this.itemName = (String) new XStream().fromXML(entryDelegate.getAccountingProperties().get(AccountingProperty.ITEM_NAME));
		this.members = (List<String>) new XStream().fromXML(entryDelegate.getAccountingProperties().get(AccountingProperty.MEMBERS));
	}

	public JCRAccountingEntryDeleteACL(String id, String user, Calendar date, String itemName, List<String> members) {
		super(id, user, date);
		
		this.itemName = itemName;
		this.members = members;

		Map<AccountingProperty, String> properties = entryDelegate.getAccountingProperties();
		properties.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));

		//convert arrayList to Array
		String[] array = members.toArray(new String[members.size()]);
		properties.put(AccountingProperty.MEMBERS, new XStream().toXML(array));
		
		entryDelegate.setEntryType(AccountingEntryType.DELETE_ACL);
	}

	@Override
	public AccountingEntryType getEntryType() {
		return AccountingEntryType.DELETE_ACL;
	}

	@Override
	public String toString() {
		String parentValue = super.toString();
		return String.format("[%s [%s]]",parentValue, getEntryType());
	}

	@Override
	public String getItemName() {		
		return itemName;	
	}

	@Override
	public List<String> getMembers() {		
		return members;
	}

}
