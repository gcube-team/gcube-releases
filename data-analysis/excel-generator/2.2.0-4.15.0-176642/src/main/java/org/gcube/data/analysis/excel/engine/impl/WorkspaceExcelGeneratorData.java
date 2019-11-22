package org.gcube.data.analysis.excel.engine.impl;

import org.gcube.data.analysis.excel.data.DataTable;
import org.gcube.data.analysis.excel.engine.ExcelGenerator;
import org.gcube.data.analysis.excel.engine.WorkspaceExcelGenerator;
import org.gcube.data.analysis.excel.engine.exceptions.ExcelNotSavedException;

public class WorkspaceExcelGeneratorData extends ExcelGenerator implements WorkspaceExcelGenerator  {

	private String 	fileName,
					folderName;
	
	public WorkspaceExcelGeneratorData(DataTable table, String fileName, String folderName) {
		super (table);
		this.fileName = fileName;
		this.folderName = folderName;
	}
	
	@Override
	public void save() throws ExcelNotSavedException{
		new WorkspaceExcelSaver(getFinalDocument(), this.fileName, this.folderName).save();
	}

}
