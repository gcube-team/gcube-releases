package org.gcube.data.analysis.excel.engine.impl;

import org.gcube.data.analysis.excel.engine.ExcelGeneratorMetadata;
import org.gcube.data.analysis.excel.engine.WorkspaceExcelGenerator;
import org.gcube.data.analysis.excel.engine.exceptions.ExcelNotSavedException;
import org.gcube.data.analysis.excel.metadata.MetadataTable;

public class WorkspaceExcelGeneratorMetadata extends ExcelGeneratorMetadata implements WorkspaceExcelGenerator   {

	private String 	fileName,
					folderName;
	
	public WorkspaceExcelGeneratorMetadata(MetadataTable metadataTable, String fileName, String folderName) {
		super (metadataTable);
		this.fileName = fileName;
		this.folderName = folderName;
	}
	
	@Override
	public void save() throws ExcelNotSavedException{
		new WorkspaceExcelSaver(getFinalDocument(), this.fileName, this.folderName).save();
	}

}
