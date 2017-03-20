/**
 * 
 */
package org.gcube.vremanagement.executor.plugin;

import org.gcube.vremanagement.executor.api.types.Scheduling;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property=Scheduling.CLASS_PROPERTY)
@JsonTypeName(value="Ref")
public class Ref {

	protected String id;
	protected String address;
	
	protected Ref() {}
	
	/**
	 * @param id
	 * @param address
	 */
	public Ref(String id, String address) {
		super();
		this.id = id;
		this.address = address;
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
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	
}