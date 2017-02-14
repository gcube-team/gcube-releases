package org.gcube.portlets.admin.fhn_manager_portlet.shared.model;

import java.io.Serializable;

import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;

public class VMTemplate implements Storable,Serializable{
	
	public static final ObjectType TYPE=ObjectType.VM_TEMPLATES;
	
	public static final String prefix="node.";
	
	public static final String ID_FIELD=prefix+"ID";
	public static final String NAME_FIELD=prefix+"NAME";
	public static final String CORES_FIELD=prefix+"CORES";
	public static final String MEMORY_FIELD=prefix+"MEMORY";
	public static final String PROVIDER_FIELD=prefix+"PROVIDER";
	
	
	
	private String id;
	private String name;
	private Integer cores;
	private Double memory;
	
	private String providerId;
	
	
	
	public VMTemplate() {
		// TODO Auto-generated constructor stub
	}
	
	
	

	
	public VMTemplate(String id, String name, Integer cores, Double memory,String providerId) {
		super();
		this.id = id;
		this.name = name;
		this.cores = cores;
		this.memory = memory;
		this.providerId=providerId;
	}





	/**
	 * @return the cores
	 */
	public Integer getCores() {
		return cores;
	}





	/**
	 * @param cores the cores to set
	 */
	public void setCores(Integer cores) {
		this.cores = cores;
	}





	/**
	 * @return the memory
	 */
	public Double getMemory() {
		return memory;
	}





	/**
	 * @param memory the memory to set
	 */
	public void setMemory(Double memory) {
		this.memory = memory;
	}


public String getProviderId() {
	return providerId;
}

public void setProviderId(String providerId) {
	this.providerId = providerId;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 8622158613374592788L;
	
	@Override
	public String getKey() {
		return getId();
	}
	
	@Override
	public Object getObjectField(String fieldName) {
		if(fieldName.equals(ID_FIELD)) return getId();
		if(fieldName.equals(NAME_FIELD)) return getNameValue();
		if(fieldName.equals(CORES_FIELD)) return getCores();
		if(fieldName.equals(MEMORY_FIELD)) return getMemory();
		if(fieldName.equals(PROVIDER_FIELD)) return getProviderId();
		return null;
	}
	
	
	@Override
	public ObjectType getType() {
		return TYPE;
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the name
	 */
	public String getNameValue() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VMTemplate other = (VMTemplate) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VMTemplate [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", cores=");
		builder.append(cores);
		builder.append(", memory=");
		builder.append(memory);
		builder.append(", providerId=");
		builder.append(providerId);
		builder.append("]");
		return builder.toString();
	}





	@Override
	public String getName() {
		return name!=null?getName():getId();
	}
	
	
	
}
