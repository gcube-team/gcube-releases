package org.gcube.vremanagement.softwaregateway.client.proxies;

import org.gcube.common.clients.fw.builders.StatelessBuilder;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.vremanagement.softwaregateway.client.SGAccessLibrary;
import org.gcube.vremanagement.softwaregateway.client.SGRegistrationLibrary;
import org.gcube.vremanagement.softwaregateway.client.fws.SGAccessServiceJAXWSStubs;
import org.gcube.vremanagement.softwaregateway.client.fws.SGRegistrationServiceJAXWSStubs;
import org.gcube.vremanagement.softwaregateway.client.plugins.AccessPlugin;
import org.gcube.vremanagement.softwaregateway.client.plugins.RegistrationPlugin;

public class Proxies {
	
	
	private static final AccessPlugin accessPlugin = new AccessPlugin();
	
	
	private static final RegistrationPlugin registrationPlugin = new RegistrationPlugin();
	 
	
	
	public static StatelessBuilder <SGAccessLibrary> accessService() {
	    return new StatelessBuilderImpl<SGAccessServiceJAXWSStubs,SGAccessLibrary>(accessPlugin);
	}


	public static StatelessBuilder <SGRegistrationLibrary> registrationService() {
	    return new StatelessBuilderImpl<SGRegistrationServiceJAXWSStubs,SGRegistrationLibrary>(registrationPlugin);
	}


}
