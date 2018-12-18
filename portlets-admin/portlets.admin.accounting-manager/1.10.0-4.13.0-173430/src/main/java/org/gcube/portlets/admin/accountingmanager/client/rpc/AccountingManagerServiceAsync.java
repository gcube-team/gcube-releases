/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Spaces;
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
 * @author Giancarlo Panichi
 *
 *
 */
public interface AccountingManagerServiceAsync {

	public static AccountingManagerServiceAsync INSTANCE = (AccountingManagerServiceAsync) GWT
			.create(AccountingManagerService.class);

	/**
	 * 
	 * @param callback
	 *            user info callback
	 */
	void hello(AsyncCallback<UserInfo> callback);

	/**
	 * Retrieve client monitor timeout
	 * 
	 * @param callback
	 *            client monitor timeout callback
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
	 *            series response callback
	 */
	void getSeriesInCache(AccountingType accountingType, SeriesRequest seriesRequest,
			AsyncCallback<SeriesResponse> callback);

	/**
	 * Retrieve Accounting Series
	 * 
	 * @param accountingType
	 *            the resource on which to accounting
	 * @param seriesRequest
	 *            filters and constraints descriptions
	 * @param callback
	 *            series callback
	 */
	void getSeries(AccountingType accountingType, SeriesRequest seriesRequest, AsyncCallback<String> callback);

	/**
	 * Accounting Request Monitor
	 * 
	 * @param operationId
	 *            operation Id
	 * @param callback
	 *            series response callback
	 */
	void operationMonitor(String operationId, AsyncCallback<SeriesResponse> callback);

	/**
	 * 
	 * @param accountingType
	 *            accounting type
	 * @param callback
	 *            array list of filter key callback
	 */
	void getFilterKeys(AccountingType accountingType, AsyncCallback<ArrayList<FilterKey>> callback);

	/**
	 * 
	 * @param filterValuesRequest
	 *            filter value request
	 * @param callback
	 *            filter value response callback
	 */
	void getFilterValues(FilterValuesRequest filterValuesRequest, AsyncCallback<FilterValuesResponse> callback);

	/**
	 * 
	 * @param callback
	 *            context callback
	 */
	void getContext(AsyncCallback<Context> callback);

	/**
	 * 
	 * @param callback
	 *            spaces callback
	 */
	void getSpaces(AsyncCallback<Spaces> callback);

	
	/**
	 * 
	 * @param accountingType
	 *            accounting type
	 * @param callback
	 *            item description callback
	 */
	void saveCSVOnWorkspace(AccountingType accountingType, AsyncCallback<ItemDescription> callback);

	/**
	 * 
	 * @param callback
	 *            the callback that return the list of enabled tabs
	 */
	void getEnableTabs(AsyncCallback<EnableTabs> callback);

	/**
	 * Check if the user is in root scope
	 * 
	 * @param callback
	 *            the callback that return true if is root scope
	 */
	void isRootScope(AsyncCallback<Boolean> callback);

}
