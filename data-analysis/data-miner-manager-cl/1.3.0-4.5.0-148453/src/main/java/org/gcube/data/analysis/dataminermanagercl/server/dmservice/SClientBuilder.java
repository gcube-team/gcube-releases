package org.gcube.data.analysis.dataminermanagercl.server.dmservice;

/**
 * Abstract class for build client of service
 * 
 * @author Giancarlo Panichi
 *
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
