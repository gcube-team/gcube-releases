package org.gcube.portlets.user.td.gwtservice.shared.document;


/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class DatasetDocument implements SDMXDocument {
	
	
	private static final long serialVersionUID = 6134053739629827095L;
	
	public static final DatasetDocument INSTANCE = new DatasetDocument();
	
	
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return "dataset";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "Dataset document";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return "Select this document if you want to retrieve dataset";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataSet Document [getId()=");
		builder.append(getId());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
