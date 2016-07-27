package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.datamodel.aggregation.AggregatedJobUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesJob;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobTop;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top Series Response 4 Job
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SeriesResponse4JobTop extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4JobTop.class);
	private Boolean showOthers;
	private Integer topNumber;
	private SortedMap<NumberedFilter, SortedMap<Calendar, Info>> topSM;

	public SeriesResponse4JobTop(Boolean showOthers, Integer topNumber,
			SortedMap<NumberedFilter, SortedMap<Calendar, Info>> topSM) {
		this.showOthers = showOthers;
		this.topNumber = topNumber;
		this.topSM = topSM;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (topSM == null || topSM.isEmpty()) {
				logger.error("Error creating series for job accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesJobDataTop> seriesJobDataTopList = new ArrayList<>();

			for (NumberedFilter topValue : topSM.keySet()) {

				ArrayList<SeriesJobData> series = new ArrayList<>();
				SortedMap<Calendar, Info> infos = topSM.get(topValue);
				for (Info info : infos.values()) {
					JSONObject jso = info.getValue();
					// Long duration =
					// jso.getLong(AggregatedJobUsageRecord.DURATION);
					Long operationCount = jso
							.getLong(AggregatedJobUsageRecord.OPERATION_COUNT);
					// Long maxInvocationTime =
					// jso.getLong(AggregatedJobUsageRecord.MAX_INVOCATION_TIME);
					// Long minInvocationTime =
					// jso.getLong(AggregatedJobUsageRecord.MIN_INVOCATION_TIME);

					series.add(new SeriesJobData(info.getCalendar().getTime(),
							operationCount, operationCount, operationCount,
							operationCount));
				}
				SeriesJobDataTop seriesJobDataTop = new SeriesJobDataTop(
						new FilterValue(topValue.getValue()), series);
				seriesJobDataTopList.add(seriesJobDataTop);

			}

			SeriesJobTop seriesJobTop = new SeriesJobTop(showOthers, topNumber,
					seriesJobDataTopList);
			SeriesJob seriesJob = new SeriesJob(seriesJobTop);

			seriesResponseSpec.setSr(seriesJob);

		} catch (Throwable e) {
			logger.error("Error creating series for job accounting top chart: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(
					"Error creating series for job accounting top chart: "
							+ e.getLocalizedMessage());
		}

	}
}
