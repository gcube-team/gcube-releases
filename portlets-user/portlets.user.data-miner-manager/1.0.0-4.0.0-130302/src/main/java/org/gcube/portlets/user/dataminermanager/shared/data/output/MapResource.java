/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.data.output;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class MapResource extends Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8772836076910728324L;
	private Map<String, Resource> map;

	/**
	 * 
	 */
	public MapResource() {
		super();
		this.setResourceType(ResourceType.MAP);
	}

	public MapResource(String resourceId, String name, String description,
			Map<String, Resource> map) {
		super(resourceId, name, description, ResourceType.MAP);
		this.map = map;
	}

	public Map<String, Resource> getMap() {
		return map;
	}

	public void setMap(Map<String, Resource> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "MapResource [map=" + map + ", getResourceId()="
				+ getResourceId() + ", getName()=" + getName()
				+ ", getDescription()=" + getDescription()
				+ ", getResourceType()=" + getResourceType() + "]";
	}

}
