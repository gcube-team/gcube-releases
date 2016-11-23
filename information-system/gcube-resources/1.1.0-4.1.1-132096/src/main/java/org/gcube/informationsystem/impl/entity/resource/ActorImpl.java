/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.impl.entity.ResourceImpl;
import org.gcube.informationsystem.model.entity.resource.Actor;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=Actor.NAME)
public abstract class ActorImpl extends ResourceImpl implements Actor {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -7959825469925979101L;
	
}
