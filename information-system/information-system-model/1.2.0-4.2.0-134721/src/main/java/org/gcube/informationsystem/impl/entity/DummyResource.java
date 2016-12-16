/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import java.util.UUID;

import org.gcube.informationsystem.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.model.entity.Resource;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class DummyResource extends ResourceImpl implements Resource {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -8522083786087905215L;

	public DummyResource(UUID uuid){
		super();
		this.header = new HeaderImpl(uuid);
	}
	
}
