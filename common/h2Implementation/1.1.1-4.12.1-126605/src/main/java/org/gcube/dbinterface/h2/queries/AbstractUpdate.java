package org.gcube.dbinterface.h2.queries;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.SimpleTable;


public abstract class AbstractUpdate {

	protected String query="";
	
	public abstract SimpleTable execute(DBSession session) throws Exception;
	
	public abstract String getExpression();
	
	public SimpleTable execute() throws Exception{
		DBSession session= DBSession.connect();
		SimpleTable table = null;
		try{
			table = this.execute(session);
		}finally{
			session.release();
		}
		return table;
	}	
	
}
