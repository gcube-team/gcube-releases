package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.excel.data.DataColumn;
import org.gcube.data.analysis.excel.data.DataTable;
import org.gcube.data.analysis.excel.data.DataTableImpl;
import org.gcube.data.analysis.excel.engine.WorkspaceExcelGenerator;
import org.gcube.data.analysis.excel.engine.impl.WorkspaceExcelGeneratorData;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.ExcelGenerator;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.ColumnBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.DataColumnBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.TableBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.ColumnFactoryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelGeneratorFromTable extends ExcelGeneratorAbstractImpl implements ExcelGenerator {

	private Logger logger;
	private TableBean tableBean;
	private String locale;
	private static final String DEFAULT_LOCALE = "en";

	
	public ExcelGeneratorFromTable(TableBean tableBean,String locale) {
		super ();
		this.logger = LoggerFactory.getLogger(this.getClass());
		if (locale == null) this.locale = DEFAULT_LOCALE;
		this.tableBean = tableBean;
		
	}
	public ExcelGeneratorFromTable(TableBean tableBean) {
		this (tableBean, DEFAULT_LOCALE);
	}


	

	private DataTable generateDataTable ()
	{
		String tableName = this.tableBean.getTableName();
		this.logger.debug("Table name "+tableName);
		ColumnBean primaryMeasure = this.tableBean.getPrimaryMeasure();
		DataTableImpl dataTable = new DataTableImpl(tableName);
		DataColumn primaryMeasureDataColumn = ColumnFactoryData.getInstance().createColumn((DataColumnBean) primaryMeasure, this.tableBean,this.locale);
		dataTable.addColumn(primaryMeasureDataColumn);

		List<ColumnBean> columnBeans = new ArrayList<>(tableBean.getAttributeColumns());
		columnBeans.add(this.tableBean.getTimeDimensionColumn());
		columnBeans.addAll(this.tableBean.getDimensionColumns());
		columnBeans.addAll(this.tableBean.getMeasureColumns());

		
		for (ColumnBean columnBean : columnBeans)
		{
			this.logger.debug("Loading column "+columnBean.getName(this.locale));
			DataColumn dataColumn= ColumnFactoryData.getInstance().createColumn((DataColumnBean)columnBean, this.tableBean,this.locale);
			dataTable.addColumn(dataColumn);
			logger.debug("Column added");

		}
		
		return dataTable;
	}
	
//	private TableMetaData generateTableMetadata ()
//	{
//		logger.debug("Generating template columns");
//		List<org.gcube.data.analysis.tabulardata.model.column.Column> columns = this.table.getColumns();
//		this.logger.debug("Table name "+this.table.getName());
//		String tableName = getDescriptionMetadata(this.table); 
//		this.logger.debug("Table name "+tableName);
//		TableMetaData tableMetadata = new TableMetaData (tableName);
//		
//		for (org.gcube.data.analysis.tabulardata.model.column.Column column : columns)
//		{
//			
//			List<LocalizedText> columnNamesMetadata = getNamesMetadata(column);
//			String columnName = getLocalizedName(columnNamesMetadata, column.getName());
//			logger.debug("Adding column "+columnName);
//			Column metadataColumn = ColumnFactory.getInstance().createColumn(columnName, column.getColumnType().getClass().getName());
//			
//			if (metadataColumn == null)
//			{
//				this.logger.debug("Column "+columnName+ " has been excluded by configuration since it is "+column.getColumnType().getClass()+" type");
//			}
//			else
//			{
//				tableMetadata.addColumn(metadataColumn);
//				logger.debug("Column added");
//			}
//	
//		}
//		
//		return tableMetadata;
//	}
	

	
	@Override
	protected WorkspaceExcelGenerator getWorkspaceExcelGenerator(String fileName, String folderName) {

		this.logger.debug("Workspace generator for data tables");
		return new WorkspaceExcelGeneratorData(generateDataTable (), fileName, folderName);
	}
	

}
