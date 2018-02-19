package org.gcube.portlets.admin.accountingmanager.client.state;

import java.io.Serializable;
import java.util.HashMap;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingClientState implements Serializable {

	private static final long serialVersionUID = 5993049979009321365L;
	private HashMap<AccountingType, AccountingClientStateData> clientState;

	public AccountingClientState() {
		clientState = new HashMap<AccountingType, AccountingClientStateData>();
	}

	public AccountingClientStateData getState(AccountingType accountingType) {
		return clientState.get(accountingType);
	}

	public void setState(AccountingType accountingType,
			AccountingClientStateData accountingStateData) {
		clientState.put(accountingType, accountingStateData);
	}

	@Override
	public String toString() {
		return "AccountingClientState [clientState=" + clientState + "]";
	}

}
