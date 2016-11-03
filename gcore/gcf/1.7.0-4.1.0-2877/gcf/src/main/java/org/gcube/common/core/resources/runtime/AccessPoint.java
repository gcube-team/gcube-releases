package org.gcube.common.core.resources.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gcube.common.core.resources.GCUBERuntimeResource;

/**
 * Access point for {@link GCUBERuntimeResource}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class AccessPoint {
	
	private String endpoint;
	
	private String entryname;
	
	private String username;
	
	private String password;
		
	private String description;
	
	private Map<String,PropertyData> properties  = new HashMap<String, PropertyData>();

	/**
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * @param endpoint the endpoint to set
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * Adds a new property	
	 * @param name the name
	 * @param value the value
	 */
	public void addProperty(String name,String value, boolean ...encrypted) {
		properties.put(name, new PropertyData(value, (encrypted!=null && encrypted.length>0)? encrypted[0]: false));
	}
	
	/**
	 * Gets a property's value
	 * @param name the name
	 * @return the value or null if the property does not exist
	 */
	public String getProperty(String name) {
		try {
			return properties.get(name).getValue();
		} catch (Exception e) { return null;}
	}
	
	/**
	 * Gets a property's encryption setting
	 * @param name the name
	 * @return true if the property was encrypted, false otherwise
	 */
	public boolean isPropertyEncrypted(String name) {
		return properties.get(name).isEncrypted();
	}
	
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets all properties' names
	 * @return the properties' names
	 */
	public Set<String> getAllPropertyNames() {
		return Collections.unmodifiableSet(properties.keySet());
	}
	
	/**
	 * @return the entryname
	 */
	public String getEntryname() {
		return entryname;
	}

	/**
	 * @param entryname the entryname to set
	 */
	public void setEntryname(String entryname) {
		this.entryname = entryname;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public class PropertyData {

		  private final String value;
		  private final boolean encrypted;

		  public PropertyData(String value, boolean encrypted) {
		    this.value = value;
		    this.encrypted = encrypted;
		  }

		  public String getValue() { return value; }
		  public boolean isEncrypted() { return encrypted; }

		  @Override
		  public int hashCode() { return value.hashCode() ; }

		  @Override
		  public boolean equals(Object o) {
		    if (o == null) return false;
		    if (!(o instanceof PropertyData)) return false;
		    PropertyData pairo = (PropertyData) o;
		    return this.value.equals(pairo.getValue());
		  }

		}

}
