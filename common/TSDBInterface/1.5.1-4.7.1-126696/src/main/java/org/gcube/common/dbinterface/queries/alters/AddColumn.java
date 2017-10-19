package org.gcube.common.dbinterface.queries.alters;

import org.gcube.common.dbinterface.ColumnDefinition;
import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.tables.Table;

public interface AddColumn extends AlterTable{

	
	public void setTable(Table table);
	
	public void setDefinition(ColumnDefinition definition);
	
	public void setAfterPosition(SimpleAttribute afterPosition);
}
