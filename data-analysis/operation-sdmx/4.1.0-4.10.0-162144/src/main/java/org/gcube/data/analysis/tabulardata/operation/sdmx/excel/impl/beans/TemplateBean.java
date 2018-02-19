package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.ColumnCategory;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateBean {

	private Template template;
	private List<TemplateColumn<?>> templateColumns;
	private TemplateColumn<?> primaryMeasure;
	private Map<String, CodelistBean> codelists;
	private Logger log;
	
	public TemplateBean (Template template)
	{
		this.log = LoggerFactory.getLogger(this.getClass());
		this.template = template;
		this.codelists = new HashMap<>();
		this.templateColumns = new ArrayList<>(template.getActualStructure());
	}

	public Template getTemplate() {
		return template;
	}
	
	public void addCodelist (String column, CodelistBean codelist)
	{
		this.codelists.put(column, codelist);
	}

	public Map<String, CodelistBean> getCodelists() {
		return codelists;
	}
	
	public void setPrimaryMeasure (String observationValue)
	{
		log.debug("Loading primary measure");
		log.debug("For obs value "+observationValue);
		int size = this.templateColumns.size();
		TemplateColumn<?> response = null;
		
		for (int i=0; (response == null && i<size); i++)
		{
			TemplateColumn<?> templateColumn = this.templateColumns.get(i);
			String columnID = templateColumn.getId();
			ColumnCategory columnType = templateColumn.getColumnType();
			log.debug("Column label "+templateColumn.getLabel());
			log.debug("Column id "+templateColumn.getId());
			log.debug("Column type "+columnType);
			
			if (columnType == ColumnCategory.MEASURE &&columnID.equals(observationValue))
			{
				log.debug("Primary measure found");
				response = this.templateColumns.remove(i);
			}

		}
		this.primaryMeasure=  response;
	}

	public List<TemplateColumn<?>> getGenericTemplateColumns() {
		return templateColumns;
	}

	public TemplateColumn<?> getPrimaryMeasure() {
		return primaryMeasure;
	}
	
	
}
