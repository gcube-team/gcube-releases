package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.datamodel.aggregation.AggregatedTaskUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesTask;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.task.SeriesTaskContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.task.SeriesTaskData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.task.SeriesTaskDataContext;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context Series Response 4 Task
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SeriesResponse4TaskContext extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4TaskContext.class);
	private Context context;
	private SortedMap<Filter, SortedMap<Calendar, Info>> contextSM;

	public SeriesResponse4TaskContext(Context context,
			SortedMap<Filter, SortedMap<Calendar, Info>> contextSM) {
		this.context = context;
		this.contextSM = contextSM;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (contextSM == null || contextSM.isEmpty()) {
				logger.error("Error creating series for task accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesTaskDataContext> seriesTaskDataContextList = new ArrayList<>();

			for (Filter contextValue : contextSM.keySet()) {

				ArrayList<SeriesTaskData> series = new ArrayList<>();
				SortedMap<Calendar, Info> infos = contextSM.get(contextValue);
				for (Info info : infos.values()) {
					JSONObject jso = info.getValue();
					// Long duration =
					// jso.getLong(AggregatedTaskUsageRecord.DURATION);
					Long operationCount = jso
							.getLong(AggregatedTaskUsageRecord.OPERATION_COUNT);
					// Long maxInvocationTime =
					// jso.getLong(AggregatedTaskUsageRecord.MAX_INVOCATION_TIME);
					// Long minInvocationTime =
					// jso.getLong(AggregatedTaskUsageRecord.MIN_INVOCATION_TIME);

					series.add(new SeriesTaskData(info.getCalendar().getTime(),
							operationCount));
				}
				SeriesTaskDataContext seriesTaskDataContext = new SeriesTaskDataContext(
						contextValue.getValue(), series);
				seriesTaskDataContextList.add(seriesTaskDataContext);

			}

			SeriesTaskContext seriesTaskContext = new SeriesTaskContext(
					context, seriesTaskDataContextList);
			SeriesTask seriesTask = new SeriesTask(seriesTaskContext);

			seriesResponseSpec.setSr(seriesTask);

		} catch (Throwable e) {
			logger.error("Error creating series for portlet accounting context chart: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(
					"Error creating series for portlet accounting context chart: "
							+ e.getLocalizedMessage());
		}
	}
}
