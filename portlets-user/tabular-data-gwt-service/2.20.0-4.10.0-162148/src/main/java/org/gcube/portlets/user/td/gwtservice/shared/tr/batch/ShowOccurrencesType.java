package org.gcube.portlets.user.td.gwtservice.shared.tr.batch;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public enum ShowOccurrencesType {
	ONLYERRORS("Only Errors"),
	ALL("All");
	
	
	/**
	 * @param text
	 */
	private ShowOccurrencesType(final String id) {
		this.id = id;
	}

	private final String id;
	
	@Override
	public String toString() {
		return id;
	}

}
