package org.gcube.common.dbinterface.queries;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.SimpleTable;

public interface Insert {

	public void setTable(SimpleTable table);
	
	public void setInsertValues(Object... insertValue);
	
	public SimpleTable execute(DBSession session) throws Exception;
		
}
