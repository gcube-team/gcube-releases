package org.gcube.portlets.admin.accountingmanager.server;

import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.admin.accountingmanager.client.rpc.AccountingManagerService;
import org.gcube.portlets.admin.accountingmanager.server.amservice.AccountingCaller;
import org.gcube.portlets.admin.accountingmanager.server.amservice.AccountingCallerInterface;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.session.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * AccountingManagerServiceImpl
 * 
 * Support service request
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
@SuppressWarnings("serial")
public class AccountingManagerServiceImpl extends RemoteServiceServlet
		implements AccountingManagerService {

	private static Logger logger = LoggerFactory
			.getLogger(AccountingManagerServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		System.out.println("Fix JAXP: jdk.xml.entityExpansionLimit=0");
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		System.out.println("initializing AccountingManager");

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public UserInfo hello() throws AccountingManagerServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getAslSession(session);
			UserInfo userInfo = new UserInfo(aslSession.getUsername(),
					aslSession.getGroupId(), aslSession.getGroupName(),
					aslSession.getScope(), aslSession.getScopeName(),
					aslSession.getUserEmailAddress(),
					aslSession.getUserFullName());
			return userInfo;
		} catch (AccountingManagerServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new AccountingManagerServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public SeriesResponse getSeries(AccountingType accountingType,
			SeriesRequest seriesRequest)
			throws AccountingManagerServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			SessionUtil.getAslSession(session);
			AccountingCallerInterface accountingCaller = new AccountingCaller();
			SeriesResponse seriesResponse = accountingCaller.getSeries(
					accountingType, seriesRequest);

			return seriesResponse;

		} catch (AccountingManagerServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getSeries(): " + e.getLocalizedMessage(), e);
			throw new AccountingManagerServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<FilterKey> getFilterKeys(AccountingType accountingType)
			throws AccountingManagerServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			SessionUtil.getAslSession(session);

			AccountingCallerInterface accountingCaller = new AccountingCaller();
			ArrayList<FilterKey> filterKeys = accountingCaller.getFilterKeys(accountingType);
			
			return filterKeys;
			
		} catch (AccountingManagerServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getFilterKeys(): " + e.getLocalizedMessage(), e);
			throw new AccountingManagerServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<FilterValue> getFilterValues(
			FilterValuesRequest filterValuesRequest)
			throws AccountingManagerServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			SessionUtil.getAslSession(session);

			AccountingCallerInterface accountingCaller = new AccountingCaller();
			ArrayList<FilterValue> filterValues = accountingCaller.getFilterValues(filterValuesRequest);
			
			return filterValues;
		
		} catch (AccountingManagerServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getFilterValues(): " + e.getLocalizedMessage(), e);
			throw new AccountingManagerServiceException(e.getLocalizedMessage());
		}

	}

}
