package org.gcube.data.analysis.excel.engine;

import java.util.List;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gcube.data.analysis.excel.Table;
import org.gcube.data.analysis.excel.data.DataColumn;
import org.gcube.data.analysis.excel.data.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExcelGenerator extends BasicExcelGenerator{

	private Logger logger;
	
	public ExcelGenerator (DataTable table)
	{
		super (table);
		this.logger = LoggerFactory.getLogger(this.getClass());

	}
	

	@Override
	protected void generateSpecificSheets (XSSFWorkbook document,Table genericTable,XSSFSheet dsdSheet, Row dsdHeaderRow, Row columnTypeRow, Row referenceRow,Font headerFont)
	{
		this.logger.debug("Generating specific data sheets");
		XSSFSheet timeSeriesSheet = document.createSheet(genericTable.getExcelTableName());
		Row timeSeriesHeaderRow = timeSeriesSheet.createRow(0);
		DataTable dataTable = (DataTable) genericTable;
		int cellColumnIndex = 0;
		List<DataColumn> tableColumns = dataTable.getColumns();
		
		for (int tableColumnIndex = 0; tableColumnIndex< tableColumns.size(); tableColumnIndex++)
		{
			DataColumn column = tableColumns.get(tableColumnIndex);
			this.logger.debug("Generating DSD column "+column.getName());
			cellColumnIndex = generateDSDColumn(document, dsdSheet, column, dsdHeaderRow, columnTypeRow,referenceRow, cellColumnIndex, headerFont);
			this.logger.debug("DSD column generated");
			this.logger.debug("Generating Timeseries column");
			generateDataColumn(timeSeriesSheet, column, timeSeriesHeaderRow, tableColumnIndex, headerFont);
			this.logger.debug("Timeseries column generated");
		}
		
		this.logger.debug("Moving data sheet to last position");
		
		document.setSheetOrder(timeSeriesSheet.getSheetName(), document.getNumberOfSheets()-1);
		
	}
	

	private void generateDataColumn (XSSFSheet timeSeriesSheet, DataColumn column,Row timeSeriesHeaderRow,int tableColumnIndex,Font headerFont)
	{
		
		setHeaderCell(timeSeriesHeaderRow, column.getName(), tableColumnIndex, headerFont);
		this.logger.debug("Setting values for column "+tableColumnIndex+1);
		setColumnValues(column.getStringValues(), tableColumnIndex, timeSeriesSheet, 1);
		this.logger.debug("Values set");
		
	}

	
}
