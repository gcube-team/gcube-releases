package org.gcube.vremanagement.vremodeler.impl.peristentobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.gcube.vremanagement.vremodeler.impl.util.ServicePair;
import org.gcube.vremanagement.vremodeler.resources.ResourceDefinition;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class FunctionalityPersisted {

	public static final String ID_FIELDNAME = "id";
	public static final String PARENT_FIELDNAME = "parent_id";

	FunctionalityPersisted() {}
	
	
	
	public FunctionalityPersisted(int id, String name, String description, boolean mandatory) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.mandatory = mandatory;
	}

	
	
	
	@DatabaseField(id=true, columnName=ID_FIELDNAME)
	private int id;
	
	@DatabaseField(canBeNull=false)
	private boolean mandatory;
	
	@DatabaseField(canBeNull=false)
	private String name;
	
	@DatabaseField(canBeNull=false)
	private String description;
	
	@DatabaseField(foreign=true, foreignAutoRefresh=true, columnName= PARENT_FIELDNAME, canBeNull=true)
	private FunctionalityPersisted parent;
	
	@DatabaseField(canBeNull=false)
	private int flag;

	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private ArrayList<ServicePair> services;
	
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private ArrayList<String> portlets;

	@ForeignCollectionField(eager = false)
	private ForeignCollection<VreFunctionalityRelation> vreRelation;
	
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private HashSet<ResourceDefinition<?>> selectableResources= new HashSet<ResourceDefinition<?>>();
	
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	private HashSet<ResourceDefinition<?>> mandatoryResources= new HashSet<ResourceDefinition<?>>();
	
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

	public FunctionalityPersisted getParent() {
		return parent;
	}

	public void setParent(FunctionalityPersisted parent) {
		this.parent = parent;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public List<ServicePair> getServices() {
		return services;
	}



	public void setServices(ArrayList<ServicePair> services) {
		this.services = services;
	}



	public List<String> getPortlets() {
		return portlets;
	}



	public void setPortlets(ArrayList<String> portlets) {
		this.portlets = portlets;
	}


	public Collection<VreFunctionalityRelation> getVreRelation() {
		return vreRelation;
	}



	public void setVreRelation(ForeignCollection<VreFunctionalityRelation> vreRelation) {
		this.vreRelation = vreRelation;
	}



	public HashSet<ResourceDefinition<?>> getSelectableResources() {
		return selectableResources;
	}



	public void setSelectableResources(
			HashSet<ResourceDefinition<?>> selectableResources) {
		this.selectableResources = selectableResources;
	}



	public HashSet<ResourceDefinition<?>> getMandatoryResources() {
		return mandatoryResources;
	}



	public void setMandatoryResources(
			HashSet<ResourceDefinition<?>> mandatoryResources) {
		this.mandatoryResources = mandatoryResources;
	}

	

	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}



	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		FunctionalityPersisted other = (FunctionalityPersisted) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
		
}
