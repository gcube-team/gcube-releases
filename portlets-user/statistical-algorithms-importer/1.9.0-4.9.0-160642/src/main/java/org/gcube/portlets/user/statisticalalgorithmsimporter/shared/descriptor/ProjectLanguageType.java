package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public enum ProjectLanguageType {

	R("R"), R_BLACKBOX("R-blackbox"), JAVA("Java"), KNIME_WORKFLOW("Knime-Workflow"), LINUX_COMPILED(
			"Linux-compiled"), OCTAVE(
					"Octave"), PYTHON("Python"), WINDOWS_COMPILED("Windows-compiled"), PRE_INSTALLED("Pre-Installed");

	private String id;

	private ProjectLanguageType(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return id;
	}

	public String getLabel() {
		return id;
	}

	public static ProjectLanguageType valueFromLabel(String label) {
		for (ProjectLanguageType type : values()) {
			if (type.getId().compareTo(label) == 0) {
				return type;
			}
		}
		return null;
	}

	public static List<ProjectLanguageType> asList() {
		List<ProjectLanguageType> list = Arrays.asList(values());
		return list;
	}

}
