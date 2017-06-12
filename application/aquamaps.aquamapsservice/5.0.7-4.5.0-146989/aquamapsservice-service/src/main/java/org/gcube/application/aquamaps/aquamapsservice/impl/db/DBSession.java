package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.ConfigurationManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.DBDescriptor;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HSPECFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





/**
 * 
 * @author lucio
 *
 */
public abstract class DBSession {

	protected final static Logger logger= LoggerFactory.getLogger(DBSession.class);
//	protected static String DEFAULT_BOOLEAN_VALUE=null;
//	protected static String DEFAULT_INTEGER_VALUE=null;
//	protected static String DEFAULT_LONG_VALUE=null;
//	protected static String DEFAULT_DOUBLE_VALUE=null;
//	static{
//		try{
//			DEFAULT_BOOLEAN_VALUE=ServiceContext.getContext().getProperty(PropertiesConstants.BOOLEAN_DEFAULT_VALUE);
//			DEFAULT_DOUBLE_VALUE=ServiceContext.getContext().getProperty(PropertiesConstants.DOUBLE_DEFAULT_VALUE);
//			DEFAULT_INTEGER_VALUE=ServiceContext.getContext().getProperty(PropertiesConstants.INTEGER_DEFAULT_VALUE);
//			DEFAULT_LONG_VALUE=ServiceContext.getContext().getProperty(PropertiesConstants.INTEGER_DEFAULT_VALUE);
//		}catch(Exception e){
//			logger.fatal("Unable to evaluate DB default values",e);
//		}
//	}
	
	protected static String CSV_DELIMITER=",";
	
	
	protected Connection connection;

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	public static enum ENGINE{MyISAM, InnoDB};

	public static enum ALTER_OPERATION{MODIFY, ADD};

	/**
	 * 
	 * @return
	 * @throws Exception
	 */

	

	public static DBSession getInternalDBSession()throws Exception{
		DBDescriptor internalDescriptor=ConfigurationManager.getVODescriptor().getInternalDB();
		try{			
			Connection conn=PoolManager.getInternalDBConnection();
			switch(internalDescriptor.getType()){
			case mysql: return new MySQLDBSession(conn);
			default: return new PostGresSQLDBSession(conn);
			}
		}catch(Exception e){
			logger.error("ERROR ON OPENING CONNECTION ",e);
			logger.error("Connection parameters were : "+internalDescriptor);
			throw e;
		}
	}

	public static DBSession getPostGisDBSession()throws Exception{
		return new PostGresSQLDBSession(PoolManager.getPostGisDBConnection());
	}


	protected DBSession(Connection conn){
		this.connection= conn;
	}

	public void close() throws Exception{
		this.connection.close();
	}

	public void disableAutoCommit() throws Exception{
		this.connection.setAutoCommit(false);
	}

	public void commit() throws Exception{
		this.connection.commit();
	}


	@Deprecated
	protected DBSession(){}


	/**
	 * 
	 * @param query
	 * @throws Exception
	 */
	@Deprecated
	public ResultSet executeQuery(String query) throws Exception{
		Statement statement=this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		return statement.executeQuery(query);
	}

	//************ DATA DEFINITION

	public abstract void createTable(String tableName, String[] columnsAndConstraintDefinition) throws Exception;

	public void disableKeys(String tableName) throws Exception{
		Statement statement = connection.createStatement();
		statement.execute("alter table "+tableName+" DISABLE KEYS");
		statement.close();
	}

	public void enableKeys(String tableName) throws Exception{
		Statement statement = connection.createStatement();
		statement.execute("alter table "+tableName+" ENABLE KEYS");
		statement.close();
	}


	public abstract void createLikeTable(String newTableName, String oldTable ) throws Exception;

	public void alterColumn(String tableName, ALTER_OPERATION op, String... columnsAndConstraintDefinition) throws Exception{
		Statement statement = connection.createStatement();
		StringBuilder createQuery= new StringBuilder("ALTER TABLE "+tableName+" ");
		for (String singleColumnDef:columnsAndConstraintDefinition)			
			createQuery.append(" "+op.toString()+" COLUMN "+singleColumnDef+",");

		createQuery.deleteCharAt(createQuery.length()-1);
		createQuery.append(";");

		logger.debug("the query is: " + createQuery.toString());
		try{
			statement.executeUpdate(createQuery.toString());
		}catch(SQLException sqle){logger.warn("error altering table");}
		statement.close();
	}

	public void createIndex(String tableName, String columnName) throws Exception{
		Statement statement = connection.createStatement();
		StringBuilder createQuery= new StringBuilder("CREATE INDEX IDX_"+tableName+"_"+columnName+" ON "+tableName+"("+columnName+");");
		logger.debug("the query is: " + createQuery.toString());
		statement.executeUpdate(createQuery.toString());
		statement.close();
	}

