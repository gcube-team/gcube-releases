package org.gcube.data.analysis.excel.engine;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gcube.data.analysis.excel.engine.exceptions.ExcelNotSavedException;
import org.gcube.data.analysis.excel.data.Column;
import org.gcube.data.analysis.excel.data.TableMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExcelGenerator {

	private Logger logger;
	protected TableMetaData tableMetadata;
	protected XSSFWorkbook document;
	
	public ExcelGenerator (TableMetaData tableMetadata)
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.tableMetadata = tableMetadata;
	}
	
	public void generate ()
	{
		this.document = new XSSFWorkbook();
		this.logger.debug("Creating XLS for table "+this.tableMetadata.getTableName());
		XSSFSheet sheet = this.document.createSheet(this.tableMetadata.getTableName());
		Row row = sheet.createRow(0);
		this.logger.debug("First row created");
		List<Column> tableColumns = this.tableMetadata.getColumns();
		Font headerFont = this.document.createFont();
		headerFont.setBold(true);

		
		for (int i = 0; i < tableColumns.size(); i++)
		{
			this.logger.debug("Generating header...");
			Cell cell = row.createCell(i, CellType.STRING);
			CellStyle headerStyle = cell.getCellStyle();
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setFont(headerFont);
			Column column = tableColumns.get(i);
			CellStyle defaultColumnStyle = generateDefaultColumnStle(column, headerStyle);
			sheet.setDefaultColumnStyle(i, defaultColumnStyle);
			cell.setCellStyle(headerStyle);
			this.logger.debug("Styles added for header and columns");
			String columnName = column.getName();
			logger.debug("Column name "+columnName);
			cell.setCellValue(columnName);
			logger.debug("Column "+i+1+" completed");
			sheet.autoSizeColumn(i);
		}
		
	}
	
	
	private CellStyle generateDefaultColumnStle (Column column, CellStyle headerStyle)
	{
		this.logger.debug("Setting default column style");
		CellStyle defaultColumnStyle = this.document.createCellStyle();
		defaultColumnStyle.cloneStyleFrom(headerStyle);
		String dataFormat = column.getDataFormat();
		this.logger.debug("Data format "+dataFormat);
		defaultColumnStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(dataFormat));
		return defaultColumnStyle;
	}
	
	public abstract void save () throws ExcelNotSavedException;
	
}
