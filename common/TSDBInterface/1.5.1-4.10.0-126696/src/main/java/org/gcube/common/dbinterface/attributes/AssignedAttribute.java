package org.gcube.common.dbinterface.attributes;

public class AssignedAttribute<T> implements Attribute {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3044992826445216904L;
	
	private SimpleAttribute attributeAlias;
	private T attribute;
	
	public AssignedAttribute(SimpleAttribute attributeAlias, T attribute) {
		this.attributeAlias = attributeAlias;
		this.attribute = attribute;
	}

	
	public String getAttribute() {
		if (attribute==null) 
			return "null AS "+attributeAlias;
		if (attribute.getClass().isAssignableFrom(String.class)) 
			return "'"+attribute+"' AS "+attributeAlias;
		else 
			return attribute+" AS "+attributeAlias;
	}

	public String toString(){
		return this.getAttribute();
	}


	/**
	 * @return the attributeAlias
	 */
	public SimpleAttribute getAttributeAlias() {
		return attributeAlias;
	}


	@Override
	public String getAttributeName() {
		return attributeAlias.getAttributeName();
	}

}
