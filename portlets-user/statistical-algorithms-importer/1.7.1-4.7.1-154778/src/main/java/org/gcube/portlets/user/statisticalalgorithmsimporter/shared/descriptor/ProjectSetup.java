package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ProjectSetup implements Serializable {

	private static final long serialVersionUID = -7559226273036843188L;
	private String language;
	private ProjectSupportType projectSupportType;

	public ProjectSetup() {
		super();
	}

	public ProjectSetup(String language, ProjectSupportType projectSupportType) {
		super();
		this.language = language;
		this.projectSupportType = projectSupportType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public ProjectSupportType getProjectSupportType() {
		return projectSupportType;
	}

	public void setProjectSupportType(ProjectSupportType projectSupportType) {
		this.projectSupportType = projectSupportType;
	}

	@Override
	public String toString() {
		return "ProjectSetup [language=" + language + ", projectSupportType=" + projectSupportType + "]";
	}

}
