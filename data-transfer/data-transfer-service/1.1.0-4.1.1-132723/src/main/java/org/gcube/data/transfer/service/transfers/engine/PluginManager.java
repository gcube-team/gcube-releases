package org.gcube.data.transfer.service.transfers.engine;

import java.util.Map;

import org.gcube.data.transfer.model.PluginDescription;
import org.gcube.data.transfer.service.transfers.engine.faults.PluginNotFoundException;

public interface PluginManager {

	public Map<String,PluginDescription> getInstalledPlugins();
	public void getPluginById(String id)throws PluginNotFoundException;
	
}
