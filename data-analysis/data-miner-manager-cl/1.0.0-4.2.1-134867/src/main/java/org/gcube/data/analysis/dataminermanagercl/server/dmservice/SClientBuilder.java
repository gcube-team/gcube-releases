package org.gcube.data.analysis.dataminermanagercl.server.dmservice;

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

	public abstract void buildSClient() throws Exception;

}
