package org.gcube.common.dbinterface.queries;

import java.io.File;
import java.util.List;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.tables.Table;

public interface CopyFromCsv {

	public void execute(DBSession session) throws Exception;	
	public void setTable(Table table) ;
	public void setEncoding(String encoding);
	public void setQuoting(char quoting);
	public void setSeparator(char separator);
	public void setFile(File file);
	public void setColumnList(List<String> columns);
	public String getExpression();
	
}
