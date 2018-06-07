package org.gcube.portlets.user.td.gwtservice.shared.destination;





public class SDMXRegistryDestination implements Destination {
	
	
	private static final long serialVersionUID = 3254879141340681969L;

	public static final SDMXRegistryDestination INSTANCE = new SDMXRegistryDestination();
	
	protected String url;
	

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return "SDMXRegistry";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "SDMX Registry destination";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return "Select this destination if you want to save in SDMX Registry";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SDMXRegistry source [getId()=");
		builder.append(getId());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append(", getUrl()=");
		builder.append(getUrl());
		builder.append("]");
		return builder.toString();
	}
	
	
	
}