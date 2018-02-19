package org.gcube.data.analysis.excel.engine;

import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gcube.data.analysis.excel.ColumnModel;
import org.gcube.data.analysis.excel.Table;
import org.gcube.data.analysis.excel.data.DataColumn;
import org.gcube.data.analysis.excel.data.DataTable;
import org.gcube.data.analysis.excel.engine.exceptions.ExcelNotSavedException;
import org.gcube.data.analysis.excel.metadata.format.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicExcelGenerator {

	private Logger logger;
	private Table table;
	private XSSFWorkbook document;
	private final String CATCH_VALUE = "catch";
	
	protected BasicExcelGenerator (Table table)
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.table = table;
	}
	
	
	public void generate ()
	{
		this.document = new XSSFWorkbook();
		this.logger.debug("Creating XLS for table "+this.table.getOriginalTableName());
		generateTable(this.document, this.table);
		this.logger.debug("All XLS sheets have been generated");
		
	}
	
	
	private void generateTable (XSSFWorkbook document,Table genericTable)
	{
		String tableName = genericTable.getExcelTableName();
		this.logger.debug("Generating structure, codelist and data sheets for table "+tableName);
		this.logger.debug("Generating structure sheet");
		XSSFSheet dsdSheet = document.createSheet(tableName+"-DSD");
		Row dsdHeaderRow = dsdSheet.createRow(0);
		this.logger.debug("Header row created");
		Row columnTypeRow = dsdSheet.createRow(1);
		this.logger.debug("Column type row created");
		Row referenceRow = dsdSheet.createRow(2);
		this.logger.debug("Reference row created");
		Font headerFont = document.createFont();
		headerFont.setBold(true);
		generateSpecificSheets(document,genericTable, dsdSheet, dsdHeaderRow, columnTypeRow,referenceRow, headerFont);

	}
	
	protected void setHeaderCell (Row headerRow, String columnName,int columnIndex,Font headerFont)
	{
		this.logger.debug("Generating header...");
		Cell headerCell = headerRow.createCell(columnIndex, CellType.STRING);
		setHeaderStyle(headerCell, headerFont, HorizontalAlignment.CENTER);
		logger.debug("Column name "+columnName);
		headerCell.setCellValue(columnName);
		logger.debug("Column "+columnIndex+1+"header completed");
	}
	
	protected void setHeaderStyle (Cell cell,Font font,HorizontalAlignment orizontalAlignment)
	{
		CellStyle headerStyle = cell.getCellStyle();
		headerStyle.setAlignment(orizontalAlignment);
		headerStyle.setFont(font);
		cell.setCellStyle(headerStyle);
		this.logger.debug("Styles added for header and columns");
		
		
	}
	
	protected int generateDSDColumn (XSSFWorkbook document,XSSFSheet dsdSheet, ColumnModel column, Row dsdHeaderRow, Row columnTypeRow, Row referenceRow,  int cellColumnIndex,Font headerFont)
	{
		setHeaderCell(dsdHeaderRow, column.getName(), cellColumnIndex, headerFont);
		Cell columnTypeCell = columnTypeRow.createCell(cellColumnIndex, CellType.STRING);
		columnTypeCell.setCellValue (column.getColumnType().toString());
		Cell referenceCell = referenceRow.createCell(cellColumnIndex, CellType.STRING);
		DataFormat dataFormat = column.getDataFormat();
		
		if (dataFormat.isCatchValue())
		{
			this.logger.debug("Catch value");
			referenceCell.setCellValue(CATCH_VALUE);
			dsdSheet.autoSizeColumn(cellColumnIndex);
			cellColumnIndex++;
			Cell attributeHeaderCell = dsdHeaderRow.createCell(cellColumnIndex, CellType.STRING);
			DataColumn attributeColumn = dataFormat.getDefinitionTable().getColumns().get(0);
			this.logger.debug("Adding attribute column");
			setHeaderStyle(attributeHeaderCell, headerFont, HorizontalAlignment.CENTER);
			String columnName = attributeColumn.getName();
			this.logger.debug("Column name "+columnName);
			attributeHeaderCell.setCellValue(columnName);
			setColumnValues(attributeColumn.getStringValues(), cellColumnIndex, dsdSheet, 2);
			this.logger.debug("Value added");
		}
		else
		{
			this.logger.debug("Data type has a reference table");
			DataTable definitionTable = dataFormat.getDefinitionTable();
			String reference = dataFormat.getReference();
			this.logger.debug("Reference "+reference);
			referenceCell.setCellValue(reference);
			generateReferenceSheet(document.createSheet(definitionTable.getExcelTableName()), definitionTable,headerFont);
		}
		
		dsdSheet.autoSizeColumn(cellColumnIndex);
		cellColumnIndex++;
		return cellColumnIndex;
	}
	
	protected void generateReferenceSheet (XSSFSheet referenceSheet, DataTable referenceTable,Font headerFont)
	{
		this.logger.debug("Creating reference sheet "+referenceTable.getExcelTableName());
		Row headerRow = referenceSheet.createRow(0);
		List<DataColumn> columns = referenceTable.getColumns();
		
		for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++)
		{
			DataColumn column = columns.get(columnIndex);
			int rowIndex = 1;
			String columnName = column.getName();
			this.logger.debug("Adding column "+columnName);
			setHeaderCell(headerRow, columnName, columnIndex, headerFont);
			Iterator<String> values = column.getStringValues().iterator();
		
			while (values.hasNext())
			{
				String value = values.next();
				this.logger.debug("Adding value "+value);
				Row dataRow = referenceSheet.getRow(rowIndex);
				
				if (dataRow == null) dataRow = referenceSheet.createRow(rowIndex);
				
				Cell valueCell = dataRow.createCell(columnIndex);
				valueCell.setCellValue(value);
				rowIndex ++;
			}
			referenceSheet.autoSizeColumn(0);
		}
	}
	

	
	protected void setColumnValues (List<String> values, int cellColumnIndex,XSSFSheet sheet, int startingRowValue)
	{
		this.logger.debug("Adding column values");
		
		for (String value : values)
		{
			Row attributeDataRow = sheet.getRow(startingRowValue);
			
			if (attributeDataRow == null) attributeDataRow = sheet.createRow(startingRowValue);
			
			Cell attributeDataCell = attributeDataRow.createCell(cellColumnIndex);
			this.logger.debug("Adding value "+value);
			attributeDataCell.setCellValue(value);
			startingRowValue ++;
		}
	}
	
	protected final XSSFWorkbook getFinalDocument ()
	{
		return this.document;
	}
	
	protected abstract void generateSpecificSheets (XSSFWorkbook document,Table genericTable,XSSFSheet dsdSheet, Row dsdHeaderRow, Row columnTypeRow, Row referenceRow,Font headerFont);

	
	public abstract void save () throws ExcelNotSavedException;

	
}
