package org.gcube.portlets.admin.accountingmanager.server;

import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.admin.accountingmanager.client.rpc.AccountingManagerService;
import org.gcube.portlets.admin.accountingmanager.server.amservice.AccountingCaller;
import org.gcube.portlets.admin.accountingmanager.server.amservice.AccountingCallerInterface;
import org.gcube.portlets.admin.accountingmanager.server.amservice.AccountingCallerTester;
import org.gcube.portlets.admin.accountingmanager.server.export.CSVManager;
import org.gcube.portlets.admin.accountingmanager.server.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.server.storage.StorageUtil;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.session.UserInfo;
import org.gcube.portlets.admin.accountingmanager.shared.workspace.ItemDescription;
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
	public UserInfo hello() throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			String token=SessionUtil.getToken(aslSession);
			UserInfo userInfo = new UserInfo(aslSession.getUsername(),
					aslSession.getGroupId(), aslSession.getGroupName(),
					aslSession.getScope(), aslSession.getScopeName(),
					aslSession.getUserEmailAddress(),
					aslSession.getUserFullName());
			logger.debug("UserInfo: "+userInfo);
			logger.debug("UserToken: "+token);
			return userInfo;
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public SeriesResponse getSeries(AccountingType accountingType,
			SeriesRequest seriesRequest) throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			String token=SessionUtil.getToken(aslSession);
			logger.debug("UserToken: "+token);
			
			AccountingCallerInterface accountingCaller;
			if (Constants.DEBUG_MODE) {
				accountingCaller = new AccountingCallerTester();
			} else {
				accountingCaller = new AccountingCaller();
			}
			SeriesResponse seriesResponse = accountingCaller.getSeries(
					accountingType, seriesRequest);
			AccountingStateData accountingStateData = new AccountingStateData(
					accountingType, seriesRequest, seriesResponse);
			SessionUtil.setAccountingStateData(session, accountingType,
					accountingStateData);
			return seriesResponse;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getSeries(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<FilterKey> getFilterKeys(AccountingType accountingType)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			String token=SessionUtil.getToken(aslSession);
			logger.debug("UserToken: "+token);
			
			AccountingCallerInterface accountingCaller;
			if (Constants.DEBUG_MODE) {
				accountingCaller = new AccountingCallerTester();
			} else {
				accountingCaller = new AccountingCaller();
			}
			ArrayList<FilterKey> filterKeys = accountingCaller
					.getFilterKeys(accountingType);

			return filterKeys;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getFilterKeys(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<FilterValue> getFilterValues(
			FilterValuesRequest filterValuesRequest) throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			String token=SessionUtil.getToken(aslSession);
			logger.debug("UserToken: "+token);

			AccountingCallerInterface accountingCaller;
			if (Constants.DEBUG_MODE) {
				accountingCaller = new AccountingCallerTester();
			} else {
				accountingCaller = new AccountingCaller();
			}
			ArrayList<FilterValue> filterValues = accountingCaller
					.getFilterValues(filterValuesRequest);

			return filterValues;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getFilterValues(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public ItemDescription saveCSVOnWorkspace(AccountingType accountingType)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			String token=SessionUtil.getToken(aslSession);
			logger.debug("UserToken: "+token);

			logger.debug("SaveDataOnWorkspace(): " + accountingType);
			AccountingStateData accountingStateData = SessionUtil
					.getAccountingStateData(session, accountingType);
			if(accountingStateData==null){
				logger.error("No series present in session for thi accounting type: "+accountingType);
				throw new ServiceException("No series present in session for thi accounting type: "+accountingType);
			}
			
			CSVManager csvManager = new CSVManager(aslSession.getUsername());
			ItemDescription itemDescription = csvManager
					.saveOnWorkspace(accountingStateData);
			return itemDescription;
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("SaveDataOnWorkspace(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String getPublicLink(ItemDescription itemDescription)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ASLSession aslSession = SessionUtil.getASLSession(session);
			String token=SessionUtil.getToken(aslSession);
			logger.debug("UserToken: "+token);
			
			logger.debug("GetPublicLink(): " + itemDescription);
			String link = StorageUtil.getPublicLink(aslSession.getUsername(),
					itemDescription.getId());

			return link;
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("getPublicLink(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

}
