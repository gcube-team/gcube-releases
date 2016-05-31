package org.gcube.data.analysis.tabulardata.cube.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.admin.Admin;
import org.gcube.data.analysis.tabulardata.cube.data.connection.unprivileged.Unprivileged;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.mapping.SQLModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Default
@Singleton
public class SQLDatabaseWrangler implements DatabaseWrangler {

	private final String DEFAULT_SCHEMA_NAME = "public";

	private static Logger log = LoggerFactory.getLogger(SQLDatabaseWrangler.class);

	private DatabaseConnectionProvider adminConnectionProvider;

	private DatabaseConnectionProvider unprivilegedConnectionProvider;

	private SQLModelMapper sqlModelMapper;

	private ResourceFinder resourceFinder;
	
	@Inject
	public SQLDatabaseWrangler(@Admin DatabaseConnectionProvider adminConnectionProvider,
			@Unprivileged DatabaseConnectionProvider unprivilegedConnectionProvider, SQLModelMapper sqlModelMapper, @Default ResourceFinder resourceFinder) {
		super();
		this.adminConnectionProvider = adminConnectionProvider;
		this.unprivilegedConnectionProvider = unprivilegedConnectionProvider;
		this.sqlModelMapper = sqlModelMapper;
		this.resourceFinder = resourceFinder;
	}

	@PostConstruct
	private void initializeSql() {
		for (String file :resourceFinder.getResourcesPath(Pattern.compile(".*\\.sql"))){
			BufferedReader reader =null;
			if (!file.contains("org/gcube/data/analysis/tabulardata/sql/"))
				file = "org/gcube/data/analysis/tabulardata/sql/"+file;
			
			InputStream is = resourceFinder.getStream(file);
			if (is==null)
				continue;
			
			try{
				reader = new BufferedReader(new InputStreamReader(is));
				String         line = null;
				StringBuilder  stringBuilder = new StringBuilder();
				while( ( line = reader.readLine() ) != null ) 
					stringBuilder.append( line );
							
				executeQuery(stringBuilder.toString());
			}catch(Exception e){
				throw new RuntimeException("error initializing sql",e);
			}finally{
				if (reader!=null )
					try {
						reader.close();
					} catch (IOException e) {
					}
			}

		}
		
	}

	@Override
	public String createTable() {
		return createTable(false);
	}
	
	@Override
	public void createTable(String name){
		createInternal(false, name);		
	}

	@Override
	public String createTable(boolean unsafe) {
		String tableName = generateTableName();
		createInternal(unsafe, tableName);
		return tableName;
	}

	private void createInternal(boolean unsafe, String tableName){
		String query = generateCreateTableQuery(tableName, unsafe);
		query += generateUserAccountGrantQuery(tableName);
		query += generateUserAccountGrantQuery(tableName + "_id_seq");
		executeQuery(query);
	}
	
	private String generateCreateTableQuery(String tableName, boolean unlogged) {
		if (unlogged)
			return String.format("CREATE UNLOGGED TABLE %1$s ( id serial primary key);", tableName);
		return String.format("CREATE TABLE %1$s ( id serial primary key);", tableName);
	}

	@Override
	public void removeTable(String tableName) {
		String query = generateDropTableQuery(tableName);
		executeQuery(query);
	}

	private String generateDropTableQuery(String tableName) {
		return String.format("DROP TABLE %1$s;", tableName);
	}

	@Override
	public String cloneTable(String tableName, boolean withData, boolean unsafe) {
		String newTableName = generateTableName();
		String query = generateCloneTableQuery(newTableName, tableName, withData, unsafe);
		query += generateUserAccountGrantQuery(newTableName);
		query += generateUserAccountGrantQuery(newTableName + "_id_seq");
		executeQuery(query);
		return newTableName;
	}

	private String generateCloneTableQuery(String newTableName, String tableToCloneName, boolean withData,
			boolean unsafe) {
		StringBuilder sb = new StringBuilder();
		String unlogged = "";
		if (unsafe)
			unlogged = "UNLOGGED";
		String data = "";

		if (withData)
			data = "WITH DATA";
		else
			data = "WITH NO DATA";

		sb.append(String.format("CREATE %1$s TABLE %2$s WITHOUT OIDS AS TABLE %3$s %4$s;", unlogged, newTableName, tableToCloneName,
				data));
		sb.append(String.format("CREATE SEQUENCE %1$s_id_seq;", newTableName));
		if (withData)
			sb.append(String.format("SELECT setval('%1$s_id_seq', max(id) ) FROM %2$s;", newTableName, tableToCloneName));
		else
			sb.append(String.format("SELECT setval('%1$s_id_seq', 1 );", newTableName));
		sb.append(String.format("ALTER TABLE %1$s ALTER id SET NOT NULL;", newTableName));
		sb.append(String.format("ALTER TABLE %1$s ALTER id SET DEFAULT nextval('%1$s_id_seq');", newTableName));
		log.debug("executing creation queries: "+sb.toString());
		return sb.toString();
	}

	@Override
	public boolean exists(String tableName) {
		return executeCount(String.format("SELECT count(*) FROM pg_tables WHERE tablename='%1$s'", tableName.toLowerCase()))>0;
		
	}

	@Override
	public void addColumn(String tableName, String columnName, DataType type) {
		String query = generateAddColumnQuery(tableName, columnName, type, null);
		executeQuery(query);
	}
	
	@Override
	public void addColumn(String tableName, String columnName, DataType type, TDTypeValue defaultValue) {
		String query = generateAddColumnQuery(tableName, columnName, type, defaultValue);
		executeQuery(query);
	}
	
