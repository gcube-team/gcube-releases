package org.gcube.data.analysis.excel.data;

public class TextColumn extends Column {

	private final String TEXT = "text";
	
	public TextColumn(String name) {
		super (name);
	}
	
	@Override
	public String getDataFormat() {
		return TEXT;
	}

}
