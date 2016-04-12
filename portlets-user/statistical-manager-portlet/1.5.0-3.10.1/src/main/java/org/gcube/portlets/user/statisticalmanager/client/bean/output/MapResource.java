/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.bean.output;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ceras
 *
 */
public class MapResource extends Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8772836076910728324L;
	private String url;
	private Map<String, Resource> map;
	
	/**
	 * 
	 */
	public MapResource() {
		super();
		this.setResourceType(ResourceType.MAP);
	}
	
	/**
	 * 
	 */
	public MapResource(String url) {
		this();
		this.url = url;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param map
	 */
	public void setMap(Map<String, Resource> map) {
		this.map = map;
	}
	
	/**
	 * @return the map
	 */
	public Map<String, Resource> getMap() {
		return map;
	}
}
