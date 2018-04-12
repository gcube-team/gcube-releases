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
 * @author Valentina Marioli valentina.marioli@isti.cnr.it
 *
 */
public class JCRAccountingEntryRead extends JCRAccountingEntry  implements AccountingEntryRead {

	private final String itemName;
	Map<AccountingProperty, String> properties;

	public JCRAccountingEntryRead(String id, String user, Calendar date, String itemName) {
		super(id, user, date);	

		this.itemName = itemName;
		
		if (properties==null)			
			properties = entryDelegate.getAccountingProperties();
		
		properties.put(AccountingProperty.ITEM_NAME, new XStream().toXML(itemName));
		entryDelegate.setEntryType(AccountingEntryType.READ);

	}

	public JCRAccountingEntryRead(String id, String user, Calendar date, String itemName, String version) {
		
		this(id, user, date, itemName);

		this.version = version;
		
		if (version!=null)
			try{
				entryDelegate.setVersion(version);
			}catch (Exception e){
				logger.error("Please update HL model");
			}

	}

	/**
	 * @param node
	 * @throws RepositoryException
	 */
	public JCRAccountingEntryRead(AccountingDelegate node) throws RepositoryException {
		super(node);

		this.itemName = (String) new XStream().fromXML(entryDelegate.getAccountingProperties().get(AccountingProperty.ITEM_NAME));
		if (entryDelegate.getAccountingProperties().get(AccountingProperty.VERSION)!=null)
			try{
				this.version = (String) new XStream().fromXML(entryDelegate.getAccountingProperties().get(AccountingProperty.VERSION));
			}catch (Exception e){
				this.version = "";
				logger.error("Please update HL model");
			}
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
		String version = super.getVersion();
		return String.format("[ user:%s, date:%s, itemName:%s, version:%s [%s] ]", user, sdf.format(date.getTime()), itemName, version, getEntryType());

	}

	public String getVersion() {
		return version;
	}


}
