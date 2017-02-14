/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output;

import java.io.Serializable;

/**
 * @author ceras
 *
 */
public class FileResource extends Resource implements Serializable {

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
	 * @param url
	 * @param mimeType
	 */
	public FileResource(String url, String mimeType) {
		this();
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
	 * @param url the url to set
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
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}
