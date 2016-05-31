package org.gcube.portlets.admin.accountingmanager.client.rpc;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.session.UserInfo;

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
	 * @throws AccountingManagerServiceException
	 */
	public UserInfo hello() throws AccountingManagerServiceException;

	/**
	 * Retrieve Accounting Series
	 * 
	 * @param accountingType
	 *            the resource on which to accounting
	 * @param seriesRequest
	 *            filters and constraints descriptions
	 * @return
	 * @throws AccountingManagerServiceException
	 */
	public SeriesResponse getSeries(AccountingType accountingType,
			SeriesRequest seriesRequest)
			throws AccountingManagerServiceException;

	/**
	 * 
	 * @param accountingType
	 *            type of accounting
	 * @return list of filter keys
	 * @throws AccountingManagerServiceException
	 */
	public ArrayList<FilterKey> getFilterKeys(AccountingType accountingType)
			throws AccountingManagerServiceException;
	
	/**
	 * 
	 * 
	 * @param filterValuesRequest request values available
	 * @return
	 * @throws AccountingManagerServiceException
	 */
	public ArrayList<FilterValue> getFilterValues(FilterValuesRequest filterValuesRequest)
			throws AccountingManagerServiceException;
	
	

}
