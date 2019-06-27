package org.gcube.common.dbinterface.attributes;

import org.gcube.common.dbinterface.Condition;

public class BooleanAttribute implements Attribute {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4369768809149102622L;

	private String attributeName;
	private Condition condition;

		
	public BooleanAttribute(String attributeName,
			Condition condition) {
		super();
		this.attributeName = attributeName;
		this.condition = condition;
	}

	@Override
	public String getAttributeName() {
		return attributeName;
	}
	
	@Override
	public String getAttribute(){
		return this.condition.getCondition()+" as "+attributeName;
	}
	
	public String toString(){
		return this.getAttribute();
	}
	
}
