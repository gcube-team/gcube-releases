package org.gcube.data.analysis.excel.engine;

import java.util.List;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gcube.data.analysis.excel.ColumnModel;
import org.gcube.data.analysis.excel.Table;
import org.gcube.data.analysis.excel.metadata.MetadataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExcelGeneratorMetadata extends BasicExcelGenerator{

	private Logger logger;

	public ExcelGeneratorMetadata (MetadataTable metadataTable)
	{
		super (metadataTable);
		this.logger = LoggerFactory.getLogger(this.getClass());
	}
	
	
	@Override
	protected void generateSpecificSheets (XSSFWorkbook document,Table genericTable,XSSFSheet dsdSheet, Row dsdHeaderRow, Row columnTypeRow, Row referenceRow,Font headerFont)
	{
		this.logger.debug("Generating specific metadata sheets");
		MetadataTable metadataTable = (MetadataTable) genericTable;
		int cellColumnIndex = 0;
		List<ColumnModel> tableColumns = metadataTable.getColumns();
		for (int tableColumnIndex = 0; tableColumnIndex< tableColumns.size(); tableColumnIndex++)
		{
			ColumnModel column = tableColumns.get(tableColumnIndex);
			this.logger.debug("Generating DSD column "+column.getName());
			cellColumnIndex = generateDSDColumn(document, dsdSheet, column, dsdHeaderRow, columnTypeRow,referenceRow, cellColumnIndex, headerFont);
			this.logger.debug("DSD column generated");
		}
	}
	

	
}
