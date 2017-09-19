package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types;

import java.util.List;
import java.util.Properties;

import org.gcube.data.analysis.excel.data.Column;
import org.gcube.data.analysis.excel.data.GenericColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnFactory {
	
	private Properties dataTypes;
	private Logger logger;
	private static ColumnFactory instance;
	
	private ColumnFactory ()
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		dataTypes = new Properties();
		this.dataTypes.getClass().getResourceAsStream("/datatypes.properties");
	}
	
	public static ColumnFactory getInstance ()
	{
		if (instance == null) instance = new ColumnFactory();
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	public Column createColumn (String columnName, String dataTypeName)
	{
		this.logger.debug("Data Type = "+dataTypeName);
		String columnTypeName = this.dataTypes.getProperty(dataTypeName);
		Column response = null;
		
		if (columnTypeName == null) 
		{
			this.logger.debug("Data type not found: using generic instance");
			response =  new GenericColumn(columnName);
		}
		else if (columnName.equalsIgnoreCase("excluded"))
		{
			this.logger.debug("Excluded column");
			response = null;
		}
		else
		{
			this.logger.debug("Data type found "+columnTypeName);
			
			try
			{
				Class<? extends Column> className = (Class<?extends Column>) Class.forName(columnTypeName);
				response = className.getConstructor(String.class).newInstance(columnName);
			} catch (Exception e)
			{
				this.logger.error("Class "+columnTypeName+" not found, using generic type",e);
				response = new GenericColumn(columnName);
			}
			

			
		}
		
		return response;
		
	}

	
}
