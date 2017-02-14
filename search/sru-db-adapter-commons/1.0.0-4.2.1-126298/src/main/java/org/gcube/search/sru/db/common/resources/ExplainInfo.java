package org.gcube.search.sru.db.common.resources;

import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExplainInfo {
	private String recordPacking;
	private String recordSchema;

	private String schemaID;
	private String schemaName;
	private Map<String, String> indexSets;
	private Map<String, ArrayList<String>> indexInfo;

	private String defaultTable;
	
	public ExplainInfo() {
	}

	@XmlElement
	public String getRecordPacking() {
		return recordPacking;
	}

	public void setRecordPacking(String recordPacking) {
		this.recordPacking = recordPacking;
	}

	@XmlElement
	public String getRecordSchema() {
		return recordSchema;
	}

	public void setRecordSchema(String recordSchema) {
		this.recordSchema = recordSchema;
	}

	@XmlElement
	public String getSchemaID() {
		return schemaID;
	}

	public void setSchemaID(String schemaID) {
		this.schemaID = schemaID;
	}

	@XmlElement
	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	@XmlElement
	public Map<String, String> getIndexSets() {
		return indexSets;
	}

	public void setIndexSets(Map<String, String> indexSets) {
		this.indexSets = indexSets;
	}

	@XmlElement
	public Map<String, ArrayList<String>> getIndexInfo() {
		return indexInfo;
	}

	public void setIndexInfo(Map<String, ArrayList<String>> indexInfo) {
		this.indexInfo = indexInfo;
	}

	@XmlElement
	public String getDefaultTable() {
		return defaultTable;
	}

	public void setDefaultTable(String defaultTable) {
		this.defaultTable = defaultTable;
	}

}
