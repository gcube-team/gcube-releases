package org.gcube.portlets.user.accountingdashboard.client.rpc;

import org.gcube.portlets.user.accountingdashboard.shared.data.ReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.RequestReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ScopeData;
import org.gcube.portlets.user.accountingdashboard.shared.exception.ServiceException;
import org.gcube.portlets.user.accountingdashboard.shared.session.UserInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
@RemoteServiceRelativePath("accountingdashboardservice")
public interface AccountingDashboardService extends RemoteService {
	/**
	 * Get informations on the current user
	 * 
	 * @return UserInfo user inforamations
	 * @throws ServiceException
	 *             exception
	 */
	public UserInfo hello() throws ServiceException;

	public ScopeData getScopeData() throws ServiceException;

	public ReportData getReport(RequestReportData requestReportdata) throws ServiceException;

}
