package org.gcube.data.analysis.excel.data;

public class TimeDateColumn extends Column {

	private final String TIME_DATE = "m/d/yy h:mm";
	
	public TimeDateColumn(String name) {
		super (name);
	}
	
	@Override
	public String getDataFormat() {
		return TIME_DATE;
	}

}
