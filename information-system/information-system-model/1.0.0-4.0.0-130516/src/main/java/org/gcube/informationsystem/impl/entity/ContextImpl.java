/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import org.gcube.informationsystem.model.entity.Context;


/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class ContextImpl extends EntityImpl implements Context {

	protected String name;
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
}
