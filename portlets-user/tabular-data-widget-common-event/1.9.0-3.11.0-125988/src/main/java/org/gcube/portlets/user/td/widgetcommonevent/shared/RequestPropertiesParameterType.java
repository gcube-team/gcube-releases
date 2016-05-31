package org.gcube.portlets.user.td.widgetcommonevent.shared;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public enum RequestPropertiesParameterType {
	Coordinates("Coordinates"),
	InvocationS ("InvocationS"),
	RefColumn("RefColumn"),
	ColumnId("ColumnId"),
	TaskId("TaskId"),
	ConditionCode("ConditionCode"),
	ValidationColumnColumnId("ValidationColumnColumnId");
	
	/**
	 * @param text
	 */
	private RequestPropertiesParameterType(final String id) {
		this.id = id;
	}

	private final String id;
	
	@Override
	public String toString() {
		return id;
	}
	
}
