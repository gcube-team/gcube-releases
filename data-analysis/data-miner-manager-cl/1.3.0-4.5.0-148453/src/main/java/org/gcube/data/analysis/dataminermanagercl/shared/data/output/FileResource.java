/**
 * 
 */
package org.gcube.data.analysis.dataminermanagercl.shared.data.output;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class FileResource extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 799627064179136509L;

	private String url;
	private String mimeType;

	/**
	 * 
	 */
	public FileResource() {
		super();
		this.setResourceType(ResourceType.FILE);
	}

	/**
	 * 
	 * @param resourceId
	 *            resource id
	 * @param name
	 *            name
	 * @param description
	 *            description
	 * @param url
	 *            url
	 * @param mimeType
	 *            mime type
	 */
	public FileResource(String resourceId, String name, String description, String url, String mimeType) {
		super(resourceId, name, description, ResourceType.FILE);
		this.url = url;
		this.mimeType = mimeType;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param mimeType
	 *            the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return "FileResource [url=" + url + ", mimeType=" + mimeType + ", getResourceId()=" + getResourceId()
				+ ", getName()=" + getName() + ", getDescription()=" + getDescription() + ", getResourceType()="
				+ getResourceType() + "]";
	}

}
