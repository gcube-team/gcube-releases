package org.gcube.application.perform.service.engine.model.importer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.gcube.application.perform.service.engine.DataBaseManager;
import org.gcube.application.perform.service.engine.impl.ExportCSVQuery;
import org.gcube.application.perform.service.engine.impl.Queries;
import org.gcube.application.perform.service.engine.impl.Query;
import org.gcube.application.perform.service.engine.impl.SchemaDefinition;
import org.gcube.application.perform.service.engine.model.CSVExportRequest;
import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.DBField.ImportRoutine;
import org.gcube.application.perform.service.engine.model.DBQueryDescriptor;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.model.InvalidRequestException;
import org.gcube.application.perform.service.engine.utils.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportedTable {


	@Override
	public String toString() {
		return "ImportedTable [tablename=" + tablename + "]";
	}

	private static final Logger log= LoggerFactory.getLogger(ImportedTable.class);

	/**
	 * CSV FILE labels -> DBField 
	 */

	private Map<String,DBField> labels;

	private ArrayList<String> csvFields; // Fields actually expected in csv

	private String tablename;
	//	private DBField routineIdField;	

	//	private String farmUUIDField;
	//	private String associationUUIDField;
	//	private String companyUUIDField;
	//	private String batchUUIDField;
	//	private Boolean analysisEnabled;

	private Query insertQuery;

	private SchemaDefinition schema; 


	public ImportedTable(String tablename, SchemaDefinition schema, ArrayList<DBField> csvFieldsDefinition) throws InternalException {
		super();
		this.schema=schema;
		this.tablename=tablename;
		// init 

		// check schema

		csvFields=new ArrayList<>();
		labels=new HashMap<>();

		for(DBField field:csvFieldsDefinition) {
			String escaped=escapeString(field.getFieldName());
			csvFields.add(field.getFieldName());
			labels.put(field.getFieldName(), new DBField(field.getType(),escaped));
		}

		check(schema.getAssociationUUIDField());
		check(schema.getBatchUUIDField());
		check(schema.getCompanyUUIDField());
		check(schema.getFarmUUIDField());

		check(schema.getAreaField());
		check(schema.getPeriodField());
		check(schema.getQuarterField());
		check(schema.getSpeciesField());
		for(String f:schema.getToReportFields())
			check(f);

		insertQuery=prepareInsertionQuery();

	}

	private void check(String field) throws InternalException {
		if(field!=null)
			if(!labels.containsKey(field)) throw new InternalException("Incoherent schema definition for table "+tablename+". Field "+field+" not found in csv.");
	}

	private DBField getRoutineIdField() {
		return new DBField(Types.BIGINT,schema.getRoutineIdFieldName());
	}


	private Query prepareInsertionQuery() {

		StringBuilder fieldList=new StringBuilder();
		StringBuilder valueString=new StringBuilder();
		ArrayList<DBField> queryFields=new ArrayList<>();


		for(DBField f:labels.values()) {
			queryFields.add(f);
			fieldList.append(f.getFieldName()+",");
			valueString.append("?,");
		}

		queryFields.add(getRoutineIdField());


		String insertSQL= String.format("INSERT INTO %1$s (%2$s) VALUES (%3$s)", tablename,
				fieldList+getRoutineIdField().getFieldName(),valueString+"?");


		return new Query(insertSQL, queryFields.toArray(new DBField[queryFields.size()]));
	}

	public String createStatement() {
		StringBuilder fieldDefinitions=new StringBuilder();

		for(DBField f:labels.values()) {
			String type="text";
			switch(f.getType()) {
			case Types.BIGINT : type="bigint";
			break;
			case Types.REAL : type="real";
			break;
			}

			fieldDefinitions.append(f.getFieldName()+" "+type+",");

		}

		String standardDefinitions=
				String.format( "%1$s bigint,"						
						+ "FOREIGN KEY (%1$s) REFERENCES "+ImportRoutine.TABLE+"("+ImportRoutine.ID+")",getRoutineIdField().getFieldName());

		return String.format("CREATE TABLE IF NOT EXISTS %1$s (%2$s, %3$s)",
				tablename,fieldDefinitions.substring(0,fieldDefinitions.lastIndexOf(",")),standardDefinitions);
	}


	/**
	 * Checks if passed set of labels is 
	 * 
	 * @param toMatchSchema
	 * @return
	 */
	public boolean matchesSchema(ArrayList<String> toMatchSchema) {		
		return csvFields.equals(toMatchSchema);		
	}


	public DBQueryDescriptor getSetRow(Map<String,String> csvRow, Long routineId) {
		DBQueryDescriptor desc=new DBQueryDescriptor();

		for(Entry<String,String> csvField:csvRow.entrySet()) {
			DBField toSetField=labels.get(csvField.getKey());

			Object value=csvField.getValue();	
			if(csvField.getValue()==null
					||csvField.getValue().isEmpty()) value=null;
			
			if(value!=null)
				try {
					switch(toSetField.getType()) {
					case Types.BIGINT : value=Long.parseLong((String) value);
					break;
					case Types.REAL : value=Double.parseDouble((String) value);
					break;
					}
				}catch(NumberFormatException e) {
					log.error("Unable to parse field {} value was {} ",csvField.getKey(),csvField.getValue());
					throw e;
				}
			desc.add(toSetField, value);
		}

		desc.add(getRoutineIdField(), routineId);
		return desc;
	}

	public Query getInsertQuery() {
		return insertQuery;
	}
	public String getTableName() {
		return tablename;
	}

	public int cleanByImportRoutine(ImportRoutineDescriptor toClean,Connection conn) throws InvalidRequestException, SQLException {
		DBField routineField=getRoutineIdField();
		Query cleanQuery=new Query(String.format("DELETE FROM %1$s WHERE %2$s =?", this.tablename,routineField.getFieldName()),
				new DBField[] {routineField});

		return cleanQuery.get(conn, new DBQueryDescriptor(routineField,toClean.getId())).executeUpdate();	

	}

	public Map<String,String> exportStatistics() throws SQLException, InternalException{
		log.debug("Exporting statistics from {} ",this);
		Connection conn= DataBaseManager.get().getConnection();
		try {
			// for statistic operations
			// get statistics query
			// export as CSV
			throw new RuntimeException("Not Yet implemented");
		}finally {
			conn.close();

		}	
	}


	public Map<String,String> exportCSV(CSVExportRequest request) throws InvalidRequestException, SQLException, InternalException, IOException {


		log.debug("Exporting {} from {} ",request, this);

		Connection conn= DataBaseManager.get().getConnection();

		try {
			CSVRecordConverter queryConverter=new CSVRecordConverter(labels);
			ExportCSVQuery exportQuery=new ExportCSVQuery("",null,request,schema,labels,csvFields);
			exportQuery.setTablename(tablename);

			Map<String,String> farmMapping=new HashMap<>();
			Map<String,String> companyMapping=new HashMap<>();
			Map<String,String> associationMapping=new HashMap<>();
			Map<String,String> batchMapping=new HashMap<>();

			PreparedStatement psFarm=Queries.GET_FARM_BY_ID.prepare(conn);
			PreparedStatement psBatch=Queries.GET_BATCH_BY_FARM_ID.prepare(conn);


			// GET Labels by Farm Id
			for(Long farmid:request.getFarmIds()) {
				ResultSet rsFarm=Queries.GET_FARM_BY_ID.fill(psFarm, new DBQueryDescriptor(DBField.Farm.fields.get(DBField.Farm.FARM_ID),farmid)).executeQuery();
				if(! rsFarm.next())
					log.warn("Unable to Find farmID "+farmid);
				else {
					farmMapping.put(rsFarm.getString(DBField.Farm.UUID), rsFarm.getString(DBField.Farm.FARM_LABEL));
					companyMapping.put(rsFarm.getString(DBField.Farm.COMPANY_UUID),rsFarm.getString(DBField.Farm.COMPANY_LABEL));
					associationMapping.put(rsFarm.getString(DBField.Farm.ASSOCIATION_UUID), rsFarm.getString(DBField.Farm.ASSOCIATION_LABEL));
					ResultSet rsBatch=Queries.GET_BATCH_BY_FARM_ID.fill(psBatch, new DBQueryDescriptor(DBField.Batch.fields.get(DBField.Batch.FARM_ID),farmid)).executeQuery();
					while(rsBatch.next()) 
						batchMapping.put(rsBatch.getString(DBField.Batch.UUID), rsBatch.getString(DBField.Batch.BATCH_NAME));

				}
			}

			// Set mappings for query and csv printer

			if(schema.getAssociationUUIDField()!=null) {
				log.debug("Setting Association Mapping : "+associationMapping);
				exportQuery.setMapping(schema.getAssociationUUIDField(), associationMapping);
				queryConverter.setMapping(schema.getAssociationUUIDField(), associationMapping);
			}
			if(schema.getCompanyUUIDField()!=null) {
				log.debug("Setting Company Mapping : "+companyMapping);
				exportQuery.setMapping(schema.getCompanyUUIDField(), companyMapping);
				queryConverter.setMapping(schema.getCompanyUUIDField(), companyMapping);
			}
			if(schema.getFarmUUIDField()!=null) {
				log.debug("Setting Farm Mapping : "+farmMapping);
				exportQuery.setMapping(schema.getFarmUUIDField(), farmMapping);
				queryConverter.setMapping(schema.getFarmUUIDField(), farmMapping);
			}
			if(schema.getBatchUUIDField()!=null) {
				log.debug("Setting Batch Mapping : "+batchMapping);
				exportQuery.setMapping(schema.getBatchUUIDField(), batchMapping);
				queryConverter.setMapping(schema.getBatchUUIDField(), batchMapping);
			}

			// Set mapping condition NB only farm supported at the moment
			if(schema.getFarmUUIDField()!=null)
				queryConverter.setCondition(schema.getFarmUUIDField(), farmMapping.keySet());



			log.trace("Performing actual query towards {} ",tablename);


			Map<String,String> toReturn=new HashMap<String,String>();

			String sqlExport=exportQuery.getQuery();
			log.debug("Query is {} ",sqlExport);
			Statement stmt=conn.createStatement();
			ResultSet csvRs=stmt.executeQuery(sqlExport);



			toReturn.put(this.schema.getRelatedDescription(), putIntoStorage(csvRs, 
					csvFields.toArray(new String[csvFields.size()]),queryConverter));



			if(schema.getToReportFields().size()>0) {
				ArrayList<String> toExtractCSVFields=schema.getToReportFields();

				queryConverter.reset();

				log.trace("Extracting {} from {} ",tablename);
				// Extract personal found values from same query
				String[] toExtractFields=new String[toExtractCSVFields.size()];


				for(String label:toExtractCSVFields) {
					String fieldName=labels.get(label).getFieldName();					
					toExtractFields[toExtractCSVFields.indexOf(label)]=fieldName;					
				}


				String sqlPersonal=exportQuery.getQueryForMappedFields(schema.getFarmUUIDField(),
						toExtractFields);
				log.debug("Query is {} ",sqlPersonal);
				csvRs=stmt.executeQuery(sqlPersonal);



				toReturn.put(this.schema.getRelatedDescription()+"_internal", putIntoStorage(csvRs, 
						schema.getToReportLabels().toArray(new String[schema.getToReportLabels().size()]),queryConverter));
			}


			return toReturn;
		}finally {
			conn.close();

		}		

	}


	public SchemaDefinition getSchema() {
		return schema;
	}

	int MAX_LENGTH=25;

	private String escapeString(String fieldname) {
		String toReturn=fieldname;
		if(toReturn.length()>MAX_LENGTH)
			toReturn=toReturn.substring(0, MAX_LENGTH);

		DBField clashing=new DBField(0,"\""+toReturn+"\"");
		int counter=1;
		while(labels.containsValue(clashing)) {
			clashing=new DBField(0,"\""+toReturn+"_"+counter+"\"");
			counter++;
		}

		return clashing.getFieldName();
	}


	private static final String putIntoStorage(ResultSet toExport,String[] headers, CSVRecordConverter converter) throws IOException, SQLException {
		CSVPrinter printer=null;
		File dataFile=null;
		try {
			dataFile=File.createTempFile("csv_out", ".csv");
			printer = CSVFormat.DEFAULT.withHeader(headers).print(new FileWriter(dataFile));
			while(toExport.next()) {				
				printer.printRecord(converter.convert(toExport));
			}			
			printer.flush();
			return StorageUtils.putOntoStorage(dataFile);
		}finally {
			if(printer!=null) {
				printer.close();
			}
			if(dataFile!=null) {
				Files.deleteIfExists(dataFile.toPath());
			}
		}
	}

}
