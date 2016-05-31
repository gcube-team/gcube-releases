package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class FilterValuesRequest implements Serializable {

	private static final long serialVersionUID = -3544245558153491901L;
	private FilterKey filterKey;
	private AccountingType accountingType;
	
	public FilterValuesRequest(){
		super();
	}

	public FilterValuesRequest(FilterKey filterKey,
			AccountingType accountingType) {
		super();
		this.filterKey = filterKey;
		this.accountingType = accountingType;
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

	@Override
	public String toString() {
		return "FilterValuesRequest [filterKey=" + filterKey
				+ ", accountingType=" + accountingType + "]";
	}
	
	
	
}
