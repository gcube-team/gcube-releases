package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.exception.InvalidFilterParameterException;


public interface DataFactory {

	
	TDTypeValue getTypeValue (Object value, Column referredColumn) throws InvalidFilterParameterException;
}
