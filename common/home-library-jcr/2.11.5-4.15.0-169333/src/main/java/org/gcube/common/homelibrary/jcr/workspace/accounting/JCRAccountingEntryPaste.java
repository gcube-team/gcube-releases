/**
 * 
 */
package org.gcube.common.homelibrary.jcr.workspace.accounting;

import java.util.Calendar;
import java.util.Map;

import org.gcube.common.homelibrary.model.exceptions.RepositoryException;

import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntryPaste;
import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;

import com.thoughtworks.xstream.XStream;

/**
 * @author Antonio Gioia antonio.gioia@isti.cnr.it
 *
 */
public class JCRAccountingEntryPaste extends JCRAccountingEntry implements AccountingEntryPaste {

	private final String fromPath;
	
	public JCRAccountingEntryPaste(AccountingDelegate node) throws RepositoryException {
		super(node);
		this.fromPath = (String) new XStream().fromXML(node.getAccountingProperties().get(AccountingProperty.FROM_PATH));	
	}

	public JCRAccountingEntryPaste(String id, String user, Calendar date, String fromPath) {
		super(id, user,date);
		
		this.fromPath = fromPath;
		
		Map<AccountingProperty, String> properties = entryDelegate.getAccountingProperties();
		properties.put(AccountingProperty.FROM_PATH, new XStream().toXML(fromPath));
		
		entryDelegate.setEntryType(AccountingEntryType.PASTE);
		
	}

	@Override
	public String getFromPath() {
		return fromPath;
	}

	@Override
	public AccountingEntryType getEntryType() {
		return AccountingEntryType.PASTE;
	}

	@Override
	public String toString() {
		String parentValue = super.toString();
		return String.format("[%s [%s, fromPath:%s]]",parentValue, AccountingEntryType.PASTE, (String) new XStream().fromXML(entryDelegate.getAccountingProperties().get(AccountingProperty.FROM_PATH)));
	}


}