	public void deleteColumn(String tableName, String columnName) throws Exception{
		Statement statement = connection.createStatement();
		String query="ALTER TABLE "+tableName+" drop column "+columnName;
		logger.debug("the query is: " + query);
		statement.executeUpdate(query.toString());
		statement.close();
	}


	public void dropTable(String table) throws Exception{
		Statement statement = connection.createStatement();
		statement.executeUpdate("DROP TABLE IF EXISTS "+table+" ");
		statement.close();
	}

	public void dropView(String view) throws Exception{
		Statement statement = connection.createStatement();
		statement.executeUpdate("DROP VIEW IF EXISTS "+view+" ");
		statement.close();
	}
	
	
//	public List<List<String>> showTableMetadata(String tableName, String... whereClause) throws Exception{
//		String query="SHOW COLUMNS FROM "+tableName+" "+((whereClause!=null && whereClause.length>0)?"WHERE "+whereClause[0]:"")+";";
//		logger.debug("executing query: "+query);
//		ResultSet rs=this.executeQuery(query);
//		int columns=rs.getMetaData().getColumnCount();
//		List<List<String>> table=  new ArrayList<List<String>>();
//		while (rs.next()){
//			List<String> row= new ArrayList<String>();
//			for (int i=1; i<=columns; i++)
//				row.add(rs.getString(i));
//			table.add(row);
//		}
//		return table;
//	}


	//*********************** DATA MANIPULATION

	//*************** PREPARED STATEMENTS

	public abstract PreparedStatement getFilterCellByAreaQuery(HSPECFields filterByCodeType, String sourceTableName, String destinationTableName)throws Exception;

	
	protected PreparedStatement getPreparedStatementForCount(List<Field> filters, String tableName)throws SQLException{
		return connection.prepareStatement(formSelectCountString(filters, tableName));
	}

	public PreparedStatement getPreparedStatementForQuery(List<Field> filters, String table,String orderColumn,OrderDirection orderDirection) throws SQLException{
		return connection.prepareStatement(formSelectQueryStringFromFields(filters, table,orderColumn,orderDirection));
	}	

	public PreparedStatement getPreparedStatementForDISTINCT(List<Field> filters, Field toSelect,String table,String orderColumn,OrderDirection orderDirection) throws SQLException{
		return connection.prepareStatement(formSelectDistinctQueryStringFromFields(filters, toSelect,table,orderColumn,orderDirection));
	}	
	
	
	public PreparedStatement getPreparedStatementForUpdate(List<Field> toSet,List<Field> keys,String tableName)throws SQLException{
		return this.connection.prepareStatement(formUpdateQuery(toSet, keys, tableName),Statement.RETURN_GENERATED_KEYS);
	}

	public PreparedStatement getPreparedStatementForInsertFromSelect(List<Field> fields, String destTable,String srcTable) throws Exception{

		String query="INSERT INTO "+destTable+" ( "+formSelectQueryStringFromFields(fields, srcTable,null,null)+" )";
//		logger.trace("the prepared statement is :"+ query);
		PreparedStatement ps= preparedStatement(query);
		return ps;
	}

	public PreparedStatement getPreparedStatementForInsert(List<Field> fields, String table) throws Exception{
		StringBuilder fieldsName=new StringBuilder("(");
		StringBuilder fieldsValues=new StringBuilder("(");
		for (Field f: fields){
			fieldsValues.append("?,");
			fieldsName.append(f.name()+",");
		}

		logger.debug(" the values are "+ fields.size());

		fieldsValues.deleteCharAt(fieldsValues.length()-1);
		fieldsValues.append(")");
		fieldsName.deleteCharAt(fieldsName.length()-1);
		fieldsName.append(")");

		String query="INSERT INTO "+table+" "+fieldsName+" VALUES "+fieldsValues;
		logger.debug("the prepared statement is :"+ query);
		PreparedStatement ps= connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
		return ps;
	}

	
	public abstract PreparedStatement getPreparedStatementForInsertOnDuplicate(List<Field> fields, String table,Integer[] keyIndexes) throws Exception;
	
	public PreparedStatement getPreparedStatementForDelete(List<Field> fields, String table) throws Exception{
		PreparedStatement ps= preparedStatement(formDeletetQueryStringFromFields(fields, table));
		return ps;
	}

