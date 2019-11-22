package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class FilterValuesRequest implements Serializable {

	private static final long serialVersionUID = -3544245558153491901L;
	private FilterKey filterKey;
	private AccountingType accountingType;
	private AccountingPeriod accountingPeriod;

	public FilterValuesRequest() {
		super();
	}

	public FilterValuesRequest(FilterKey filterKey,
			AccountingType accountingType, AccountingPeriod accountingPeriod) {
		super();
		this.filterKey = filterKey;
		this.accountingType = accountingType;
		this.accountingPeriod = accountingPeriod;
	}

	public FilterKey getFilterKey() {
		return filterKey;
	}

	public void setFilterKey(FilterKey filterKey) {
		this.filterKey = filterKey;
	}

	public AccountingType getAccountingType() {
		return accountingType;
	}

	public void setAccountingType(AccountingType accountingType) {
		this.accountingType = accountingType;
	}

	public AccountingPeriod getAccountingPeriod() {
		return accountingPeriod;
	}

	public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
		this.accountingPeriod = accountingPeriod;
	}

	@Override
	public String toString() {
		return "FilterValuesRequest [filterKey=" + filterKey
				+ ", accountingType=" + accountingType + ", accountingPeriod="
				+ accountingPeriod + "]";
	}

}
