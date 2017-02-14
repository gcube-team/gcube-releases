package org.gcube.portlets.admin.fhn_manager_portlet.shared.communication;

public enum Operation {

	
	START_NODE("Start Node",101,"Starts the selected remote node"),
	STOP_NODE("Stop Node",102,"Stops the selected remote node"),

	CREATE_OBJECT("Register Element",201,"Registers the information in the system"),
	DESTROY_OBJECT("Destroy Element",202,"Removes the information from the system"),
	
	
	GATHER_INFORMATION("Gather Information",301,"Gathers Information about the selected object and related entities.");
	
	private final String name;
	private final int code;
	private final String description;
	private Operation(String name, int code, String description) {
		this.name = name;
		this.code = code;
		this.description = description;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	
	
	
}
