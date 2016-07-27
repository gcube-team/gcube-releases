/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.data.output;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ImageResource extends Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8772836076910728324L;
	private String link;
	private String mimeType;

	/**
	 * 
	 */
	public ImageResource() {
		super();
		this.setResourceType(ResourceType.IMAGE);
	}

	/**
	 * 
	 * @param resourceId
	 * @param name
	 * @param description
	 * @param link
	 */
	public ImageResource(String resourceId, String name, String description,
			String link, String mimeType) {
		super(resourceId, name, description, ResourceType.IMAGE);
		this.link = link;
		this.mimeType = mimeType;
	}

	/**
	 * 
	 * @return link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * 
	 * @param link
	 */
	public void setLink(String link) {
		this.link = link;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return "ImagesResource [link=" + link + ", mimeType=" + mimeType
				+ ", getResourceId()=" + getResourceId() + ", getName()="
				+ getName() + ", getDescription()=" + getDescription()
				+ ", getResourceType()=" + getResourceType() + "]";
	}

}
