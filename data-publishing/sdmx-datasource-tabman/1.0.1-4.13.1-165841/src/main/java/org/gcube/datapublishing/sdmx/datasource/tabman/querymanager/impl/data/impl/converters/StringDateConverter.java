package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.impl.converters;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.metadata.column.PeriodTypeMetadata;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl.data.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringDateConverter implements DateConverter {

	private Logger logger;
	
	public StringDateConverter() {
		this.logger = LoggerFactory.getLogger(StringDateConverter.class);
	}
	
	@Override
	public Object convertDate(Date date, Column referredColumn) {
		String response = null;

		try
		{
			PeriodType periodType = getPeriodType(referredColumn);
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
			String pattern = "";
			int divFactor = 10;
			
			switch (periodType)
			{
			case DAY:
				pattern = "dd";
			case MONTH:
			case QUARTER:
				pattern = "MM"+pattern;
			case YEAR:
				pattern = "yyyy"+pattern;
				simpleDateFormat.applyPattern(pattern);
				response = simpleDateFormat.format(date);
				break;
			case CENTURY:
				divFactor = divFactor*10;
			case DECADE:
				pattern = "yyyy";
				simpleDateFormat.applyPattern(pattern);
				response = String.valueOf((Integer.parseInt(simpleDateFormat.format(date))/divFactor));
				break;
		
			}
			return response;
		} catch (NoSuchMetadataException e)
		{
			this.logger.error("Time period metadata not present: unable to retrieve the time format",e);
		}
		
		return response;
	}

	private PeriodType getPeriodType (Column referredColumn) throws NoSuchMetadataException
	{
			PeriodTypeMetadata periodTypeMetadata = referredColumn.getMetadata(PeriodTypeMetadata.class);
			return periodTypeMetadata.getType();
	}

	
}
