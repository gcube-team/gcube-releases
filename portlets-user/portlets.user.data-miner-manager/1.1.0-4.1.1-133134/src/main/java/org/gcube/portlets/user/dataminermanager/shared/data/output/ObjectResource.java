/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.data.output;


/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ObjectResource extends Resource {

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
	public ObjectResource(String resourceId, String name, String description,
			String value) {
		super(resourceId, name, description, ResourceType.OBJECT);
		this.value = value;
	}

	/**
	 * @return the url
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "ObjectResource [value=" + value + ", getResourceId()="
				+ getResourceId() + ", getName()=" + getName()
				+ ", getDescription()=" + getDescription()
				+ ", getResourceType()=" + getResourceType() + "]";
	}

}
