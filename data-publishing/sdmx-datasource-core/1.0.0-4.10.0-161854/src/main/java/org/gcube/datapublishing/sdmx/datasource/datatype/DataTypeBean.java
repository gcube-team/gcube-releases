package org.gcube.datapublishing.sdmx.datasource.datatype;

import org.sdmxsource.sdmx.api.constants.DATA_TYPE;

public class DataTypeBean {

	private DATA_TYPE sdmxDataType;
	private String responseDataType;

	DataTypeBean(String responseDataType, DATA_TYPE sdmxDataType) {
		this.responseDataType = responseDataType;
		this.sdmxDataType = sdmxDataType;
	}
	
	public DATA_TYPE getSdmxDataType() {
		return sdmxDataType;
	}
	

	
	public String getResponseDataType() {
		return responseDataType;
	}
	
	
	@Override
	public String toString() {
		return new String ("Mime type "+this.responseDataType+" SDMX type "+this.sdmxDataType);
	}
	
}
