package org.gcube.dbinterface.h2.queries.alters;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.alters.CreateIndexOnField;
import org.gcube.common.dbinterface.tables.Table;

public class CreateIndexOnFieldImpl implements CreateIndexOnField {

	private String query= "CREATE INDEX <%NAME%> ON <%TABLE%> (<%COLNAME%>)";
	
	private Table table;
	private String field;
	
	public void setTable(Table table) {
		this.table = table;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setLowerCase(boolean lowerCase) {
		//this.lowerCase = lowerCase;
	}

	public void execute(DBSession session) throws Exception {
		session.executeUpdate(this.getExpression());
	}

	public String getExpression() {
		String columnName = field;
		String indexName = table.getTableName()+field+"idx"; 
		return query.replace("<%TABLE%>", this.table.getTable()).replace("<%COLNAME%>", columnName).replace("<%NAME%>", indexName);
	}

}
