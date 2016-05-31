package org.gcube.portlets.admin.accountingmanager.server.amservice;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerServiceException;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface AccountingCallerInterface {

	public ArrayList<FilterKey> getFilterKeys(AccountingType accountingType)
			throws AccountingManagerServiceException;

	public ArrayList<FilterValue> getFilterValues(
			FilterValuesRequest filterValuesRequest)
			throws AccountingManagerServiceException;

	public SeriesResponse getSeries(AccountingType accountingType,
			SeriesRequest seriesRequest)
			throws AccountingManagerServiceException;

}