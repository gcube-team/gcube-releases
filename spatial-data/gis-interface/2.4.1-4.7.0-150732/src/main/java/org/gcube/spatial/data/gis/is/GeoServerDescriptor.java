package org.gcube.spatial.data.gis.is;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GeoServerDescriptor extends AbstractGeoServerDescriptor {

	private Long hostedLayersCount;
	
	private HashMap<String,Set<String>> datastores=new HashMap<>();
	private HashSet<String> workspaces=new HashSet<>();
	private HashSet<String> styles=new HashSet<>();
	
	
	public GeoServerDescriptor(String url, String user, String password, Long hostedLayersCount) {
		super(url, user, password);
		this.hostedLayersCount=hostedLayersCount;
	}

	@Override
	public Long getHostedLayersCount() {
		return hostedLayersCount;
	}
	
	@Override
	public Set<String> getDatastores(String workspace) {
		return datastores.get(workspace);
	}
	
	@Override
	public Set<String> getStyles() {
		return styles;
	}
	
	@Override
	public Set<String> getWorkspaces() {
		return workspaces;
	}
	
	public void setDatastores(HashMap<String, Set<String>> datastores) {
		this.datastores = datastores;
	}
	
	public void setHostedLayersCount(Long hostedLayersCount) {
		this.hostedLayersCount = hostedLayersCount;
	}
	
	public void setStyles(HashSet<String> styles) {
		this.styles = styles;
	}
	public void setWorkspaces(HashSet<String> workspaces) {
		this.workspaces = workspaces;
	}
}
