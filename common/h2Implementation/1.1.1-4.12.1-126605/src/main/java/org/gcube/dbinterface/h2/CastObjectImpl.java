package org.gcube.dbinterface.h2;

import org.gcube.common.dbinterface.CastObject;
import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.types.Type;
import org.gcube.common.dbinterface.utils.Utility;

public class CastObjectImpl implements CastObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5566140346409955363L;
	
	private SimpleAttribute attribute=null;
	private Type newType; 
	private String value=null;
	private boolean useCastFunction = false;
	
	public String getCast() {
		if (!useCastFunction) return (this.attribute!=null?this.attribute.getAttribute():"'"+value+"'")+"::"+this.newType.getType().getListSqlTypes().get(0);
		else return newType.getType().getSpecificFunction()+"("+(this.attribute!=null?this.attribute.getAttribute():"'"+value+"'")+")";
	}

	public void setField(SimpleAttribute attribute) {
		this.attribute= attribute;
	}

	public void setType(Type type) {
		this.newType= type;
	}

	public void setStringValue(String value) {
		this.value= this.escapeSingleQuote(value);
	}
	
	public String toString(){
		return this.getCast();
	}

	/**
	 * @return the useCastFunction
	 */
	public boolean isUseCastFunction() {
		return useCastFunction;
	}

	/**
	 * @param useCastFunction the useCastFunction to set
	 */
	public void setUseCastFunction(boolean useCastFunction) {
		this.useCastFunction = useCastFunction;
	}

	/**
	 * @return the newType
	 */
	public Type getType() {
		return newType;
	}


	@Override
	public String escapeSingleQuote(String value) {
		return value!=null? value.replaceAll("'", "''"): null;
	}

	
	
}
