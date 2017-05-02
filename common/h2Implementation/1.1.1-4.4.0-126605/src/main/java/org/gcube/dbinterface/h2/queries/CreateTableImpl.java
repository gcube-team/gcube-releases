package org.gcube.dbinterface.h2.queries;

import java.util.LinkedHashMap;

import org.gcube.common.dbinterface.ColumnDefinition;
import org.gcube.common.dbinterface.TableAlreadyExistsException;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.CreateTable;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.types.*;

public class CreateTableImpl extends AbstractUpdate implements CreateTable{
	
	//private static GCUBELog logger= new GCUBELog(CreateTable.class);
	String query= "CREATE TABLE <%NAME%> (<%COLUMNSDEFINITON%>)"; 
	
	private ColumnDefinition[] columnsDefinition;
	private String tableName;
	
	@Override
	public SimpleTable execute(DBSession session) throws TableAlreadyExistsException, Exception {
		GetMetadataImpl metadata= new GetMetadataImpl();
		metadata.setTable(this.tableName);
		if (metadata.getResults(session).size()>0) throw new TableAlreadyExistsException("the table "+this.tableName+" already exists");
		session.executeUpdate(this.getExpression());
		SimpleTable table=new SimpleTable(this.tableName);
		LinkedHashMap<String, Type> mapping= new LinkedHashMap<String, Type>();
		for (ColumnDefinition cd:this.columnsDefinition )
			mapping.put(cd.getLabel(), cd.getType());
		
		table.setFieldsMapping(mapping);
		return table;
	}
	
	@Override
	public String getExpression(){
		String tempDef="";
		for (ColumnDefinition columnDefinition: this.columnsDefinition)
			tempDef+=columnDefinition.getDefinition()+" ,";
		return query.replace("<%NAME%>",this.tableName ).replace("<%COLUMNSDEFINITON%>", tempDef.subSequence(0, tempDef.length()-2));
	}
	
	public CreateTableImpl(){}
	
	public void setTableName(String tableName){
		this.tableName= tableName;
	}
	
	public void setColumnsDefinition(ColumnDefinition ... columnsDefinition){
		this.columnsDefinition= columnsDefinition;
	}
		
}
