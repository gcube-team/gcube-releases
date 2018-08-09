package org.gcube.portlets.admin.accountingmanager.shared.tabs;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class EnableTab implements Serializable {

	private static final long serialVersionUID = -6236674776252330224L;

	private AccountingType accountingType;
	private ArrayList<String> enableRoles;

	public EnableTab() {
		super();
	}

	public EnableTab(AccountingType accountingType,
			ArrayList<String> enableRoles) {
		super();
		this.accountingType = accountingType;
		this.enableRoles = enableRoles;
	}

	public AccountingType getAccountingType() {
		return accountingType;
	}

	public void setAccountingType(AccountingType accountingType) {
		this.accountingType = accountingType;
	}

	public ArrayList<String> getEnableRoles() {
		return enableRoles;
	}

	public void setEnableRoles(ArrayList<String> enableRoles) {
		this.enableRoles = enableRoles;
	}

	@Override
	public String toString() {
		return "EnableTab [accountingType=" + accountingType + ", enableRoles="
				+ enableRoles + "]";
	}

}
