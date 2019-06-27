package org.gcube.common.dbinterface.queries;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.SimpleTable;

public interface InsertFromSelect {

	public void setTable(SimpleTable table);
	
	public SimpleTable execute(DBSession session) throws Exception;
	
	public void setSubQuery(Select query);
	
	public String getExpression();
	
}
