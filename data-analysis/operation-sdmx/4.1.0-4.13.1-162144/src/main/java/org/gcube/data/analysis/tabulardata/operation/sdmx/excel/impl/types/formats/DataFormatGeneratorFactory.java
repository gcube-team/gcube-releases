package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats;

import org.gcube.data.analysis.excel.ColumnModel.ColumnType;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.data.DataFormatGeneratorData;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.metadata.DataFormatGeneratorMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataFormatGeneratorFactory {

	@SuppressWarnings("unchecked")
	public static DataFormatGeneratorData getDataFormatGeneratorData (String typeDataFormatName)
	{
		Logger logger = LoggerFactory.getLogger(DataFormatGeneratorFactory.class);
		DataFormatGeneratorData response = null;
		
		if (typeDataFormatName == null) 
		{
			logger.debug("Data type not found: using generic instance");
			response =  new GenericDataFormatGenerator();
		}
		else
		{
			logger.debug("Data format found "+typeDataFormatName);
			
			try
			{
				Class<? extends DataFormatGeneratorData> className = (Class<?extends DataFormatGeneratorData>) Class.forName(typeDataFormatName);
				response = className.newInstance();
			} catch (Exception e)
			{
				logger.error("Class "+typeDataFormatName+" not found, using generic type",e);
				response = new GenericDataFormatGenerator();
			}
			

			
		}
		
		return response;
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static DataFormatGeneratorMetadata getDataFormatGeneratorMetadata (String typeDataFormatName)
	{
		Logger logger = LoggerFactory.getLogger(DataFormatGeneratorFactory.class);
		DataFormatGeneratorMetadata response = null;
		
		if (typeDataFormatName == null) 
		{
			logger.debug("Data type not found: using generic instance");
			response =  new GenericDataFormatGenerator();
		}
		else
		{
			logger.debug("Data format found "+typeDataFormatName);
			
			try
			{
				Class<? extends DataFormatGeneratorMetadata> className = (Class<?extends DataFormatGeneratorMetadata>) Class.forName(typeDataFormatName);
				response = className.newInstance();
			} catch (Exception e)
			{
				logger.error("Class "+typeDataFormatName+" not found, using generic type",e);
				response = new GenericDataFormatGenerator();
			}
			

			
		}
		
		return response;
		
	}
	

}
