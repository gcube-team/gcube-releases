package org.gcube.portlets.admin.accountingmanager.shared.data.query;

import java.io.Serializable;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingFilterDefinition;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingPeriod;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class SeriesRequest implements Serializable {

	private static final long serialVersionUID = -109538024097615414L;
	private AccountingPeriod accountingPeriod;
	private AccountingFilterDefinition accountingFilterDefinition;

	public SeriesRequest() {
		super();
	}

	public SeriesRequest(AccountingPeriod accountingPeriod,
			AccountingFilterDefinition accountingFilterDefinition) {
		super();
		this.accountingPeriod = accountingPeriod;
		this.accountingFilterDefinition = accountingFilterDefinition;
	}

	public AccountingPeriod getAccountingPeriod() {
		return accountingPeriod;
	}

	public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
		this.accountingPeriod = accountingPeriod;
	}

	public AccountingFilterDefinition getAccountingFilterDefinition() {
		return accountingFilterDefinition;
	}

	public void setAccountingFilterDefinition(
			AccountingFilterDefinition accountingFilterDefinition) {
		this.accountingFilterDefinition = accountingFilterDefinition;
	}

	@Override
	public String toString() {
		return "SeriesRequest [accountingPeriod=" + accountingPeriod
				+ ", accountingFilterDefinition=" + accountingFilterDefinition
				+ "]";
	}

	

}
