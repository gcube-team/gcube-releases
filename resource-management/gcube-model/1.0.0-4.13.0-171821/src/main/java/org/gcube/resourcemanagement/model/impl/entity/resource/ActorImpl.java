/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.resource;

import org.gcube.informationsystem.model.impl.entity.ResourceImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.Actor;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Actor.NAME)
public abstract class ActorImpl extends ResourceImpl implements Actor {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -7959825469925979101L;
	
}
