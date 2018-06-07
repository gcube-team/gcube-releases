package org.gcube.portlets.admin.accountingmanager.client.state;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Spaces;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingClientStateData implements Serializable {

	private static final long serialVersionUID = -2080165745912743812L;
	private AccountingType accountingType;
	private SeriesRequest seriesRequest;
	private SeriesResponse seriesResponse;
	private ArrayList<FilterKey> availableFilterKeys;
	private Context availableContext;
	private Spaces availableSpaces;
	private Boolean rootScope;

	public AccountingClientStateData() {
		super();
	}

	/**
	 * 
	 * @param accountingType
	 *            accounting type
	 * @param rootScope
	 *            root scope
	 */
	public AccountingClientStateData(AccountingType accountingType, Boolean rootScope) {
		super();
		this.accountingType = accountingType;
		this.seriesRequest = null;
		this.seriesResponse = null;
		this.availableFilterKeys = null;
		this.availableContext = null;
		this.availableSpaces = null;
		this.rootScope = rootScope;
	}

	/**
	 * 
	 * @param accountingType
	 *            accounting type
	 * @param seriesRequest
	 *            series request
	 * @param seriesResponse
	 *            series response
	 * @param availableFilterKeys
	 *            available filter keys
	 * @param availableContext
	 *            available context
	 * @param availableSpaces
	 *            available spaces
	 * @param rootScope
	 *            root scope
	 */
	public AccountingClientStateData(AccountingType accountingType, SeriesRequest seriesRequest,
			SeriesResponse seriesResponse, ArrayList<FilterKey> availableFilterKeys, Context availableContext,
			Spaces availableSpaces, Boolean rootScope) {
		super();
		this.accountingType = accountingType;
		this.seriesRequest = seriesRequest;
		this.seriesResponse = seriesResponse;
		this.availableFilterKeys = availableFilterKeys;
		this.availableContext = availableContext;
		this.availableSpaces = availableSpaces;
		this.rootScope = rootScope;
	}

	public AccountingType getAccountingType() {
		return accountingType;
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

	public ArrayList<FilterKey> getAvailableFilterKeys() {
		return availableFilterKeys;
	}

	public void setAvailableFilterKeys(ArrayList<FilterKey> availableFilterKeys) {
		this.availableFilterKeys = availableFilterKeys;
	}

	public Context getAvailableContext() {
		return availableContext;
	}

	public void setAvailableContext(Context availableContext) {
		this.availableContext = availableContext;
	}

	public Boolean getRootScope() {
		return rootScope;
	}

	public void setRootScope(Boolean rootScope) {
		this.rootScope = rootScope;
	}

	public void setAccountingType(AccountingType accountingType) {
		this.accountingType = accountingType;
	}

	public Spaces getAvailableSpaces() {
		return availableSpaces;
	}

	public void setAvailableSpaces(Spaces availableSpaces) {
		this.availableSpaces = availableSpaces;
	}

	@Override
	public String toString() {
		return "AccountingClientStateData [accountingType=" + accountingType + ", seriesRequest=" + seriesRequest
				+ ", seriesResponse=" + seriesResponse + ", availableFilterKeys=" + availableFilterKeys
				+ ", availableContext=" + availableContext + ", availableSpaces=" + availableSpaces + ", rootScope="
				+ rootScope + "]";
	}

}
