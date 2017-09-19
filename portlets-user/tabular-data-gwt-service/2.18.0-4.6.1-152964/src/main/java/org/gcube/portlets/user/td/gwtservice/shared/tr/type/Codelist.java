/**
 * 
 */
package org.gcube.portlets.user.td.gwtservice.shared.tr.type;

import java.io.Serializable;

/**
 * 
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class Codelist implements Serializable {

	private static final long serialVersionUID = -8353499109124097114L;

	private String id;
	private String name;
	private String agencyId;
	private String version;
	private String description;

	public Codelist() {
	}

	public Codelist(String id, String name, String agencyId, String version, String description) {
		this.id = id;
		this.name = name;
		this.agencyId = agencyId;
		this.version = version;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Codelist [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", agencyId=");
		builder.append(agencyId);
		builder.append(", version=");
		builder.append(version);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");

		return builder.toString();
	}
}
