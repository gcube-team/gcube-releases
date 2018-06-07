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
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryDelete;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryUnshare;

import com.thoughtworks.xstream.XStream;
/**
 * @author Valentina Marioli valentina.marioli@isti.cnr.it
 *
 */
public class JCRAccountingEntryDelete extends JCRAccountingEntry implements AccountingEntryDelete {
	
	protected String itemName;
	protected String from;

	/**
	 * @param node
	 * @throws RepositoryException
	 */
	public JCRAccountingEntryDelete(AccountingDelegate node) throws RepositoryException {
		
		super(node);
		this.itemName =  (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.ITEM_NAME));
		this.from =  (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.FROM_PATH));

	}
	
	public JCRAccountingEntryDelete(String id, String user, Calendar date, String nodeName, String from) {
		super(id, user, date);
		
		this.itemName = nodeName;
		this.from = from;
		
		Map<AccountingProperty, String> properties = entryDelegate.getAccountingProperties();
		properties.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));
		properties.put(AccountingProperty.FROM_PATH, new XStream().toXML(from));
		
		entryDelegate.setEntryType(AccountingEntryType.DELETE);
	}

	@Override
	public AccountingEntryType getEntryType() {
		return AccountingEntryType.DELETE;
	}
	
	@Override
	public String toString() {
		String parentValue = super.toString();
		return String.format("[%s [%s, itemName:%s, deletedFrom:%s]]",parentValue, getEntryType(), getItemName(), getDeletedFrom());

	}
	
	
	@Override
	public String getItemName() {		
		return itemName;
	}

	@Override
	public String getDeletedFrom() { 
		return from;
	}

}
