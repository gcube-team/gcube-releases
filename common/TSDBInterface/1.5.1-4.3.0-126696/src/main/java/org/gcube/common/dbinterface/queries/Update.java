package org.gcube.common.dbinterface.queries;

import org.gcube.common.dbinterface.Condition;
import org.gcube.common.dbinterface.conditions.OperatorCondition;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;

public interface Update {

	public SimpleTable execute(DBSession session) throws Exception;
	
	public String getExpression();
	
	public void setTable(Table table);
	
	public void setFromTables(Table ... tables);
	
	public void setFilter(Condition filter);
	
	public int getAffectedLines();
	
	@SuppressWarnings("rawtypes")
	public void setOperators(OperatorCondition ...operators);
	
}
