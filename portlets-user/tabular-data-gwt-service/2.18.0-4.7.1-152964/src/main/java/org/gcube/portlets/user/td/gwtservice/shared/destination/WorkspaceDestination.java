package org.gcube.portlets.user.td.gwtservice.shared.destination;




public class WorkspaceDestination implements Destination {
	
	
	private static final long serialVersionUID = 2826706131664617270L;
	
	public static final WorkspaceDestination INSTANCE = new WorkspaceDestination();
	
	
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return "Workspace";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "Workspace destination";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return "Select this destination if you want to save in Workspace";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Workspace source [getId()=");
		builder.append(getId());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append("]");
		return builder.toString();
	}
	
	
	
}