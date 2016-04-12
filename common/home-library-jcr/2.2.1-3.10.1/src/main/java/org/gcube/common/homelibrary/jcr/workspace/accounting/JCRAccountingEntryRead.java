/**
 * 
 */
package org.gcube.common.homelibrary.jcr.workspace.accounting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryRead;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;

import com.thoughtworks.xstream.XStream;

/**
 * @author Antonio Gioia antonio.gioia@isti.cnr.it
 *
 */
public class JCRAccountingEntryRead extends JCRAccountingEntry  implements AccountingEntryRead {

	private final String itemName;

	public JCRAccountingEntryRead(String id, String user, Calendar date, String itemName) {
		super(id, user, date);	
		
		this.itemName = itemName;
		
		Map<AccountingProperty, String> properties = entryDelegate.getAccountingProperties();
		properties.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));
		
		entryDelegate.setEntryType(AccountingEntryType.READ);
		
	}
	
	/**
	 * @param node
	 * @throws RepositoryException
	 */
	public JCRAccountingEntryRead(AccountingDelegate node) throws RepositoryException {
		super(node);
		
		this.itemName = (String) new XStream().fromXML(entryDelegate.getAccountingProperties().get(AccountingProperty.ITEM_NAME));
	}
	
	
	@Override
	public String getItemName() {		
		return itemName;
	}
	
	@Override
	public AccountingEntryType getEntryType() {
		return AccountingEntryType.READ;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String user = super.getUser();
		Calendar date = super.getDate();
		return String.format("Read[ itemName:%s, user:%s, date:%s ]", itemName, user,sdf.format(date.getTime()));
	}


}
