package org.gcube.vomanagement.usermanagement.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Email implements Serializable {
	private String email;
	private String type;
	boolean primary;
	
	public Email() {
		super();
	}
	public Email(String email, String type, boolean primary) {
		super();
		this.email = email;
		this.type = type;
		this.primary = primary;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isPrimary() {
		return primary;
	}
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
	@Override
	public String toString() {
		return "Email [email=" + email + ", type=" + type + ", primary="
				+ primary + "]";
	}
	
	
}
