package gr.uoa.di.madgik.workflow.adaptor.search.utils.wrappers.datasource;

import java.util.HashMap;
import java.util.Map;

public class DataSourceWrapperFactoryConfig 
{
	private Map<DataSourceWrapper.Type, String> types = new HashMap<DataSourceWrapper.Type, String>();
	
	private DataSourceWrapperFactoryConfig() { }
	
	public static DataSourceWrapperFactoryConfig newInstance() 
	{
		return new DataSourceWrapperFactoryConfig();
	}
	
	public DataSourceWrapperFactoryConfig add(DataSourceWrapper.Type type, String value)
	{
		types.put(type, value);
		return this;
	}
	
	public String get(DataSourceWrapper.Type type)
	{
		return types.get(type);
	}
	
	public void validate() throws Exception
	{
		for(DataSourceWrapper.Type t : DataSourceWrapper.Type.values())
		{
			if(!types.containsKey(t)) throw new Exception("Missing type: " + t);
		}
	}
}
