package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats;

import org.gcube.data.analysis.excel.metadata.format.DataFormat;
import org.gcube.data.analysis.excel.metadata.format.GenericFormat;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.ColumnBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.TemplateBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.data.DataFormatGeneratorData;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.metadata.DataFormatGeneratorMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericDataFormatGenerator implements DataFormatGeneratorData, DataFormatGeneratorMetadata
{
	private Logger logger;
	
	public GenericDataFormatGenerator() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		
	}
	
	
	
	@Override
	public DataFormat getDataFormat (TemplateColumn<?> column,TemplateBean templateBean)
	{
		this.logger.debug("Generic format without references ");
		return new GenericFormat(column.getLabel()+"_reference", "N/D");
	}



	@Override
	public DataFormat getDataFormat(ColumnBean columnBean,String locale) {
		this.logger.debug("Generic format without references ");
		return new GenericFormat(columnBean.getName(locale)+"_reference", "N/D");
	}

}
