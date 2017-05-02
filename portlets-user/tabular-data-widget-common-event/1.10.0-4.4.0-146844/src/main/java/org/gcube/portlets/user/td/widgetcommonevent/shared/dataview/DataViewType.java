package org.gcube.portlets.user.td.widgetcommonevent.shared.dataview;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
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
