package org.gcube.application.perform.service.engine.model.importer;

public class AnalysisType {

	private String name;
	private String id;
	
	
	@Override
	public String toString() {
		return "AnalysisType [name=" + name + ", id=" + id + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public AnalysisType(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}
	
	public AnalysisType(ImportRoutineDescriptor desc) {
		this(desc.getBatch_type(),desc.getBatch_type());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AnalysisType other = (AnalysisType) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
