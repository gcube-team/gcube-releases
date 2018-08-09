/**
 * 
 */
package org.gcube.common.homelibrary.jcr.workspace.accounting;

import java.util.Calendar;
import java.util.Map;

import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRestore;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryUnshare;

import com.thoughtworks.xstream.XStream;
/**
 * @author Valentina Marioli valentina.marioli@isti.cnr.it
 *
 */
public class JCRAccountingEntryRestore extends JCRAccountingEntry implements AccountingEntryRestore {
	
	protected String itemName;

	/**
	 * @param node
	 * @throws RepositoryException
	 */
	public JCRAccountingEntryRestore(AccountingDelegate node) throws RepositoryException {
		
		super(node);
		this.itemName =  (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.ITEM_NAME));

	}
	
	public JCRAccountingEntryRestore(String id, String user, Calendar date, String nodeName) {
		super(id, user, date);
		
		this.itemName = nodeName;
		
		Map<AccountingProperty, String> properties = entryDelegate.getAccountingProperties();
		properties.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));
		
		entryDelegate.setEntryType(AccountingEntryType.RESTORE);
	}

	@Override
	public AccountingEntryType getEntryType() {
		return AccountingEntryType.RESTORE;
	}
	
	@Override
	public String toString() {
		String parentValue = super.toString();
		return String.format("[%s [%s, itemName:%s]]",parentValue, getEntryType(), getItemName());
	}
	
	
	@Override
	public String getItemName() {		
		return itemName;
	}

}
