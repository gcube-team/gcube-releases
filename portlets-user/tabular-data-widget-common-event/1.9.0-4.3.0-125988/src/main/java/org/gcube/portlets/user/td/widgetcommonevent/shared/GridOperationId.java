package org.gcube.portlets.user.td.widgetcommonevent.shared;

public enum GridOperationId {
	ROWADD("1"),
	ROWEDIT("2"),
	ROWDELETE("3"),
	REPLACE("4");
	
	/**
	 * @param text
	 */
	private GridOperationId(final String id) {
		this.id = id;
	}

	private final String id;
	
	@Override
	public String toString() {
		return id;
	}
}

