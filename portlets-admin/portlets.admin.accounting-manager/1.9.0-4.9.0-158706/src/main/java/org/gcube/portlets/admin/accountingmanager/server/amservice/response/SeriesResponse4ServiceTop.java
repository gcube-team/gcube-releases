package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesService;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceTop;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top Series Response 4 Service
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class SeriesResponse4ServiceTop extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4ServiceTop.class);
	private Boolean showOthers;
	private Integer topNumber;
	private SortedMap<NumberedFilter, SortedMap<Calendar, Info>> topSM;

	public SeriesResponse4ServiceTop(Boolean showOthers, Integer topNumber,
			SortedMap<NumberedFilter, SortedMap<Calendar, Info>> topSM) {
		this.showOthers = showOthers;
		this.topNumber = topNumber;
		this.topSM = topSM;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (topSM == null || topSM.isEmpty()) {
				logger.error("Error creating series for service accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesServiceDataTop> seriesServiceDataTopList = new ArrayList<>();

			for (NumberedFilter topValue : topSM.keySet()) {

				ArrayList<SeriesServiceData> series = new ArrayList<>();
				SortedMap<Calendar, Info> infos = topSM.get(topValue);
				for (Info info : infos.values()) {
					JSONObject jso = info.getValue();
					Long duration = jso
							.getLong(AggregatedServiceUsageRecord.DURATION);
					Long operationCount = jso
							.getLong(AggregatedServiceUsageRecord.OPERATION_COUNT);
					Long maxInvocationTime = jso
							.getLong(AggregatedServiceUsageRecord.MAX_INVOCATION_TIME);
					Long minInvocationTime = jso
							.getLong(AggregatedServiceUsageRecord.MIN_INVOCATION_TIME);

					series.add(new SeriesServiceData(info.getCalendar()
							.getTime(), operationCount, duration,
							maxInvocationTime, minInvocationTime));

				}
				SeriesServiceDataTop seriesServiceDataTop = new SeriesServiceDataTop(
						new FilterValue(topValue.getValue()), series);
				seriesServiceDataTopList.add(seriesServiceDataTop);

			}

			SeriesServiceTop seriesServiceTop = new SeriesServiceTop(
					showOthers, topNumber, seriesServiceDataTopList);
			SeriesService seriesService = new SeriesService(seriesServiceTop);

			seriesResponseSpec.setSr(seriesService);

		} catch (Throwable e) {
			logger.error("Error creating series for service accounting top chart: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(
					"Error creating series for service accounting top chart: "
							+ e.getLocalizedMessage());
		}

	}
}
