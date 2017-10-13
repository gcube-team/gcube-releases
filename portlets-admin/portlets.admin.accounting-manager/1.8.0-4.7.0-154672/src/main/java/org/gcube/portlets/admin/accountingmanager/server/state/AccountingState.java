package org.gcube.portlets.admin.accountingmanager.server.state;

import java.io.Serializable;
import java.util.HashMap;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingState implements Serializable {

	private static final long serialVersionUID = 5993049979009321365L;
	private HashMap<AccountingType, AccountingStateData> state;

	public AccountingState() {
		state = new HashMap<AccountingType, AccountingStateData>();
	}

	public AccountingStateData getState(AccountingType accountingType) {
		return state.get(accountingType);
	}

	public void setState(AccountingType accountingType,
			AccountingStateData accountingStateData) {
		state.put(accountingType, accountingStateData);
	}

	@Override
	public String toString() {
		return "AccountingState [state=" + state + "]";
	}

	
	

}
