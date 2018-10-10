package org.gcube.vremanagement.vremodeler.impl.peristentobjects;

import org.gcube.common.core.resources.GCUBEGenericResource;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class GenericResource implements ResourceInterface {

	@DatabaseField(width = 60,id = true)
	private String id;
	
	@DatabaseField(canBeNull=false)
	private String type;

	@DatabaseField(canBeNull=false)
	private String name;
	
	@DatabaseField()
	private String description;
	
	@DatabaseField(dataType= DataType.LONG_STRING)
	private String body;
	
	GenericResource(){}
		
	

	public GenericResource(String id, String type, String name,
			String description, String body) {
		super();
		this.id = id;
		this.type = type;
		this.name = name;
		this.description = description;
		this.body = body;
	}



	/* (non-Javadoc)
	 * @see org.gcube.vremanagement.vremodeler.impl.peristentobjects.ResourceInterfaceA#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.gcube.vremanagement.vremodeler.impl.peristentobjects.ResourceInterfaceA#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see org.gcube.vremanagement.vremodeler.impl.peristentobjects.ResourceInterfaceA#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see org.gcube.vremanagement.vremodeler.impl.peristentobjects.ResourceInterfaceA#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public String getBody() {
		return body;
	}



	public void setBody(String body) {
		this.body = body;
	}



	@Override
	public String getResourceType() {
		return GCUBEGenericResource.TYPE;
	}
	
	
	
}
