package org.gcube.application.perform.service.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.application.perform.service.engine.model.CSVExportRequest;
import org.gcube.application.perform.service.engine.model.DBField;

public class ExportCSVQuery extends Query {

	private Map<String,Map<String,String>> mappings=new HashMap<>(); 

	private String tablename;

	private CSVExportRequest theRequest;
	private SchemaDefinition schema;

	private Map<String,DBField> actualStructure;

	private ArrayList<String> exportCSVFieldOrder; 
	
	public ExportCSVQuery(String query, DBField[] queryParams, CSVExportRequest theRequest, 
			SchemaDefinition schema, Map<String,DBField> actualStructure, ArrayList<String> exportFieldsOrder) {
		super(query, queryParams);
		this.theRequest=theRequest;
		this.schema=schema;
		this.actualStructure=actualStructure;
		
		exportCSVFieldOrder=exportFieldsOrder;
		
	}


	public void setMapping(String field, Map<String,String> mapping) {
		mappings.put(field, mapping);
	}




	public void setTablename(String tablename) {
		this.tablename = tablename;
	}


	@Override
	public String getQuery() {

//		String selectedFields=replaceWithMappings(getFieldList());
		String selectedFields=getFieldList();

		String conditionString =getConditionString();
		if(conditionString.length()>0) conditionString= "WHERE "+conditionString;


		return String.format("SELECT %1$s FROM %2$s %3$s", 
				selectedFields, tablename, conditionString);
	}

	public String getQueryForMappedFields(String filterMappingKey,String...fields) {
		StringBuilder b=new StringBuilder();
		for(String f:fields) 
			b.append(f+",");
		b.setLength(b.lastIndexOf(","));

		log.debug("Creating query for fields {} against table {} ",b,tablename);

//		String selectedFields=replaceWithMappings(b.toString());
		String selectedFields=b.toString();
		String condition=getFilterWithMapping(filterMappingKey);



		return String.format("SELECT %1$s FROM %2$s %3$s", 
				selectedFields, tablename, condition);
	}

	private String getFilterWithMapping(String mappingFilterKey) {

		StringBuilder conditionBuilder=new StringBuilder("WHERE ");
		if(mappings.containsKey(mappingFilterKey)&&(!mappings.get(mappingFilterKey).isEmpty())) {
			String actualField=actualField(mappingFilterKey);
			log.debug("Setting filter By Mappings for field {}, size {} ",actualField,mappings.get(mappingFilterKey).size());

			conditionBuilder.append("(");
			for(Entry<String,String> mappingFilter:mappings.get(mappingFilterKey).entrySet()) {
				conditionBuilder.append(String.format("%1$s = '%2$s' OR", actualField,mappingFilter.getKey()));
			}
			conditionBuilder.setLength(conditionBuilder.lastIndexOf("OR"));
			conditionBuilder.append(")");
			
			// Add selection filter.. 
			String filteringCondition=getConditionString();
			if(filteringCondition.length()>0) conditionBuilder.append(" AND ("+filteringCondition+")");			
		}else {
			log.debug("No mappings to search For ");
			conditionBuilder.append("FALSE"); 
		}
		return conditionBuilder.toString();
	}


//	private String replaceWithMappings(String selectionFields) {
//		String toReturn=selectionFields;
//		// fieldLabel -> (uuid->name)
//		for(Entry<String,Map<String,String>> mapping: mappings.entrySet()) {
//			if(exists(mapping.getKey())) {
//				String actualMapped=actualField(mapping.getKey());
//				if(toReturn.contains(actualMapped)) {
//					StringBuilder caseBuilder=new StringBuilder("CASE "+actualMapped);
//					for(Entry<String,String> condition: mapping.getValue().entrySet())
//						caseBuilder.append(String.format("WHEN '%1$s' THEN '%2$s'", condition.getKey(),condition.getValue()));
//					
//					caseBuilder.append(String.format(" ELSE %1$s END AS %1$s", actualMapped));
//
//					toReturn=toReturn.replace(actualMapped, caseBuilder.toString());
//				}
//			}
//		}
//
//		return toReturn.toString();		
//	}


	private String getConditionString() {

		ArrayList<String> orGroups=new ArrayList<String>();

		// AREA
		if(theRequest.getAreas().size()>0 && schema.getAreaField()!=null && exists(schema.getAreaField())) 
			orGroups.add(getFilterByMultipleValues(theRequest.getAreas(), schema.getAreaField()));

		// QUARTER
		if(theRequest.getQuarters().size()>0 && schema.getQuarterField()!=null && exists(schema.getQuarterField())) 
			orGroups.add(getFilterByMultipleValues(theRequest.getQuarters(), schema.getQuarterField()));

		// SPECIES ID 
		if(theRequest.getSpeciesIds().size()>0 && schema.getSpeciesField()!=null && exists(schema.getSpeciesField())) 
			orGroups.add(getFilterByMultipleValues(theRequest.getSpeciesIds(), schema.getSpeciesField()));


		// PERIOD
		if(theRequest.getPeriods().size()>0 && schema.getPeriodField()!=null && exists(schema.getPeriodField())) 
			orGroups.add(getFilterByMultipleValues(theRequest.getPeriods(), schema.getPeriodField()));

		StringBuilder toReturn=new StringBuilder("");
		for(String orGroup:orGroups) {
			toReturn.append("("+orGroup+") AND ");
		}

		if(toReturn.length()>0)
			toReturn.setLength(toReturn.lastIndexOf("AND"));


		return toReturn.toString();

	}


	private String getFieldList() {
		StringBuilder b=new StringBuilder();
		for(String label:exportCSVFieldOrder) {
			DBField f = actualStructure.get(label);
			b.append(f.getFieldName()+",");		
		}
		return b.toString().substring(0,b.lastIndexOf(","));
	}

	private String actualField(String label) {
		return actualStructure.get(label).getFieldName();
	}

	private boolean exists(String label) {
		return actualStructure.containsKey(label);
	}

	private String getFilterByMultipleValues(Set<String> filterValues,String filterLabel) {
		String field=actualField(filterLabel);
		StringBuilder toReturn=new StringBuilder();
		for(String p:filterValues)
			toReturn.append(String.format("%1$s = '%2$s' OR",field,p));
		toReturn.setLength(toReturn.lastIndexOf("OR"));
		return toReturn.toString();
	}
}
