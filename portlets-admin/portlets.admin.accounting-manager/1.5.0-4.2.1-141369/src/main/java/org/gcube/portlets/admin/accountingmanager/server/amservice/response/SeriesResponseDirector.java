package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;


/**
 * Series Response Director
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SeriesResponseDirector {
	SeriesResponseBuilder seriesResponseBuilder;

	public void setSeriesResponseBuilder(
			SeriesResponseBuilder seriesResponseBuilder) {
		this.seriesResponseBuilder = seriesResponseBuilder;
	}

	public SeriesResponse getSeriesResponse() {
		return seriesResponseBuilder.getSeriesResponseSpec().getSr();

	}
	
	public ArrayList<SeriesResponse> getListOfSeriesResponse() {
		return seriesResponseBuilder.getSeriesResponseSpec().getSrs();

	}
	
	public void constructSeriesResponse() throws ServiceException {
		seriesResponseBuilder.createSpec();
		seriesResponseBuilder.buildSeriesResponse();

	}
}
