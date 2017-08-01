package org.gcube.dbinterface.h2.queries;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.DropTable;
import org.gcube.common.dbinterface.tables.SimpleTable;

public class DropTableImpl extends AbstractUpdate implements DropTable{

	String query="DROP TABLE <%TABLE%>";
	
	private String tableName;
	
	public DropTableImpl(){}
	
	@Override
	public SimpleTable execute(DBSession session) throws Exception {
		session.executeUpdate(this.getExpression());
		return null;
	}

	@Override
	public String getExpression() {
		return query.replace("<%TABLE%>", this.tableName);
	}

	public void setTableName(String tableName){
		this.tableName=tableName;
	}
	
}
