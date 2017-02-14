package org.gcube.dbinterface.h2.queries;

import java.sql.ResultSet;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.Selection;
import org.gcube.common.dbinterface.queries.Union;


public class UnionImpl implements Union {

	//private GCUBELog logger= new GCUBELog(UnionImpl.class);
	
	private String query= " <%LEFT%> UNION ALL <%RIGHT%>";
	
	private Selection leftQuery;
	private Selection rightQuery;
	
	public void setLeftQuery(Selection query) {
		this.leftQuery= query;
	}

	public void setRightQuery(Selection query) {
		this.rightQuery= query;
	}

	public String getExpression() {
		return query.replace("<%LEFT%>", this.leftQuery.getExpression()).replace("<%RIGHT%>", this.rightQuery.getExpression());
	}

	public String getResultAsJSon(boolean useTableCount, boolean ... resultSetReuse) throws Exception {
		DBSession session= DBSession.connect();
		String tempJson= getResultAsJSon(session, useTableCount, resultSetReuse);
		session.release();
		return tempJson;
	}

	public String getResultAsJSon(DBSession session, boolean useTableCount, boolean ... resultSetReuse) throws Exception {
		return  null;
	}

	public ResultSet getResults(boolean ... resultSetReuse) throws Exception {
		DBSession session = DBSession.connect();
		ResultSet tempRes=this.getResults(session, resultSetReuse);
		session.release();
		return tempRes;
	}

	public ResultSet getResults(DBSession session, boolean ... resultSetReuse) throws Exception {
		return session.execute(this.getExpression(), (resultSetReuse==null || resultSetReuse.length==0)?false:resultSetReuse[0]);
	}
	
	
}
