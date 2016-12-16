package org.gcube.portlets.user.td.gwtservice.shared.source;

public enum SourceType {
	FILE("File"),
	URL("Url"), 
	WORKSPACE("Workspace"), 
	SDMXRegistry("SDMXRegistry");
	
	/**
	 * @param text
	 */
	private SourceType(final String id) {
		this.id = id;
	}

	private final String id;
	
	@Override
	public String toString() {
		return id;
	}
	
}
