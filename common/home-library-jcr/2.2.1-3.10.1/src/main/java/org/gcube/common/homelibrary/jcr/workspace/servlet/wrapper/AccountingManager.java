package org.gcube.common.homelibrary.jcr.workspace.servlet.wrapper;

import java.util.Calendar;

import org.gcube.common.homelibary.model.items.accounting.AccountingDelegate;
import org.gcube.common.homelibary.model.items.accounting.AccountingEntryType;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;

public class AccountingManager {
	AccountingDelegate item;


	public AccountingManager(AccountingDelegate item){
		this.item = item;
	}

	public AccountingManager(String id, Calendar date, String user, AccountingEntryType entryType) {
		item.setId(id);
		item.setDate(date);
		item.setUser(user);
		item.setEntryType(entryType);
	}

	public void save(JCRServlets servlets) throws Exception {
			servlets.saveAccountingItem(item);
		
	}

}
