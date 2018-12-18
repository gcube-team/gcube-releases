package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesStorage;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageDataContext;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context Series Response 4 Storage
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class SeriesResponse4StorageContext extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4StorageContext.class);
	private Context context;
	private SortedMap<Filter, SortedMap<Calendar, Info>> contextSM;

	public SeriesResponse4StorageContext(Context context,
			SortedMap<Filter, SortedMap<Calendar, Info>> contextSM) {
		this.context = context;
		this.contextSM = contextSM;

	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (contextSM == null || contextSM.isEmpty()) {
				logger.error("Error creating series for storage accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesStorageDataContext> seriesStorageDataContextList = new ArrayList<>();

			for (Filter contextValue : contextSM.keySet()) {

				ArrayList<SeriesStorageData> series = new ArrayList<>();
				SortedMap<Calendar, Info> infos = contextSM.get(contextValue);
				for (Info info : infos.values()) {
					JSONObject jso = info.getValue();
					Long dataVolume = jso
							.getLong(AggregatedStorageUsageRecord.DATA_VOLUME);
					Long operationCount = jso
							.getLong(AggregatedStorageUsageRecord.OPERATION_COUNT);
					series.add(new SeriesStorageData(info.getCalendar()
							.getTime(), dataVolume, operationCount));

				}
				SeriesStorageDataContext seriesStorageDataContext = new SeriesStorageDataContext(
						contextValue.getValue(), series);
				seriesStorageDataContextList.add(seriesStorageDataContext);

			}

			SeriesStorageContext seriesStorageContext = new SeriesStorageContext(
					context, seriesStorageDataContextList);
			SeriesStorage seriesStorage = new SeriesStorage(
					seriesStorageContext);

			seriesResponseSpec.setSr(seriesStorage);
		} catch (Throwable e) {
			logger.error("Error creating series for storage accounting context chart: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(
					"Error creating series for storage accounting context chart: "
							+ e.getLocalizedMessage());
		}
	}
}
