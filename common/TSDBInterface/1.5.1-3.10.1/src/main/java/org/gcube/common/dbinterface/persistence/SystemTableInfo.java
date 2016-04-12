package org.gcube.common.dbinterface.persistence;

import java.sql.ResultSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import org.gcube.common.dbinterface.ColumnDefinition;
import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.TableAlreadyExistsException;
import org.gcube.common.dbinterface.attributes.SimpleAttribute;
import org.gcube.common.dbinterface.conditions.ANDCondition;
import org.gcube.common.dbinterface.conditions.OperatorCondition;
import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.CreateTable;
import org.gcube.common.dbinterface.queries.Delete;
import org.gcube.common.dbinterface.queries.Insert;
import org.gcube.common.dbinterface.queries.Select;
import org.gcube.common.dbinterface.tables.SimpleTable;
import org.gcube.common.dbinterface.tables.Table;
import org.gcube.common.dbinterface.types.Type;
import org.gcube.common.dbinterface.types.Type.Types;
import org.gcube.common.dbinterface.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemTableInfo {

	private static final Logger logger = LoggerFactory.getLogger(SystemTableInfo.class);
	
	private static final String SYSTEM_INFO_TABLE="internalgcubesysteminfotable";
	private static final String TABLE_NAME_FIELD="tablename";
	private static final String FIELD_NAME_FIELD="fieldname";
	private static final String COLUMN_NAME_FIELD="coulumnname";
	
	private static SystemTableInfo systemTableInfo= null;
	
	private SystemTableInfo() throws Exception{
		DBSession session= null;
		try{
			session = DBSession.connect();
			this.create(session);
		}catch (TableAlreadyExistsException e) {
			logger.trace("the system table already exists");
		}finally{
			if (session!=null) session.release();
		}
	}
	
	protected static SystemTableInfo getSystemInfo() throws Exception{
		if (systemTableInfo == null) systemTableInfo =  new SystemTableInfo();
		return systemTableInfo;
	}
	
	private void create(DBSession session) throws TableAlreadyExistsException,Exception{
		CreateTable creator= DBSession.getImplementation(CreateTable.class);
		creator.setTableName(SYSTEM_INFO_TABLE);
		ColumnDefinition tableName= DBSession.getImplementation(ColumnDefinition.class);
		tableName.setLabel(TABLE_NAME_FIELD);
		tableName.setSpecification(Specification.NOT_NULL);
		tableName.setType(new Type(Types.STRING, 100));
		ColumnDefinition fieldName= DBSession.getImplementation(ColumnDefinition.class);
		fieldName.setLabel(FIELD_NAME_FIELD);
		fieldName.setSpecification(Specification.NOT_NULL);
		fieldName.setType(new Type(Types.STRING, 100));
		ColumnDefinition columnName= DBSession.getImplementation(ColumnDefinition.class);
		columnName.setLabel(COLUMN_NAME_FIELD);
		columnName.setSpecification(Specification.NOT_NULL);
		columnName.setType(new Type(Types.STRING, 100));
		creator.setColumnsDefinition(tableName, fieldName, columnName);
		creator.execute(session);
		Utility.createIndexOnField(new Table(SYSTEM_INFO_TABLE), TABLE_NAME_FIELD, true).execute(session);		
	}
	
	protected TreeMap<String, String> retrieveInfo(String tableName) throws Exception{
		//the map to return has the class field name as key and the field name in the table as value
		Select select= DBSession.getImplementation(Select.class);
		select.setTables(new SimpleTable(SYSTEM_INFO_TABLE));
		select.setFilter(new OperatorCondition<SimpleAttribute, String>(new SimpleAttribute(TABLE_NAME_FIELD), tableName, " LIKE "));
		select.setAttributes(new SimpleAttribute(FIELD_NAME_FIELD), new SimpleAttribute(COLUMN_NAME_FIELD));
		TreeMap<String, String> toReturn= new TreeMap<String, String>();
		DBSession session= DBSession.connect();
		try{
			//logger.trace("retrieve info query is "+select.getExpression());
			ResultSet res= select.getResults(session);
			while (res.next())
				toReturn.put(res.getString(FIELD_NAME_FIELD), res.getString(COLUMN_NAME_FIELD));
		}finally{
			session.release();
		}
		return toReturn;
	}
	
	protected String retrieveFieldName(String tableName, String columnName) throws Exception{
		//the map to return has the class field name as key and the field name in the table as value
		Select select= DBSession.getImplementation(Select.class);
		select.setTables(new SimpleTable(SYSTEM_INFO_TABLE));
		select.setFilter(new ANDCondition(new OperatorCondition<SimpleAttribute, String>(new SimpleAttribute(TABLE_NAME_FIELD), tableName, " LIKE "),
				new OperatorCondition<SimpleAttribute, String>(new SimpleAttribute(COLUMN_NAME_FIELD), columnName," LIKE ")));
		select.setAttributes(new SimpleAttribute(FIELD_NAME_FIELD));
		DBSession session= DBSession.connect();
		try{
			logger.trace("retrieve info query is "+select.getExpression());
			ResultSet res= select.getResults(session);
			if (!res.next()) throw new Exception("error retrieving the field corresponding to column name "+columnName);
			return res.getString(1);
		}finally{
			session.release();
		}
		
	}
	
	protected void addInfo(Map<String, String> fieldMapping, String tableName) throws Exception{
		Insert insert = DBSession.getImplementation(Insert.class);
		insert.setTable(new SimpleTable(SYSTEM_INFO_TABLE));
		DBSession session= null;
		try{
			session =  DBSession.connect();
			for (Entry<String, String> entry: fieldMapping.entrySet()){
				insert.setInsertValues(tableName, entry.getKey(), entry.getValue());
				insert.execute(session);
			}
		}finally{
			if(session!=null) session.release();
		}
		
	}
	
	protected void deleteInfo(String tableName) throws Exception{
		Delete deleteInfo= DBSession.getImplementation(Delete.class);
		deleteInfo.setTable(new SimpleTable(SYSTEM_INFO_TABLE));
		deleteInfo.setFilter(new OperatorCondition<SimpleAttribute, String>(new SimpleAttribute(TABLE_NAME_FIELD), tableName, " LIKE "));
		DBSession session= null;
		try{
			deleteInfo.execute(session);
		}finally{
			if (session!=null) session.release();
		}
	}
	
}
