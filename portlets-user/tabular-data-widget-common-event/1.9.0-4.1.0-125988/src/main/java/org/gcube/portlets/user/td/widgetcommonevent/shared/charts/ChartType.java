package org.gcube.portlets.user.td.widgetcommonevent.shared.charts;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
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