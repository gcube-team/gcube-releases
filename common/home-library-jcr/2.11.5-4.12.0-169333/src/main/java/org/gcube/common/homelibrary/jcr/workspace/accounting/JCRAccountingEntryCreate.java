/**
 * 
 */
package org.gcube.common.homelibrary.jcr.workspace.accounting;

import java.util.Calendar;
import java.util.Map;

import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryCreate;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;

import com.thoughtworks.xstream.XStream;

/**
 * @author Antonio Gioia antonio.gioia@isti.cnr.it
 *
 */
public class JCRAccountingEntryCreate extends JCRAccountingEntry implements AccountingEntryCreate {
	
	private final String itemName;
	/**
	 * @param node
	 * @throws RepositoryException
	 */
	public JCRAccountingEntryCreate(AccountingDelegate node) throws RepositoryException {
		super(node);
		
		this.itemName = (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.ITEM_NAME));
	}
	
	public JCRAccountingEntryCreate(String id, String user, Calendar date, String itemName) {
		super(id, user, date);
		
		this.itemName = itemName;
		
		Map<AccountingProperty, String> properties = entryDelegate.getAccountingProperties();
		properties.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));
		
		entryDelegate.setEntryType(AccountingEntryType.CREATE);
	}

	@Override
	public AccountingEntryType getEntryType() {
		return AccountingEntryType.CREATE;
	}
	
	@Override
	public String toString() {
		String parentValue = super.toString();
		return String.format("[%s [%s]]",parentValue, getEntryType());
	}
	
	
	public String getItemName() {		
		return itemName;
	}

}
