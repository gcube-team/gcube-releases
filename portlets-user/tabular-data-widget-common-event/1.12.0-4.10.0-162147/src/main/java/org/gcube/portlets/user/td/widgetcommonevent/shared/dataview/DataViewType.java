package org.gcube.portlets.user.td.widgetcommonevent.shared.dataview;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public enum DataViewType {
	GRID("Grid"),
	RESOURCES("Resources");
	
	/**
	 * @param text
	 */
	private DataViewType(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}
	
}
