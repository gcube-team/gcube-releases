/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.session.UserInfo;

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
	 * Retrieve Accounting Series
	 * 
	 * @param accountingType
	 *            the resource on which to accounting
	 * @param seriesRequest
	 *            filters and constraints descriptions
	 * @param callback
	 */
	void getSeries(AccountingType accountingType, SeriesRequest seriesRequest,
			AsyncCallback<SeriesResponse> callback);

	/**
	 * 
	 * @param accountingType
	 *            accounting type
	 * @param callback
	 */
	void getFilterKeys(AccountingType accountingType,
			AsyncCallback<ArrayList<FilterKey>> callback);

	/**
	 * 
	 * @param filterValuesRequest request values available
	 * @param callback
	 */
	void getFilterValues(FilterValuesRequest filterValuesRequest,
			AsyncCallback<ArrayList<FilterValue>> callback);

}
