package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.metadata;

import org.gcube.data.analysis.excel.metadata.format.DataFormat;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.TemplateBean;

public interface DataFormatGeneratorMetadata 
{

	
	public DataFormat getDataFormat (TemplateColumn<?> template, TemplateBean templateBean);
}
