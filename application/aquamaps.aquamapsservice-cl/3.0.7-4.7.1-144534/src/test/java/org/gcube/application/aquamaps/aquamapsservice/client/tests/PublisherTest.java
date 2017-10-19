package org.gcube.application.aquamaps.aquamapsservice.client.tests;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.publisher;

import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import org.gcube.application.aquamaps.aquamapsservice.client.proxies.Publisher;
import org.gcube.common.scope.api.ScopeProvider;

public class PublisherTest {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException, Exception {
		ScopeProvider.instance.set(TestCommon.SCOPE);
		Publisher pub=publisher().withTimeout(5, TimeUnit.MINUTES).build();

//		System.out.println(pub.getMapsBySpecies(new String[]{
//				"Fis-53544"
//		}, true, true, Arrays.asList(new Resource[]{				
//		})));
		
		System.out.println(pub.getBulkUpdates(true, false, null, 0).getAbsolutePath());
		
		
		
	}

}
