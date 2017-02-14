/**
 * 
 */
package org.gcube.common.homelibrary.jcr.workspace.accounting;

import java.util.Calendar;
import java.util.Map;

import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRenaming;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;

import com.thoughtworks.xstream.XStream;
/**
 * @author Antonio Gioia antonio.gioia@isti.cnr.it
 *
 */
public class JCRAccountingEntryRenaming extends JCRAccountingEntry implements AccountingEntryRenaming{

	private final String oldItemName;
	private String newItemName;

	public JCRAccountingEntryRenaming(AccountingDelegate node) throws RepositoryException {
		super(node);

		this.oldItemName = (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.OLD_ITEM_NAME));
		try{
			this.newItemName = (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.NEW_ITEM_NAME));
		}catch (Exception e){
			this.newItemName = "";
		}
	}

	public JCRAccountingEntryRenaming(String id, String user, Calendar date, String oldItemName, String newItemName) {
		super(id, user, date);

		this.oldItemName = oldItemName;
		this.newItemName = newItemName;

		Map<AccountingProperty, String> properties = entryDelegate.getAccountingProperties();
		properties.put(AccountingProperty.OLD_ITEM_NAME, new XStream().toXML(oldItemName));
		properties.put(AccountingProperty.NEW_ITEM_NAME, new XStream().toXML(newItemName));

		entryDelegate.setEntryType(AccountingEntryType.RENAMING);
	}

	@Override
	public String getOldItemName() {
		return oldItemName;
	}

	@Override
	public String getNewItemName() {
		return newItemName;
	}

	@Override
	public AccountingEntryType getEntryType() {
		return AccountingEntryType.RENAMING;
	}

	@Override
	public String toString() {
		String parentValue = super.toString();
		return String.format("[%s [%s, oldItemName:%s, newItemName:%s]]",parentValue, AccountingEntryType.RENAMING, oldItemName, newItemName);
	}


}
