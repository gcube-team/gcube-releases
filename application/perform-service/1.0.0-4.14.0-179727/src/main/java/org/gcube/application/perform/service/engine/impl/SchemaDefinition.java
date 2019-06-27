package org.gcube.application.perform.service.engine.impl;

import java.util.ArrayList;
import java.util.Properties;

import org.gcube.application.perform.service.engine.model.importer.AnalysisType;

public class SchemaDefinition {

	
	private static final String DESCRIPTION="description";
	private static final String FARM="farm";
	private static final String ASSOCIATION="association";
	private static final String BATCH="batch";
	private static final String COMPANY="company";
	private static final String ROUTINE_ID="routine";
	private static final String AREA="area";
	private static final String SPECIES="species";
	private static final String PERIOD="period";
	private static final String QUARTER="quarter";
			
	private static final String CSV="csv";
	private static final String ENABLE_ANALYSIS="enable_analysis";
	private static final String REPORT_FIELDS="report_fields";
	private static final String REPORT_LABELS="report_labels";
	
	
	
	
	public String getRelatedDescription() {
		return relatedDescription;
	}
	public AnalysisType getRelatedAnalysis() {
		return relatedAnalysis;
	}
	public String getCsvPath() {
		return csvPath;
	}
	public String getFarmUUIDField() {
		return farmUUIDField;
	}
	public String getAssociationUUIDField() {
		return associationUUIDField;
	}
	public String getBatchUUIDField() {
		return batchUUIDField;
	}
	public String getCompanyUUIDField() {
		return companyUUIDField;
	}
	
	public void setCsvPath(String csvPath) {
		this.csvPath = csvPath;
	}
	public Boolean getAnalysisEnabled() {
		return analysisEnabled;
	}
	public String getRoutineIdFieldName() {
		return routineIdFieldName;
	}
	
	
	public String getAreaField() {
		return areaField;
	}
	
	public String getPeriodField() {
		return periodField;
	}
	public String getQuarterField() {
		return quarterField;
	}
	public String getSpeciesField() {
		return speciesField;
	}
	
	public ArrayList<String> getToReportFields() {
		return toReportFields;
	}
	
	public ArrayList<String> getToReportLabels() {
		return toReportLabels;
	}
	
	public SchemaDefinition(AnalysisType relatedAnalysis, Properties props) {
		super();
		this.relatedDescription = props.getProperty(DESCRIPTION);
		this.relatedAnalysis = relatedAnalysis;
		this.csvPath = props.getProperty(CSV);
		this.farmUUIDField = props.getProperty(FARM);
		this.associationUUIDField = props.getProperty(ASSOCIATION);
		this.batchUUIDField = props.getProperty(BATCH);
		this.companyUUIDField = props.getProperty(COMPANY);
		this.routineIdFieldName=props.getProperty(ROUTINE_ID);
		this.areaField=props.getProperty(AREA);
		this.speciesField=props.getProperty(SPECIES);
		this.quarterField=props.getProperty(QUARTER);
		this.periodField=props.getProperty(PERIOD);
		
		
		
		this.analysisEnabled=Boolean.parseBoolean(props.getProperty(ENABLE_ANALYSIS, "false"));
		
		if(props.containsKey(REPORT_FIELDS)) {
			String fieldList=props.getProperty(REPORT_FIELDS);
			for(String field: fieldList.split(",")) 
				toReportFields.add(field);			
		}
		
		if(props.containsKey(REPORT_LABELS)) {
			String labelList=props.getProperty(REPORT_LABELS);
			for(String label: labelList.split(",")) 
				toReportLabels.add(label);			
		}
		
	}
	
	private String relatedDescription;
	private AnalysisType relatedAnalysis; 
	
	private String csvPath;
	private String farmUUIDField;
	private String associationUUIDField;
	private String batchUUIDField;
	private String companyUUIDField;
	
	private Boolean analysisEnabled;
	
	private String routineIdFieldName;

	private ArrayList<String> toReportFields=new ArrayList<>();
	
	private ArrayList<String> toReportLabels=new ArrayList<>();
	
	private String areaField;
	private String periodField;
	private String quarterField;
	private String speciesField;
}