	private String generateAddColumnQuery(String tableName, String columnName, DataType type,TDTypeValue defaultValue) {
		return String.format("ALTER TABLE %1$s ADD COLUMN %2$s %3$s %4$s", tableName, columnName, getColumnSQLType(type), getDefaultValueSQL(defaultValue));
	}

	private String getColumnSQLType(DataType type) {
		return sqlModelMapper.translateDataTypeToSQL(type);
	}

	private String getDefaultValueSQL(TDTypeValue defaultValue) {
		if (defaultValue==null) return "";
		else return String.format("DEFAULT %s", sqlModelMapper.translateModelValueToSQL(defaultValue));
	}
	
	@Override
	public void removeColumn(String tableName, String columnName) {
		String query = generateDropColumnQuery(tableName, columnName);
		executeQuery(query);
	}

	private String generateDropColumnQuery(String tableName, String columnName) {
		return String.format("ALTER TABLE %1$s DROP COLUMN %2$s;", tableName, columnName);
	}
	
	@Override
	public void alterColumnType(String tableName, String columnName,
			DataType newType) {
		String query = generateAlterTypeQuery(tableName, columnName, newType);
		executeQuery(query);		
	}

	private String generateAlterTypeQuery(String tableName, String columnName, DataType type) {
		return String.format("ALTER TABLE %1$s ALTER COLUMN %2$s TYPE %3$s;", tableName, columnName, getColumnSQLType(type));
	}
			
	private String generateTableName() {
		String tableName = null;
		int count = 0;

		do {
			tableName = RandomStringUtils.random(32, true, false).toLowerCase();
			log.debug("Generated table name: " + tableName);
			Connection connection = null;
			Statement statement = null;
			try {
				connection = adminConnectionProvider.getConnection();
				statement = connection.createStatement();
				statement.execute(String.format(
						"SELECT * FROM pg_tables WHERE schemaname='%1$s' AND tablename='%2$s';", DEFAULT_SCHEMA_NAME,
						tableName));
				count = statement.getFetchSize();
				log.debug(String.format("Table with name '%1$s' found %2$s times.", tableName, count));
			} catch (SQLException e) {
				log.error("Error occurred while verifying generated table name.", e);
				throw new RuntimeException("Unable to generate a table name", e);
			} finally {
				DbUtils.closeQuietly(connection);
				DbUtils.closeQuietly(statement);
			}

		} while (count > 0);

		return tableName;
	}

	public void executeQuery(String query) {
		log.debug("Executing SQL query: " + query);
		Connection connection = null;
		Statement statement = null;
		try {
			connection = adminConnectionProvider.getConnection();
			statement = connection.createStatement();
			statement.execute(query + ";");
			connection.close();
		} catch (SQLException e) {
			log.error("Unable to execute query: " + query, e);
			throw new RuntimeException("Error encountered while executing database query: " + query,e);
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(statement);
		}
	}

	private int executeCount(String query) {
		log.debug("Executing SQL query: " + query);
		Connection connection = null;
		Statement statement = null;
		try {
			connection = adminConnectionProvider.getConnection();
			statement = connection.createStatement();
			ResultSet ret = statement.executeQuery(query + ";");
			int toReturn = 0;
			if (ret.next())
				toReturn = ret.getInt(1);
			connection.close();
			return toReturn;
		} catch (SQLException e) {
			log.error("Unable to execute query: " + query, e);
			throw new RuntimeException("Error encountered while executing database query: " + query,e);
		} finally {
			DbUtils.closeQuietly(connection);
			DbUtils.closeQuietly(statement);
		}
	}
	
	private String generateUserAccountGrantQuery(String tableName) {
		String unprivilegedUser = unprivilegedConnectionProvider.getDatabaseEndpoint().getCredentials().getUsername();
		return String.format("GRANT SELECT,UPDATE,INSERT ON TABLE %1$s TO %2$s;", tableName, unprivilegedUser);
	}

	@Override
	public void createIndex(String tableName, String columnName) {
		Thread t = new Thread(new IndexCreator(tableName, columnName));
		t.start();
	}

	private class IndexCreator implements Runnable {

		private String tableName;
		private String columnName;

		public IndexCreator(String tableName, String columnName) {
			this.tableName = tableName;
			this.columnName = columnName;
		}

		@Override
		public void run() {
			String query = String.format("CREATE INDEX ON %1$s ( %2$s );", tableName, columnName);
			executeQuery(query);
		}
	}

	@Override
	public void setNullable(String tableName, String columnName, boolean nullable) {
		String notNullSnippet = "SET NOT NULL";
		String nullableSnippet = "DROP NOT NULL";
		executeQuery(String.format("ALTER TABLE %s ALTER COLUMN %s %s;", tableName, columnName,
				nullable ? nullableSnippet : notNullSnippet));
	}

	@Override
	public void createTriggerOnTable(String triggerName, List<Condition> conditions, HTime htime, String targetTableName, String procedure) {
		
		if (conditions.isEmpty()) throw new IllegalArgumentException("at least a condition has to be set");
		StringBuilder sBuilder = new StringBuilder();
		for (Condition cond : conditions)
			sBuilder.append(cond.name()).append(" OR ");
		
		String conds = sBuilder.delete(sBuilder.length()-4, sBuilder.length()).toString();
		
		executeQuery(String.format(
				"CREATE TRIGGER %s %s %s ON %s FOR EACH ROW EXECUTE PROCEDURE %s;", triggerName, htime.name(), conds, 
				targetTableName, procedure));
	}

	
	
	@Override
	public void createUniqueIndex(String tableName, String columnName) {
		executeQuery(String.format("CREATE UNIQUE INDEX ON %s ( %s );", tableName, columnName));
	}
}
