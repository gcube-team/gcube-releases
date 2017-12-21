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
public class FileResource extends Resource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 799627064179136509L;

	private String url;
	private String mimeType;
	private boolean netcdf;

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
	 * @param netcdf
	 *            netcdf
	 */
	public FileResource(String resourceId, String name, String description, String url, String mimeType,
			boolean netcdf) {
		super(resourceId, name, description, ResourceType.FILE);
		this.url = url;
		this.mimeType = mimeType;
		this.netcdf = netcdf;
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

	/**
	 * 
	 * @return true if is NetCDF file
	 */
	public boolean isNetcdf() {
		return netcdf;
	}

	/**
	 * 
	 * @param netcdf 
	 *           true if is NetCDF file
	 */
	public void setNetcdf(boolean netcdf) {
		this.netcdf = netcdf;
	}

	@Override
	public String toString() {
		return "FileResource [url=" + url + ", mimeType=" + mimeType + ", netcdf=" + netcdf + ", getResourceId()="
				+ getResourceId() + ", getName()=" + getName() + ", getDescription()=" + getDescription()
				+ ", getResourceType()=" + getResourceType() + ", isTabular()=" + isTabular() + ", isObject()="
				+ isObject() + ", isFile()=" + isFile() + ", isMap()=" + isMap() + ", isImages()=" + isImages()
				+ ", isError()=" + isError() + ", toString()=" + super.toString() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + "]";
	}

}
