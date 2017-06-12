package org.gcube.data.transfer.service.transfers.engine;

import java.util.Map;

import org.gcube.data.transfer.model.PluginDescription;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.ExecutionReport;
import org.gcube.data.transfer.service.transfers.engine.faults.PluginExecutionException;
import org.gcube.data.transfer.service.transfers.engine.faults.PluginNotFoundException;

public interface PluginManager {

	public Map<String,PluginDescription> getInstalledPlugins();
	public ExecutionReport execute(PluginInvocation invocation)throws PluginNotFoundException,PluginExecutionException;
	
}
