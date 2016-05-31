package org.gcube.common.couchdbconnector;

import org.gcube.common.couchdb.connector.Entity;


public class EntityExt extends Entity {

	private String name;
	private String value;
	
	public EntityExt(){}
	
	public EntityExt(String id, String name, String value) {
		super(id);
		
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "EntityExt [name=" + name + ", value=" + value + ", get_id()="
				+ get_id() + ", get_rev()=" + get_rev() + "]";
	}

			
	
}
