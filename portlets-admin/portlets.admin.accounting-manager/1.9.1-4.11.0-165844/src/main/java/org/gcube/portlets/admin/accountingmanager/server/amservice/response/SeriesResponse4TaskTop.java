package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
//import org.gcube.accounting.datamodel.aggregation.AggregatedTaskUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesTask;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.task.SeriesTaskData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.task.SeriesTaskDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.task.SeriesTaskTop;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top Series Response 4 Task
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class SeriesResponse4TaskTop extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4TaskTop.class);
	private Boolean showOthers;
	private Integer topNumber;
	private SortedMap<NumberedFilter, SortedMap<Calendar, Info>> topSM;

	public SeriesResponse4TaskTop(Boolean showOthers, Integer topNumber,
			SortedMap<NumberedFilter, SortedMap<Calendar, Info>> topSM) {
		this.showOthers = showOthers;
		this.topNumber = topNumber;
		this.topSM = topSM;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (topSM == null || topSM.isEmpty()) {
				logger.error("Error creating series for task accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesTaskDataTop> seriesTaskDataTopList = new ArrayList<>();

			for (NumberedFilter topValue : topSM.keySet()) {

				ArrayList<SeriesTaskData> series = new ArrayList<>();
				SortedMap<Calendar, Info> infos = topSM.get(topValue);
				for (Info info : infos.values()) {
					@SuppressWarnings("unused")
					JSONObject jso = info.getValue();
					// Long duration =
					// jso.getLong(AggregatedTaskUsageRecord.DURATION);
					//Long operationCount = jso
					//		.getLong(AggregatedTaskUsageRecord.OPERATION_COUNT);
					// Long maxInvocationTime =
					// jso.getLong(AggregatedTaskUsageRecord.MAX_INVOCATION_TIME);
					// Long minInvocationTime =
					// jso.getLong(AggregatedTaskUsageRecord.MIN_INVOCATION_TIME);

					//series.add(new SeriesTaskData(info.getCalendar().getTime(),
					//		operationCount));
				}
				SeriesTaskDataTop seriesTaskDataTop = new SeriesTaskDataTop(
						new FilterValue(topValue.getValue()), series);
				seriesTaskDataTopList.add(seriesTaskDataTop);

			}

			SeriesTaskTop seriesTaskTop = new SeriesTaskTop(showOthers,
					topNumber, seriesTaskDataTopList);
			SeriesTask seriesTask = new SeriesTask(seriesTaskTop);

			seriesResponseSpec.setSr(seriesTask);

		} catch (Throwable e) {
			logger.error("Error creating series for portlet accounting top chart: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(
					"Error creating series for portlet accounting top chart: "
							+ e.getLocalizedMessage());
		}
	}
}
