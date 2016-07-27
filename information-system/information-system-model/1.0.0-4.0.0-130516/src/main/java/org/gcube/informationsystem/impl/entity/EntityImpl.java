/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public abstract class EntityImpl implements Entity {
	
	protected Header header;
	
	protected EntityImpl(){
		this.header = new HeaderImpl();
	}
	
	@Override
	public Header getHeader() {
		return this.header;
	}

}
