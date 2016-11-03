package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HSPECFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;

public class PostGresSQLDBSession extends DBSession {


	public PostGresSQLDBSession(Connection conn){
		super(conn);
	}





	@Override
	public ResultSet executeFilteredQuery(List<Field> filters, String table, String orderColumn, OrderDirection orderMode)throws Exception{
		PreparedStatement ps=getPreparedStatementForQuery(filters, table, orderColumn, orderMode);
		return fillParameters(filters,0, ps).executeQuery();
	}


	@Override
	public PreparedStatement fillParameters(List<Field> fields,int parameterOffset, PreparedStatement ps) throws SQLException{
		//		logger.debug("Fillin prepared statement : ");
		for(int i=0;i<fields.size();i++){
			int psIndex=i+1+parameterOffset;
			Field f=fields.get(i);
			if(f.isNull()) ps.setNull(psIndex, ps.getParameterMetaData().getParameterType(psIndex));
			else{
				//			logger.trace("Field "+f.getName()+" = "+f.getValue()+" ( "+f.getType()+" )");
				switch(f.type()){
				case BOOLEAN:{ 
					Integer value=f.getValueAsBoolean()?1:0;
					ps.setInt(psIndex, value);
					break;
				}
				case DOUBLE: ps.setDouble(psIndex, f.getValueAsDouble());
				break;
				case INTEGER: try{
					ps.setInt(psIndex, f.getValueAsInteger());
				}catch(NumberFormatException e){
					//trying long
					ps.setLong(psIndex, Long.parseLong(f.value()));
				}
				break;	
				case TIMESTAMP : try{
					ps.setTimestamp(psIndex, Timestamp.valueOf(f.value()));
				}catch(IllegalArgumentException e){
					ps.setNull(psIndex, Types.TIMESTAMP);
				}break;
				case STRING: ps.setString(psIndex,f.value());
				break;
				case LONG: ps.setLong(psIndex, f.getValueAsLong());
				break;
				}			
			}
		}
		return ps;
	}


	@Override
	public boolean checkExist(String tableName, List<Field> keys)
			throws Exception {
		PreparedStatement ps=getPreparedStatementForQuery(keys, tableName, null, null);
		ResultSet rs=fillParameters(keys,0, ps).executeQuery();
		return rs.first();
	}


	@Override
	public int deleteOperation(String tableName, List<Field> filters)
			throws Exception {
		PreparedStatement ps=getPreparedStatementForDelete(filters, tableName);
		return fillParameters(filters,0, ps).executeUpdate();
	}


	@Override
	public Long getCount(String tableName, List<Field> filters) throws Exception {
		PreparedStatement ps=getPreparedStatementForCount(filters, tableName);
		ResultSet rs=fillParameters(filters,0, ps).executeQuery();
		if(rs.next()) return rs.getLong(1);
		else return 0l;
	}


	@Override
	public PreparedStatement getFilterCellByAreaQuery(HSPECFields filterByCodeType,
			String sourceTableName, String destinationTableName) throws Exception {

		String conditionString=null;

		switch(filterByCodeType){
		case eezall : conditionString=" ? NOT IN s."+filterByCodeType;
		break;
		default : conditionString=" s."+filterByCodeType+"= ? ";
		break;



		//		case faoaream : return preparedStatement("INSERT IGNORE INTO "+destinationTableName+" ( Select "+sourceTableName+".* from "+sourceTableName+
		//						" where "+sourceTableName+"."+HSPECFields.faoaream+" = ? ) ");
		//		case eezall : return "INSERT IGNORE INTO "+destinationTableName+" ( Select "+sourceTableName+".* from "+sourceTableName+
		//						" where find_in_set( ? , "+sourceTableName+"."+HSPECFields.eezall+")) ";
		//		case lme : return "INSERT IGNORE INTO "+destinationTableName+" ( Select "+sourceTableName+".* from "+sourceTableName+
		//						" where "+sourceTableName+"."+HSPECFields.lme+" = ? ) ";
		//		default : throw new SQLException("Invalid Field "+filterByCodeType);
		}
		String query="INSERT INTO "+destinationTableName+" (SELECT * FROM "+sourceTableName+" s WHERE "+conditionString+" EXCEPT "+
				"( SELECT * FROM "+destinationTableName+" ) )";
		logger.trace("FILTER STRING : "+query);

		return preparedStatement(query);
	}


	@Override
	public List<List<Field>> insertOperation(String tableName,
			List<List<Field>> rows) throws Exception {
		List<List<Field>> toReturn= new ArrayList<List<Field>>();
		//**** Create Query
		if(rows.size()==0) throw new Exception("Empty rows to insert");

		PreparedStatement ps= getPreparedStatementForInsert(rows.get(0), tableName);

		for(List<Field> row:rows){
			ps=fillParameters(row,0, ps);
			if(ps.executeUpdate()>0)
				toReturn.addAll(getGeneratedKeys(ps));
		}
		return toReturn;
	}


	@Override
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


	@Override
	public void createLikeTable(String newTableName, String oldTable)
			throws Exception {
		this.dropTable(newTableName);
		Statement statement = connection.createStatement();
		statement.executeUpdate("CREATE TABLE  "+newTableName+" ( LIKE "+oldTable+" )");
		statement.close();
	}


	@Override
	public void createTable(String tableName,
			String[] columnsAndConstraintDefinition)
					throws Exception {

		this.dropTable(tableName);

		Statement statement = connection.createStatement();

		StringBuilder createQuery= new StringBuilder("CREATE TABLE "+tableName+" (");
		for (String singleColumnDef:columnsAndConstraintDefinition)			
			createQuery.append(singleColumnDef+",");

		createQuery.deleteCharAt(createQuery.length()-1);
		createQuery.append(") ");

		logger.debug("the query is: " + createQuery.toString());
		statement.executeUpdate(createQuery.toString());
		statement.close();
	}





	@Override
	public PreparedStatement getPreparedStatementForInsertOnDuplicate(
			List<Field> fields, String table, Integer[] keyIndexes)
					throws Exception {
		//TODO
		throw new Exception("YET TO IMPLEMENT");
	}





	@Override
	public ResultSet getDistinct(Field toSelect,List<Field> filters, String table,
			String orderColumn, OrderDirection orderMode) throws Exception {
		PreparedStatement ps=getPreparedStatementForDISTINCT(filters, toSelect, table, orderColumn, orderMode);
		return fillParameters(filters,0, ps).executeQuery();
	}


	@Override
	public String exportTableToCSV(String tableName, boolean hasHeaders,char delimiter) throws Exception {
		Statement stmt = null;
		try{
			File out=new File(ServiceContext.getContext().getFolderPath(FOLDERS.IMPORTS),ServiceUtils.generateId(tableName, ".csv"));
			stmt=connection.createStatement();
			String copyString ="COPY "+tableName+" TO '"+out.getAbsolutePath()+"' WITH DELIMITER '"+delimiter+"'"+(hasHeaders?" CSV HEADER":"");
			logger.debug("Gonna execute copy  : "+copyString);
			stmt.execute(copyString);			
			return out.getAbsolutePath();
		}catch(Exception e){
			throw e;
		}finally{
			if(stmt!=null)stmt.close();
		}
	}
}
