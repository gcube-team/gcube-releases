/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.data.output;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class MapResource extends Resource {

	private static final long serialVersionUID = 8772836076910728324L;
	private LinkedHashMap<String, Resource> map;

	public MapResource() {
		super();
		this.setResourceType(ResourceType.MAP);
	}

	/**
	 * 
	 * @param resourceId
	 *            resource id
	 * @param name
	 *            name
	 * @param description
	 *            description
	 * @param map
	 *            map of resources
	 */
	public MapResource(String resourceId, String name, String description, LinkedHashMap<String, Resource> map) {
		super(resourceId, name, description, ResourceType.MAP);
		this.map = map;
	}

	public Map<String, Resource> getMap() {
		return map;
	}

	public void setMap(LinkedHashMap<String, Resource> map) {
		this.map = map;
	}

	@Override
	public String toString() {
		return "MapResource [map=" + map + ", getResourceId()=" + getResourceId() + ", getName()=" + getName()
				+ ", getDescription()=" + getDescription() + ", getResourceType()=" + getResourceType() + "]";
	}

}
