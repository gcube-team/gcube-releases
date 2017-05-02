/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.session.UserInfo;
import org.gcube.portlets.admin.accountingmanager.shared.tabs.EnableTabs;
import org.gcube.portlets.admin.accountingmanager.shared.workspace.ItemDescription;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface AccountingManagerServiceAsync {

	public static AccountingManagerServiceAsync INSTANCE = (AccountingManagerServiceAsync) GWT
			.create(AccountingManagerService.class);

	/**
	 * 
	 * @param callback
	 */
	void hello(AsyncCallback<UserInfo> callback);

	/**
	 * Retrieve client monitor timeout
	 *  
	 * @param callback
	 */
	void getClientMonitorTimeout(AsyncCallback<Long> callback);

	
	/**
	 * Retrieve Accounting Series in Cache
	 * 
	 * @param accountingType
	 *            the resource on which to accounting
	 * @param seriesRequest
	 *            filters and constraints descriptions
	 * @param callback
	 */
	void getSeriesInCache(AccountingType accountingType,
			SeriesRequest seriesRequest, AsyncCallback<SeriesResponse> callback);

	/**
	 * Retrieve Accounting Series
	 * 
	 * @param accountingType
	 *            the resource on which to accounting
	 * @param seriesRequest
	 *            filters and constraints descriptions
	 * @param callback
	 */
	void getSeries(AccountingType accountingType, SeriesRequest seriesRequest,
			AsyncCallback<String> callback);
	
	
	/**
	 * Accounting Request Monitor
	 * 
	 * @param operationId
	 * @param callback
	 */
	void operationMonitor(String operationId, AsyncCallback<SeriesResponse> callback);
	
	/**
	 * 
	 * @param accountingType
	 *            accounting type
	 * @param callback
	 */
	void getFilterKeys(AccountingType accountingType,
			AsyncCallback<ArrayList<FilterKey>> callback);

	void getFilterValues(FilterValuesRequest filterValuesRequest,
			AsyncCallback<FilterValuesResponse> callback);

	/**
	 * 
	 * @param callback
	 */
	void getContext(AsyncCallback<Context> callback);

	/**
	 * 
	 * @param itemDescription
	 *            item description
	 * @param callback
	 */
	void getPublicLink(ItemDescription itemDescription,
			AsyncCallback<String> callback);

	/**
	 * 
	 * @param accountingStateData
	 * @param callback
	 */
	void saveCSVOnWorkspace(AccountingType accountingType,
			AsyncCallback<ItemDescription> callback);

	/**
	 * 
	 * @param callback
	 *            return the list of enabled tabs
	 */
	void getEnableTabs(AsyncCallback<EnableTabs> callback);
	
	/**
	 * Check if the user is in root scope
	 * 
	 * @param callback
	 */
	void isRootScope(AsyncCallback<Boolean> callback);
	
	

}
