package org.gcube.portlets.user.td.widgetcommonevent.shared;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public enum RequestPropertiesParameterType {
	Coordinates("Coordinates"), InvocationS("InvocationS"), RefColumn("RefColumn"), ColumnId("ColumnId"), TaskId(
			"TaskId"), ConditionCode("ConditionCode"), ValidationColumnColumnId("ValidationColumnColumnId");

	private RequestPropertiesParameterType(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}

}
