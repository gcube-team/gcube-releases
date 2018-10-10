package org.gcube.dbinterface.h2.queries;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.Insert;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.types.Cast;
import org.gcube.common.dbinterface.types.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InsertValues extends AbstractUpdate implements Insert{
		
	private String query=" INSERT INTO <%NAME%> (<%FIELDS%>) VALUES (<%ENTRIES%>)";
	
	private static final Logger logger = LoggerFactory.getLogger(InsertValues.class);
	
	private SimpleTable table;
	private PreparedStatement preparedStatement;
	
	private  Object[] insertValues;
	
	public InsertValues() throws Exception{}

	public void setTable(SimpleTable table){
		this.table= table;
	}
	
	@Override
	public SimpleTable execute(DBSession session) throws Exception {
		if (this.insertValues.length!=this.table.getFieldsMapping().values().size()) throw new Exception("the number of values does not correspond to the table entries("+this.insertValues.length+"!="+this.table.getFieldsMapping().values().size()+")");
		String entries="";
		String fields="";
		List<Type> typeList= new ArrayList<Type>();
		List<Object> valuesList= new ArrayList<Object>();
		int k=0;
		for (Entry<String,Type> entry: this.table.getFieldsMapping().entrySet()){
			if (!entry.getValue().isAutoincrement()){
				fields+=entry.getKey()+",";
				entries+="?,";
				typeList.add(entry.getValue());
				valuesList.add(insertValues[k]);
			}
			k++;

		}
		fields= fields.substring(0, fields.length()-1);
		entries= entries.substring(0, entries.length()-1);
		
		//logger.debug("the prepared statement is "+query.replace("<%NAME%>", this.table.getTableName()).replace("<%ENTRIES%>", entries).replace("<%FIELDS%>", fields));
		
		this.preparedStatement= session.getPreparedStatement(query.replace("<%NAME%>", this.table.getTableName()).replace("<%ENTRIES%>", entries).replace("<%FIELDS%>", fields));
		
		//Type[] valuesType=this.table.getFieldsMapping().values().toArray(new Type[0]);
		for (int i=1; i<=typeList.size(); i++){
			//if(valuesType[i-1].isAutoincrement()) continue;
			Object value;
			if (valuesList.get(i-1)!=null && valuesList.get(i-1).getClass().getName().compareTo(typeList.get(i-1).getType().getJavaClass().getName())!=0){
				value=Cast.apply(valuesList.get(i-1).getClass(), typeList.get(i-1), valuesList.get(i-1));
			}else value= valuesList.get(i-1);
			if (value==null)
				this.preparedStatement.setNull(i, Types.NULL);
			else{
				Method m=PreparedStatement.class.getMethod(typeList.get(i-1).getType().getReflectionMethodSet(),int.class, typeList.get(i-1).getType().getJavaClass());
				try{
					m.invoke(this.preparedStatement,i,value);
				}catch(InvocationTargetException e){
					logger.error("error inserting value");
				}
			}
		}
		this.preparedStatement.execute();
		return this.table;
	}

	@Override
	public String getExpression() {
		return this.preparedStatement.toString();
	}
	
	public void setInsertValues(Object... insertValue){
		this.insertValues= insertValue;
	}
	
}
