package org.gcube.data.analysis.tabulardata.query.parameters;

public enum QueryOrderDirection {
	
	ASCENDING("ASC"),
	DESCENDING("DESC");
	
	private String sqlKeyword;
	
	private QueryOrderDirection(String sqlKeyword){
		this.sqlKeyword = sqlKeyword;
	}
	
	public String getSQLKeyword(){
		return sqlKeyword;
	}

}
