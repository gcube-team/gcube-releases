package org.gcube.datapublishing.sdmx.datasource.datatype;

import java.util.Map;

import org.sdmxsource.sdmx.api.constants.DATA_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataTypeManagerImpl implements DataTypeManager {

	private Map<String, DATA_TYPE> dataTypes;
	private Map<String, Integer> priorities;
	private String defaultResponseType;
	
	private Logger logger;
	
	public DataTypeManagerImpl() {
		this.logger = LoggerFactory.getLogger(DataTypeManagerImpl.class);
	}
	
	@Override
	public DataTypeBean getDataType(String acceptHeader) {

		DataTypeBean response = new DataTypeBean(this.defaultResponseType,this.dataTypes.get(this.defaultResponseType));
		
		if (acceptHeader != null && acceptHeader.trim().length()>0)
		{
			this.logger.debug("Accept header "+acceptHeader);
			String [] acceptedDataTypes = acceptHeader.split(",");
			int candidatePriority = 0;
			
			for (String acceptedDataType : acceptedDataTypes)
			{
				acceptedDataType = acceptedDataType.replaceAll(" ", "");
				this.logger.debug("Type "+acceptedDataType);
				DATA_TYPE responseCandidate = this.dataTypes.get(acceptedDataType);
				this.logger.debug("Data type "+responseCandidate);
				
				if (responseCandidate != null && this.priorities.get(acceptedDataType)> candidatePriority)
				{
					response = new DataTypeBean(acceptedDataType, responseCandidate);
					candidatePriority = this.priorities.get(acceptedDataType);
				}
				
			}
			this.logger.debug("Selected data type "+response+ " with priority "+candidatePriority);
		}
		return response;
	}

	public void setDataTypes(Map<String, DATA_TYPE> dataTypes) {
		this.dataTypes = dataTypes;
	}

	public void setPriorities(Map<String, Integer> priorities) {
		this.priorities = priorities;
	}



	public void setDefaultResponseType(String defaultResponseType) {
		this.defaultResponseType = defaultResponseType;
	}
	
	
	
	

}
