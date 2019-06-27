package org.gcube.portlets.user.accountingdashboard.shared.data;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class RequestReportData implements Serializable {

	private static final long serialVersionUID = -7428707426843173730L;
	private ScopeData scopeData;
	private String dateFrom;
	private String dateTo;

	public RequestReportData() {
		super();
	}

	public RequestReportData(ScopeData scopeData, String dateFrom, String dateTo) {
		super();
		this.scopeData = scopeData;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public ScopeData getScopeData() {
		return scopeData;
	}

	public void setScopeData(ScopeData scopeData) {
		this.scopeData = scopeData;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	@Override
	public String toString() {
		return "RequestReportData [scopeData=" + scopeData + ", dateFrom=" + dateFrom + ", dateTo=" + dateTo + "]";
	}

}
