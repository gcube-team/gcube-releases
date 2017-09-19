package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.excel.data.Column;
import org.gcube.data.analysis.excel.data.TableMetaData;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.ExcelGenerator;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.ColumnFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelGeneratorFromTemplate extends ExcelGeneratorAbstractImpl implements ExcelGenerator {

	private Logger logger;
	private Template template;
	
	
	public ExcelGeneratorFromTemplate(Template template) {
		super ();
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.template = template;
		
	}

	private TableMetaData generateTableMetadata (String name)
	{
		logger.debug("Generating template columns");
		List<TemplateColumn<?>> templateColumns = new ArrayList<>(template.getActualStructure());
		TableMetaData table = new TableMetaData (name);
		
		for (TemplateColumn<?> templateColumn : templateColumns)
		{
			String columnName = templateColumn.getLabel();
			logger.debug("Adding column "+columnName);
			Column column = ColumnFactory.getInstance().createColumn(columnName, templateColumn.getValueType().getName());
			table.addColumn(column);
			logger.debug("Column added");
		}
		
		return table;
	}
	
	@Override
	public void generateExcel(String fileName, String folderName) {

		this.logger.debug("Generating excel file "+fileName+ " in the folder "+folderName);
		super.setTable(this.generateTableMetadata(fileName));
		this.logger.debug("Metadata table generated");
		super.generateExcel(fileName, folderName);
	}
	

}
