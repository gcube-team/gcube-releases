/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import java.util.UUID;

import org.gcube.informationsystem.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.model.entity.Facet;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class DummyFacet extends FacetImpl implements Facet {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -1527529288324120341L;
	
	public static final String NAME = Facet.NAME;
	
	public DummyFacet(UUID uuid){
		super();
		this.header = new HeaderImpl(uuid);
	}
	
	
}
