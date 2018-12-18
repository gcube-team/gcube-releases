package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

public interface DataFactoryMap {

	public DataFactory getDataFactory (DataType dataType);
	
}
