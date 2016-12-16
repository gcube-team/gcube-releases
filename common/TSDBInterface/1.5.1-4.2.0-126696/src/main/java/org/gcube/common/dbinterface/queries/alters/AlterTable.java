package org.gcube.common.dbinterface.queries.alters;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.SimpleTable;

public interface AlterTable {

	public SimpleTable execute(DBSession session) throws Exception;
	
	public String getExpression();
}
