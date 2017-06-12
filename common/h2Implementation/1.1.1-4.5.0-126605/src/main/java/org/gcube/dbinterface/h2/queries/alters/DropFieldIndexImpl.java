package org.gcube.dbinterface.h2.queries.alters;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.alters.DropFieldIndex;
import org.gcube.common.dbinterface.tables.Table;

public class DropFieldIndexImpl implements DropFieldIndex{

	private String query= "DROP INDEX <%NAME%>";
	
	private Table table;
	private String field;
	
	public void setTable(Table table) {
		this.table = table;
	}

	public void setField(String field) {
		this.field = field;
	}
	
	public void execute(DBSession session) throws Exception {
		session.executeUpdate(this.getExpression());
	}

	public String getExpression() {
		String indexName = table.getTableName()+field+"idx"; 
		return query.replace("<%NAME%>", indexName);
	}
}
