/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import org.gcube.informationsystem.model.entity.Context;

import com.fasterxml.jackson.annotation.JsonTypeName;


/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Context.NAME)
public class ContextImpl extends EntityImpl implements Context {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -5070590328223454087L;
	
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
