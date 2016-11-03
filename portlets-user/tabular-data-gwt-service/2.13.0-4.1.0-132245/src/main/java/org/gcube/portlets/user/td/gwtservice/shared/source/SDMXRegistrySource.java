package org.gcube.portlets.user.td.gwtservice.shared.source;





public class SDMXRegistrySource implements Source {
	
	
	private static final long serialVersionUID = 3254879141340681969L;

	public static final SDMXRegistrySource INSTANCE = new SDMXRegistrySource();
	
	protected String url;
	

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return SourceType.SDMXRegistry.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "SDMX Registry source";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return "Select this source if you want to retrieve document from SDMX Registry";
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