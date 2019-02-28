package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.data;

import java.util.Arrays;
import java.util.List;

import org.gcube.data.analysis.excel.metadata.format.CodelistDataFormat;
import org.gcube.data.analysis.excel.metadata.format.CodelistDataFormat.CodeMap;
import org.gcube.data.analysis.excel.metadata.format.DataFormat;
import org.gcube.data.analysis.excel.metadata.format.GenericMeasureFormat;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.ColumnBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodeBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DimensionAttributeDataFormatGenerator implements DataFormatGeneratorData
{
	private Logger logger;

	
	public DimensionAttributeDataFormatGenerator() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		
	}

	@Override
	public DataFormat getDataFormat(ColumnBean columnBean,String locale) 
	{
		this.logger.debug("Referenced data format: generic dimension or attribute");
		String columnName = columnBean.getName(locale);
		this.logger.debug("Column name = "+columnName);
		CodelistBean codelist = columnBean.getAssociatedCodelist();
		
		if (codelist != null)
		{
			List<CodeBean> codes = codelist.getItems();
			CodeMap codelistColumns = new CodeMap();
			
			for (CodeBean code : codes)
			{
				String id = code.getId();
				this.logger.debug("Loading code id "+id);
				String description = code.getName().toString();
				this.logger.debug("Loading code description "+description);
				codelistColumns.addElement(id, description);
			}
			
		
			return new CodelistDataFormat(columnName+"_reference", codelistColumns);
		}
		else
		{
			this.logger.debug("Attribute data format: data type measure ");
			String dataType = columnBean.getColumn().getDataType().getName();
			this.logger.debug("Data type "+dataType);
			return new GenericMeasureFormat(columnName+"_reference", Arrays.asList(dataType));
		}
		

	}

}
