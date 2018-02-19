/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.data.output;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ImageResource extends Resource {

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
	 *            resource id
	 * @param name
	 *            name
	 * @param description
	 *            description
	 * @param link
	 *            link
	 * @param mimeType
	 *            mime type
	 */
	public ImageResource(String resourceId, String name, String description, String link, String mimeType) {
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
	 * @param link link
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
		return "ImagesResource [link=" + link + ", mimeType=" + mimeType + ", getResourceId()=" + getResourceId()
				+ ", getName()=" + getName() + ", getDescription()=" + getDescription() + ", getResourceType()="
				+ getResourceType() + "]";
	}

}
