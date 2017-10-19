package org.gcube.common.dbinterface.attributes;

public enum AggregationFunctions {

	AVG("AVG"),
	MIN("MIN"),
	MAX("MAX"),
	SUM("SUM"),
	COUNT("COUNT");
	
	
	private String sqlFunction;
	
	AggregationFunctions(String sqlFunction){
		this.sqlFunction=sqlFunction;
	}
	
	public String getSqlFunction(){
		return this.sqlFunction;
	}
	
}
