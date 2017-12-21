package org.gcube.data.analysis.dataminermanagercl.server.dmservice;



/**
 * Director
 * 
 * @author Giancarlo Panichi
 * 
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
	
	public void constructSClient() throws Exception {
		sClientBuilder.createSpec();
		sClientBuilder.buildSClient();

	}
}
