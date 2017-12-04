/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import java.util.Map;

import org.gcube.informationsystem.model.ER;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Resource;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Relations
 */
// @JsonDeserialize(as=RelationImpl.class) Do not uncomment to manage subclasses
public interface Relation<Out extends Entity, In extends Entity> extends ER {
	
	public static final String NAME = "Relation"; //Relation.class.getSimpleName();
	
	public static final String SOURCE_PROPERTY = "source";
	public static final String TARGET_PROPERTY = "target";
	
	public static final String PROPAGATION_CONSTRAINT = "propagationConstraint";
	
	/* Overriding getHeader method to create Header property in type */
	@ISProperty(name=HEADER_PROPERTY, mandatory=true, nullable=false)
	@Override
	public Header getHeader();
	
	// @JsonBackReference
	// @JsonIgnore
	@JsonIgnoreProperties({ Resource.CONSISTS_OF_PROPERTY, Resource.IS_RELATED_TO_PROPERTY })
	@JsonGetter(value=SOURCE_PROPERTY)
	public Out getSource();
	
	@JsonIgnore
	public void setSource(Out source);
	
	@JsonGetter(value=TARGET_PROPERTY)
	public In getTarget();
	
	@JsonIgnore
	public void setTarget(In target);
	
	@ISProperty(name=PROPAGATION_CONSTRAINT)
	public PropagationConstraint getPropagationConstraint();
	
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
