package org.gcube.common.dbinterface.queries.alters;

import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.tables.Table;

public interface DropColumn extends AlterTable{

	public void setTable(Table table);
	
	public void setColumn(SimpleAttribute column);

}
