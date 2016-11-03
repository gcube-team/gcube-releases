package org.gcube.data.analysis.tabulardata.service.impl;

import org.gcube.data.analysis.tabulardata.service.TabularDataService;

public class TabularDataServiceFactory {

	private static TabularDataService singleton = new TabularDataServiceImpl();
	
	private TabularDataServiceFactory(){}
	
	public static TabularDataService getService(){
		return singleton;
	}
}
