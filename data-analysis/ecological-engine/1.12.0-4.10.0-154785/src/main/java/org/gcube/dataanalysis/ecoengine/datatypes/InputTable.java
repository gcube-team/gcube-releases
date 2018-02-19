package org.gcube.dataanalysis.ecoengine.datatypes;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;

public class InputTable extends StatisticalType{
	
	protected List<TableTemplates> templateNames;
	
	public InputTable(List<TableTemplates> templateName, String name, String description, String defaultValue, boolean optional) {
		super(name, description, defaultValue, optional);
		this.templateNames=templateName;
	}

	public InputTable(List<TableTemplates> templateName,String name, String description, String defaultValue) {
		super(name, description, defaultValue);
		this.templateNames=templateName;
	}

	public InputTable(List<TableTemplates> templateName,String name, String description) {
		super(name, description);
		this.templateNames=templateName;
	}

	
	
	public List<TableTemplates> getTemplateNames() {
		return templateNames;
	}

	public void setTemplateNames(List<TableTemplates> templateName) {
		this.templateNames = templateName;
	}

	public String getTableName(){
		return super.name;
	}
	
	
}
