package org.gcube.dataanalysis.ecoengine.datatypes;

import java.util.List;
import java.util.UUID;

import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;

public class OutputTable extends InputTable{
	
	public OutputTable(List<TableTemplates> templateName, String name, String tableName, String description, String defaultValue, boolean optional) {
		super(templateName, name, description, defaultValue, optional);
		this.tableName=tableName;
	}

	public OutputTable(List<TableTemplates> templateName, String name, String tableName, String description, String defaultValue) {
		super(templateName, name, description, defaultValue);
		this.tableName=tableName;
	}

	public OutputTable(List<TableTemplates> templateName, String name,  String tableName, String description) {
		super(templateName, name, description);
		this.tableName=tableName;
	}

	protected String tableName;
	
	public String getTableName(){
		return tableName;
	}
	
		
	
}
