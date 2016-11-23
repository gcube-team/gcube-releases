package org.gcube.common.dbinterface.queries;

import org.gcube.common.dbinterface.Condition;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;

public interface Delete {

	public void setTable(Table table);
	
	public <T extends Condition> void setFilter(T filter);
	
	public SimpleTable execute(DBSession session) throws Exception;
	
	public String getExpression();
	
	public int getDeletedItems();
}
