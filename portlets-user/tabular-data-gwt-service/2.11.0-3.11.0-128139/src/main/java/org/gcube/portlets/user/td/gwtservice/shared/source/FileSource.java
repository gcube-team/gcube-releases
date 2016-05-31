package org.gcube.portlets.user.td.gwtservice.shared.source;



public class FileSource implements Source {
	
	
	private static final long serialVersionUID = -5990408094142286488L;
	
	public static final FileSource INSTANCE = new FileSource();
	
	
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return SourceType.FILE.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "File source";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return "Select this source if you want to retrieve document from File";
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