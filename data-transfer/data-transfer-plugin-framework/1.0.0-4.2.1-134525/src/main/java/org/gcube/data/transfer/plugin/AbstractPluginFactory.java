package org.gcube.data.transfer.plugin;

import java.util.Map;

import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.fails.ParameterException;
import org.gcube.data.transfer.plugin.fails.PluginInitializationException;
import org.gcube.data.transfer.plugin.fails.PluginShutDownException;

public abstract class AbstractPluginFactory<T extends AbstractPlugin> {

	
	// Init
	public abstract boolean init() throws PluginInitializationException;
	
	// On service down
	public abstract boolean shutDown()throws PluginShutDownException;
			
	public abstract void checkInvocation(PluginInvocation invocation) throws ParameterException;
	
	// Description
	public abstract String getID();
	public abstract String getDescription();
	public abstract Map<String,String> getParameters();
	
	public abstract T createWorker(PluginInvocation invocation);	
}
