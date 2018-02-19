package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.impl.factories;

import org.gcube.data.analysis.sdmx.DataInformationProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DataFactory;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.exception.InvalidFilterParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerDataFactory implements DataFactory {

	private Logger logger;
	
	public IntegerDataFactory() {
		this.logger = LoggerFactory.getLogger(IntegerDataFactory.class);
	}
	

	@Override
	public TDTypeValue getTypeValue(Object value, Column referredColumn) throws InvalidFilterParameterException
	{
	
		try
		{
			return  new TDInteger(Integer.parseInt((String)value));
		} catch (ClassCastException e)
		{
			this.logger.error("Invalid value", e);
			throw e;
		}
		
		
		catch (Exception e)
		{
			this.logger.error("Invalid parameter",e);
			throw new InvalidFilterParameterException(DataInformationProvider.getInstance().getColumnConverter().local2Registry(referredColumn.getLocalId().getValue()), "Integer");
		}
		
		
	}

}
