package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.impl.factories;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DataFactory;

public class StringDataFactory implements DataFactory {



	@Override
	public TDTypeValue getTypeValue(Object value, Column referredColumn) {
		return new TDText((String) value);
	}

}
