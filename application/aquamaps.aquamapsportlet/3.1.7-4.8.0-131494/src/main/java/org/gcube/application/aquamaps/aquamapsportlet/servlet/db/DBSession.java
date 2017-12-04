package org.gcube.application.aquamaps.aquamapsportlet.servlet.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.derby.tools.ij;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





/**
 * 
 * @author lucio
 *
 */
public class DBSession {

	private static final Logger logger = LoggerFactory.getLogger(DBSession.class);


	protected static final String DB_DIR = "AquaMapsPortletServlet_DB";
	protected static final String DB_DRIVER_CLASS = "org.apache.derby.jdbc.EmbeddedDriver";

	protected static String dbDirPath;


	protected Connection connection;

	private static Map<String, DBSession> instanceMap=new ConcurrentHashMap<String, DBSession>();


	private static String getDBPath(String scope){
		return DB_DIR+scope.replaceAll("/", "-");
	}
	
	public static void dropDataBase(String scope){
		logger.debug("Removing db instance for scope "+scope+", instance is "+instanceMap.get(scope));
		
		String tmpDirName = System.getProperty("java.io.tmpdir");

		File tmpDir = new File(tmpDirName);
		String dbDirPath=getDBPath(scope);
		File dbDir = new File(tmpDir, dbDirPath);
		if(dbDir.exists()){
			try{
				Class.forName(DB_DRIVER_CLASS).newInstance();

				String connectionUrl = "jdbc:derby:"+dbDir.getAbsolutePath()+";shutdown=true";
				logger.debug("Connection url: "+connectionUrl);

				DriverManager.getConnection(connectionUrl);
			}catch(Exception e){
//				logger.warn("Unable to shutdown DB "+dbDir.getAbsolutePath(),e);
				//Shutdown raises an exception
			}
			try {
				logger.debug("Deleting dbDir: "+dbDir.getAbsolutePath());
				FileUtils.deleteDirectory(dbDir);
			} catch (IOException e) {
				logger.warn("Unable to delete folder "+dbDir.getAbsolutePath(), e);
			}
		}

	}

	public static synchronized DBSession getInstance(String scope)throws Exception{
		if (!instanceMap.containsKey(scope) || (instanceMap.containsKey(scope) && instanceMap.get(scope) == null)){



			String tmpDirName = System.getProperty("java.io.tmpdir");

			File tmpDir = new File(tmpDirName);

			dbDirPath=getDBPath(scope);
			File dbDir = new File(tmpDir, dbDirPath);

//			if (!dbDir.exists()) {
//				logger.debug("dbDir doesn't exists, initalizing db");
//				initializeDB(dbDir);
//			}
			
			// force db init
			initializeDB(dbDir);


			instanceMap.put(scope, new DBSession( connectDB(scope)));
		}else {
			DBSession instance=instanceMap.get(scope);
			if( (instance.getConnection()==null)||(instance.getConnection().isClosed()))
				instanceMap.put(scope, new DBSession( connectDB(scope)));
		}

		return instanceMap.get(scope);
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}



	/**
	 * 
	 * @return
	 * @throws Exception
	 */


	public static void initializeDB(File dbDir) throws Exception
	{
		try{
			logger.debug("Initializing DB Dir: "+dbDir.getAbsolutePath());
			dbDirPath = dbDir.getAbsolutePath();
	
			if (dbDir.exists()){
				logger.debug("Init Process, Deleting existing dir "+dbDir.getAbsolutePath());
				try {
					FileUtils.deleteDirectory(dbDir);
				} catch (IOException e) {
					logger.error("Error removing the dbDir: "+dbDir.getAbsolutePath(), e);
					throw new Exception("Error initializing the db",e);
				}
			}
	
			try {
				Class.forName(DB_DRIVER_CLASS).newInstance();
			} catch (ClassNotFoundException e) {
				throw new Exception("Error loading jdbc driver class: "+DB_DRIVER_CLASS,e);
			}
	
			String connectionUrl = "jdbc:derby:"+dbDir.getAbsolutePath()+";create=true";
			logger.debug("Connection url: "+connectionUrl);
	
			Connection connection;
			try {
				connection = DriverManager.getConnection(connectionUrl);
			} catch (SQLException e) {
				throw new Exception("Error connecting to the db with url: ",e);
			}
	
	
			try {
				executeBatch(connection, "sql/createTables.sql");
			} catch (Exception e) {
				logger.error("Error creating the schemas", e);
				throw new Exception("Error initializing the db",e);
			}
		}catch(Exception e){
			if (dbDir.exists()){
				logger.debug("Something went wrong, Deleting existing dir "+dbDir.getAbsolutePath());
				try {
					FileUtils.deleteDirectory(dbDir);
				} catch (IOException e1) {
					logger.error("Error removing the dbDir: "+dbDir.getAbsolutePath(), e1);
					throw new Exception("Error initializing the db",e1);
				}
			}
			throw e;
		}
	}

