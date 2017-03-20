package org.gcube.vremanagement.resourcemanager.client.proxies;

import org.gcube.common.clients.fw.builders.StatefulBuilder;
import org.gcube.common.clients.fw.builders.StatefulBuilderImpl;
import org.gcube.common.clients.fw.builders.StatelessBuilder;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.vremanagement.resourcemanager.client.RMBinderLibrary;
import org.gcube.vremanagement.resourcemanager.client.RMAdminLibrary;
import org.gcube.vremanagement.resourcemanager.client.RMControllerLibrary;
import org.gcube.vremanagement.resourcemanager.client.RMReportingLibrary;
import org.gcube.vremanagement.resourcemanager.client.fws.RMBinderServiceJAXWSStubs;
import org.gcube.vremanagement.resourcemanager.client.fws.RMControllerServiceJAXWSStubs;
import org.gcube.vremanagement.resourcemanager.client.fws.RMAdminServiceJAXWSStubs;
import org.gcube.vremanagement.resourcemanager.client.fws.RMReportingServiceJAXWSStubs;
import org.gcube.vremanagement.resourcemanager.client.plugins.AdminPlugin;
import org.gcube.vremanagement.resourcemanager.client.plugins.BinderPlugin;
import org.gcube.vremanagement.resourcemanager.client.plugins.ControllerPlugin;
import org.gcube.vremanagement.resourcemanager.client.plugins.ReportingPlugin;

public class Proxies {
	
	private static final BinderPlugin binderPlugin = new BinderPlugin();
	
	private static final AdminPlugin adminPlugin = new AdminPlugin();
	
	private static final ControllerPlugin controllerPlugin = new ControllerPlugin();
	
	private static final ReportingPlugin reportingPlugin = new ReportingPlugin();
	 
	
	public static StatelessBuilder<RMBinderLibrary> binderService() {
	    return new StatelessBuilderImpl<RMBinderServiceJAXWSStubs,RMBinderLibrary>(binderPlugin);
	}
	
	public static StatelessBuilder <RMAdminLibrary> adminService() {
	    return new StatelessBuilderImpl<RMAdminServiceJAXWSStubs,RMAdminLibrary>(adminPlugin);
	}

	public static StatelessBuilder <RMControllerLibrary> controllerService() {
	    return new StatelessBuilderImpl<RMControllerServiceJAXWSStubs,RMControllerLibrary>(controllerPlugin);
	}

	public static StatelessBuilder <RMReportingLibrary> reportingService() {
	    return new StatelessBuilderImpl<RMReportingServiceJAXWSStubs,RMReportingLibrary>(reportingPlugin);
	}


}
