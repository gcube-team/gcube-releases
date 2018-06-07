package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.List;

import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.datamodel.aggregation.AggregatedJobUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesJob;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.job.SeriesJobData;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Series Response 4 Job Basic
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class SeriesResponse4JobBasic extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4JobBasic.class);
	private List<Info> infos;

	public SeriesResponse4JobBasic(List<Info> infos) {
		this.infos = infos;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (infos.size() <= 0) {
				logger.error("Error creating series for job accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesJobData> series = new ArrayList<SeriesJobData>();
			for (Info info : infos) {
				JSONObject jso = info.getValue();
				Long duration = jso.getLong(AggregatedJobUsageRecord.DURATION);
				Long operationCount = jso
						.getLong(AggregatedJobUsageRecord.OPERATION_COUNT);
				Long maxInvocationTime = jso
						.getLong(AggregatedJobUsageRecord.MAX_INVOCATION_TIME);
				Long minInvocationTime = jso
						.getLong(AggregatedJobUsageRecord.MIN_INVOCATION_TIME);

				series.add(new SeriesJobData(info.getCalendar().getTime(),
						operationCount, duration, maxInvocationTime,
						minInvocationTime));

			}
			SeriesJobBasic seriesJobBasic = new SeriesJobBasic(series);

			SeriesJob seriesJob = new SeriesJob(seriesJobBasic);

			seriesResponseSpec.setSr(seriesJob);
		} catch (Throwable e) {
			logger.error("Error creating series for job accounting basic chart: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(
					"Error creating series for job accounting basic chart: "
							+ e.getLocalizedMessage());
		}

	}
}
