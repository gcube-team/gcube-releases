/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class ResourceTypeUIElement extends UIElement implements IsSerializable {

	/** The list of resources of this {@link ResourceTypeUIElement} */
	private List<ResourceUIElement> resources;
	
	/**
	 * Class constructor
	 */
	public ResourceTypeUIElement() {
		this.resources = new LinkedList<ResourceUIElement>();
	}
	
	/**
	 * Returns the list of resources of this {@link ResourceTypeUIElement}
	 * @return
	 */
	public List<ResourceUIElement> getResources() {
		return this.resources;
	}
	
	/**
	 * Adds a resource to this {@link ResourceTypeUIElement}'s resource list
	 * @param resource the resource to add
	 */
	public void addResource(ResourceUIElement resource) {
		this.resources.add(resource);
	}
	
	/**
	 * Checks if a resource with the given UID is contained in the resource list
	 * of this {@link ResourceTypeUIElement}
	 * @param resourceUID the UID of the resource being searched
	 * @return
	 */
	public boolean containsResource(String resourceUID) {
		for (ResourceUIElement r : resources)
			if (r.getUID().equals(resourceUID))
				return true;
		return false;
	}
	
	/**
	 * Searches for a resource with a given UID in the resource list of this
	 * {@link ResourceTypeUIElement}
	 * @param resourceUID the resource UID to search for
	 * @return the found resource
	 */
	public ResourceUIElement findResource(String resourceUID) {
		for (ResourceUIElement r : resources)
			if (r.getUID().equals(resourceUID))
				return r;
		return null;
	}
}
