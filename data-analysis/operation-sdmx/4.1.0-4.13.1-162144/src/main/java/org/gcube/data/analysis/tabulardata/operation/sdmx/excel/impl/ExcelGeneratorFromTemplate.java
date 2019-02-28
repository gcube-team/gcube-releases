package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.excel.ColumnModel;
import org.gcube.data.analysis.excel.engine.WorkspaceExcelGenerator;
import org.gcube.data.analysis.excel.engine.impl.WorkspaceExcelGeneratorMetadata;
import org.gcube.data.analysis.excel.metadata.MetadataTable;
import org.gcube.data.analysis.excel.metadata.MetadataTableImpl;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.ExcelGenerator;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.TemplateBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.ColumnFactoryMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelGeneratorFromTemplate extends ExcelGeneratorAbstractImpl implements ExcelGenerator {

	private Logger logger;
	private TemplateBean templateBean;
	
	
	public ExcelGeneratorFromTemplate(TemplateBean templateBean) {
		super ();
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.templateBean = templateBean;
		
	}

	private MetadataTable generateMetadataTable (String name)
	{
		logger.debug("Generating template columns");
		MetadataTableImpl table = new MetadataTableImpl(name);
		logger.debug("Adding primary measure");
		TemplateColumn<?> primaryMeasureColumn = this.templateBean.getPrimaryMeasure();
		ColumnModel primaryColumnModel = ColumnFactoryMetadata.getInstance().createColumn(primaryMeasureColumn, this.templateBean, true);
		table.addColumn(primaryColumnModel);
		this.logger.debug("Getting other columns");
		List<TemplateColumn<?>> templateColumns = new ArrayList<>(this.templateBean.getGenericTemplateColumns());
		
		for (TemplateColumn<?> templateColumn : templateColumns)
		{
			String columnName = templateColumn.getLabel();
			logger.debug("Adding column "+columnName);
			ColumnModel column = ColumnFactoryMetadata.getInstance().createColumn(templateColumn,templateBean,false);
			table.addColumn(column);
			logger.debug("Column added");
		}
		
		return table;
	}
	


	@Override
	protected WorkspaceExcelGenerator getWorkspaceExcelGenerator(String fileName, String folderName) 
	{
		this.logger.debug("Workspace generator for data tables");
		return new WorkspaceExcelGeneratorMetadata(generateMetadataTable (fileName), fileName, folderName);
	}
	

}
