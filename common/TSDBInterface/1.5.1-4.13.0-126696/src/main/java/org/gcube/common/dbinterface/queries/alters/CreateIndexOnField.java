package org.gcube.common.dbinterface.queries.alters;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.Table;

public interface CreateIndexOnField {

	public void setTable(Table table);
	
	public void setField(String field);

	public void setLowerCase(boolean lowerCase);
	
	public void execute(DBSession session) throws Exception;
	
	public String getExpression();
}
