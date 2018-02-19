package org.gcube.portlets.admin.accountingmanager.shared.data;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class AccountingPeriod implements Serializable {

	private static final long serialVersionUID = 4241461469179338817L;
	
	private String startDate;
	private String endDate;
	private AccountingPeriodMode period;
	
	public AccountingPeriod(){
		super();
	}
	
	public AccountingPeriod(String startDate, String endDate,
			AccountingPeriodMode period) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.period = period;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public AccountingPeriodMode getPeriod() {
		return period;
	}

	public void setPeriod(AccountingPeriodMode period) {
		this.period = period;
	}

	@Override
	public String toString() {
		return "AccountingPeriod [startDate=" + startDate + ", endDate="
				+ endDate + ", period=" + period + "]";
	}

	

	

	
}
