package org.gcube.portlets.user.accountingdashboard.server;

import javax.servlet.ServletException;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.portlets.user.accountingdashboard.client.rpc.AccountingDashboardService;
import org.gcube.portlets.user.accountingdashboard.server.accounting.AccountingService;
import org.gcube.portlets.user.accountingdashboard.server.accounting.AccountingServiceType;
import org.gcube.portlets.user.accountingdashboard.server.util.ServiceCredentials;
import org.gcube.portlets.user.accountingdashboard.shared.data.ReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.RequestReportData;
import org.gcube.portlets.user.accountingdashboard.shared.data.ScopeData;
import org.gcube.portlets.user.accountingdashboard.shared.exception.ServiceException;
import org.gcube.portlets.user.accountingdashboard.shared.session.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
@SuppressWarnings("serial")
public class AccountingDashboardServiceImpl extends RemoteServiceServlet implements AccountingDashboardService {

	private static Logger logger = LoggerFactory.getLogger(AccountingDashboardServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("AccountingDashbord Service started!");

	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public UserInfo hello() throws ServiceException {
		try {
			logger.debug("AccountingDashboardServiceImpl hello()");
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(this.getThreadLocalRequest());
			UserInfo userInfo = new UserInfo(serviceCredentials.getUserName(), serviceCredentials.getGroupId(),
					serviceCredentials.getGroupName(), serviceCredentials.getScope(), serviceCredentials.getEmail(),
					serviceCredentials.getFullName());
			logger.debug("UserInfo: " + userInfo);
			return userInfo;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new ServiceException("Error retrivieving user credentials: " + e.getLocalizedMessage(), e);
		}

	}

	@Override
	public ScopeData getScopeData() throws ServiceException {
		try {
			logger.debug("AccountingDashboardServiceImpl getScopeData()");
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(this.getThreadLocalRequest());
			AccountingService as = new AccountingService(getAccountingContext(serviceCredentials.getScope()));
			ScopeData scopeData = as.getTree(this.getThreadLocalRequest());
			return scopeData;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("GetScopeData(): " + e.getLocalizedMessage(), e);
			throw new ServiceException("Error retrieving scope info: " + e.getLocalizedMessage(), e);
		}

	}

	@Override
	public ReportData getReport(RequestReportData requestReportData) throws ServiceException {
		try {
			logger.debug("AccountingDashboardServiceImpl getReport(): " + requestReportData);
			ServiceCredentials serviceCredentials = SessionUtil.getServiceCredentials(this.getThreadLocalRequest());
			AccountingService as = new AccountingService(getAccountingContext(serviceCredentials.getScope()));
			ReportData reportData = as.getReport(this.getThreadLocalRequest(), requestReportData);
			return reportData;
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage(), e);
			throw e;
		} catch (Throwable e) {
			logger.error("GetReportData(): " + e.getLocalizedMessage(), e);
			throw new ServiceException("Error retrieving report: " + e.getLocalizedMessage(), e);
		}
	}

	private AccountingServiceType getAccountingContext(String scope) {
		try {
			ScopeBean scopeBean = new ScopeBean(scope);
			if (scopeBean.is(Type.VRE)) {
				return AccountingServiceType.CurrentScope;
			} else {
				return AccountingServiceType.PortalContex;
			}
		} catch (Throwable e) {
			logger.error("getAccountingContext(): " + e.getLocalizedMessage(), e);
			return AccountingServiceType.CurrentScope;
		}
	}

}