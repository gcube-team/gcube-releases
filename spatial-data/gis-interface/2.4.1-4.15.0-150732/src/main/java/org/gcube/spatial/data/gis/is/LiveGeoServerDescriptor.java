package org.gcube.spatial.data.gis.is;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

public class LiveGeoServerDescriptor extends AbstractGeoServerDescriptor {

	
	public LiveGeoServerDescriptor(String url, String user, String password) {
		super(url, user, password);
	}

	@Override
	public Set<String> getDatastores(String workspace) throws MalformedURLException {
		return new HashSet<String>(getReader().getDatastores(workspace).getNames());
	}
	
	@Override
	public Long getHostedLayersCount() throws MalformedURLException {
		return new Long(getReader().getLayers().size());
	}
	
	@Override
	public Set<String> getStyles() throws MalformedURLException {
		return new HashSet<String>(getReader().getStyles().getNames());
	}

	@Override
	public Set<String> getWorkspaces() throws MalformedURLException {
		return new HashSet<String>(getReader().getWorkspaceNames());
	}
	
}
