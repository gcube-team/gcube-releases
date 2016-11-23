/**
 * 
 */
package org.gcube.common.homelibrary.jcr.workspace.accounting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibary.model.items.accounting.AccountingProperty;
import org.gcube.common.homelibrary.home.workspace.accounting.AccountingEntry;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper.AccountingManager;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * @author Antonio Gioia antonio.gioia@isti.cnr.it
 *
 */
public abstract class JCRAccountingEntry implements AccountingEntry {
	

	protected AccountingDelegate entryDelegate;
	protected String user;
	protected Calendar date;
	
	protected static Logger logger = LoggerFactory.getLogger(JCRAccountingEntry.class);
	
	public JCRAccountingEntry(AccountingDelegate entryDelegate) throws RepositoryException {
		this.entryDelegate = entryDelegate;
		this.user = entryDelegate.getUser();
		this.date = entryDelegate.getDate();
	}
	
	public JCRAccountingEntry(String id, String user, Calendar date) {
		this.user = user;
		this.date = date;
		
		this.entryDelegate = new AccountingDelegate();
		entryDelegate.setId(id);
		entryDelegate.setUser(user);
		entryDelegate.setDate(date);
		
		entryDelegate.setAccountingProperties(new HashMap<AccountingProperty, String>());
	}

	@Override
	public abstract AccountingEntryType getEntryType();
	
	@Override
	public String getUser() {
		return user;
	}

	@Override
	public Calendar getDate() {
		return date;
	}

	public void save(JCRSession servlets) throws RepositoryException  {

		AccountingManager wrap = new AccountingManager(entryDelegate);
		try {
			wrap.save(servlets);
		} catch (Exception e) {
			logger.error("impossible to add accounting entry ", e);
		}

	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return String.format("[ user:%s, date:%s ]",user ,sdf.format(date.getTime()));
	}

}
