package org.gcube.data.spd.client.manager;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.executor;
import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.client.proxies.Executor;
import org.gcube.data.spd.client.proxies.Manager;

public class JobTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		ScopeProvider.instance.set("/gcube/devsec");
		getStatus();
	}

	private static void getJobFile() throws Exception {
		
		Executor creator = executor().build();
		String link = creator.getResultLink("f63a96b0-e94b-11e2-a63c-e05c5bb7d509||8c788f50-eace-11e2-b167-8f9adc39871b");
		System.out.println("resultLink is "+ link);
	}
	
	private static void getJobErorFile() throws Exception {
		
		Executor creator = executor().build();
		String link = creator.getErrorLink("f63a96b0-e94b-11e2-a63c-e05c5bb7d509||8c788f50-eace-11e2-b167-8f9adc39871b");
		System.out.println("errorLink is "+ link);
	}
	
	private static void getStatus() throws Exception {
			
		Executor creator = executor().build();
		org.gcube.data.spd.stubs.types.Status status = creator.getStatus("f63a96b0-e94b-11e2-a63c-e05c5bb7d509||8c788f50-eace-11e2-b167-8f9adc39871b");
		System.out.println("status is "+ status);
	}
	
	private static void getJobs() throws Exception {
		
		Executor creator = executor().build();
		org.gcube.data.spd.stubs.types.Status status = creator.getStatus("f63a96b0-e94b-11e2-a63c-e05c5bb7d509||8c788f50-eace-11e2-b167-8f9adc39871b");
		System.out.println("status is "+ status);
	}
		
}
