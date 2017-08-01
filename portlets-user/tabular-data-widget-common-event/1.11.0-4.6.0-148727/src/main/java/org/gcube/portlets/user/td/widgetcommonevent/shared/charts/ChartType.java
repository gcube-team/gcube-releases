package org.gcube.portlets.user.td.widgetcommonevent.shared.charts;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public enum ChartType {
	TopRating("Top Rating");
	
	
	/**
	 * @param text
	 */
	private ChartType(final String id) {
		this.id = id;
	}

	private final String id;
	
	@Override
	public String toString() {
		return id;
	}
	
}