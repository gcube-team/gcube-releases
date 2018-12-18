package org.gcube.common.dbinterface.queries;

import java.sql.ResultSet;
import org.gcube.common.dbinterface.pool.DBSession;

public interface Selection {

	public String getResultAsJSon(boolean useTableCount, boolean ...resultSetReuse) throws Exception;
	
	public String getResultAsJSon(DBSession session, boolean useTableCount, boolean... resultSetReuse) throws Exception;
	
	public ResultSet getResults(boolean ...resultSetReuse ) throws Exception;
	
	public ResultSet getResults(DBSession session, boolean... resultSetReuse) throws Exception;
		
	public String getExpression();
	
}
