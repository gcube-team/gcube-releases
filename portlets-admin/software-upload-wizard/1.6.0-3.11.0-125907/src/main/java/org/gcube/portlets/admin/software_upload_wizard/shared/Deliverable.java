package org.gcube.portlets.admin.software_upload_wizard.shared;

import java.io.Serializable;

public class Deliverable implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5509856055545599212L;
	
	public static final String SERVICE_PROFILE = "Service profile";
	public static final String README = "README";
	public static final String INSTALL = "INSTALL";
	public static final String CHANGELOG = "changelog.xml";
	public static final String MAINTAINERS = "MAINTAINERS";
	public static final String LICENSE = "LICENSE";
	public static final String POM = "pom.xml";

	private String name = "";
	private String content = "";

	public Deliverable(){
		
	}
	
	public Deliverable(String name, String content) {
		super();
		this.name = name;
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = "\n";

		result.append(this.getClass().getName() + " Object {" + NEW_LINE);
		result.append(" Name: " + getName() + NEW_LINE);
		result.append(" Content lenght: " + getContent().length() + NEW_LINE);
		result.append("}" + NEW_LINE);

		return result.toString();
	}

}
