/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import java.util.Map;

import org.gcube.informationsystem.impl.relation.RelationImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Relations
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonDeserialize(as=RelationImpl.class)
public interface Relation<Out extends Entity, In extends Entity> {
	
	public static final String NAME = "Relation"; //Relation.class.getSimpleName();
	
	public static final String HEADER_PROPERTY = Entity.HEADER_PROPERTY;
	
	public static final String SOURCE_PROPERTY = "source";
	public static final String TARGET_PROPERTY = "target";
	public static final String RELATION_PROPERTY = "relationProperty";
	
	
	@ISProperty(name=HEADER_PROPERTY, mandatory=true, nullable=false)
	public Header getHeader();
	
	@JsonBackReference
	@JsonGetter(value=SOURCE_PROPERTY)
	public Out getSource();
	
	@JsonGetter(value=TARGET_PROPERTY)
	public In getTarget();
	
	@ISProperty(name=RELATION_PROPERTY)
	public RelationProperty getRelationProperty();
	
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
	
}
