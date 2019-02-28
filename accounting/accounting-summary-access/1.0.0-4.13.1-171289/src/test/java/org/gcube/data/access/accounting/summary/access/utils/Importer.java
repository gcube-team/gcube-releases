package org.gcube.data.access.accounting.summary.access.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;

import org.gcube.accounting.accounting.summary.access.impl.DBStructure.CONTEXTS;
import org.gcube.accounting.accounting.summary.access.impl.DBStructure.DIMENSIONS;
import org.gcube.accounting.accounting.summary.access.impl.DBStructure.Measure;
import org.postgresql.util.PSQLException;
public class Importer {

	private static Connection getSourceConn() throws SQLException, ClassNotFoundException {
		
		// PRODUCTION OLD DB
		String user="analytics_board_u";
		String password ="b52b64ab07ea0b5";
		String url ="jdbc:postgresql://postgresql-srv.d4science.org:5432/analytics_board";
		
		Class.forName("org.postgresql.Driver");		
		Connection conn = DriverManager.getConnection(url, user, password);
		conn.setAutoCommit(false);
	
		return conn;
	}
	
	private static Connection getDestinationConn() throws SQLException, ClassNotFoundException{
		//DEV OLD DB
//		String user="analytics_b_dev_u";
//		String password ="78cb625303be21b";
//		String url ="jdbc:postgresql://postgresql-srv-dev.d4science.org:5432/analytics_b_dev";
		

//		// PRODUCTION OLD DB
				String user="analytics_board_u";
				String password ="b52b64ab07ea0b5";
				String url ="jdbc:postgresql://postgresql-srv.d4science.org:5432/analytics_board";
		
		
		Class.forName("org.postgresql.Driver");		
		Connection conn = DriverManager.getConnection(url, user, password);
		conn.setAutoCommit(false);
	
		return conn;
	}
	
