package org.gcube.common.vremanagement.ghnmanager.client.proxies;

import org.gcube.common.clients.fw.builders.StatelessBuilder;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.common.vremanagement.ghnmanager.client.GHNManagerLibrary;
import org.gcube.common.vremanagement.ghnmanager.client.fws.GHNManagerServiceJAXWSStubs;
import org.gcube.common.vremanagement.ghnmanager.client.plugins.GHNManagerServicePlugin;

public class Proxies {
	
	private static final GHNManagerServicePlugin plugin = new GHNManagerServicePlugin();
	 
	public static StatelessBuilder<GHNManagerLibrary> service() {
	    return new StatelessBuilderImpl<GHNManagerServiceJAXWSStubs,GHNManagerLibrary>(plugin);
	}

}
