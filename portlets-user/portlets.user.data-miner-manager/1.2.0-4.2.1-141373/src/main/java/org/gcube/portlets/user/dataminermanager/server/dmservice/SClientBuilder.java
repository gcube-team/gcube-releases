package org.gcube.portlets.user.dataminermanager.server.dmservice;

import org.gcube.portlets.user.dataminermanager.shared.exception.ServiceException;

/**
 * Abstract class for build client of service
 * 
 * @author "Giancarlo Panichi" email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public abstract class SClientBuilder {
	protected SClientSpec sClientSpec;

	public SClientSpec getSClientSpec() {
		return sClientSpec;
	}

	public void createSpec() {
		sClientSpec = new SClientSpec();

	}

	public abstract void buildSClient() throws ServiceException;

}
