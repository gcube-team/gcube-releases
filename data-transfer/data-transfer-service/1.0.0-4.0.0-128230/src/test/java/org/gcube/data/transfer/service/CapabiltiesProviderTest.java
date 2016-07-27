package org.gcube.data.transfer.service;

import org.gcube.data.transfer.service.transfers.engine.impl.CapabilitiesProviderImpl;
import org.junit.Test;

public class CapabiltiesProviderTest {

	@Test
	public void getHostname(){
		System.out.println(new CapabilitiesProviderImpl().get());
	}
	
}
