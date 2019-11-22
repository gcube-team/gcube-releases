package org.gcube.common.dbinterface.queries;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.SimpleTable;

public interface DropTable {

	public SimpleTable execute(DBSession session) throws Exception;
	
	public String getExpression();
	
	public void setTableName(String tableName);
}
