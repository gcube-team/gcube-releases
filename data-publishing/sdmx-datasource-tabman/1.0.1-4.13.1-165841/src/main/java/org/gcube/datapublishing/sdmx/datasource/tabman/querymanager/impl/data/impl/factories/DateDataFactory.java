package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.impl.factories;

import java.util.Date;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DataFactory;

public class DateDataFactory implements DataFactory {

	@Override
	public TDTypeValue getTypeValue(Object value,  Column referredColumn) {

		
		return new TDDate((Date) value);
	}


}
