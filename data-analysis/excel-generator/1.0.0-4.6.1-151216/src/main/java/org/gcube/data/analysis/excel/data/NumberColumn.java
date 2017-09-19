package org.gcube.data.analysis.excel.data;

public class NumberColumn extends Column {

	private final String NUMBER = "0.00";
	
	public NumberColumn(String name) {
		super (name);
	}
	
	@Override
	public String getDataFormat() {
		return NUMBER;
	}

}
