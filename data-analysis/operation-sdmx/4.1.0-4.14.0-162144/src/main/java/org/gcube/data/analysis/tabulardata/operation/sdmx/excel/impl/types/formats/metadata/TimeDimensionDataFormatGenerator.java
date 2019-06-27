package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.metadata;

import java.util.Arrays;

import org.gcube.data.analysis.excel.metadata.format.DataFormat;
import org.gcube.data.analysis.excel.metadata.format.GenericMeasureFormat;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.commons.utils.TimeDimensionReference;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.TemplateBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeDimensionDataFormatGenerator implements DataFormatGeneratorMetadata
{
	private Logger logger;
	
	public TimeDimensionDataFormatGenerator() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		
	}
	
	
	
	@Override
	public DataFormat getDataFormat (TemplateColumn<?> column,TemplateBean templateBean)
	{
		this.logger.debug("Attribute data format: data type measure ");
		String timeDimensionName = ((TimeDimensionReference) column.getReference()).getPeriod().getName();
		this.logger.debug("Data format "+timeDimensionName);
		return new GenericMeasureFormat(column.getLabel()+"_reference", Arrays.asList(timeDimensionName));
	}




}
