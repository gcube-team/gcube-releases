package org.gcube.portlets.user.gcubegisviewer.client;

import java.io.Serializable;

import org.gcube.portlets.user.gisviewer.client.commons.beans.GisViewerBaseLayerInterface;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 14, 2014
 *
 */
public class GisViewerBaseLayer implements GisViewerBaseLayerInterface, Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 594905971684323321L;
	
	private String title;
	private String name;
	private String wmsURL;
	private boolean display = false;

	private String scope;
	
	public GisViewerBaseLayer(){}
	/**
	 * @param title
	 * @param name
	 * @param wmsURL
	 * @param display
	 */
	public GisViewerBaseLayer(String title, String name, String wmsURL, boolean display) {
		super();
		this.title = title;
		this.name = name;
		this.wmsURL = wmsURL;
		this.display = display;
	}
	
	public String getTitle() {
		return title;
	}
	public String getName() {
		return name;
	}
	public String getWmsServiceBaseURL() {
		return wmsURL;
	}
	public boolean isDisplay() {
		return display;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWmsURL(String wmsURL) {
		this.wmsURL = wmsURL;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}
	
	/**
	 * @param string
	 */
	public void setScope(String scope) {
		this.scope = scope;
		
	}

	public String getScope() {
		return scope;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GisViewerBaseLayer [title=");
		builder.append(title);
		builder.append(", name=");
		builder.append(name);
		builder.append(", wmsURL=");
		builder.append(wmsURL);
		builder.append(", display=");
		builder.append(display);
		builder.append(", scope=");
		builder.append(scope);
		builder.append("]");
		return builder.toString();
	}
	
}