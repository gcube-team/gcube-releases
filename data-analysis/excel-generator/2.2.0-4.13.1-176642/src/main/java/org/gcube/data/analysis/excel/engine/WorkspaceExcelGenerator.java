package org.gcube.data.analysis.excel.engine;

import org.gcube.data.analysis.excel.engine.exceptions.ExcelNotSavedException;

public interface WorkspaceExcelGenerator {

	public void save() throws ExcelNotSavedException;
	
	public void generate ();
}
