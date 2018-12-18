/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.resource;

import org.gcube.resourcemanagement.model.impl.entity.resource.PersonImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Person
 */
@JsonDeserialize(as=PersonImpl.class)
public interface Person extends Actor {
	
	public static final String NAME = "Person"; // Person.class.getSimpleName();
	public static final String DESCRIPTION = "Person";
	public static final String VERSION = "1.0.0";
		
}
