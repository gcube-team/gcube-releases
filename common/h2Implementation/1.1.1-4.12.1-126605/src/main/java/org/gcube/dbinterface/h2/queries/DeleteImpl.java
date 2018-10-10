package org.gcube.dbinterface.h2.queries;

import org.gcube.common.dbinterface.Condition;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.Delete;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;

public class DeleteImpl extends AbstractUpdate implements Delete{

	String query="DELETE FROM <%TABLE%> <%WHERE%>";
	
	private Table table;
	private Condition filter=null;
	private int deletedItems = 0;
	
	@Override
	public SimpleTable execute(DBSession session) throws Exception {
		deletedItems = session.executeUpdate(this.getExpression());
		if (SimpleTable.class.getName().compareTo(this.table.getClass().getName())==0)
			 return (SimpleTable) this.table;
		else {
			SimpleTable table=new SimpleTable(this.table.getTable());
			table.initializeCount();
			return table;
		}
	}

	@Override
	public String getExpression() {
		return  this.filter!=null?query.replace("<%TABLE%>", this.table.getTable()).replace("<%WHERE%>", " WHERE "+this.filter.getCondition()):
				query.replace("<%TABLE%>", this.table.getTable()).replace("<%WHERE%>", "");
		
	}

	public DeleteImpl(){}
	
	public void setTable(Table table){
		this.table= table;
	}
	
	public Condition getFilter() {
		return filter;
	}

	@Override
	public void setFilter(Condition filter) {
		this.filter = filter;
	}

	@Override
	public int getDeletedItems() {
		return deletedItems;
	}

}
