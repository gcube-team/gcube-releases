package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.List;

import org.gcube.accounting.analytics.Info;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Series Response 4 Portlet Basic
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class SeriesResponse4PortletBasic extends SeriesResponseBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(SeriesResponse4PortletBasic.class);
	
	private List<Info> infos;

	public SeriesResponse4PortletBasic(List<Info> infos) {
		this.infos=infos;
	}

	@Override
	public void buildSeriesResponse() throws ServiceException {
		if(infos.size()<=0){
			logger.error("Error creating series for portlet accounting: No data available!");
			throw new ServiceException("No data available!");
		}

		//SeriesPortletBasic seriesPortletBasic=new SeriesPortletBasic(series);
		//SeriesPortlet seriesPortlet = new SeriesPortlet(seriesPortletBasic);
		
		SeriesResponse seriesResponse = new SeriesResponse();

		seriesResponseSpec.setSr(seriesResponse);

	}
}
