package org.gcube.portlets.user.gcubegeoexplorer.client;

import java.io.Serializable;

import org.gcube.portlets.user.geoexplorer.client.beans.GeoexplorerMetadataStyleInterface;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 14, 2014
 *
 */
public class GeoexplorerMetadataStyle implements GeoexplorerMetadataStyleInterface, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4015608312536522267L;
	
	private String name;
	private String style;
	private String scope;
	private boolean display = false;
	
	public GeoexplorerMetadataStyle() {
	}

	/**
	 * @param name
	 * @param style
	 * @param scope
	 * @param display
	 */
	public GeoexplorerMetadataStyle(String name, String style, String scope,
			boolean display) {
		super();
		this.name = name;
		this.style = style;
		this.scope = scope;
		this.display = display;
	}

	public String getName() {
		return name;
	}

	public String getStyle() {
		return style;
	}

	public String getScope() {
		return scope;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MetadataStyle [name=");
		builder.append(name);
		builder.append(", style=");
		builder.append(style);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", display=");
		builder.append(display);
		builder.append("]");
		return builder.toString();
	}
	
	
}