package org.gcube.portlets.admin.accountingmanager.server.util;

import java.io.Serializable;

import javax.servlet.http.HttpSession;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TaskRequest implements Serializable {

	private static final long serialVersionUID = -4010108343968344171L;
	private String operationId;
	private HttpSession httpSession;
	private ServiceCredentials serviceCredentials;
	private AccountingType accountingType;
	private SeriesRequest seriesRequest;

	public TaskRequest(String operationId, HttpSession httpSession,
			ServiceCredentials serviceCredentials,
			AccountingType accountingType, SeriesRequest seriesRequest) {
		super();
		this.operationId = operationId;
		this.httpSession = httpSession;
		this.serviceCredentials = serviceCredentials;
		this.accountingType = accountingType;
		this.seriesRequest = seriesRequest;
		
	}

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	public ServiceCredentials getServiceCredentials() {
		return serviceCredentials;
	}

	public void setServiceCredentials(ServiceCredentials serviceCredentials) {
		this.serviceCredentials = serviceCredentials;
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

	@Override
	public String toString() {
		return "TaskRequest [operationId=" + operationId + ", httpSession="
				+ httpSession + ", serviceCredentials=" + serviceCredentials
				+ ", accountingType=" + accountingType + ", seriesRequest="
				+ seriesRequest + "]";
	}

}
