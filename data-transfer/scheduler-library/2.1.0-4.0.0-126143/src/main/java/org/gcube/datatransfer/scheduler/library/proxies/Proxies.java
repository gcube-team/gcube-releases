package org.gcube.datatransfer.scheduler.library.proxies;

import org.gcube.common.clients.fw.builders.StatefulBuilder;
import org.gcube.common.clients.fw.builders.StatefulBuilderImpl;
import org.gcube.common.clients.fw.builders.StatelessBuilder;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.datatransfer.scheduler.library.BinderLibrary;
import org.gcube.datatransfer.scheduler.library.ManagementLibrary;
import org.gcube.datatransfer.scheduler.library.SchedulerLibrary;
import org.gcube.datatransfer.scheduler.library.fws.BinderServiceJAXWSStubs;
import org.gcube.datatransfer.scheduler.library.fws.ManagementServiceJAXWSStubs;
import org.gcube.datatransfer.scheduler.library.fws.SchedulerServiceJAXWSStubs;
import org.gcube.datatransfer.scheduler.library.plugins.BinderServicePlugin;
import org.gcube.datatransfer.scheduler.library.plugins.ManagementServicePlugin;
import org.gcube.datatransfer.scheduler.library.plugins.SchedulerServicePlugin;

public class Proxies {
	
	private static final SchedulerServicePlugin schedulerPlugin = new SchedulerServicePlugin();
	private static final ManagementServicePlugin managementPlugin = new ManagementServicePlugin();
	private static final BinderServicePlugin binderPlugin = new BinderServicePlugin();

	public static StatefulBuilder<SchedulerLibrary> transferScheduler() {
	    return new StatefulBuilderImpl<SchedulerServiceJAXWSStubs,SchedulerLibrary>(schedulerPlugin);
	}
	
	public static StatelessBuilder<ManagementLibrary> transferManagement() {
	    return new StatelessBuilderImpl<ManagementServiceJAXWSStubs,ManagementLibrary>(managementPlugin);
	}
	
	public static StatelessBuilder<BinderLibrary> transferBinder() {
	    return new StatelessBuilderImpl<BinderServiceJAXWSStubs,BinderLibrary>(binderPlugin);
	}
	
}
