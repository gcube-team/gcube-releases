package org.gcube.portlets.admin.ishealthmonitor.client;


import org.gcube.portlets.admin.ishealthmonitor.client.dialog.ISMonitor;
import org.gcube.portlets.admin.ishealthmonitor.client.highchartsjs.HighChartJSInjector;
import org.gcube.portlets.admin.ishealthmonitor.client.highchartsjs.HighchartsBundle;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;


/**
 * It will not show if RMP is NOT in STANDALONE MODE
 */
public class Resource_ishealth_monitor implements EntryPoint {
	public void onModuleLoad() {
		/**
		 * This inject the needed javascript modules for drawing highcharts automatically
		 */
		HighchartsBundle bundle = GWT.create(HighchartsBundle.class);
		HighChartJSInjector.inject(bundle.jQueryJS().getText());
		HighChartJSInjector.inject(bundle.highchartsJS().getText());
		HighChartJSInjector.inject(bundle.gxtAdapaterJS().getText());
		/*
		 * just for running standalone uncomment this line and
		 * 
		 * ISMonitor#initScopes add scope manually (see commented lines) and in 
		 * 
		 *  ISMonitorServiceImpl#getResourceTypeTree use GCUBEScope.getScope(scope) instead of ScopeManager
		 *
		 */
		//ISMonitor.pingIS();
	}
}
