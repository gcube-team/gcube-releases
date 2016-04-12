package org.gcube.portlets.admin.software_upload_wizard.shared;

import java.io.Serializable;

public class Maintainer implements Serializable {

	private String firstName;
	private String lastName;
	private String email;
	private String organization;

	public Maintainer() {
	}

	public Maintainer(String firstName, String lastName, String email,
			String organization) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.organization = organization;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

}
