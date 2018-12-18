package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl;

import org.gcube.data.analysis.excel.engine.WorkspaceExcelGenerator;
import org.gcube.data.analysis.excel.engine.exceptions.ExcelNotSavedException;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.ExcelGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ExcelGeneratorAbstractImpl implements ExcelGenerator{

	private Logger logger;
	
	public ExcelGeneratorAbstractImpl ()
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		
	}
	
	
	protected abstract WorkspaceExcelGenerator getWorkspaceExcelGenerator (String fileName, String folderName);
	
	@Override
	public boolean generateExcel (String fileName, String folderName)
	{
		logger.debug("Generating excel on the workspace");
		logger.debug("Token retrieved");
		
		if (!fileName.endsWith("xls")&&!fileName.endsWith("xlsx")) fileName = fileName+".xlsx";
		
		logger.debug("Saving file "+ fileName+ " in the folder "+folderName);
		WorkspaceExcelGenerator excelGenerator = getWorkspaceExcelGenerator(fileName, folderName);
		logger.debug("Generating file..");
		excelGenerator.generate();
		logger.debug("File generated");
		
		try
		{
			logger.debug("Saving file..");
			excelGenerator.save();
			logger.debug("File saved");
			return true;
		} catch (ExcelNotSavedException e)
		{
			logger.warn("Excel file not saved",e);
			return false;
		}
			

	}
}
