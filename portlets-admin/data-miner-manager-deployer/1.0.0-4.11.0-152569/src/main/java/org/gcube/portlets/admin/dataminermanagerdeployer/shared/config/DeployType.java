package org.gcube.portlets.admin.dataminermanagerdeployer.shared.config;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public enum DeployType {
	Stage("stage"), Production("add");

	private DeployType(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}

	public String getLabel() {
		return id;
	}

	public boolean compareId(String identificator) {
		if (identificator.compareTo(id) == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static DeployType getTypeFromId(String id) {
		for (DeployType testType : values()) {
			if (testType.id.compareToIgnoreCase(id) == 0) {
				return testType;
			}
		}
		return null;
	}

}