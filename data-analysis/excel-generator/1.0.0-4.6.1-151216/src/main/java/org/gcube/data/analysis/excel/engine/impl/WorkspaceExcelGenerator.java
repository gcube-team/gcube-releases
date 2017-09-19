package org.gcube.data.analysis.excel.engine.impl;

import java.io.ByteArrayInputStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.gcube.data.analysis.excel.engine.ExcelGenerator;
import org.gcube.data.analysis.excel.engine.exceptions.ExcelNotSavedException;
import org.gcube.data.analysis.excel.engine.target.WorkspaceTargetManager;
import org.gcube.data.analysis.excel.data.TableMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkspaceExcelGenerator extends ExcelGenerator {

	private Logger logger;
	private String 	fileName,
					folderName;
	
	public WorkspaceExcelGenerator(TableMetaData tableMetadata, String fileName, String folderName) {
		super (tableMetadata);
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.fileName = fileName;
		this.folderName = folderName;
	}
	
	@Override
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
