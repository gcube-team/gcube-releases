package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesStorage;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageTop;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top Series Response 4 Storage
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class SeriesResponse4StorageTop extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4StorageTop.class);
	private Boolean showOthers;
	private Integer topNumber;
	private SortedMap<NumberedFilter, SortedMap<Calendar, Info>> topSM;

	public SeriesResponse4StorageTop(Boolean showOthers, Integer topNumber,
			SortedMap<NumberedFilter, SortedMap<Calendar, Info>> topSM) {
		this.showOthers = showOthers;
		this.topNumber = topNumber;
		this.topSM = topSM;

	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (topSM == null || topSM.isEmpty()) {
				logger.error("Error creating series for storage accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesStorageDataTop> seriesStorageDataTopList = new ArrayList<>();

			for (NumberedFilter topValue : topSM.keySet()) {

				ArrayList<SeriesStorageData> series = new ArrayList<>();
				SortedMap<Calendar, Info> infos = topSM.get(topValue);
				for (Info info : infos.values()) {
					JSONObject jso = info.getValue();
					Long dataVolume = jso
							.getLong(AggregatedStorageUsageRecord.DATA_VOLUME);
					Long operationCount = jso
							.getLong(AggregatedStorageUsageRecord.OPERATION_COUNT);
					series.add(new SeriesStorageData(info.getCalendar()
							.getTime(), dataVolume, operationCount));

				}
				SeriesStorageDataTop seriesStorageDataTop = new SeriesStorageDataTop(
						new FilterValue(topValue.getValue()), series);
				seriesStorageDataTopList.add(seriesStorageDataTop);

			}

			SeriesStorageTop seriesStorageTop = new SeriesStorageTop(
					showOthers, topNumber, seriesStorageDataTopList);
			SeriesStorage seriesStorage = new SeriesStorage(seriesStorageTop);

			seriesResponseSpec.setSr(seriesStorage);
		} catch (Throwable e) {
			logger.error("Error creating series for storage accounting top chart: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(
					"Error creating series for storage accounting basic chart: "
							+ e.getLocalizedMessage());
		}
	}
}
