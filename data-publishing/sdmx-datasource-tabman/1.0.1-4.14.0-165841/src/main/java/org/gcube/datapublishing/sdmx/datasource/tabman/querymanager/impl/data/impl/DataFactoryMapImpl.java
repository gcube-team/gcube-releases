package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.impl;

import java.util.Map;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DataFactory;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DataFactoryMap;

public class DataFactoryMapImpl implements DataFactoryMap {

	private Map<String, DataFactory> dataFactoryMap;
	
	public void setDataFactoryMap (Map<String, DataFactory> dataFactoryMap)
	{
		this.dataFactoryMap = dataFactoryMap;
	}
	
	@Override
	public DataFactory getDataFactory(DataType dataType) {
	
		return this.dataFactoryMap.get(dataType.getName());
	}

}
