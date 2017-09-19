/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.file;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public enum HeaderPresence {
	
	NONE("None"),
	FIRST_LINE("First line"),
	FIRST_LINE_COMMENTED_INCLUDED("First line, include commented");
	
	protected String label;
	
	private HeaderPresence(String label)
	{
		this.label = label;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	public static HeaderPresence fromLabel(String label)
	{
		for (HeaderPresence headerPresence:HeaderPresence.values()) {
			if (headerPresence.label.equals(label)) return headerPresence;
		}
		throw new IllegalArgumentException("Unknown label value \""+label+"\"");
	}
}
