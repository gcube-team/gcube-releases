package org.gcube.data.transfer.service.transfers.engine;

import java.util.Map;

import org.gcube.data.transfer.model.ExecutionReport;
import org.gcube.data.transfer.model.PluginDescription;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.fails.PluginException;
import org.gcube.data.transfer.plugin.fails.PluginExecutionException;
import org.gcube.data.transfer.service.transfers.engine.faults.PluginNotFoundException;
import org.gcube.data.transfer.service.transfers.engine.impl.PluginManagerImpl;

public interface PluginManager {

	public Map<String,PluginDescription> getInstalledPlugins();
	public ExecutionReport execute(PluginInvocation invocation,String transferredFile)throws PluginException, PluginNotFoundException;
	public void shutdown();
	public Object getPluginInfo(String pluginID) throws PluginNotFoundException, PluginExecutionException;
	
	public void initPlugins();
	
	public static PluginManager get() {
		return PluginManagerImpl.get();
	}
}
