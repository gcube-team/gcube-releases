package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;
import java.util.List;

import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesStorage;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageBasic;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.storage.SeriesStorageData;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Series Response 4 Storage Basic
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SeriesResponse4StorageBasic extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4StorageBasic.class);
	private List<Info> infos;

	public SeriesResponse4StorageBasic(List<Info> infos) {
		this.infos = infos;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		try {
			if(infos.size()<=0){
				logger.error("Error creating series for storage accounting: No data available!");
				throw new ServiceException("No data available!");
			}
			
			ArrayList<SeriesStorageData> series=new ArrayList<SeriesStorageData>();
			for (Info info : infos) {
				JSONObject jso = info.getValue();
				Long dataVolume = jso.getLong(AggregatedStorageUsageRecord.DATA_VOLUME);
				Long operationCount = jso
						.getLong(AggregatedStorageUsageRecord.OPERATION_COUNT);
				series.add(new SeriesStorageData(info.getCalendar().getTime(), dataVolume, operationCount));
			
			}
			
			SeriesStorageBasic seriesStorageBasic=new SeriesStorageBasic(series);
			SeriesStorage seriesStorage = new SeriesStorage(seriesStorageBasic);
			
			seriesResponseSpec.setSr(seriesStorage);
		} catch (Throwable e) {
			logger.error("Error creating series for storage accounting basic chart: "+e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException("Error creating series for storage accounting basic chart: "+e.getLocalizedMessage());
		}
	}
}
