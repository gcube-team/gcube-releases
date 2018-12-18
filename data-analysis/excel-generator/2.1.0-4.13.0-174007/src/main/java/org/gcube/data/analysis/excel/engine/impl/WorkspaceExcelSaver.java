package org.gcube.data.analysis.excel.engine.impl;

import java.io.ByteArrayInputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gcube.data.analysis.excel.engine.exceptions.ExcelNotSavedException;
import org.gcube.data.analysis.excel.engine.target.WorkspaceTargetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkspaceExcelSaver {

	private Logger logger;
	private String 	fileName,
					folderName;
	private XSSFWorkbook document;
	
	public WorkspaceExcelSaver(XSSFWorkbook document, String fileName, String folderName) {

		this.logger = LoggerFactory.getLogger(this.getClass());
		this.fileName = fileName;
		this.folderName = folderName;
		this.document = document;
	}
	
	public void save() throws ExcelNotSavedException{

		this.logger.debug("Saving the document");
		
		try
		{
			logger.debug("Buffering excel data...");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			this.document.write(outputStream);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			logger.debug("Input stream ready");
			
			WorkspaceTargetManager target = new WorkspaceTargetManager();
			target.generateFile(this.folderName, this.fileName, inputStream);
			inputStream.close();
			outputStream.close();
			
		} catch (Exception e )
		{
			logger.error("Unable to save the excel file");
			throw new ExcelNotSavedException("Unable to save in the workspace",e);
		}
		

		
		
	}

}
