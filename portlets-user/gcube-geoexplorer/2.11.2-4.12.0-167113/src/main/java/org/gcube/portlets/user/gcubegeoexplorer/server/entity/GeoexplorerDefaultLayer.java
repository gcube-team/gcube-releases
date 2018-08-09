package org.gcube.portlets.user.gcubegeoexplorer.server.entity;

import java.io.Serializable;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 26, 2013
 *
 */
public class GeoexplorerDefaultLayer implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -254074805783985479L;
	
	
	private String name;
	private String description;
	private String scope;
	private String UUID;
	private boolean isBaseLayer;
	
	public GeoexplorerDefaultLayer() {
		super();
	}
	
	/**
	 * @param name
	 * @param description
	 * @param scope
	 * @param uUID
	 * @param isBaseLayer
	 * @param hashLayersName
	 */
	public GeoexplorerDefaultLayer(String name, String description, String scope, String uUID, boolean isBaseLayer) {
		this.name = name;
		this.description = description;
		this.scope = scope;
		this.UUID = uUID;
		this.isBaseLayer = isBaseLayer;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getScope() {
		return scope;
	}

	public String getUUID() {
		return UUID;
	}

	public boolean isBaseLayer() {
		return isBaseLayer;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public void setBaseLayer(boolean isBaseLayer) {
		this.isBaseLayer = isBaseLayer;
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoexplorerDefaultLayer [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", UUID=");
		builder.append(UUID);
		builder.append(", isBaseLayer=");
		builder.append(isBaseLayer);
		builder.append("]");
		return builder.toString();
	}

	
}