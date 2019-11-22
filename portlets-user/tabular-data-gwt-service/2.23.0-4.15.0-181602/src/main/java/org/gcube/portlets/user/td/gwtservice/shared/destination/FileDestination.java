package org.gcube.portlets.user.td.gwtservice.shared.destination;



public class FileDestination implements Destination {
	
	
	private static final long serialVersionUID = -5990408094142286488L;
	
	public static final FileDestination INSTANCE = new FileDestination();
	
	
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return "File";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "File destination";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return "Select this destination if you want to save in File";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("File source [getId()=");
		builder.append(getId());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append("]");
		return builder.toString();
	}
	
	
	
}