	@Deprecated
	public PreparedStatement preparedStatement(String query) throws Exception{
		return this.connection.prepareStatement(query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	}
		
	public abstract PreparedStatement fillParameters(List<Field> fields,int parameterOffset,
			PreparedStatement ps) throws SQLException ;
	
	
	
	//************ EXECUTED OPERATIONS

	public abstract String exportTableToCSV(String tableName,boolean exportHeaders,char delimiter)throws Exception;
	
	
	
	public abstract boolean checkExist(String tableName, List<Field> keys)throws Exception;
	public abstract List<List<Field>> insertOperation(String tableName, List<List<Field>> rows) throws Exception;
	public abstract int updateOperation(String tableName, List<List<Field>> keys,List<List<Field>> rows) throws Exception;
	public abstract ResultSet executeFilteredQuery(List<Field> filters, String table, String orderColumn, OrderDirection orderMode)throws Exception;


	public abstract Long getCount(String tableName, List<Field> filters) throws Exception;
	public abstract int deleteOperation(String tableName, List<Field> filters) throws Exception;


	public long getTableCount(String tableName) throws Exception{
		Statement statement = connection.createStatement();
		ResultSet rs =statement.executeQuery("SELECT COUNT(*) FROM "+tableName);
		long value=0l;
		if(rs.next())
			value=rs.getLong(1);
		
		statement.close();
		return value;
	}
	
	public abstract ResultSet getDistinct(Field toSelect, List<Field> filters, String table, String orderColumn, OrderDirection orderMode) throws Exception;

	@Deprecated
	public void executeUpdate(String query) throws Exception{
		Statement statement = connection.createStatement();
		statement.executeUpdate(query);
		statement.close();
	}
	public boolean checkTableExist(String tableName)throws Exception{
		Statement stmt=connection.createStatement();
		try{
			stmt.execute("SELECT * FROM "+tableName+" LIMIT 1 OFFSET 0");
		}catch(SQLException e){
			return false;
		}finally{
			stmt.close();
		}
		return true;
	}

	protected List<List<Field>> getGeneratedKeys(PreparedStatement ps) throws SQLException{
		ResultSet rs=ps.getGeneratedKeys();
		ResultSetMetaData rsMeta=rs.getMetaData();
		List<List<Field>> toReturn= new ArrayList<List<Field>>();
		while(rs.next()){
			List<Field> row= new ArrayList<Field>();
			for(int i=1;i<=rsMeta.getColumnCount();i++)
				row.add(new Field(rsMeta.getColumnName(i),rs.getString(i),FieldType.STRING));
			toReturn.add(row);
		}
		return toReturn;
	}


	//********************* STRING FORM UTILITIES


	protected static String formSelectQueryStringFromFields(List<Field> filters,String table,String sortColumn,OrderDirection sortDirection){
		String toReturn="SELECT * FROM "+table+
		(((filters!=null)&&filters.size()>0)?" WHERE "+getCondition(filters,"AND"):"")+
		((sortColumn!=null&&!sortColumn.equalsIgnoreCase("null"))?" ORDER BY "+sortColumn+" "+sortDirection:"");
//		logger.debug("QUERY STRING IS : "+toReturn);
		return toReturn;
	}

	
	protected static String formSelectDistinctQueryStringFromFields(List<Field> filters,Field toSelectField,String table,String sortColumn,OrderDirection sortDirection){
		String toReturn="SELECT DISTINCT("+toSelectField.name()+") FROM "+table+
		(((filters!=null)&&filters.size()>0)?" WHERE "+getCondition(filters,"AND"):"")+
		((sortColumn!=null&&!sortColumn.equalsIgnoreCase("null"))?" ORDER BY "+sortColumn+" "+sortDirection:"");
//		logger.debug("QUERY STRING IS : "+toReturn);
		return toReturn;
	}
	
	
	protected static String formSelectCountString(List<Field> filters, String tableName){
		return "SELECT COUNT(*) FROM "+tableName+(((filters!=null)&&filters.size()>0)?" WHERE "+getCondition(filters,"AND"):"");
	}

	protected static String formDeletetQueryStringFromFields(List<Field> filters,String table){
		return "DELETE FROM "+table+(((filters!=null)&&filters.size()>0)?" WHERE "+getCondition(filters,"AND"):"");
	}

	protected static String formUpdateQuery(List<Field> toSet, List<Field> keys,String tableName){
		String toReturn="UPDATE "+tableName+" SET "+getCondition(toSet,",")+
		(((keys!=null)&&keys.size()>0)?" WHERE "+getCondition(keys,"AND"):"");
//		logger.debug("QUERY STRING IS : "+toReturn);
		return toReturn;
	}
	
	private static String getCondition(List<Field> filters,String operator){
		StringBuilder query=new StringBuilder();
		if((filters!=null)&&filters.size()>0){
			for(Field f:filters)query.append(" "+f.name()+" = ? "+operator);
			query.delete(query.lastIndexOf(operator),query.lastIndexOf(operator)+operator.length());
		}
//		logger.debug("Formed condition string "+query);
		return query+"";
	}

	
}