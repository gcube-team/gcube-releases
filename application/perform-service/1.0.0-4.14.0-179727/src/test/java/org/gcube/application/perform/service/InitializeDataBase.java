package org.gcube.application.perform.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.gcube.application.perform.service.engine.DataBaseManager;
import org.gcube.application.perform.service.engine.model.DBField.Batch;
import org.gcube.application.perform.service.engine.model.DBField.Farm;
import org.gcube.application.perform.service.engine.model.DBField.ImportRoutine;
import org.gcube.application.perform.service.engine.model.InternalException;

public class InitializeDataBase {

	public static void main(String[] args) throws SQLException, InternalException, IOException {
//		TokenSetter.set("/gcube/preprod/preVRE");
		TokenSetter.set("/d4science.research-infrastructures.eu/FARM/PerformFISH-KPIs");
		LocalConfiguration.init(Paths.get("src/main/webapp/WEB-INF/config.properties").toUri().toURL());
	
		
		DataBaseManager db=DataBaseManager.get();
		Connection conn=db.getConnection();
		
//		Statement stmt=conn.createStatement();
//		
//		// CREATE BATCHES
//		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+Batch.TABLE+" ("
//				+ Batch.BATCH_ID+" bigserial NOT NULL,"
//				+ Batch.UUID+" uuid NOT NULL,"
//				+ Batch.FARM_ID+" bigint NOT NULL,"
//				+ Batch.BATCH_TYPE+" varchar(100),"
//				+ Batch.BATCH_NAME+" text,"
//				+ "PRIMARY KEY ("+Batch.BATCH_ID+"),"
//				+ "FOREIGN KEY ("+Batch.FARM_ID+") REFERENCES farms(farmid))"				
//		);
//		
//		// CREATE IMPORT ROUTINE
//		
//		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+ImportRoutine.TABLE+" ("
//				+ ImportRoutine.ID+" bigserial NOT NULL,"
//				+ ImportRoutine.FARM_ID+" bigint NOT NULL,"
//				+ ImportRoutine.BATCH_TYPE+" varchar(100) NOT NULL,"
//				+ ImportRoutine.SOURCE_URL+" text,"
//				+ ImportRoutine.SOURCE_VERSION+" text,"
//				+ ImportRoutine.START+" timestamp with time zone,"
//				+ ImportRoutine.END+" timestamp with time zone,"
//				+ ImportRoutine.STATUS+" varchar(20),"
//				+ ImportRoutine.CALLER+" text,"
//				+ ImportRoutine.COMPUTATION_ID+" text,"
//				+ ImportRoutine.COMPUTATION_URL+" text,"
//				+ ImportRoutine.COMPUTATION_OPID+" text,"
//				+ ImportRoutine.COMPUTATION_OPNAME+" text,"
//				+ ImportRoutine.COMPUTATION_REQ+" text,"
//				+ ImportRoutine.LOCK+" varchar(200),"
//				+ "primary key ("+ImportRoutine.ID+"))");
//		
//		
//		stmt.executeUpdate("CREATE OR REPLACE VIEW "+Farm.TABLE+" AS ("
//				+ "Select f.farmid as "+Farm.FARM_ID+", f.uuid as "+Farm.UUID+", c.companyid as "+Farm.COMPANY_ID+", "
//				+ "c.uuid as "+Farm.COMPANY_UUID+", a.associationid as "+Farm.ASSOCIATION_ID+", a.uuid as "+Farm.ASSOCIATION_UUID+", "
//				+ "c.name as "+Farm.COMPANY_LABEL+", a.name as "+Farm.ASSOCIATION_LABEL+", f.name as "+Farm.FARM_LABEL+" "
//				+ "FROM farms as f INNER JOIN companies as c ON f.companyid=c.companyid "
//				+ "INNER JOIN associations as a ON c.associationid = a. associationid)");
//		
		
		// CREATE FARM VIEW
//		try {
//		ResultSet rs=stmt.executeQuery("Select * from "+Farm.TABLE);
//		if(rs.next()) {
//			// table already present
//		}
//		}catch(SQLException e) {
//			// Expected error on table not found, trying to create it 
//			
//		}
		
//		stmt.executeQuery("CREATE VIEW suca as SELECT 1");
		
		// CREATE IMPORTED TABLES
		
//		//AnagraphicGrow
//		stmt.executeUpdate(new TableCreator("/home/fabio/Documents/work files/Perform/Grow_out_Aggregated_Batch_Data_Entry_KPI_aggregated.csv",
//				ImportedData.AnagraphicGrow.TABLE,ImportedData.AnagraphicGrow.FARM_ID,ImportedData.AnagraphicGrow.ROUTINE_ID).getCreateStatement());
//		
//		stmt.executeUpdate(new TableCreator("/home/fabio/Documents/work files/Perform/Grow_out_Aggregated_Batch_Data_Entry_KPI.csv",
//				ImportedData.BatchesTable.TABLE,ImportedData.BatchesTable.FARM_ID,ImportedData.BatchesTable.ROUTINE_ID).getCreateStatement());
//		
//		stmt.executeUpdate(new TableCreator("/home/fabio/Documents/work files/Perform/KPIs_annual (2).csv",
//				ImportedData.AnnualTable.TABLE,ImportedData.AnnualTable.FARM_ID,ImportedData.AnnualTable.ROUTINE_ID).getCreateStatement());
//		
//		stmt.executeUpdate(new TableCreator("/home/fabio/Documents/work files/Perform/KPIs_antibiotics.csv",
//				ImportedData.AntibioticsTable.TABLE,ImportedData.AntibioticsTable.FARM_ID,ImportedData.AntibioticsTable.ROUTINE_ID).getCreateStatement());
//		
//		stmt.executeUpdate(new TableCreator("/home/fabio/Documents/work files/Perform/KPIs_antiparasitic.csv",
//				ImportedData.AntiparasiticTable.TABLE,ImportedData.AntiparasiticTable.FARM_ID,ImportedData.AntiparasiticTable.ROUTINE_ID).getCreateStatement());
//		
//		stmt.executeUpdate(new TableCreator("/home/fabio/Documents/work files/Perform/KPIs_lethalincidents.csv",
//				ImportedData.LethalIncidentsTable.TABLE,ImportedData.LethalIncidentsTable.FARM_ID,ImportedData.LethalIncidentsTable.ROUTINE_ID).getCreateStatement());
//		
		
		// CREATE ANAGRAPHIC OUTPUT
		
		
		
		// CREATE PUBLIC OUTPUT VIEW
		
		
		
		
		conn.commit();
	}

	
	
	
}
