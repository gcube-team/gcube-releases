package org.gcube.data.spd.client.proxies;


import java.util.List;

import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.exceptions.InvalidQueryException;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.service.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedPluginException;
import org.gcube.data.streams.Stream;

public interface ManagerClient {

	public <T extends ResultElement> Stream<T> search(String query) throws InvalidQueryException, UnsupportedPluginException, UnsupportedCapabilityException;
	
	public List<PluginDescription> getPluginsDescription();
}
