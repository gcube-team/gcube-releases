/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.resource;

import org.gcube.informationsystem.model.reference.annotations.Abstract;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.impl.entity.resource.ActorImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Actor
 */
@Abstract
@JsonDeserialize(as=ActorImpl.class)
public interface Actor extends Resource {
	
	public static final String NAME = "Actor"; // Actor.class.getSimpleName();
	public static final String DESCRIPTION = "Actor";
	public static final String VERSION = "1.0.0";
	
}
