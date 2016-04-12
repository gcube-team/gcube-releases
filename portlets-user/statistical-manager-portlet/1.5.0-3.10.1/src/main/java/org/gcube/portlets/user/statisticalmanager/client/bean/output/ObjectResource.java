/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.bean.output;

import java.io.Serializable;

/**
 * @author ceras
 *
 */
public class ObjectResource extends Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8772836076910728324L;
	private String value;
	
	/**
	 * 
	 */
	public ObjectResource() {
		super();
		this.setResourceType(ResourceType.OBJECT);
	}
	
	/**
	 * 
	 */
	public ObjectResource(String value) {
		this();
		this.value = value;
	}
	
	/**
	 * @return the url
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