	public static void executeBatch(Connection connection, String batchFile) throws Exception
	{
		logger.debug("executeBatch "+batchFile);

		InputStream batchStream = DBManager.class.getResourceAsStream(batchFile);

		File tmpOut;
		FileOutputStream fos;
		try {
			tmpOut = File.createTempFile("batchExecution", "logger");
			fos = new FileOutputStream(tmpOut);
		} catch (IOException e) {
			logger.error("Error creating the tmp out file", e);
			throw e;
		}

		int exceptions;
		try {
			exceptions = ij.runScript(connection, batchStream, "UTF-8",fos, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Error executing the batch sql", e);
			throw e;
		}

		if (logger.isTraceEnabled() || exceptions>0){
			//			logger.debug("Batch logger:");

			try {
				LineNumberReader lnr = new LineNumberReader(new FileReader(tmpOut));
				String line;
				while((line = lnr.readLine())!=null){
					if (exceptions>0) logger.error(line);
					//					else logger.debug(line);
				}
				lnr.close();
			} catch (FileNotFoundException e) {
				logger.error("Error reading the output batch file", e);
			} catch (IOException e) {
				logger.error("Error reading the output batch file", e);
			}
		}

		tmpOut.delete();

		if (exceptions>0){
			throw new Exception(exceptions+" exceptions during script batch execution.");
		}

	}

	public static Connection connectDB(String scope) throws Exception
	{

		String tmpDirName = System.getProperty("java.io.tmpdir");

		File tmpDir = new File(tmpDirName);

		dbDirPath=getDBPath(scope);
		File dbDir = new File(tmpDir, dbDirPath);
		dbDir.mkdirs();

		logger.debug("Connecting dbDir: "+dbDir.getAbsolutePath());
		dbDirPath = dbDir.getAbsolutePath();

		try {
			Class.forName(DB_DRIVER_CLASS).newInstance();
		} catch (ClassNotFoundException e) {
			logger.error("Error loading jdbc driver class: "+DB_DRIVER_CLASS, e);
			throw new Exception("Error initializing the db",e);
		}

		String connectionUrl = "jdbc:derby:"+dbDir.getAbsolutePath();
		logger.debug("Connection url: "+connectionUrl);

		Connection connection;
		try {
			connection = DriverManager.getConnection(connectionUrl);
		} catch (SQLException e) {
			logger.error("Error connecting to the db with url: "+connectionUrl, e);
			throw new Exception("Error initializing the db",e);
		}

		return connection;

	}



	private DBSession(Connection conn){
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
		logger.debug("DIRECT QUERY IS : "+query);
		Statement statement=this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		return statement.executeQuery(query);
	}

	//************ DATA DEFINITION


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

	public List<List<String>> showTableMetadata(String tableName, String... whereClause) throws Exception{
		String query="SHOW COLUMNS FROM "+tableName+" "+((whereClause!=null && whereClause.length>0)?"WHERE "+whereClause[0]:"")+";";
		logger.debug("executing query: "+query);
		ResultSet rs=this.executeQuery(query);
		int columns=rs.getMetaData().getColumnCount();
		List<List<String>> table=  new ArrayList<List<String>>();
		while (rs.next()){
			List<String> row= new ArrayList<String>();
			for (int i=1; i<=columns; i++)
				row.add(rs.getString(i));
			table.add(row);
		}
		return table;
	}


	//*********************** DATA MANIPULATION

	//*************** PREPARED STATEMENTS



	protected PreparedStatement getPreparedStatementForCount(List<Field> filters, String tableName)throws SQLException{
		return connection.prepareStatement(formSelectCountString(filters, tableName));
	}

	public PreparedStatement getPreparedStatementForQuery(List<Field> filters, String table,String orderColumn,String orderDirection) throws SQLException{
		return connection.prepareStatement(formSelectQueryStringFromFields(filters, table,orderColumn,orderDirection));
	}	

	public PreparedStatement getPreparedStatementForUpdate(List<Field> toSet,List<Field> keys,String tableName)throws SQLException{
		return this.connection.prepareStatement(formUpdateQuery(toSet, keys, tableName),Statement.RETURN_GENERATED_KEYS);
	}

	public PreparedStatement getPreparedStatementForInsertFromSelect(List<Field> fields, String destTable,String srcTable) throws Exception{

		String query="INSERT INTO "+destTable+" ( "+formSelectQueryStringFromFields(fields, srcTable,null,null)+" )";
		logger.debug("the prepared statement is :"+ query);
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


	//	public abstract PreparedStatement getPreparedStatementForInsertOnDuplicate(List<Field> fields, String table,Integer[] keyIndexes) throws Exception;

	public PreparedStatement getPreparedStatementForDelete(List<Field> fields, String table) throws Exception{
		PreparedStatement ps= preparedStatement(formDeletetQueryStringFromFields(fields, table));
		return ps;
	}

	@Deprecated
	public PreparedStatement preparedStatement(String query) throws Exception{
		logger.debug("Prepared statement : "+query);
		return this.connection.prepareStatement(query,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	}

	//	public PreparedStatement fillParameters(List<Field> fields, PreparedStatement ps) throws SQLException{
	//		for(int i=0;i<fields.size();i++){
	//			Field f=fields.get(i);
	//			switch(f.getType()){
	//			case BOOLEAN: ps.setBoolean(i+1, Boolean.parseBoolean(f.getValue()));
	//			break;
	//			case DOUBLE: ps.setFloat(i+1, Float.parseFloat(f.getValue()));
	//			break;
	//			case INTEGER: ps.setInt(i+1, Integer.parseInt(f.getValue()));
	//			break;				
	//			case STRING: ps.setString(i+1,f.getValue());
	//			break;
	//
	//			}			
	//		}
	//		return ps;
	//	}


	public PreparedStatement fillParameters(List<Field> fields,int parameterOffset, PreparedStatement ps) throws SQLException{
		//		logger.debug("Fillin prepared statement : ");
		for(int i=0;i<fields.size();i++){
			Field f=fields.get(i);
			//			logger.trace("Field "+f.getName()+" = "+f.getValue()+" ( "+f.getType()+" )");
			switch(f.type()){
			case BOOLEAN:{ 
				Integer value=f.getValueAsBoolean()?1:0;
				ps.setInt(i+1+parameterOffset, value);
				break;
			}
			case DOUBLE: ps.setDouble(i+1+parameterOffset, f.getValueAsDouble());
			break;
			case INTEGER: try{
				ps.setInt(i+1+parameterOffset, f.getValueAsInteger());
			}catch(NumberFormatException e){
				//trying long
				ps.setLong(i+1+parameterOffset, Long.parseLong(f.value()));
			}
			break;				
			case STRING: ps.setString(i+1+parameterOffset,f.value());
			break;
			case LONG: ps.setLong(i+1+parameterOffset, f.getValueAsLong());
			break;
			}			
		}
		return ps;
	}



	//************ EXECUTED OPERATIONS


	public List<List<Field>> insertOperation(String tableName,
			List<List<Field>> rows) throws Exception {
		List<List<Field>> toReturn= new ArrayList<List<Field>>();
		//**** Create Query
		if(rows.size()==0) throw new Exception("Empty rows to insert");

		PreparedStatement ps=getPreparedStatementForInsert(rows.get(0), tableName);

		for(List<Field> row:rows){
			ps=fillParameters(row,0, ps);
			if(ps.executeUpdate()>0)
				toReturn.addAll(getGeneratedKeys(ps));
		}
		logger.debug("INSERTED "+toReturn.size()+" ENTRIES");
		return toReturn;
	}
	public int updateOperation(String tableName, List<List<Field>> keys,
			List<List<Field>> rows) throws Exception {
		int count=0;
		//**** Create Query

		if(rows.size()==0) throw new Exception("Empty rows to insert");
		if(keys.size()==0) throw new Exception("Empty keys");
		if(rows.size()!=keys.size()) throw new Exception("Un matching rows/keys sizes "+rows.size()+"/"+keys.size());

		PreparedStatement ps=getPreparedStatementForUpdate(rows.get(0), keys.get(0), tableName);


		for(int i=0;i<rows.size();i++){

			//fill values
			ps=fillParameters(rows.get(i), 0, ps);
			//fill keys
			ps=fillParameters(keys.get(i),rows.get(i).size(),ps);
			count+=ps.executeUpdate();
		}
		return count;
	}
	public ResultSet executeFilteredQuery(List<Field> filters, String table, String orderColumn, String orderMode)throws Exception{
		PreparedStatement ps=getPreparedStatementForQuery(filters, table, orderColumn, orderMode);
		return fillParameters(filters,0, ps).executeQuery();
	}

	public int getCount(String tableName, List<Field> filters) throws Exception {
		PreparedStatement ps=getPreparedStatementForCount(filters, tableName);
		ResultSet rs=fillParameters(filters,0, ps).executeQuery();
		if(rs.next()) return rs.getInt(1);
		else return 0;
	}
	public int deleteOperation(String tableName, List<Field> filters)
	throws Exception {
		PreparedStatement ps=getPreparedStatementForDelete(filters, tableName);
		int count=fillParameters(filters,0, ps).executeUpdate();
		logger.debug("DELETED "+count+" ENTRIES");
		return count;
	}


	public int getTableCount(String tableName) throws Exception{
		Statement statement = connection.createStatement();
		ResultSet rs =statement.executeQuery("SELECT COUNT(*) FROM "+tableName);
		rs.next();
		return rs.getInt(1);
	}


	@Deprecated
	public int executeUpdate(String query) throws Exception{
		Statement statement = connection.createStatement();
		return statement.executeUpdate(query);
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


	protected static String formSelectQueryStringFromFields(List<Field> filters,String table,String sortColumn,String sortDirection){
		String toReturn="SELECT * FROM "+table+
		(((filters!=null)&&filters.size()>0)?" WHERE "+getCondition(filters,"AND"):"")+
		((sortColumn!=null)?" ORDER BY "+sortColumn+" "+sortDirection:"");
		logger.debug("QUERY STRING IS : "+toReturn);
		return toReturn;
	}

	protected static String formSelectCountString(List<Field> filters, String tableName){
		String toReturn="SELECT COUNT(*) FROM "+tableName+(((filters!=null)&&filters.size()>0)?" WHERE "+getCondition(filters,"AND"):"");
		logger.debug("QUERY STRING IS : "+toReturn);
		return toReturn;
	}

	protected static String formDeletetQueryStringFromFields(List<Field> filters,String table){
		String toReturn="DELETE FROM "+table+(((filters!=null)&&filters.size()>0)?" WHERE "+getCondition(filters,"AND"):"");
		logger.debug("QUERY STRING IS : "+toReturn);
		return toReturn;
	}

	protected static String formUpdateQuery(List<Field> toSet, List<Field> keys,String tableName){
		String toReturn="UPDATE "+tableName+" SET "+getCondition(toSet,",")+
		(((keys!=null)&&keys.size()>0)?" WHERE "+getCondition(keys,"AND"):"");
		logger.debug("QUERY STRING IS : "+toReturn);
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