	public static void main(String[] args) throws Exception {

	
		// Get Connection source
		Connection sourceConn=getSourceConn();
		// Get Connection out
		
		Connection outConn=getDestinationConn();
		
		
		// Create tables if don't exist
		
		System.out.println("Creating tables if not present..");
		Statement createStatment=outConn.createStatement();
		
		createStatment.execute("CREATE TABLE IF NOT EXISTS "+CONTEXTS.TABLENAME+
				"("+CONTEXTS.ID+" varchar(125) NOT NULL, "
						+ CONTEXTS.LABEL+" varchar (256) NOT NULL,"
						+"CONSTRAINT "+CONTEXTS.TABLENAME+"_pKey PRIMARY KEY ("+CONTEXTS.ID+"))");
		
		createStatment.execute("CREATE TABLE IF NOT EXISTS "+DIMENSIONS.TABLENAME+
				"("+DIMENSIONS.ID+" varchar(125) NOT NULL, "
						+ DIMENSIONS.LABEL+" varchar (256) NOT NULL,"
						+ DIMENSIONS.GROUP+" varchar (256) NOT NULL,"
						+ DIMENSIONS.AGGREGATED_MEASURE+" varchar (256) DEFAULT NULL,"
						+"CONSTRAINT "+DIMENSIONS.TABLENAME+"_pKey PRIMARY KEY ("+DIMENSIONS.ID+"))");
		
		createStatment.execute("CREATE TABLE IF NOT EXISTS "+Measure.TABLENAME+
				"("+Measure.MEASURE+" bigint NOT NULL, "
						+ Measure.TIME+" timestamp with time zone NOT NULL,"
						+ Measure.CONTEXT+" varchar (125) NOT NULL,"
						+ Measure.DIMENSION+" varchar (125) NOT NULL,"						
						+"CONSTRAINT "+Measure.TABLENAME+"_pKey PRIMARY KEY ("+Measure.TIME+","+Measure.CONTEXT+","+Measure.DIMENSION+"),"
						+"CONSTRAINT context_foreignKey FOREIGN KEY ("+Measure.CONTEXT+") REFERENCES "+CONTEXTS.TABLENAME+"("+CONTEXTS.ID+"),"
						+"CONSTRAINT dimension_foreignKey FOREIGN KEY ("+Measure.DIMENSION+") REFERENCES "+DIMENSIONS.TABLENAME+"("+DIMENSIONS.ID+"))");
		
				
		System.out.println("Querying for existing measures...");
		
		Statement sourceStmt=sourceConn.createStatement();
		ResultSet sourceRS=sourceStmt.executeQuery("Select monthly_measure.id as id, monthly_measure.measure as measure, monthly_measure.day as day,context.dname as context, context.name as context_label, measure_type.name as dimension"
				+ " from monthly_measure inner join context on monthly_measure.context_id=context.id "
				+ " inner join measure_type on monthly_measure.measure_type_id=measure_type.id where context.dname is not null AND monthly_measure.measure>0 order by id");
		
		// LOAD PRE EXISTING CONTEXTS
		HashMap<String,String> insertedContexts=new HashMap<>();
		Statement outStmt=outConn.createStatement();
		ResultSet outRS=outStmt.executeQuery("Select * from "+CONTEXTS.TABLENAME);
		while(outRS.next()){
			insertedContexts.put(outRS.getString(CONTEXTS.ID), outRS.getString(CONTEXTS.LABEL));
		}
		PreparedStatement contextPs=outConn.prepareStatement("Insert into "+CONTEXTS.TABLENAME+" ("+CONTEXTS.ID+", "+CONTEXTS.LABEL+") values (?,?)");
		
		HashMap<String,String> insertedDimensions=new HashMap<>();
		outRS=outStmt.executeQuery("Select * from "+DIMENSIONS.TABLENAME);
		while(outRS.next()){
			insertedDimensions.put(outRS.getString(DIMENSIONS.ID), outRS.getString(DIMENSIONS.LABEL));
		}
		PreparedStatement dimensionsPs=outConn.prepareStatement("Insert into "+DIMENSIONS.TABLENAME+" ("+DIMENSIONS.ID+", "+DIMENSIONS.LABEL+","+DIMENSIONS.GROUP+") values (?,?,?)");
		
		
		PreparedStatement measurePs=outConn.prepareStatement("Insert into "+Measure.TABLENAME+" ("+Measure.CONTEXT+","+Measure.DIMENSION+","+Measure.TIME+","+Measure.MEASURE+") values(?,?,?,?)"
				+" ON CONFLICT ON CONSTRAINT "+Measure.TABLENAME+"_pKey DO UPDATE SET "+Measure.MEASURE+"=?"); 
		
		long counter=0l;
		
		System.out.println("Importing....");
		
		long startime=System.currentTimeMillis();
		
		while(sourceRS.next()){
			try {
			Long measure = sourceRS.getLong("measure");
			String context = sourceRS.getString("context");
			String contextLabel= sourceRS.getString("context_label");
			String dimension = sourceRS.getString("dimension");
			String dimensionLabel= dimension;
//			Date day=sourceRS.getDate("day");
			Timestamp instant=new Timestamp(sourceRS.getDate("day").getTime());
			
			
			
			if(!insertedContexts.containsKey(context)){
				// register context
				contextPs.setString(1, context);
				contextPs.setString(2, contextLabel);
				if(contextPs.executeUpdate()==0) throw new Exception("No inserted context");
				insertedContexts.put(context,contextLabel);
			}
			
			if(!insertedDimensions.containsKey(dimension)){
				//register dimension
				dimensionsPs.setString(1, dimension);
				dimensionsPs.setString(2, dimensionLabel);
				dimensionsPs.setString(3, dimensionLabel);
				if(dimensionsPs.executeUpdate()==0) throw new Exception("No inserted dimension");
				insertedDimensions.put(dimension,dimensionLabel);
			}
			
			// Insert measure
			
			measurePs.setString(1, context);
			measurePs.setString(2, dimension);
			measurePs.setTimestamp(3, instant);
			measurePs.setLong(4, measure);
			measurePs.setLong(5, measure);
			if(measurePs.executeUpdate()==0) throw new Exception("No inserted Measure");
			
			counter++;
			if(counter%1000==0)
				System.out.println(String.format("Inserted %1$s rows [%2$s dims, %3$s contexts]", counter, insertedDimensions.size(),insertedContexts.size()));
			
			}catch(PSQLException e) {
				System.out.println("Problematic Row ID "+sourceRS.getLong("id"));
				throw e;
			}
		}
		
		System.out.println("Committing..");
		
		outConn.commit();
		System.out.println("Done importing "+counter+" rows in "+(System.currentTimeMillis()-startime)+" ms");
	}

}
