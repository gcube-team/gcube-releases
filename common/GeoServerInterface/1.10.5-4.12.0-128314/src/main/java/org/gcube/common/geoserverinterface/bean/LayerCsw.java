/**
 * 
 */
package org.gcube.common.geoserverinterface.bean;

/**
 * @author ceras
 *
 */
public class LayerCsw {
	
	private String uuid, name, title, description, geoserverUrl;
	
	/**
	 * @param name
	 * @param title
	 * @param geoserverUrl
	 */
	public LayerCsw(String uuid, String name, String title, String description, String geoserverUrl) {
		super();
		this.name = name;
		this.title = title;
		this.description = description;
		this.geoserverUrl = geoserverUrl;
		this.uuid = uuid;
	}

	/**
	 * 
	 */
	public LayerCsw() {
		super();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the geoserverUrl
	 */
	public String getGeoserverUrl() {
		return geoserverUrl;
	}

	/**
	 * @param geoserverUrl the geoserverUrl to set
	 */
	public void setGeoserverUrl(String geoserverUrl) {
		this.geoserverUrl = geoserverUrl;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
