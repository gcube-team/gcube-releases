package org.gcube.portlets.user.td.gwtservice.shared.document;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class CodelistDocument implements SDMXDocument {
	
	private static final long serialVersionUID = 8732679674877915333L;
	
	public static final CodelistDocument INSTANCE = new CodelistDocument();
	
	
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return "codelist";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return "Codelist document";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return "Select this document if you want to retrieve codelist";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Codelist Document [getId()=");
		builder.append(getId());
		builder.append(", getName()=");
		builder.append(getName());
		builder.append(", getDescription()=");
		builder.append(getDescription());
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
