/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.client.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ceras
 *
 */
public class CapabilitiesResult {
	private List<LayerItem> layerItems = new ArrayList<LayerItem>(); 
	private List<GroupItem> groupItems = new ArrayList<GroupItem>();
	int totalLayers, totalGroups;
	/**
	 * @return the layerItems
	 */
	public List<LayerItem> getLayerItems() {
		return layerItems;
	}
	/**
	 * @param layerItems the layerItems to set
	 */
	public void setLayerItems(List<LayerItem> layerItems) {
		this.layerItems = layerItems;
	}
	/**
	 * @return the groupItems
	 */
	public List<GroupItem> getGroupItems() {
		return groupItems;
	}
	/**
	 * @param groupItems the groupItems to set
	 */
	public void setGroupItems(List<GroupItem> groupItems) {
		this.groupItems = groupItems;
	}
	/**
	 * @return the totalLayers
	 */
	public int getTotalLayers() {
		return totalLayers;
	}
	/**
	 * @param totalLayers the totalLayers to set
	 */
	public void setTotalLayers(int totalLayers) {
		this.totalLayers = totalLayers;
	}
	/**
	 * @return the totalGroups
	 */
	public int getTotalGroups() {
		return totalGroups;
	}
	/**
	 * @param totalGroups the totalGroups to set
	 */
	public void setTotalGroups(int totalGroups) {
		this.totalGroups = totalGroups;
	}
	
	public void addLayer(LayerItem layerItem) {
		this.layerItems.add(layerItem);
	}
	
	public void addGroup(GroupItem groupItem) {
		this.groupItems.add(groupItem);
	}
}
