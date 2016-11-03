/**
 * 
 */
package org.gcube.documentstore.records;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.gcube.documentstore.exception.InvalidValueException;
import org.gcube.documentstore.records.implementation.RequiredField;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public interface Record extends Comparable<Record>, Serializable {

	/**
	 * KEY : The unique identifier for the {@link Record}
	 * The ID SHOULD automatically created by the implementation class.
	 */
	@RequiredField
	public static final String ID = "id";
	
	/**
	 * KEY : The instant when the {@link Record} was created. 
	 * The value MUST be recorded in UTC milliseconds from the epoch.
	 */
	@RequiredField
	public static final String CREATION_TIME = "creationTime";
	
	/**
	 * KEY : The Type of the represented {@link Record}
	 */
	@RequiredField
	public static final String RECORD_TYPE = "recordType";
	
	/**
	 * @return a Set containing the keys of required fields
	 * The returned Set MUST be a copy of the internal representation. 
	 * Any modification to the returned Set MUST not affect the object
	 */
	public Set<String> getRequiredFields();
	
	/**
	 * @return a Set containing the keys of computed fields
	 * The returned Set MUST be a copy of the internal representation. 
	 * Any modification to the returned Set MUST not affect the object
	 */
	public Set<String> getComputedFields();
	
	/**
	 * 
	 * @param instance
	 * @return
	 * @throws Exception
	 */
	public SortedSet<String> getQuerableKeys() throws Exception;
	
	/**
	 * Return the {@link Record} Type
	 * @return {@link Record} Type
	 */
	public String getRecordType();
	
	/**
	 * Return the unique id for this {@link Record}
	 * @return {@link Record} Unique ID
	 */
	public String getId();

	/**
	 * The ID SHOULD be automatically created by the implementation Class. 
	 * Set the ID only if you really know what you are going to do.
	 * Set the unique id for this {@link Record}
	 * @param id Unique ID
	 * @throws InvalidValueException
	 */
	public void setId(String id) throws InvalidValueException;

	/**
	 * Return the instant when this {@link Record} was created.
	 * @return the creation time for this {@link Record}
	 */
	public Calendar getCreationTime();

	/**
	 * The CreationTime is automatically created by the implementation Class. 
	 * Set the CreationTime only if you really know what you are going to do.
	 * Set the instant when this {@link Record} was created.
	 * @param creationTime Creation Time
	 * @throws InvalidValueException
	 */
	public void setCreationTime(Calendar creationTime) throws InvalidValueException;
	
	/**
	 * Return all resource-specific properties. The returned Map is a copy of
	 * the internal representation. Any modification to the returned Map MUST
	 * not affect the object
	 * @return a Map containing the properties
	 */
	public Map<String, Serializable> getResourceProperties();

	/**
	 * Set all resource-specific properties, replacing existing ones
	 */
	public void setResourceProperties(Map<String, ? extends Serializable> resourceSpecificProperties) throws InvalidValueException;

	/**
	 * Return the value of the given resource property.
	 * @param key the key of the requested property 
	 * @return the value of the given resource property
	 */
	public Serializable getResourceProperty(String key);
	
	/**
	 * Set the value of the given resource property.
	 * If the key has the value of one of the predefined property, the value
	 * is validated.
	 * @param key the key of the requested property 
	 * @param value the value of the given resource property
	 */
	public void setResourceProperty(String key, Serializable value) throws InvalidValueException;
	
	/**
	 * Validate the Resource Record.
	 * The validation check if all the Required Field are set and has valid 
	 * value.
	 * @throws InvalidValueException
	 */
	public void validate() throws InvalidValueException;

}
