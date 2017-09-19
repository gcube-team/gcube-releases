package org.gcube.common.dbinterface.queries.alters;

import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.tables.Table;
import org.gcube.common.dbinterface.types.Type;

public interface ModifyColumnType extends AlterTable{

	
	public void setTable(Table table);
	
	public void setColumn(SimpleAttribute column);
	
	public void setNewType(Type newType);
	
	public void setUseCast(boolean useCast);
}
