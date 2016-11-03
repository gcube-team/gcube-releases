/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.tr.type;

import java.io.Serializable;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class Agencies implements Serializable {
	
	private static final long serialVersionUID = -8353499109124097114L;
	
	protected String id;
	protected String name;
	protected String description;
	protected String nameLabel;
	
	
	public Agencies(){}
	
	/**
	 * @param id
	 * @param name
	 * @param agencyId
	 */
	public Agencies(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.nameLabel=id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * 
	 * @return label
	 */
	public String getNameLabel() {
		return nameLabel;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Agency [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}
}
