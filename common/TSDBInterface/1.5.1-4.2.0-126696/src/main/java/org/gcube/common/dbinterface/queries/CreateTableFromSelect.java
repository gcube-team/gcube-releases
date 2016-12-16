package org.gcube.common.dbinterface.queries;

import org.gcube.common.dbinterface.TableAlreadyExistsException;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.SimpleTable;

public interface CreateTableFromSelect {

	public SimpleTable execute(DBSession session) throws TableAlreadyExistsException, Exception;
	
	public String getExpression();
	
	public void setTableName(String tableName);
	
	public void setSelect(Select query);
	
	public void setWithData();
	
	public void setWithoutData();
	
	public boolean isWithData();
}
