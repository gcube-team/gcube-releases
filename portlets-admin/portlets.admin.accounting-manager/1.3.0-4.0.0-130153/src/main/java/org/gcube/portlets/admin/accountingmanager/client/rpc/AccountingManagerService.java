package org.gcube.portlets.admin.accountingmanager.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.session.UserInfo;
import org.gcube.portlets.admin.accountingmanager.shared.workspace.ItemDescription;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
@RemoteServiceRelativePath("ams")
public interface AccountingManagerService extends RemoteService {
	/**
	 * Get informations on the current user
	 * 
	 * @return
	 * @throws ServiceException
	 */
	public UserInfo hello() throws ServiceException;

	/**
	 * Retrieve Accounting Series
	 * 
	 * @param accountingType
	 *            the resource on which to accounting
	 * @param seriesRequest
	 *            filters and constraints descriptions
	 * @return
	 * @throws ServiceException
	 */
	public SeriesResponse getSeries(AccountingType accountingType,
			SeriesRequest seriesRequest)
			throws ServiceException;

	/**
	 * 
	 * @param accountingType
	 *            type of accounting
	 * @return list of filter keys
	 * @throws ServiceException
	 */
	public ArrayList<FilterKey> getFilterKeys(AccountingType accountingType)
			throws ServiceException;
	
	/**
	 * 
	 * 
	 * @param filterValuesRequest request values available
	 * @return
	 * @throws ServiceException
	 */
	public ArrayList<FilterValue> getFilterValues(FilterValuesRequest filterValuesRequest)
			throws ServiceException;
	
	/**
	 * 
	 * @param itemDescription item description
	 * @return
	 * @throws ServiceException
	 */
	public String getPublicLink(ItemDescription itemDescription)
			throws ServiceException;

	/**
	 * 
	 * @param accountingStateData
	 * @return
	 * @throws ServiceException
	 */
	public ItemDescription saveCSVOnWorkspace(AccountingType accountingType)
			throws ServiceException;
	
	

}
