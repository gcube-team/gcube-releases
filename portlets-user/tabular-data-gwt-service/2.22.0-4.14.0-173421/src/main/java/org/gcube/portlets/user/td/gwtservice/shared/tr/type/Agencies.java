/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.tr.type;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class Agencies implements Serializable {

	private static final long serialVersionUID = -8353499109124097114L;

	private String id;
	private String name;
	private String description;
	private String nameLabel;

	public Agencies() {
	}

	public Agencies(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.nameLabel = id;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getNameLabel() {
		return nameLabel;
	}

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
