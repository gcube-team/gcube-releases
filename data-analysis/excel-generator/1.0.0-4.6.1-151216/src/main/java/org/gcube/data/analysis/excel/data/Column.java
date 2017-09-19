package org.gcube.data.analysis.excel.data;

public abstract class Column {

//	public enum DATA_TYPE {
//		STRING ("string"),
//		DATE ("date"),
//		NUMBER ("number");
//		
//		private String type;
//		
//		DATA_TYPE (String type)
//		{
//			this.type = type;
//		}
//		
//		@Override
//		public String toString ()
//		{
//			return type;
//		}
//	}
	

	private String name;
	
	public Column (String name)
	{
		this.name = name;
	}

	public abstract String getDataFormat();
	
	

	public String getName() {
		return name;
	}
	
	
}
