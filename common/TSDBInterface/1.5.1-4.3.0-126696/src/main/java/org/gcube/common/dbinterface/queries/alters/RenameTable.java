package org.gcube.common.dbinterface.queries.alters;

import org.gcube.common.dbinterface.tables.Table;

public interface RenameTable  extends AlterTable{

	public void setNewName(String newName);
	
	public void setTable(Table table);
	
}
