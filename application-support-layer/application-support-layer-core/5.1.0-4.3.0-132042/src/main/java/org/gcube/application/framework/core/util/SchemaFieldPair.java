package org.gcube.application.framework.core.util;

import java.util.ArrayList;

public class SchemaFieldPair implements Cloneable{
	
	String schema;
	String fieldName;
	String sortValue;
	ArrayList<String> metadataCollectionIds;
	ArrayList<String> metadataCollectionLanguage;
	ArrayList<String> contentCollectionIds;
	
	public ArrayList<String> getContentCollectionIds() {
		return contentCollectionIds;
	}
	
	public ArrayList<String> getMetadataCollectionLanguage() {
		return metadataCollectionLanguage;
	}


	public String getSortValue() {
		return sortValue;
	}


	public void setSortValue(String sortValue) {
		this.sortValue = sortValue;
	}


	public SchemaFieldPair() {
		schema = new String();
		fieldName = new String();
		sortValue = new String();
	}
	
	public SchemaFieldPair (String schemaName, String field, String sortValueName) {
		schema = schemaName;
		fieldName = field;
		sortValue = sortValueName;
		metadataCollectionIds = new ArrayList();
		metadataCollectionLanguage = new ArrayList();
		contentCollectionIds = new ArrayList();
	}
	
	
	public ArrayList<String> getMetadataCollectionIds() {
		return metadataCollectionIds;
	}
	
	public void addMetadataColIdAndLanguage(String mid, String language) {
		metadataCollectionIds.add(mid);
		metadataCollectionLanguage.add(language);
	}
	
	public void addContentCollectionId(String colId) {
		if (contentCollectionIds == null)
			contentCollectionIds = new ArrayList<String>();
		contentCollectionIds.add(colId);
	}
	
	public SchemaFieldPair clone() {
		
		SchemaFieldPair newSFP = new SchemaFieldPair();
		newSFP.setFieldName(new String(this.fieldName));
		newSFP.setSchema(new String(this.schema));
		newSFP.setSortValue(new String(this.sortValue));
		if (metadataCollectionIds != null) {
			for (int i = 0; i < metadataCollectionIds.size(); i++) {
				newSFP.addMetadataColIdAndLanguage(new String(this.metadataCollectionIds.get(i)), new String(this.metadataCollectionLanguage.get(i)));
			}
		}
		if (contentCollectionIds != null) {
			for (int i = 0; i < contentCollectionIds.size(); i++) {
				newSFP.addContentCollectionId(this.contentCollectionIds.get(i));
			}
		}
		
		return newSFP;
	}
	
	/**
	 * 
	 * @param schemaName
	 */
	public void setSchema(String schemaName) {
		schema = schemaName;
	}
	
	/**
	 * 
	 * @param field
	 */
	public void setFieldName (String field) {
		fieldName = field;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public String getFieldName () {
		return fieldName;
	}

}
