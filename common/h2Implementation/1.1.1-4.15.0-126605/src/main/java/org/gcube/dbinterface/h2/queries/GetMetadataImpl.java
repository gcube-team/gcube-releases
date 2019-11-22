package org.gcube.dbinterface.h2.queries;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.GetMetadata;
import org.gcube.common.dbinterface.types.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GetMetadataImpl implements GetMetadata{

	private static final Logger logger = LoggerFactory.getLogger(GetMetadataImpl.class);
	
	private String tableName;
	
	public GetMetadataImpl(){}
	
	public void setTable(String tableName){
		this.tableName= tableName.toUpperCase();
	}
		
	public LinkedHashMap<String, Type> getResults() throws Exception {
		DBSession session=DBSession.connect();
		LinkedHashMap<String, Type> tempMapping;
		try{
			tempMapping= getResults(session);
		}finally{
			session.release();
		}
		return tempMapping;
	}

	public LinkedHashMap<String, Type> getResults(DBSession session)
			throws Exception {
		ResultSet result = session.getDBMetadata().getColumns(null, null, this.tableName, null);
				
		List<String> primaryKeyList= new ArrayList<String>(2); 
		try{
			ResultSet primaryKeyRS= session.getDBMetadata().getPrimaryKeys(null, null, this.tableName);
			while (primaryKeyRS.next())
				primaryKeyList.add(primaryKeyRS.getString("column_name"));
		}catch (Exception e) {
			logger.trace("no prymary keys found for "+tableName);
		}
			
		LinkedHashMap<String, Type> tempMapping= new LinkedHashMap<String, Type>(); 
		
		while (result.next()){
			logger.trace("fieldId: "+result.getString("COLUMN_NAME")+"  and the type is "+result.getInt("COLUMN_SIZE")+" "+result.getString("TYPE_NAME")+"  "+result.getInt("NUM_PREC_RADIX")+"   "+result.getInt("DECIMAL_DIGITS"));
			String typeName;
			if (result.getString("TYPE_NAME").equals("numeric") && result.getInt("DECIMAL_DIGITS")>0)
				typeName = "decimal";
			else typeName = result.getString("TYPE_NAME");
			Type tmpType=Type.parseType(typeName,result.getInt("COLUMN_SIZE"),result.getInt("COLUMN_SIZE"),result.getInt("DECIMAL_DIGITS"));
			try{
				if (result.getString("COLUMN_DEF")!=null && result.getString("COLUMN_DEF").contains("nextval")) tmpType.setAutoincrement(true);
			}catch (Exception e) {tmpType.setAutoincrement(false);}
			if (primaryKeyList.contains(result.getString("COLUMN_NAME"))) tmpType.setPrimaryKey(true);
			tempMapping.put(result.getString("COLUMN_NAME"),tmpType);
		}
		return tempMapping;
	}

	
}
