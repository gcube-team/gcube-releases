package org.gcube.opensearch.opensearchdatasource.processor;

import java.util.Map;

import gr.uoa.di.madgik.grs.record.field.FieldDefinition;

public class FieldDefinitionInfo {
	public FieldDefinition[] fieldDefinition;
	public Map<String, Integer> fieldPositions;
	
	public FieldDefinitionInfo(FieldDefinition[] fieldDefinition, Map<String, Integer> fieldPositions) {
		this.fieldDefinition = fieldDefinition;
		this.fieldPositions = fieldPositions;
	}
}
