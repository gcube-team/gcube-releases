/**
 * 
 */
package org.gcube.portlets.user.accountingdashboard.client.rpc;

import org.gcube.portlets.user.accountingdashboard.shared.data.ReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.RequestReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ScopeData;
import org.gcube.portlets.user.accountingdashboard.shared.session.UserInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface AccountingDashboardServiceAsync {

	public static AccountingDashboardServiceAsync INSTANCE = (AccountingDashboardServiceAsync) GWT
			.create(AccountingDashboardService.class);

	void hello(AsyncCallback<UserInfo> callback);

	void getScopeData(AsyncCallback<ScopeData> callback);

	void getReport(RequestReportData requestReportdata, AsyncCallback<ReportData> asyncCallback);

}
