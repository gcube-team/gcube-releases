package org.gcube.common.dbinterface;

import org.gcube.common.dbinterface.types.Type;

public interface ColumnDefinition extends Comparable<ColumnDefinition>{

	public String getDefinition();
	
	public void setLabel(String label);
	
	public void setType(Type type);
	
	public void setSpecification(Specification ... specifications);
	
	public String getLabel();
	
	public Type getType();
	
}
