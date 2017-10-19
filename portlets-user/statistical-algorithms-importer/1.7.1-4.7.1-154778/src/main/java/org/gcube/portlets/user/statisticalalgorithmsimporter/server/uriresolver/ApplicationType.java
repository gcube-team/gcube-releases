package org.gcube.portlets.user.statisticalalgorithmsimporter.server.uriresolver;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public enum ApplicationType {
	GIS("GIS"), SMP("SMP"), SMP_ID("SMP-ID");

	/**
	 * @param text
	 */
	private ApplicationType(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}
}
