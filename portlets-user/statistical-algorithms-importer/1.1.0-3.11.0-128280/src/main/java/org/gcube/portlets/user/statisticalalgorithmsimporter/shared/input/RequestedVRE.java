package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input;

import java.io.Serializable;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RequestedVRE implements Serializable {

	private static final long serialVersionUID = -7548059008384829524L;
	private int id;
	private String name;
	private String description;

	public RequestedVRE() {
		super();
	}

	public RequestedVRE(int id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "RequestedVRE [id=" + id + ", name=" + name + ", description="
				+ description + "]";
	}

	
	

}
