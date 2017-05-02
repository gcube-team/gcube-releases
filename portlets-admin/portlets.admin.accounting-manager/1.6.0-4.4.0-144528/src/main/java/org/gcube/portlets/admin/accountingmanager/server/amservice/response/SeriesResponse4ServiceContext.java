package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesService;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceDataContext;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context Series Response 4 Service
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SeriesResponse4ServiceContext extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4ServiceContext.class);
	private Context context;
	private SortedMap<Filter, SortedMap<Calendar, Info>> contextSM;

	public SeriesResponse4ServiceContext(Context context,
			SortedMap<Filter, SortedMap<Calendar, Info>> contextSM) {
		this.context = context;
		this.contextSM = contextSM;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (contextSM == null || contextSM.isEmpty()) {
				logger.error("Error creating series for service accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesServiceDataContext> seriesServiceDataContextList = new ArrayList<>();

			for (Filter contextValue : contextSM.keySet()) {

				ArrayList<SeriesServiceData> series = new ArrayList<>();
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

					series.add(new SeriesServiceData(info.getCalendar()
							.getTime(), operationCount, duration,
							maxInvocationTime, minInvocationTime));

				}
				SeriesServiceDataContext seriesServiceDataContext = new SeriesServiceDataContext(
						contextValue.getValue(), series);
				seriesServiceDataContextList.add(seriesServiceDataContext);

			}

			SeriesServiceContext seriesServiceContext = new SeriesServiceContext(context,
					seriesServiceDataContextList);
			SeriesService seriesService = new SeriesService(
					seriesServiceContext);

			seriesResponseSpec.setSr(seriesService);

		} catch (Throwable e) {
			logger.error("Error creating series for service accounting context chart: "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException(
					"Error creating series for service accounting context chart: "
							+ e.getLocalizedMessage());
		}

	}
}
