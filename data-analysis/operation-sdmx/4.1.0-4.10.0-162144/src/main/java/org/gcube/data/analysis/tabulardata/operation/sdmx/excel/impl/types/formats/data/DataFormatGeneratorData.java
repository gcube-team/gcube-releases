package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.data;

import org.gcube.data.analysis.excel.metadata.format.DataFormat;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.ColumnBean;

public interface DataFormatGeneratorData 
{
	
	public DataFormat getDataFormat (ColumnBean columnBean,String locale);
}
