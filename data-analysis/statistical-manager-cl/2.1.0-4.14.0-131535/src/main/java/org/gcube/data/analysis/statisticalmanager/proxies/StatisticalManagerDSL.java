package org.gcube.data.analysis.statisticalmanager.proxies;



import org.gcube.common.clients.fw.builders.StatefulBuilder;
import org.gcube.common.clients.fw.builders.StatefulBuilderImpl;
import org.gcube.common.clients.fw.builders.StatelessBuilderImpl;
import org.gcube.data.analysis.statisticalmanager.plugins.StatisticalManagerDataSpacePlugin;
import org.gcube.data.analysis.statisticalmanager.plugins.StatisticalManagerFactoryPlugin;
import org.gcube.data.analysis.statisticalmanager.plugins.StatisticalManagerServicePlugin;
import org.gcube.data.analysis.statisticalmanager.stubs.ComputationFactoryStub;
import org.gcube.data.analysis.statisticalmanager.stubs.ComputationStub;
import org.gcube.data.analysis.statisticalmanager.stubs.DataSpaceStub;



public class StatisticalManagerDSL {
	
	private static final StatisticalManagerFactoryPlugin factory_plugin = new StatisticalManagerFactoryPlugin();
	private static final StatisticalManagerServicePlugin service_plugin = new StatisticalManagerServicePlugin();
	private static final StatisticalManagerDataSpacePlugin dataSpace_plugin = new StatisticalManagerDataSpacePlugin();

	
	
	public static StatefulBuilder<StatisticalManagerService> stateful() {
		return new StatefulBuilderImpl<ComputationStub,StatisticalManagerService>(service_plugin);
	}
	
	public static StatelessBuilderImpl<ComputationFactoryStub, StatisticalManagerDefaultFactory> createStateful() {
		return new StatelessBuilderImpl<ComputationFactoryStub,StatisticalManagerDefaultFactory>(factory_plugin);
	}
	
	public static StatelessBuilderImpl<DataSpaceStub, StatisticalManagerDefaultDataSpace> dataSpace() {	
		return new StatelessBuilderImpl<DataSpaceStub,StatisticalManagerDefaultDataSpace>(dataSpace_plugin);
	}

	
}
