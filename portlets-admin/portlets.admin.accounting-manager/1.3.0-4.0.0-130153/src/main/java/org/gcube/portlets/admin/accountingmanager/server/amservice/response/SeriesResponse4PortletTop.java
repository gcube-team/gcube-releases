package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.datamodel.aggregation.AggregatedPortletUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesPortlet;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet.SeriesPortletData;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet.SeriesPortletDataTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.portlet.SeriesPortletTop;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top Series Response 4 Portlet
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SeriesResponse4PortletTop extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4PortletTop.class);
	private Boolean showOthers;
	private Integer topNumber;
	private SortedMap<NumberedFilter, SortedMap<Calendar, Info>> topSM;

	public SeriesResponse4PortletTop(Boolean showOthers, Integer topNumber,
			SortedMap<NumberedFilter, SortedMap<Calendar, Info>> topSM) {
		this.showOthers = showOthers;
		this.topNumber = topNumber;
		this.topSM = topSM;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if (topSM == null || topSM.isEmpty()) {
				logger.error("Error creating series for portlet accounting: No data available!");
				throw new ServiceException("No data available!");
			}

			ArrayList<SeriesPortletDataTop> seriesPortletDataTopList = new ArrayList<>();

			for (NumberedFilter topValue : topSM.keySet()) {

				ArrayList<SeriesPortletData> series = new ArrayList<>();
				SortedMap<Calendar, Info> infos = topSM.get(topValue);
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
				SeriesPortletDataTop seriesPortletDataTop = new SeriesPortletDataTop(
						new FilterValue(topValue.getValue()), series);
				seriesPortletDataTopList.add(seriesPortletDataTop);

			}

			SeriesPortletTop seriesPortletTop = new SeriesPortletTop(
					showOthers, topNumber, seriesPortletDataTopList);
			SeriesPortlet seriesPortlet = new SeriesPortlet(seriesPortletTop);

			seriesResponseSpec.setSr(seriesPortlet);

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
