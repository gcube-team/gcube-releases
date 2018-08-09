package org.gcube.dbinterface.h2.queries.alters;

import org.gcube.common.dbinterface.ColumnDefinition;
import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.alters.AddColumn;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;
import org.gcube.dbinterface.h2.queries.AbstractUpdate;


public class AddColumnImpl extends AbstractUpdate implements AddColumn{

	private String query= "ALTER TABLE <%TABLE%> ADD COLUMN <%COLUMNDEFINITION%> <%POSITION%>";
	
	private Table table;
	private ColumnDefinition definition;
	private SimpleAttribute afterPosition=null;
	
	public AddColumnImpl(){}
	
		
	@Override
	public SimpleTable execute(DBSession session) throws Exception {
		session.executeUpdate(this.getExpression());
		if (SimpleTable.class.getName().compareTo(this.table.getClass().getName())==0){
			 ((SimpleTable) this.table).initializeFieldMapping();
			return (SimpleTable) this.table;
		}else {
			SimpleTable table=new SimpleTable(this.table.getTable());
			table.initializeCount();
			return table;
		}
	}

	@Override
	public String getExpression() {
		return this.afterPosition==null? query.replace("<%TABLE%>",this.table.getTable()).replace("<%COLUMNDEFINITION%>",this.definition.getDefinition()).replace("<%POSITION%>", ""):
			query.replace("<%TABLE%>",this.table.getTable()).replace("<%COLUMNDEFINITION%>",this.definition.getDefinition()).replace("<%POSITION%>", "AFTER "+this.afterPosition.getAttribute());
	}


	public SimpleAttribute getAfterPosition() {
		return afterPosition;
	}


	public void setAfterPosition(SimpleAttribute afterPosition) {
		this.afterPosition = afterPosition;
	}


	public void setTable(Table table) {
		this.table = table;
	}


	public void setDefinition(ColumnDefinition definition) {
		this.definition = definition;
	}

}
