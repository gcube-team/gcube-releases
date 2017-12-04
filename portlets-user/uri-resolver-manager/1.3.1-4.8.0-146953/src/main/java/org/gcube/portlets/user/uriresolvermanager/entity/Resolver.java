/**
 * 
 */
package org.gcube.portlets.user.uriresolvermanager.entity;


/**
 * The Class Resolver.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 4, 2015
 */
public class Resolver {
	
	private String resourceName;
	private String entryName;
	
	/**
	 * Instantiates a new resolver.
	 *
	 * @param resourceName the resource name
	 * @param entryName the entry name
	 */
	public Resolver(String resourceName, String entryName) {
		super();
		this.resourceName = resourceName;
		this.entryName = entryName;
	}
	
	/**
	 * Gets the resource name.
	 *
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}
	
	/**
	 * Gets the entry name.
	 *
	 * @return the entryName
	 */
	public String getEntryName() {
		return entryName;
	}
	
	/**
	 * Sets the resource name.
	 *
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	/**
	 * Sets the entry name.
	 *
	 * @param entryName the entryName to set
	 */
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Resolver [resourceName=");
		builder.append(resourceName);
		builder.append(", entryName=");
		builder.append(entryName);
		builder.append("]");
		return builder.toString();
	}
}
