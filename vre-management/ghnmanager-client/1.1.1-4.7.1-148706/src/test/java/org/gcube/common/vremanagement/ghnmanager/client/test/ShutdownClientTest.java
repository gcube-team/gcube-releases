package org.gcube.common.vremanagement.ghnmanager.client.test;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.vremanagement.ghnmanager.client.GHNManagerLibrary;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.ShutdownOptions;
import org.gcube.common.vremanagement.ghnmanager.client.proxies.Proxies;
import org.junit.Test;

public class ShutdownClientTest {
	
	@Test
	public void shutdownTest(){
		
		ScopeProvider.instance.set("/gcube");
		GHNManagerLibrary library = Proxies.service().at(URI.create("https://node2.tsec.d4science.research-infrastructures.eu:8443")).withTimeout(30, TimeUnit.SECONDS).build();

		ShutdownOptions options = new ShutdownOptions();
		options.setRestart(false);
		options.setClean(false);	
		try {
			library.shutdown(options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
