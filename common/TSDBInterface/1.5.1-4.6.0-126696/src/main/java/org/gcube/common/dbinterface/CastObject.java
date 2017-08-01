package org.gcube.common.dbinterface;

import java.io.Serializable;

import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.types.Type;

public interface CastObject extends Serializable{

	public void setType(Type type);
	
	public Type getType();
	
	public void setField(SimpleAttribute attribute);
	
	public void setStringValue(String value);
	
	public String getCast();
	
	public void setUseCastFunction(boolean useCastFunction);
	
	public boolean isUseCastFunction();
	
	public String escapeSingleQuote(String value);
}
