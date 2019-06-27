package org.gcube.application.perform.service.engine.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.gcube.application.perform.service.LocalConfiguration;
import org.gcube.application.perform.service.engine.DataBaseManager;
import org.gcube.application.perform.service.engine.PerformanceManager;
import org.gcube.application.perform.service.engine.dm.DMException;
import org.gcube.application.perform.service.engine.dm.DMUtils;
import org.gcube.application.perform.service.engine.model.CSVExportRequest;
import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.DBField.Batch;
import org.gcube.application.perform.service.engine.model.DBField.Farm;
import org.gcube.application.perform.service.engine.model.DBField.ImportRoutine;
import org.gcube.application.perform.service.engine.model.DBQueryDescriptor;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.model.InvalidRequestException;
import org.gcube.application.perform.service.engine.model.importer.AnalysisType;
import org.gcube.application.perform.service.engine.model.importer.ImportRoutineDescriptor;
import org.gcube.application.perform.service.engine.model.importer.ImportedTable;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceManagerImpl implements PerformanceManager{


	private static final Logger log= LoggerFactory.getLogger(PerformanceManagerImpl.class);


	private static Map<AnalysisType,Set<ImportedTable>> analysisConfiguration=new HashMap<>();

	public static Map<AnalysisType, Set<ImportedTable>> getAnalysisConfiguration() {
		return analysisConfiguration;
	}

	@Override
	public Map<String, String> generateCSV(CSVExportRequest request) throws SQLException, InvalidRequestException, InternalException, IOException {
		log.trace("Serving {} ",request);
		HashMap<String,String> toReturn=new HashMap<>();
		Set<ImportedTable> tables=getAnalysisSet(request.getType());
		log.debug("Found {} tables in configuration",tables.size());
		for(ImportedTable t:tables) {
			SchemaDefinition schema=t.getSchema();
			if(schema.getAnalysisEnabled()) {
				log.debug("Exporting {} : {} ",schema.getRelatedDescription(),t.getTableName());			
				toReturn.putAll(t.exportCSV(request));				
			}
		}
		return toReturn;
	}

	
	@Override
	public Map<String, String> getStatistics(AnalysisType type)
			throws SQLException, InvalidRequestException, InternalException, IOException {
		log.trace("Getting statistics for {} ",type);
		HashMap<String,String> toReturn=new HashMap<>();
		Set<ImportedTable> tables=getAnalysisSet(type);
		log.debug("Found {} tables in configuration",tables.size());
		for(ImportedTable t:tables) {			
			
				log.debug("Exporting {} : {} ",t.getSchema().getRelatedDescription(),t.getTableName());			
				toReturn.putAll(t.exportStatistics());				
			
		}
		return toReturn;
	}
	
	
	@Override
	public void loadOutputData(ImportRoutineDescriptor desc) throws SQLException, InvalidRequestException, InternalException, IOException, DMException{
		log.info("Importing output for {} ",desc);
		ComputationId computation=DMUtils.getComputation(desc);
		Map<String,String> outputs=DMUtils.getOutputFiles(computation);
		Connection conn=DataBaseManager.get().getConnection();
		try {
			for(Entry<String,String> entry:outputs.entrySet()) {
				parse(entry.getValue(),entry.getKey(),desc,conn);
			}
			log.debug("IMPORTED ALL FILES for {}, gonna clean previous routines output. ",desc);

			removeOlderEquivalents(desc, conn);
			log.debug("COMMITTING...");
			conn.commit();
			log.info("Successfully imported data for {} ",desc);
		}finally {
			conn.close();
		}		
	}


	public static void initDatabase() throws SQLException, InternalException {
		DataBaseManager db=DataBaseManager.get();
		Connection conn=db.getConnection();
		Statement stmt=conn.createStatement();
		log.info("Checking / updateing base schema..");
		
		// CREATE BATCHES
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+Batch.TABLE+" ("
				+ Batch.BATCH_ID+" bigserial NOT NULL,"
				+ Batch.UUID+" uuid NOT NULL,"
				+ Batch.FARM_ID+" bigint NOT NULL,"
				+ Batch.BATCH_TYPE+" varchar(100),"
				+ Batch.BATCH_NAME+" text,"
				+ "PRIMARY KEY ("+Batch.BATCH_ID+"),"
				+ "FOREIGN KEY ("+Batch.FARM_ID+") REFERENCES farms(farmid))"				
		);
		
		// CREATE IMPORT ROUTINE
		
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+ImportRoutine.TABLE+" ("
				+ ImportRoutine.ID+" bigserial NOT NULL,"
				+ ImportRoutine.FARM_ID+" bigint NOT NULL,"
				+ ImportRoutine.BATCH_TYPE+" varchar(100) NOT NULL,"
				+ ImportRoutine.SOURCE_URL+" text,"
				+ ImportRoutine.SOURCE_VERSION+" text,"
				+ ImportRoutine.START+" timestamp with time zone,"
				+ ImportRoutine.END+" timestamp with time zone,"
				+ ImportRoutine.STATUS+" varchar(20),"
				+ ImportRoutine.CALLER+" text,"
				+ ImportRoutine.COMPUTATION_ID+" text,"
				+ ImportRoutine.COMPUTATION_URL+" text,"
				+ ImportRoutine.COMPUTATION_OPID+" text,"
				+ ImportRoutine.COMPUTATION_OPNAME+" text,"
				+ ImportRoutine.COMPUTATION_REQ+" text,"
				+ ImportRoutine.LOCK+" varchar(200),"
				+ "primary key ("+ImportRoutine.ID+"))");
		
		
		stmt.executeUpdate("CREATE OR REPLACE VIEW "+Farm.TABLE+" AS ("
				+ "Select f.farmid as "+Farm.FARM_ID+", f.uuid as "+Farm.UUID+", c.companyid as "+Farm.COMPANY_ID+", "
				+ "c.uuid as "+Farm.COMPANY_UUID+", a.associationid as "+Farm.ASSOCIATION_ID+", a.uuid as "+Farm.ASSOCIATION_UUID+", "
				+ "c.name as "+Farm.COMPANY_LABEL+", a.name as "+Farm.ASSOCIATION_LABEL+", f.name as "+Farm.FARM_LABEL+" "
				+ "FROM farms as f INNER JOIN companies as c ON f.companyid=c.companyid "
				+ "INNER JOIN associations as a ON c.associationid = a. associationid)");
		
		
		
		
		for(Entry<AnalysisType,Set<ImportedTable>> entry:getAnalysisConfiguration().entrySet()) {
			for(ImportedTable t:entry.getValue()) {
				String createStmt=t.createStatement();
				log.debug("Creating Table with stmt {} ",createStmt);
				stmt.execute(createStmt);
			}
		}
		
		if(Boolean.parseBoolean(LocalConfiguration.getProperty(LocalConfiguration.COMMIT_SCHEMA)))
			conn.commit();
	}


	public static void importSchema(SchemaDefinition schema,String csvBasePath) throws IOException, SQLException, InternalException {
		log.info("Loading schema {} ",schema);

		String actualCSVPath=csvBasePath+"/"+schema.getCsvPath();

		log.debug("CSV path : {} ",actualCSVPath);

		ArrayList<DBField> csvFieldsDefinition=getCSVFieldsDefinition(actualCSVPath,schema);

		AnalysisType analysisType=schema.getRelatedAnalysis();

		String tablename=(analysisType.getId()+"_"+schema.getRelatedDescription()).toLowerCase().replaceAll(" ", "_");

		

		ImportedTable table=new ImportedTable(
				tablename, schema, 
				csvFieldsDefinition);

		if(!analysisConfiguration.containsKey(analysisType))
			analysisConfiguration.put(schema.getRelatedAnalysis(), new HashSet<>());
		analysisConfiguration.get(schema.getRelatedAnalysis()).add(table);

	}



	static Set<ImportedTable> getAnalysisSet(AnalysisType type) throws InvalidRequestException{		
		if(!analysisConfiguration.containsKey(type))
			throw new InvalidRequestException("Analysis Configuration not found for "+type);
		return analysisConfiguration.get(type);
	}

	private static final void removeOlderEquivalents(ImportRoutineDescriptor last,Connection conn) throws SQLException, InvalidRequestException {
		log.debug("Removing imports replaced by {} ",last);

		DBQueryDescriptor desc=new DBQueryDescriptor().
				add(DBField.ImportRoutine.fields.get(ImportRoutine.FARM_ID),last.getFarmId()).
				add(DBField.ImportRoutine.fields.get(ImportRoutine.BATCH_TYPE),last.getBatch_type()).
				add(DBField.ImportRoutine.fields.get(ImportRoutine.SOURCE_URL),last.getSourceUrl()).
				add(DBField.ImportRoutine.fields.get(ImportRoutine.ID),last.getId()).
				add(DBField.ImportRoutine.fields.get(ImportRoutine.END),new Timestamp(Instant.now().toEpochMilli()));


		ResultSet rsEquivalents=Queries.GET_OLDER_EQUIVALENT_IMPORT_ROUTINE.get(conn, desc).executeQuery();

		while(rsEquivalents.next()) {			
			ImportRoutineDescriptor older=Queries.rowToDescriptor(rsEquivalents);
			log.debug("Removing outputs from {} ",older);
			AnalysisType type=new AnalysisType(older);
			for(ImportedTable table:analysisConfiguration.get(type)) {
				log.debug("Cleaning {} of {} outputs",table.getTableName(),older);
				table.cleanByImportRoutine(older,conn);
			}
		}


	}


	private static final long parse(String path, String description, ImportRoutineDescriptor routine, Connection conn) throws IOException, SQLException, InvalidRequestException {
		URL csvUrl = new URL(path);
		BufferedReader in = new BufferedReader(new InputStreamReader(csvUrl.openStream()));

		CSVParser parser= CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

		AnalysisType type=new AnalysisType(routine);

		try {
			log.debug("Parsing file {} : {} ",description,path);
			// Extract CSV Schema
			ArrayList<String> csvSchema=new ArrayList<String>();
			for(Entry<String,Integer> entry : parser.getHeaderMap().entrySet()) {
				csvSchema.add(entry.getValue(), entry.getKey());
			}

			log.debug("CSV Schema is {} ",csvSchema);

			long counter=0l;
			//Get the right table
			for(ImportedTable table:analysisConfiguration.get(type)) {
				if(table.matchesSchema(csvSchema)) {
					log.debug("Matching table is {} ",table.getTableName());
					Query query=table.getInsertQuery();
					PreparedStatement psInsert=query.prepare(conn);			
					log.debug("Reading csvLines");
					for(CSVRecord record:parser) {
						DBQueryDescriptor desc=table.getSetRow(record.toMap(), routine.getId());
						query.fill(psInsert, desc);
						counter+=psInsert.executeUpdate();
					}			
					log.debug("Inserted {} lines into {} for routine {} [FARM ID {}]",counter,table.getTableName(),routine.getId(),routine.getFarmId());
				}
			}
			return counter;
		}finally {
			parser.close();
			in.close();
		}

	}	

	// ************************** SCHEMA PARSING 
	private static final String FLOAT_REGEX="\\d*\\.\\d*";
	private static final String INTEGER_REGEX="\\d*";


	private static ArrayList<DBField> getCSVFieldsDefinition(String csvFile,SchemaDefinition schema) throws IOException{

		Reader in = null;
		CSVParser parser= null;
		try {
			HashSet<String> deanonimizationLabels=new HashSet<>();
			if(schema.getAssociationUUIDField()!=null)
				deanonimizationLabels.add(schema.getAssociationUUIDField());
			if(schema.getFarmUUIDField()!=null)
				deanonimizationLabels.add(schema.getFarmUUIDField());
			if(schema.getBatchUUIDField()!=null)
				deanonimizationLabels.add(schema.getBatchUUIDField());
			if(schema.getCompanyUUIDField()!=null)
				deanonimizationLabels.add(schema.getCompanyUUIDField());


			in=new FileReader(csvFile);		
			parser=CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
			Map<String,Integer> headers =parser.getHeaderMap();
			ArrayList<DBField> toReturn = new ArrayList<>();


			
			headers.forEach((key,value) ->{
				int type=Integer.MIN_VALUE;
				if(deanonimizationLabels.contains(key)) type=Types.VARCHAR;
				toReturn.add(new DBField(type,key));
			});
			
			
			
			parser.forEach(record ->{
				toReturn.forEach(field->{
					if(field.getType()==Types.VARCHAR) {
						// skip, field already considered as text
					}else {						
						String value=record.get(field.getFieldName());
						if(value.matches(FLOAT_REGEX)||value.matches(INTEGER_REGEX)) field.setType(Types.REAL);
						else field.setType(Types.VARCHAR);
					}
				});								
			});
			

			return toReturn;

		}finally{
			if(in!=null) in.close();
			if(parser!=null) parser.close();
		}
	}


}
