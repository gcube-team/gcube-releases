package org.gcube.portlets.widgets.openlayerbasicwidgets.shared.data;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public enum ProjectionType {

	EPSG4326("EPSG:4326"),
	EPSG3857("EPSG:3857");
	
	private final String id;
	
	private ProjectionType(final String id) {
		this.id = id;
	}


	@Override
	public String toString() {
		return id;
	}
	
	public String getLabel() {
		return id;
	}

	
	public static ProjectionType getProjectionTypeFromId(String id) {
		for (ProjectionType projectionType : values()) {
			if (projectionType.id.compareToIgnoreCase(id) == 0) {
				return projectionType;
			}
		}
		return null;
	}
	
	
	
}
