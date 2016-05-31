package org.gcube.portlets.user.td.gwtservice.shared.source;



public class UrlSource implements Source {
	
	
	private static final long serialVersionUID = -5990408094142286488L;
	
	public static final UrlSource INSTANCE = new UrlSource();
	
	
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return SourceType.URL.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "Url source";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return "Select this source if you want to retrieve document from Url";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Url source [getId()=");
		builder.append(getId());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append("]");
		return builder.toString();
	}
	
	
	
}