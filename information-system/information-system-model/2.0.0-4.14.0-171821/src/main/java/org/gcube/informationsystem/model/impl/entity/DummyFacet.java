/**
 * 
 */
package org.gcube.informationsystem.model.impl.entity;

import java.util.UUID;

import org.gcube.informationsystem.model.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.model.reference.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class DummyFacet extends FacetImpl implements Facet {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -1527529288324120341L;
	
	public DummyFacet(UUID uuid) {
		super();
		this.header = new HeaderImpl(uuid);
	}

	public DummyFacet(){
		super();
	}
	
}
