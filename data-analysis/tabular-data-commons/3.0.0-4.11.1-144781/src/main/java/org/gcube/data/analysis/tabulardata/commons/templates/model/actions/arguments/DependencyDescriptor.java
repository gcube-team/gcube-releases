package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments;

public class DependencyDescriptor {

	public enum DependencyType {
		LOCAL,
		GLOBAL
	}

	private String dependencyId;
	private DependencyType depType;

	public DependencyDescriptor(String dependencyId, DependencyType depType) {
		super();
		this.dependencyId = dependencyId;
		this.depType = depType;
	}

	public String getDependencyId() {
		return dependencyId;
	}

	public DependencyType getDepType() {
		return depType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dependencyId == null) ? 0 : dependencyId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DependencyDescriptor other = (DependencyDescriptor) obj;
		if (dependencyId == null) {
			if (other.dependencyId != null)
				return false;
		} else if (!dependencyId.equals(other.dependencyId))
			return false;
		return true;
	}
		
}
