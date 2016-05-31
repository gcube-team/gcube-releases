package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.List;

import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesService;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.service.SeriesServiceData;
import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Series Response 4 Service Basic
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SeriesResponse4ServiceBasic extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4ServiceBasic.class);
	private List<Info> infos;

	public SeriesResponse4ServiceBasic(List<Info> infos) {
		this.infos=infos;
	}

	@Override
	public void buildSeriesResponse() throws AccountingManagerServiceException {
		try {
			if(infos.size()<=0){
				logger.error("Error creating series for service accounting: No data available!");
				throw new AccountingManagerServiceException("No data available!");
			}
			
			ArrayList<SeriesServiceData> series=new ArrayList<SeriesServiceData>();
			for (Info info : infos) {
				JSONObject jso = info.getValue();
				Long duration = jso.getLong(AggregatedServiceUsageRecord.DURATION);
				Long operationCount = jso
						.getLong(AggregatedServiceUsageRecord.OPERATION_COUNT);
				Long maxInvocationTime = jso.getLong(AggregatedServiceUsageRecord.MAX_INVOCATION_TIME);
				Long minInvocationTime = jso.getLong(AggregatedServiceUsageRecord.MIN_INVOCATION_TIME);
				
				series.add(new SeriesServiceData(info.getCalendar().getTime(), operationCount, duration, maxInvocationTime, minInvocationTime));
				
			}

			SeriesServiceBasic seriesServiceBasic=new SeriesServiceBasic(series);
			
			SeriesService seriesService = new SeriesService(seriesServiceBasic);

			seriesResponseSpec.setSr(seriesService);
		} catch (Throwable e) {
			logger.error("Error creating series for service accounting chart basic: "+e.getLocalizedMessage());
			e.printStackTrace();
			throw new AccountingManagerServiceException("Error creating series for service accounting chart basic: "+e.getLocalizedMessage());
		}

	}
}
