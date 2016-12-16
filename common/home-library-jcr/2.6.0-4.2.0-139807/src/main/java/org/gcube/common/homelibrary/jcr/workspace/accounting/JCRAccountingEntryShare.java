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
public class JCRAccountingEntryShare extends JCRAccountingEntry implements AccountingEntryShare {

	private final String itemName;
	private final List<String> members;

	
	protected static Logger logger = LoggerFactory.getLogger(JCRAccountingEntryShare.class);
	
	/**
	 * @param node
	 * @throws RepositoryException
	 */
	@SuppressWarnings("unchecked")
	public JCRAccountingEntryShare(AccountingDelegate node) throws RepositoryException {

		super(node);
		this.itemName = (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.ITEM_NAME));	
		this.members = (List<String>) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.MEMBERS));
	}

	public JCRAccountingEntryShare(String id, String user, Calendar date, String nodeName, List<String> members) {
		super(id, user, date);

		this.itemName = nodeName;
		this.members = members;
		
		Map<AccountingProperty, String> properties = entryDelegate.getAccountingProperties();
		properties.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));
		
		String[] array = members.toArray(new String[members.size()]);
		properties.put(AccountingProperty.MEMBERS, new XStream().toXML(array));
		
		entryDelegate.setEntryType(AccountingEntryType.SHARE);
	}

	@Override
	public AccountingEntryType getEntryType() {
		return AccountingEntryType.SHARE;
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
