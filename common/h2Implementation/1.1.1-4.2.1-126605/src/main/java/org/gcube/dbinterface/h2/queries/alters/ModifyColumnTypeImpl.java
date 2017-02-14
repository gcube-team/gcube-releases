package org.gcube.dbinterface.h2.queries.alters;

import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.alters.ModifyColumnType;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;
import org.gcube.common.dbinterface.types.Type;
import org.gcube.dbinterface.h2.queries.AbstractUpdate;


public class ModifyColumnTypeImpl extends AbstractUpdate implements ModifyColumnType{

	private Table table;
	private SimpleAttribute column;
	private Type newType; 
	private boolean useCast= true;
	public ModifyColumnTypeImpl(){}
	
	private String query= "ALTER TABLE <%TABLE%> ALTER COLUMN <%COLNAME%> TYPE <%TYPE%> <%USING%>";
	
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
		String toReturn= query.replace("<%TABLE%>", table.getTable()).replace("<%COLNAME%>", column.getAttribute()).replace("<%TYPE%>", this.newType.getTypeDefinition());
		if (this.newType.getType().getSpecificFunction()!=null && useCast) return toReturn.replace("<%USING%>", "USING "+this.newType.getType().getSpecificFunction()+"("+column.getAttribute()+")");
		else return toReturn.replace("<%USING%>","");
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public void setColumn(SimpleAttribute column) {
		this.column = column;
	}

	public void setNewType(Type newType) {
		this.newType = newType;
	}

	/**
	 * @param useCast the useCast to set
	 */
	public void setUseCast(boolean useCast) {
		this.useCast = useCast;
	}

	
}
