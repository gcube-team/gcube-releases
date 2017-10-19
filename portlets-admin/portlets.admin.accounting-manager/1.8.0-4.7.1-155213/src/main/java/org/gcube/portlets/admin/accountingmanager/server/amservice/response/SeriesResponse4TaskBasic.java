package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.List;

import org.gcube.accounting.analytics.Info;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Series Response 4 Task Basic
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class SeriesResponse4TaskBasic extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4TaskBasic.class);
	private List<Info> infos;

	public SeriesResponse4TaskBasic(List<Info> infos) {
		this.infos=infos;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		if(infos.size()<=0){
			logger.error("Error creating series for task accounting: No data available!");
			throw new ServiceException("No data available!");
		}

		//SeriesTaskBasic seriesTaskBasic=new SeriesTaskBasic(series);
		//SeriesTask seriesTask = new SeriesTask(seriesTaskBasic);
		
		SeriesResponse seriesResponse = new SeriesResponse();

		seriesResponseSpec.setSr(seriesResponse);

	}
}
