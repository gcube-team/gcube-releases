package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesJob;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobDataContext;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context Series Response 4 Job
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class SeriesResponse4JobContext extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4JobContext.class);
	private Context context;
	private SortedMap<Filter, SortedMap<Calendar, Info>> contextSM;

	public SeriesResponse4JobContext(Context context,
			SortedMap<Filter, SortedMap<Calendar, Info>> contextSM) {
		this.context=context;
		this.contextSM = contextSM;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (contextSM == null || contextSM.isEmpty()) {
				logger.error("Error creating series for job accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesJobDataContext> seriesJobDataContextList = new ArrayList<>();

			for (Filter contextValue : contextSM.keySet()) {

				ArrayList<SeriesJobData> series = new ArrayList<>();
				SortedMap<Calendar, Info> infos = contextSM.get(contextValue);
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

					series.add(new SeriesJobData(info.getCalendar().getTime(),
							operationCount, duration, maxInvocationTime,
							minInvocationTime));

				}
				SeriesJobDataContext seriesJobDataContext = new SeriesJobDataContext(
						contextValue.getValue(), series);
				seriesJobDataContextList.add(seriesJobDataContext);

			}

			SeriesJobContext seriesJobContext = new SeriesJobContext(context,
					seriesJobDataContextList);
			SeriesJob seriesService = new SeriesJob(seriesJobContext);

			seriesResponseSpec.setSr(seriesService);

		} catch (Throwable e) {
			logger.error("Error creating series for job accounting context chart: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(
					"Error creating series for job accounting context chart: "
							+ e.getLocalizedMessage());
		}

	}
}
