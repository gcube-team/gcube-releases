/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.beans;

import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.geoexplorer.client.beans.LayerItem;
import org.gcube.portlets.user.geoexplorer.client.beans.Workspace;
import org.gcube.portlets.user.geoexplorer.server.GeoExplorerServiceParameters;

/**
 * @author ceras
 *
 */
public class ScopeLayers {
	
	private GeoExplorerServiceParameters parameters;
	private String scope;
	private Map<Workspace, List<LayerItem>> mapWorkspaces;
	
	/**
	 * @param mapWorkspaces
	 */
	public ScopeLayers(String scope, GeoExplorerServiceParameters parameters, Map<Workspace, List<LayerItem>> mapWorkspaces) {
		super();
		this.scope = scope;
		this.parameters = parameters;
		this.mapWorkspaces = mapWorkspaces;
	}

	/**
	 * @return the mapWorkspaces
	 */
	public Map<Workspace, List<LayerItem>> getMapWorkspaces() {
		return mapWorkspaces;
	}

	/**
	 * @param mapWorkspaces the mapWorkspaces to set
	 */
	public void setMapWorkspaces(Map<Workspace, List<LayerItem>> mapWorkspaces) {
		this.mapWorkspaces = mapWorkspaces;
	}
	
	public void putWorkspaceLayers(Workspace workspace, List<LayerItem> layerItems) {
		this.mapWorkspaces.put(workspace, layerItems);
	}
	
	public List<LayerItem> getLayerItemsByWorkspace(Workspace workspace) {
		return this.mapWorkspaces.get(workspace);
	}
	
	/**
	 * @return the parameters
	 */
	public GeoExplorerServiceParameters getParameters() {
		return parameters;
	}
	
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(GeoExplorerServiceParameters parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}
	
	/**
	 * @param scope the scope to set
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
	
}
