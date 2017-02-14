package org.gcube.data.analysis.tabulardata.operation.comet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.dbutils.DbUtils;
import org.fao.fi.comet.mapping.model.Mapping;
import org.fao.fi.comet.mapping.model.MappingData;
import org.fao.fi.comet.mapping.model.MappingDetail;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.CodeColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.harmonization.HarmonizationRule;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.HarmonizationRuleHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.comet.model.MappedRow;
import org.gcube.data.analysis.tabulardata.operation.comet.model.MappedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingParser {

	private static Logger logger = LoggerFactory.getLogger(MappingParser.class);
	
	public static enum MappingDirection{
		FORWARD,
		BACKWARD
	}
	
	public static class ParserConfiguration{
		private Table previousCodelistVersion;
		private Table currentCodelistVersion;
		private Column oldCodesColumn;
		private double scoreThreshold=1.0d;
		private boolean skipOnError=true;
		private MappingDirection direction=MappingDirection.BACKWARD; //Cotrix behaviour
		
		public ParserConfiguration(Table previousCodelistVersion,
				Table currentCodelistVersion,Column oldCodesColumn) {
			super();
			this.previousCodelistVersion = previousCodelistVersion;
			this.currentCodelistVersion = currentCodelistVersion;
			this.oldCodesColumn=oldCodesColumn;
		}

		public Column getOldCodesColumn() {
			return oldCodesColumn;
		}
		public Table getCurrentCodelistVersion() {
			return currentCodelistVersion;
		}
		public Table getPreviousCodelistVersion() {
			return previousCodelistVersion;
		}
		
		public void setDirection(MappingDirection direction) {
			this.direction = direction;
		}
		
		public MappingDirection getDirection() {
			return direction;
		}
		
		
		public boolean isSkipOnError() {
			return skipOnError;
		}
		
		/**
		 * @return the scoreThreshold
		 */
		public double getScoreThreshold() {
			return scoreThreshold;
		}
		/**
		 * @param scoreThreshold the scoreThreshold to set
		 */
		public void setScoreThreshold(double scoreThreshold) {
			this.scoreThreshold = scoreThreshold;
		}
		/**
		 * @param skipOnError the skipOnError to set
		 */
		public void setSkipOnError(boolean skipOnError) {
			this.skipOnError = skipOnError;
		}
	}

	private ParserConfiguration config;
	private SQLExpressionEvaluatorFactory evaluatorFactory;
	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	
	private Map<String,String> columnsMapping=new HashMap<>();
	
	public MappingParser(ParserConfiguration config,SQLExpressionEvaluatorFactory evaluatorFactory,CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) throws IOException, JAXBException {
		this.config=config;
		this.evaluatorFactory=evaluatorFactory;
		this.cubeManager=cubeManager;
		this.connectionProvider=connectionProvider;
		analyzeTableStructure();
	}

	
	
	
	
	private long foundMappingsCount=0;
	private long parsedRulesCount=0;
	
	private Table rulesTable=null;
	private Connection conn=null;
	private PreparedStatement loadRowFromCurrent=null;
	private PreparedStatement loadRowFromPrevious=null;
	private PreparedStatement insertRuleStmt=null;
	

	public void parse(String xmlFileId) throws Exception{
		XMLStreamReader xmler =null;
		try{
			if(rulesTable==null) rulesTable=HarmonizationRuleHelper.createTable(cubeManager);
			
			//TODO parse mapping by mapping instead of DOM for less memory usage
			logger.debug("Parsing file "+xmlFileId);
			XMLInputFactory xmlif = XMLInputFactory.newInstance();
			xmler= xmlif.createXMLStreamReader(ImportCodeListMappingFactory.getInputStreamById(xmlFileId));
			JAXBContext ctxUnmarshall = JAXBContext.newInstance(MappingData.class);
			MappingData deserialized = (MappingData)ctxUnmarshall.createUnmarshaller().unmarshal(xmler);
			Collection<Mapping> mappings=deserialized.getMappings();
			if(conn==null) conn=connectionProvider.getConnection();
			logger.debug("Parsed {} mappings",mappings.size());	
			foundMappingsCount=mappings.size();
			for(Mapping mapping:mappings){
				try{
					MappedRow sourceRow=loadRow((config.getDirection().equals(MappingDirection.FORWARD)), 
							mapping.getSource().getId().getElementId().toString());
					
					for(MappingDetail targetCandidate:mapping.getTargets()){
						if(targetCandidate.getScore()>=config.getScoreThreshold()){
							MappedRow targetRow=loadRow(config.getDirection().equals(MappingDirection.BACKWARD),
									targetCandidate.getTargetElement().getId().getElementId().toString());
							
							List<MappedValue> mappedValueList=config.getDirection().equals(MappingDirection.FORWARD)?
									getChangeSet(sourceRow,targetRow):getChangeSet(targetRow, sourceRow);
									
							for(MappedValue mapped:mappedValueList){
								HarmonizationRule rule=getRule(mapped);
								storeRule(rule);
								System.out.println(rule);
								parsedRulesCount++;
							}
						}
					}
				}catch(Throwable t){
					logger.debug("Skipping mapping for source element {} ",mapping.getSource().getId().getElementId(),t);
					
				}
			}	
			logger.debug(String.format("Generated %s rules out of %s mappings into %s table.",parsedRulesCount,foundMappingsCount,rulesTable));
		}finally{
			if(xmler!=null)xmler.close();
			if(insertRuleStmt!=null)DbUtils.closeQuietly(insertRuleStmt);
			if(conn!=null)DbUtils.closeQuietly(conn);
		}
	}




	

	private HarmonizationRule getRule(MappedValue mapped) throws Exception{
		Column referred=getConfig().getCurrentCodelistVersion().getColumnByName(mapped.getFieldName());
		TDTypeValue targetValue=referred.getDataType().fromString(mapped.getTargetValue());
		TDTypeValue sourceValue=referred.getDataType().fromString(mapped.getSourceValue());		
		return new HarmonizationRule(sourceValue,targetValue,
				referred.getLocalId(),true,
				evaluatorFactory.getEvaluator(sourceValue).evaluate(),
				evaluatorFactory.getEvaluator(targetValue).evaluate());
	}

	
	
	
	private int storeRule(HarmonizationRule r) throws SQLException, JAXBException{
		
		
		if(insertRuleStmt==null) {
			String insertStatement="INSERT INTO "+rulesTable.getName()+"("+
					HarmonizationRule.ENABLED+","+
					HarmonizationRule.REFERRED_CODELIST_COLUMN+","+
					HarmonizationRule.TO_CHANGE_VALUE_FIELD+","+
					HarmonizationRule.TO_CHANGE_VALUE_DESCRIPTION+","+
					HarmonizationRule.TO_SET_VALUE_FIELD+","+
					HarmonizationRule.TO_SET_VALUE_DESCRIPTION+") VALUES (?,?,?,?,?,?)";
			insertRuleStmt=conn.prepareStatement(insertStatement);			
		}
		
		Map<String,String> values=r.asMap();
		
		insertRuleStmt.setBoolean(1, r.isEnabled());		
		insertRuleStmt.setString(2, values.get(HarmonizationRule.REFERRED_CODELIST_COLUMN));
		insertRuleStmt.setString(3, values.get(HarmonizationRule.TO_CHANGE_VALUE_FIELD));
		insertRuleStmt.setString(4, r.getToChangeValueDescription());
		insertRuleStmt.setString(5, values.get(HarmonizationRule.TO_SET_VALUE_FIELD));
		insertRuleStmt.setString(6, r.getToSetValueDescription());
		
		return insertRuleStmt.executeUpdate();
	}

	
	
	
	private List<MappedValue> getChangeSet(MappedRow original,MappedRow newRow) throws Exception{		 
		ArrayList<MappedValue> toReturn=new ArrayList<>();	
		for(Entry<String,String> colMapping:columnsMapping.entrySet()){
			String originalValue=original.get(colMapping.getKey());
			String newValue=newRow.get(colMapping.getValue());
			if(originalValue!=null&&newValue!=null&&!originalValue.equals(newValue))
				toReturn.add(new MappedValue(originalValue, newValue, colMapping.getValue()));
		}
//		
//		
//		for(Entry<String,String> originalEntry:original.entrySet()){
//			String originalValue=originalEntry.getValue();
//			String currentValue;
//			List<String> labels;
//			if(newRow.getValuesPerField().containsKey(originalEntry.getKey())){
//				// same field name
//				currentValue=newRow.getValuesPerField().get(originalEntry.getKey());
//				labels=newRow.getLabelsPerField().get(originalEntry.getKey());
//			}else{
//				// need to look for same label
//				ArrayList<String> originalLabels=original.getLabelsPerField().get(originalEntry.getKey());				
//				for(Entry<String,ArrayList<String>> labelEntry:newRow.getLabelsPerField().entrySet()){
//					for(String toSearchLabel:originalLabels){
//						if(labelEntry.getValue().contains)
//					}
//				}
//				
//			}
//			
//			if(!originalEntry.getValue().equals(newRow.getValuesPerField().get(originalEntry.getKey())))
//				toReturn.add(new MappedValue(originalEntry.getValue(), newRow.getValuesPerField().get(originalEntry.getKey()), originalEntry.getKey()));
//			
//
//		}
		return toReturn;
	}
	
	
	private MappedRow loadRow(boolean fromPreviousVersion, String code) throws Exception{
		Table codelistTable=null;
		PreparedStatement toUseStmt=null;
		if(fromPreviousVersion){
			codelistTable=config.getPreviousCodelistVersion();			
			if(loadRowFromPrevious==null)loadRowFromPrevious=conn.prepareStatement(
					getLoadRowStmt(codelistTable,config.getOldCodesColumn().getName()));
			toUseStmt=loadRowFromPrevious;
		}else{
			codelistTable=config.getCurrentCodelistVersion();
			if(loadRowFromCurrent==null) loadRowFromCurrent=conn.prepareStatement(
					getLoadRowStmt(codelistTable,
							codelistTable.getColumnsByType(CodeColumnType.class).get(0).getName()));
			toUseStmt=loadRowFromCurrent;
		}
		toUseStmt.setString(1, code);
		ResultSet rs=toUseStmt.executeQuery();
		
		
		if(rs.next()){			
			HashMap<String,String> valuesPerField=new HashMap<>();
			
			List<Column> columns=codelistTable.getColumnsExceptTypes(CodeColumnType.class,IdColumnType.class,ValidationColumnType.class);
		
			//for each column get labels and value 
			
			for(Column col:columns){

				ArrayList<String> labels=OperationHelper.getLabels(col);
				

				valuesPerField.put(col.getName(), rs.getString(col.getName()));
			}
			
			rs.close();
			return new MappedRow(valuesPerField,code);
			
			
		}else throw new Exception("Row with code "+code+" not found");
	}
	
	private String getLoadRowStmt(Table theCodelist, String codeColumnName){		
		String tableName=theCodelist.getName();
		return String.format("Select * from %s WHERE %s = ?",tableName,codeColumnName);
	}
	
	public ParserConfiguration getConfig() {
		return config;
	}
	public long getParsedRulesCount() {
		return parsedRulesCount;
	}
	public long getFoundMappingsCount() {
		return foundMappingsCount;
	}
	
	public Table getRulesTable() {
		return rulesTable;
	}
	
	
	private void analyzeTableStructure(){
		// init column mapping
		for(Column previous:config.getPreviousCodelistVersion().getColumnsExceptTypes(CodeColumnType.class,IdColumnType.class,ValidationColumnType.class)){
			ArrayList<String> previousLabels=OperationHelper.getLabels(previous);
			for(Column current:config.getCurrentCodelistVersion().getColumnsExceptTypes(CodeColumnType.class,IdColumnType.class,ValidationColumnType.class)){
				
				if(previous.getName().equals(current.getName())	// same name
						||previous.getLocalId().equals(current.getLocalId())) // same localId
					columnsMapping.put(previous.getName(),current.getName());
				else{
					//try with labels
					for(String currentLabel:OperationHelper.getLabels(current))
						if(previousLabels.contains(currentLabel)){
							columnsMapping.put(previous.getName(),current.getName());
							break;
						}
				}
			}
		}
	}
}
