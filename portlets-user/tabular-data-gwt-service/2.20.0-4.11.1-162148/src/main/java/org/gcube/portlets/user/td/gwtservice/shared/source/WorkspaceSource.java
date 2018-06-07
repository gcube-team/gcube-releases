package org.gcube.portlets.user.td.gwtservice.shared.source;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class WorkspaceSource implements Source {

	private static final long serialVersionUID = 2826706131664617270L;

	public static final WorkspaceSource INSTANCE = new WorkspaceSource();

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return SourceType.WORKSPACE.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "Workspace source";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return "Select this source if you want to retrieve document from Workspace";
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