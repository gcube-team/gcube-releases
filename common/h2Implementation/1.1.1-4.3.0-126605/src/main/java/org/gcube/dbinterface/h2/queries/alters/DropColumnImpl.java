package org.gcube.dbinterface.h2.queries.alters;

import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.alters.DropColumn;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;
import org.gcube.dbinterface.h2.queries.AbstractUpdate;


public class DropColumnImpl extends AbstractUpdate  implements DropColumn{

	private String query= "ALTER TABLE <%TABLE%> DROP COLUMN <%COLNAME%>";
	
	private Table table;
	private SimpleAttribute column;
		
	@Override
	public SimpleTable execute(DBSession session) throws Exception {
		session.executeUpdate(this.getExpression());
		if (SimpleTable.class.getName().compareTo(this.table.getClass().getName())==0){
			SimpleTable tmpTable=(SimpleTable) this.table;
			tmpTable.initializeFieldMapping();
			return tmpTable;
		}
		else {
			SimpleTable table=new SimpleTable(this.table.getTable());
			table.initializeCount();
			return table;
		}
	}

	@Override
	public String getExpression() {
		return query.replace("<%TABLE%>", this.table.getTable()).replace("<%COLNAME%>", this.column.getAttribute());
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public void setColumn(SimpleAttribute column) {
		this.column = column;
	}

}
