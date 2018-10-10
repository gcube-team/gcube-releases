package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.impl.factories;

import org.gcube.data.analysis.sdmx.DataInformationProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DataFactory;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.exception.InvalidFilterParameterException;

public class BooleanDataFactory implements DataFactory {



	@Override
	public TDTypeValue getTypeValue(Object value, Column referredColumn) throws InvalidFilterParameterException {
		
		String stringValue = (String) value;
		boolean booleanValue = false;
		
		if (stringValue != null && stringValue.equalsIgnoreCase("true")) booleanValue = true;
		else if (stringValue != null && stringValue.equalsIgnoreCase("false")) booleanValue = false;
		else throw new InvalidFilterParameterException(DataInformationProvider.getInstance().getColumnConverter().local2Registry(referredColumn.getLocalId().getValue()), "Boolean");
		
		
		return new TDBoolean(booleanValue);
	}

}
