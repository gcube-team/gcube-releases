package org.gcube.dbinterface.h2.queries;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.Types;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.types.Cast;


public class SingleSet extends AbstractUpdate{

	private String query="UPDATE <%TABLE%> SET <%FIELD%>=? WHERE <%FILDCOND%>=?";
	
	
	private String fieldToSet;
	private String fieldToCompare;
	private SimpleTable table;
	private PreparedStatement statement;
	private Object valueField=null;
	private Object valueCond=null;
	
	public SingleSet(SimpleTable table, String fieldToSet, String fieldToCompare) throws Exception{
		this.fieldToSet=fieldToSet;
		this.table= table;
		this.fieldToCompare=fieldToCompare;
		statement=DBSession.connect().getPreparedStatement(query.replace("<%TABLE%>", this.table.getTableName()).replace("<%FIELD%>", this.fieldToSet).replace("<%FILDCOND%>", this.fieldToCompare));
	}

	@Override
	public SimpleTable execute(DBSession session) throws Exception {
		if (this.valueField==null && this.valueCond==null) throw new Exception("no value set");
		
		Object value=null;
		if ((value=Cast.apply(this.valueField.getClass(), this.table.getFieldsMapping().get(fieldToSet) , this.valueField))==null){
			this.statement.setNull(1, Types.NULL);
		}else{
			Method m=PreparedStatement.class.getMethod(this.table.getFieldsMapping().get(fieldToSet).getType().getReflectionMethodSet(),int.class, this.table.getFieldsMapping().get(fieldToSet).getType().getJavaClass() );
			try{
				m.invoke(this.statement,1,value);
			}catch(InvocationTargetException e){
				e.getTargetException().printStackTrace();
				throw new Exception("error updating table "+table.getTableName());
			}
		}
		Method m1=PreparedStatement.class.getMethod(this.table.getFieldsMapping().get(fieldToCompare).getType().getReflectionMethodSet(),int.class, this.table.getFieldsMapping().get(fieldToCompare).getType().getJavaClass() );
		try{
			m1.invoke(this.statement,2,Cast.apply(this.valueCond.getClass(), this.table.getFieldsMapping().get(this.fieldToCompare) , this.valueCond));
		}catch(InvocationTargetException e){
			e.getTargetException().printStackTrace();
			throw new Exception("error updating table "+table.getTableName());
		}

		this.statement.execute();
		return this.table;
	}

	@Override
	public String getExpression() {
		return this.query;
	}
	
	public void setValueField(Object value){
		this.valueField=value;
	}
	
	public void setValueCondition(Object value){
		this.valueCond=value;
	}
	
}
