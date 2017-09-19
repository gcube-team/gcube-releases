package org.gcube.data.analysis.excel.data;

public class GenericColumn extends Column {

	private final String GENERAL = "General";
	
	public GenericColumn(String name) {
		super (name);
	}
	
	@Override
	public String getDataFormat() {
		return GENERAL;
	}

}
