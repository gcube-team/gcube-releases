package org.gcube.data.analysis.excel;

import org.gcube.data.analysis.excel.metadata.format.DataFormat;

public interface ColumnModel {

	public enum ColumnType {
		DIMENSION ("DIMENSION"),
		ATTRIBUTE ("ATTRIBUTE"),
		TIMEDIMENSION ("TIMEDIMENSION"),
		CODE ("CODE"),
		CODE_DESCRIPTION ("CODEDESCRIPTION"),
		MEASURE ("MEASURE");
		
		private String type;
		
		ColumnType (String type)
		{
			this.type = type;
		}
		
		@Override
		public String toString() {
			return type;
		}
		
	}
	
	public String getName();
	
	public DataFormat getDataFormat();
	
	public ColumnType getColumnType ();
	
}
