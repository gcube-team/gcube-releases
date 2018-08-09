package org.gcube.dbinterface.h2.queries;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.gcube.common.dbinterface.pool.DBSession;
import org.gcube.common.dbinterface.queries.CopyFromCsv;
import org.gcube.common.dbinterface.tables.Table;

//TODO: the quoting, escaping and separetor is fixed
public class CopyFromCsvImpl implements CopyFromCsv {

	private String query = "INSERT INTO <%TABLENAME%> SELECT <%COLUMNLIST%> FROM CSVREAD('<%FILENAME%>')";
	
	private Table table;
	private char quoting;
	private char separator;
	private String encoding = null;
	private File file;
	private List<String> columnList= null;
	
	@Override
	public void setTable(Table table) {
		this.table = table;
	}

	@Override
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public void setQuoting(char quoting) {
		this.quoting = quoting;
	}

	@Override
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void setSeparator(char separator) {
		this.separator = separator;
	}
	
	@Override
	public void setColumnList(List<String> columns) {
		this.columnList = columns;
		
	}
	
	@Override
	public String getExpression() {
		String columnListTMP="";
		if (columnList != null && columnList.size()>0){
			StringBuffer bufferTMP = new StringBuffer("(");
			for (String column : columnList)
				bufferTMP.append(column+",");
			columnListTMP = bufferTMP.substring(0, bufferTMP.length()-1)+")";
		}
		return query.replace("<%TABLENAME%>", this.table.getTableName())
				.replace("<%COLUMNLIST%>", columnListTMP).replace("<%FILENAME%>", this.file.getAbsolutePath());
	}

	@Override
	public void execute(DBSession session) throws Exception {
		session.execute(this.getExpression(),false);
	}

}
