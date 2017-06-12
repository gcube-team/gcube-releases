package org.gcube.application.aquamaps.aquamapsservice.impl.monitor;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HSQLDB {

	final static Logger logger= LoggerFactory.getLogger(HSQLDB.class);

	private static Connection getConnection()throws Exception{
		try {

			Class.forName("org.hsqldb.jdbcDriver" );
		} catch (Exception e) {
			System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
			logger.error("",e);
			throw e;
		}
		String connectionString="jdbc:hsqldb:file:"+ServiceContext.getContext().getPersistenceRoot()+File.separator+"ReportDB";
		try{
			logger.trace("Connecting to reportDB : "+connectionString);			
			return DriverManager.getConnection(connectionString+";ifexists=true", "SA", "");
		}catch(Exception e){
			logger.trace("Error, trying to create DB..");
			try{
				Connection c=DriverManager.getConnection(connectionString, "SA", "");
				logger.trace("Creating tables..");
				Statement stmt=c.createStatement();
				stmt.executeUpdate("CREATE CACHED TABLE ITEMS (registeredValue BIGINT, valueName VARCHAR(20), time TIMESTAMP, PRIMARY KEY(time))");
				logger.trace("Tables created..");
				stmt.close();
				c.commit();
				c.close();
				return DriverManager.getConnection(connectionString+";ifexists=true", "SA", "");
			}catch(Exception e1){
				logger.error("Unable to init DB",e);
				throw e;
			}
		}
	}

	public static void insertReportItem(String valueName, long value)throws Exception{
		Connection c = null;
		PreparedStatement ps=null;
		try{
			c=getConnection();
			ps= c.prepareStatement("INSERT INTO ITEMS (registeredValue, valueName, time) values (?,?,?)");
			ps.setLong(1, value);
			ps.setString(2,valueName);
			ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			ps.executeUpdate();
		}catch(Exception e){
//			logger.error("", e);
			throw e;
		}finally{
			if(ps!=null&&!ps.isClosed()) ps.close();
			if(c!=null&&!c.isClosed()) c.close();
		}
	}

	public static ReportItem getReport(String valueName)throws Exception{
		Connection c = null;
		PreparedStatement ps=null;
		try{
			ReportItem toReturn=new ReportItem();
			c= getConnection();
			ps=c.prepareStatement("Select count(*) from ITEMS where valueName=?");
			ps.setString(1, valueName);
			ResultSet rs=ps.executeQuery();
			rs.next();
			toReturn.setOvercomesTotal(rs.getLong(1));
			//LAST 10 hours
			ps=c.prepareStatement("Select count(*) from ITEMS where valueName=? AND (HOUR(time)-HOUR(now())<?)");
			ps.setString(1,valueName);
			ps.setInt(2,11);
			rs=ps.executeQuery();
			rs.next();
			toReturn.setOvercomesInLast10Hours(rs.getLong(1));
			ps.setInt(2,25);
			rs=ps.executeQuery();
			rs.next();
			toReturn.setOvercomesInLast24Hours(rs.getLong(1));
			rs.close();
			return toReturn;
		}catch(Exception e){
//			logger.error("", e);
			throw e;
		}finally{
			if(ps!=null&&!ps.isClosed()) ps.close();
			if(c!=null&&!c.isClosed()) c.close();
		}
	}

	
	
}
