/**
 * 
 */
package org.gcube.informationsystem.model.entity;

import org.gcube.informationsystem.impl.entity.ContextImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Context
 */
@JsonDeserialize(as=ContextImpl.class)
public interface Context extends Entity {
	
	public static final String NAME = "Context"; //Context.class.getSimpleName();
	
	public static final String NAME_PROPERTY = "name";
	
	@ISProperty(name=NAME_PROPERTY, mandatory=true, nullable=false)
	public String getName();
	
	public void setName(String name);
}
