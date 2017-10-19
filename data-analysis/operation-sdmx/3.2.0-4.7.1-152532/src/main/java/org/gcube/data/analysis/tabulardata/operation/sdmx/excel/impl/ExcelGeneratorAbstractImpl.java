package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl;

import org.gcube.data.analysis.excel.data.TableMetaData;
import org.gcube.data.analysis.excel.engine.exceptions.ExcelNotSavedException;
import org.gcube.data.analysis.excel.engine.impl.WorkspaceExcelGenerator;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.ExcelGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ExcelGeneratorAbstractImpl implements ExcelGenerator{

	private Logger logger;
	private TableMetaData tableMetadata;
	
	
	public ExcelGeneratorAbstractImpl ()
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		
	}
	
	protected void setTable (TableMetaData tableMetadata)
	{
		this.tableMetadata = tableMetadata;
	}
	
	
	
	@Override
	public void generateExcel (String fileName, String folderName)
	{
		logger.debug("Generating excel on the workspace");
		logger.debug("Token retrieved");
		
		if (!fileName.endsWith("xls")&&!fileName.endsWith("xlsx")) fileName = fileName+".xlsx";
		
		logger.debug("Saving file "+ fileName+ " in the folder "+folderName);
		WorkspaceExcelGenerator excelGenerator = new WorkspaceExcelGenerator(tableMetadata, fileName, folderName);
		logger.debug("Generating file..");
		excelGenerator.generate();
		logger.debug("File generated");
		
		try
		{
			logger.debug("Saving file..");
			excelGenerator.save();
			logger.debug("File saved");
		} catch (ExcelNotSavedException e)
		{
			logger.warn("Excel file not saved",e);
			
		}
			

	}
}
