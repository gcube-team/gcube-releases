package org.gcube.portlets.admin.accountingmanager.server.state;

import java.io.Serializable;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingStateData implements Serializable {

	private static final long serialVersionUID = -2080165745912743812L;
	private AccountingType accountingType;
	private SeriesRequest seriesRequest;
	private SeriesResponse seriesResponse;

	public AccountingStateData() {
		super();
	}

	public AccountingStateData(AccountingType accountingType,
			SeriesRequest seriesRequest, SeriesResponse seriesResponse) {
		super();
		this.accountingType = accountingType;
		this.seriesRequest = seriesRequest;
		this.seriesResponse = seriesResponse;
	}

	public AccountingType getAccountingType() {
		return accountingType;
	}

	public void setAccountingType(AccountingType accountingType) {
		this.accountingType = accountingType;
	}

	public SeriesRequest getSeriesRequest() {
		return seriesRequest;
	}

	public void setSeriesRequest(SeriesRequest seriesRequest) {
		this.seriesRequest = seriesRequest;
	}

	public SeriesResponse getSeriesResponse() {
		return seriesResponse;
	}

	public void setSeriesResponse(SeriesResponse seriesResponse) {
		this.seriesResponse = seriesResponse;
	}

	@Override
	public String toString() {
		return "AccountingStateData [accountingType=" + accountingType
				+ ", seriesRequest=" + seriesRequest + ", seriesResponse="
				+ seriesResponse + "]";
	}

}
