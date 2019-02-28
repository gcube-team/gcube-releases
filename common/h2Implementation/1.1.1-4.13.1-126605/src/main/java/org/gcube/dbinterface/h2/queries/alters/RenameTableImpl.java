package org.gcube.dbinterface.h2.queries.alters;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.alters.RenameTable;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;
import org.gcube.dbinterface.h2.queries.AbstractUpdate;

public class RenameTableImpl extends AbstractUpdate implements RenameTable{

	private String query= "ALTER TABLE <%TABLENAME%> RENAME TO <%NEWNAME%>";

	private String newName;
	private Table table;
	
	@Override
	public SimpleTable execute(DBSession session) throws Exception {
		session.executeUpdate(this.getExpression());
		SimpleTable table= new SimpleTable(this.newName);
		table.initializeCount();
		table.initializeFieldMapping();
		return table;
	}

	@Override
	public String getExpression() {
		return query.replace("<%TABLENAME%>", table.getTableName()).replace("<%NEWNAME%>", this.newName);
	}

	public void setNewName(String newName) {
		this.newName= newName;		
	}

	public void setTable(Table table) {
		this.table= table;
	}
	
	
	
}
