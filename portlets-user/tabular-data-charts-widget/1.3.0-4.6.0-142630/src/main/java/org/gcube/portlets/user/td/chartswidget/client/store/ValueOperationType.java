package org.gcube.portlets.user.td.chartswidget.client.store;

/**
* 
* @author giancarlo
* email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
*
*/
public enum ValueOperationType {
	AVG("AVG"),
	SUM("SUM"), 
	MAX("MAX"), 
	MIN("MIN");
	
	/**
	 * @param text
	 */
	private ValueOperationType(final String id) {
		this.id = id;
	}

	private final String id;
	
	@Override
	public String toString() {
		return id;
	}
	
}