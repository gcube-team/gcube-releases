package org.gcube.informationsystem.model.entity;

import java.util.Map;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.annotations.Abstract;
import org.gcube.informationsystem.model.exceptions.InvalidFacet;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Facets
 */
@Abstract
@JsonDeserialize(as=FacetImpl.class)
public abstract interface Facet extends Entity {
	
	public static final String NAME = "Facet"; //Facet.class.getSimpleName();
	public static final String DESCRIPTION = "This is the base class for Facet";
	public static final String VERSION = "1.0.0";
	
	/**
	 * Return all properties. The returned Map is a copy of
	 * the internal representation. Any modification to the returned Map MUST
	 * not affect the object
	 * @return a Map containing the properties
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties();

	/**
	 * Set all properties, replacing existing ones
	 */
	public void setAdditionalProperties(Map<String, Object> additionalProperties);

	/**
	 * Return the value of the given property.
	 * @param key the key of the requested property 
	 * @return the value of the given property
	 */
	public Object getAdditionalProperty(String key);
	
	/**
	 * Set the value of the given property.
	 * @param key the key of the requested property 
	 * @param value the value of the given resource property
	 */
	@JsonAnySetter
	public void setAdditionalProperty(String key, Object value);
	

	@Override
	public void validate() throws InvalidFacet;
	
}
