package org.gcube.portlets.user.dataminermanager.server.dmservice;

import org.gcube.portlets.user.dataminermanager.shared.exception.ServiceException;


/**
 * Director
 * 
 * @author "Giancarlo Panichi"
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SClientDirector {
	SClientBuilder sClientBuilder;

	public void setSClientBuilder(
			SClientBuilder sClientBuilder) {
		this.sClientBuilder = sClientBuilder;
	}

	public SClient getSClient() {
		return sClientBuilder.getSClientSpec().getSClient();

	}
	
	public void constructSClient() throws ServiceException {
		sClientBuilder.createSpec();
		sClientBuilder.buildSClient();

	}
}
