package org.gcube.dbinterface.h2.queries;

import org.gcube.common.dbinterface.Condition;
import org.gcube.common.dbinterface.conditions.OperatorCondition;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.Update;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;



public class UpdateImpl extends AbstractUpdate implements Update{

	String query="UPDATE <%TABLE%> SET <%OPERATOR%> <%FROMTABLE%> <%WHERE%>";
	
	@SuppressWarnings("rawtypes")
	private OperatorCondition[] operators;
	private Condition filter;
	private Table table;
	private Table[] fromTables;
	private int affectedLines= -1;
	
	public UpdateImpl(){}

	@Override
	public SimpleTable execute(DBSession session) throws Exception {
		//System.out.println(this.getExpression());
		affectedLines = session.executeUpdate(this.getExpression());
		if (SimpleTable.class.getName().compareTo(this.table.getClass().getName())==0)
			return (SimpleTable) this.table;
		else {
			SimpleTable table=new SimpleTable(this.table.getTable());
			table.initializeCount();
			return table;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String getExpression() {
		String operator="";
		if (this.operators!=null && this.operators.length>0){
			StringBuilder attrBuilder= new StringBuilder();
			for (OperatorCondition ops: this.operators){
				attrBuilder.append(ops.getCondition());
				attrBuilder.append(" ,");
			}
			operator=  attrBuilder.substring(0, attrBuilder.length()-2);
		}
		
		String fromTable="";
		if (this.fromTables!=null && this.fromTables.length>0){
			StringBuilder fromtableBuilder= new StringBuilder(" FROM ");
			for (Table table: this.fromTables){
				fromtableBuilder.append(table.getTable());
				fromtableBuilder.append(" ,");
			}
			fromTable=  fromtableBuilder.substring(0, fromtableBuilder.length()-2);
		}
		
		String filter=this.filter!=null?" WHERE "+this.filter.getCondition():"";
				
		return  query.replace("<%TABLE%>", this.table.getTable()).replace("<%OPERATOR%>",operator).replace("<%WHERE%>", filter).replace("<%FROMTABLE%>", fromTable);
						
	}

	public void setFilter(Condition filter) {
		this.filter=filter;
	}

	@SuppressWarnings("rawtypes")
	public void setOperators(OperatorCondition... operators) {
		this.operators= operators;
	}

	public void setTable(Table table) {
		this.table= table;

	}

	/**
	 * @param fromTable the fromTable to set
	 */
	public void setFromTables(Table ... fromTables) {
		this.fromTables = fromTables;
	}

	@Override
	public int getAffectedLines() {
		return affectedLines;
	}
		
	
}
