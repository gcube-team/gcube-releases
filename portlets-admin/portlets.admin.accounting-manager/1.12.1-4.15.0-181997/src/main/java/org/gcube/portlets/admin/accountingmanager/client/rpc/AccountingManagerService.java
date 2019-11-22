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
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.session.UserInfo;
import org.gcube.portlets.admin.accountingmanager.shared.tabs.EnableTabs;
import org.gcube.portlets.admin.accountingmanager.shared.workspace.ItemDescription;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
@RemoteServiceRelativePath("ams")
public interface AccountingManagerService extends RemoteService {
	/**
	 * Get informations on the current user
	 * 
	 * @return user info
	 * @throws ServiceException
	 *             service exception
	 */
	public UserInfo hello() throws ServiceException;

	/**
	 * 
	 * @return timeout
	 * @throws ServiceException
	 *             service exception
	 */
	public Long getClientMonitorTimeout() throws ServiceException;

	/**
	 * Retrieve Accounting Series in Cache
	 * 
	 * @param accountingType
	 *            the resource on which to accounting
	 * @param seriesRequest
	 *            filters and constraints descriptions
	 * @return series
	 * @throws ServiceException
	 *             service exception
	 */
	public SeriesResponse getSeriesInCache(AccountingType accountingType, SeriesRequest seriesRequest)
			throws ServiceException;

	/**
	 * Retrieve Accounting Series
	 * 
	 * @param accountingType
	 *            the resource on which to accounting
	 * @param seriesRequest
	 *            filters and constraints descriptions
	 * @return series
	 * @throws ServiceException
	 *             series exception
	 */
	public String getSeries(AccountingType accountingType, SeriesRequest seriesRequest) throws ServiceException;

	/**
	 * Accounting Request Monitor
	 * 
	 * @param operationId operation id
	 * @return series
	 * @throws ServiceException
	 *             service exception
	 */
	public SeriesResponse operationMonitor(String operationId) throws ServiceException;

	/**
	 * 
	 * @param accountingType
	 *            type of accounting
	 * @return list of filter keys
	 * @throws ServiceException
	 *             service exception
	 */
	public ArrayList<FilterKey> getFilterKeys(AccountingType accountingType) throws ServiceException;

	/**
	 * 
	 * 
	 * @param filterValuesRequest
	 *            request values available
	 * @return filter values
	 * @throws ServiceException
	 *             service exception
	 */
	public FilterValuesResponse getFilterValues(FilterValuesRequest filterValuesRequest) throws ServiceException;

	/**
	 * Retrieve Context available
	 * 
	 * @return context
	 * @throws ServiceException
	 *             service exception
	 */
	public Context getContext() throws ServiceException;

	/**
	 * Retrieve spaces available
	 * 
	 * @return spaces
	 * @throws ServiceException
	 *             service exception
	 */
	public Spaces getSpaces() throws ServiceException;


	/**
	 * 
	 * @param accountingType
	 *            accounting type
	 * @return item description
	 * @throws ServiceException
	 *             service exception
	 */
	public ItemDescription saveCSVOnWorkspace(AccountingType accountingType) throws ServiceException;

	/**
	 * 
	 * @return enable tabs
	 * @throws ServiceException
	 *             service exception
	 */
	public EnableTabs getEnableTabs() throws ServiceException;

	/**
	 * Check if the user is in root scope
	 * 
	 * @return true if is root scope
	 * @throws ServiceException
	 *             service exception
	 */
	public Boolean isRootScope() throws ServiceException;

}
