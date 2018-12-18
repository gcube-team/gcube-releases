package org.gcube.vremanagement.vremodeler.impl.peristentobjects;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable
public class Collection {
		
	public static final String ID_FIELDNAME = "id";

	Collection(){}
	
	public Collection(String id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	@DatabaseField(width = 60,id = true, columnName=ID_FIELDNAME)
	private String id;
	
	@DatabaseField(canBeNull=false)
	private String name;
	
	@DatabaseField(canBeNull=false)
	private String description;

	@ForeignCollectionField(eager = false)
	private ForeignCollection<VreCollectionRelation> vreRelation;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public ForeignCollection<VreCollectionRelation> getVreRelation() {
		return vreRelation;
	}

	public void setVreRelation(ForeignCollection<VreCollectionRelation> vreRelation) {
		this.vreRelation = vreRelation;
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
		Collection other = (Collection) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
