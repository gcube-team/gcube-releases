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
public class ImagesResource extends Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8772836076910728324L;
	private Map<String, String> mapImages;
	private String folderUrl;
	
	/**
	 * 
	 */
	public ImagesResource() {
		super();
		this.setResourceType(ResourceType.IMAGES);
	}
	
	/**
	 * 
	 */
	public ImagesResource(String folderUrl) {
		this();
		this.folderUrl = folderUrl;
	}

	/**
	 * @return the urls
	 */
	public Map<String, String> getMapImages() {
		return mapImages;
	}
	
	/**
	 * @param urls the urls to set
	 */
	public void setMapImages(Map<String, String> map) {
		this.mapImages = map;
	}
	
	public String getFolderUrl() {
		return folderUrl;
	}
	
	/**
	 * @param folderUrl the folderUrl to set
	 */
	public void setFolderUrl(String folderUrl) {
		this.folderUrl = folderUrl;
	}

	@Override
	public String toString() {
		return "ImagesResource [getResourceId()=" + getResourceId()
				+ ", getDescription()=" + getDescription() + ", getName()="
				+ getName() + ", getResourceType()=" + getResourceType()
				+ ", mapImages=" + mapImages + ", folderUrl=" + folderUrl + "]";
	}

	
	
	
}
