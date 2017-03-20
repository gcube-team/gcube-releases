package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.datamodel.aggregation.AggregatedPortletUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesPortlet;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet.SeriesPortletContext;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet.SeriesPortletData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet.SeriesPortletDataContext;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context Series Response 4 Portlet
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SeriesResponse4PortletContext extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4PortletContext.class);
	private Context context;
	private SortedMap<Filter, SortedMap<Calendar, Info>> contextSM;

	public SeriesResponse4PortletContext(Context context,
			SortedMap<Filter, SortedMap<Calendar, Info>> contextSM) {
		this.context = context;
		this.contextSM = contextSM;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (contextSM == null || contextSM.isEmpty()) {
				logger.error("Error creating series for portlet accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesPortletDataContext> seriesPortletDataContextList = new ArrayList<>();

			for (Filter contextValue : contextSM.keySet()) {

				ArrayList<SeriesPortletData> series = new ArrayList<>();
				SortedMap<Calendar, Info> infos = contextSM.get(contextValue);
				for (Info info : infos.values()) {
					JSONObject jso = info.getValue();
					// Long duration =
					// jso.getLong(AggregatedPortletUsageRecord.DURATION);
					Long operationCount = jso
							.getLong(AggregatedPortletUsageRecord.OPERATION_COUNT);
					// Long maxInvocationTime =
					// jso.getLong(AggregatedPortletUsageRecord.MAX_INVOCATION_TIME);
					// Long minInvocationTime =
					// jso.getLong(AggregatedPortletUsageRecord.MIN_INVOCATION_TIME);

					series.add(new SeriesPortletData(info.getCalendar()
							.getTime(), operationCount));
				}
				SeriesPortletDataContext seriesPortletDataContext = new SeriesPortletDataContext(
						contextValue.getValue(), series);
				seriesPortletDataContextList.add(seriesPortletDataContext);

			}

			SeriesPortletContext seriesPortletContext = new SeriesPortletContext(
					context, seriesPortletDataContextList);
			SeriesPortlet seriesPortlet = new SeriesPortlet(
					seriesPortletContext);

			seriesResponseSpec.setSr(seriesPortlet);

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
