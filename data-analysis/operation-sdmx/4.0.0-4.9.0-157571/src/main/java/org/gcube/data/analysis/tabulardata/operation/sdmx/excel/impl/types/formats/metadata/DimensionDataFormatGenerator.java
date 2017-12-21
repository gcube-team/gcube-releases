package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.metadata;

import java.util.List;

import org.gcube.data.analysis.excel.metadata.format.CodelistDataFormat;
import org.gcube.data.analysis.excel.metadata.format.CodelistDataFormat.CodeMap;
import org.gcube.data.analysis.excel.metadata.format.DataFormat;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.TemplateBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodeBean;
import org.sdmxsource.sdmx.api.model.beans.codelist.CodelistBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DimensionDataFormatGenerator implements DataFormatGeneratorMetadata
{
	private Logger logger;

	
	public DimensionDataFormatGenerator() {
		this.logger = LoggerFactory.getLogger(this.getClass());
		
	}

	@Override
	public DataFormat getDataFormat (TemplateColumn<?> column,TemplateBean templateBean)
	{
		this.logger.debug("Referenced data format: generic dimension ");
		String columnName = column.getLabel();
		this.logger.debug("Column name = "+columnName);
		CodelistBean codelist = templateBean.getCodelists().get(columnName);
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
		
	
		return new CodelistDataFormat(column.getLabel()+"_reference", codelistColumns);
	}

	
}
