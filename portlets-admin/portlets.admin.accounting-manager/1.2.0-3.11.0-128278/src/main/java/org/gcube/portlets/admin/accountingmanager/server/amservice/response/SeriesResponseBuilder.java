package org.gcube.portlets.admin.accountingmanager.server.amservice.response;

import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerServiceException;

/**
 * Abstract class for build Series Response
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public abstract class SeriesResponseBuilder {
	protected SeriesResponseSpec seriesResponseSpec;
	
	public SeriesResponseSpec getSeriesResponseSpec(){
		return seriesResponseSpec;
	}
	public void createSpec(){
		seriesResponseSpec=new SeriesResponseSpec();
		
	}
	
	public abstract void buildSeriesResponse() throws AccountingManagerServiceException;
	    
	
}
