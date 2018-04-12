package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.impl.factories;

import org.gcube.data.analysis.sdmx.DataInformationProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DataFactory;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.exception.InvalidFilterParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumericDataFactory implements DataFactory {

	private Logger logger;
	
	public NumericDataFactory() {
		this.logger = LoggerFactory.getLogger(NumericDataFactory.class);
	}
	

	@Override
	public TDTypeValue getTypeValue(Object value, Column referredColumn) throws InvalidFilterParameterException
	{
	
		try
		{
			return  new TDNumeric(Double.parseDouble((String)value));
		} catch (ClassCastException e)
		{
			this.logger.error("Invalid value", e);
			throw e;
		}
		
		
		catch (Exception e)
		{
			this.logger.error("Invalid parameter",e);
			throw new InvalidFilterParameterException(DataInformationProvider.getInstance().getColumnConverter().local2Registry(referredColumn.getLocalId().getValue()), "Numeric");
		}
		
		
	}

}
