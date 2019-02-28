package org.gcube.vremanagement.vremodeler.impl.peristentobjects;

import org.gcube.common.core.resources.GCUBERuntimeResource;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class RuntimeResource implements ResourceInterface{

	
	@DatabaseField(id = true,width = 60)
	private String id;
	
	@DatabaseField(canBeNull=false)
	private String name;
	
	@DatabaseField(canBeNull=false)
	private String type;
	
	@DatabaseField(canBeNull=false)
	private String description;
		
	RuntimeResource(){}

	
	public RuntimeResource(String id, String name, String type,
			String description) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.description = description;
	}



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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public String getResourceType() {
		return GCUBERuntimeResource.TYPE;
	}
	
	
	
	
	
}
