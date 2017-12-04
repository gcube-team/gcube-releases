package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.gcube.data.analysis.excel.ColumnModel;
import org.gcube.data.analysis.excel.ColumnModel.ColumnType;
import org.gcube.data.analysis.excel.metadata.MetadataColumn;
import org.gcube.data.analysis.excel.metadata.format.CatchMeasureFormat;
import org.gcube.data.analysis.tabulardata.commons.templates.model.columns.TemplateColumn;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.TemplateBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.DataFormatGeneratorFactory;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.metadata.DataFormatGeneratorMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnFactoryMetadata {
	
	private Logger logger;
	private static ColumnFactoryMetadata instance;
	private Properties dataFormats,
						columnTypes;
	
	private String DEFAULT_COLUMN_TYPE = "ATTRIBUTE";
	
	private ColumnFactoryMetadata ()
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.dataFormats = new Properties();
		this.columnTypes = new Properties();
		
		try {
			this.dataFormats.load(this.getClass().getResourceAsStream("/dataformatMetadata.properties"));
		} catch (IOException e) {
			this.logger.error("Unable to find dataformat metadata properties",e);
		}

		try {
			this.columnTypes.load(this.getClass().getResourceAsStream("/columnTypeMetadata.properties"));
		} catch (IOException e) {
			this.logger.error("Unable to find dataformat data properties",e);
		}
	}
	
	public static ColumnFactoryMetadata getInstance ()
	{
		if (instance == null) instance = new ColumnFactoryMetadata();
		return instance;
	}
	


	public ColumnModel createColumn (TemplateColumn<?> template,TemplateBean templateBean,boolean isPrimaryMeasure)
	{

		this.logger.debug("Data Type = "+template.getValueType().getName());
		ColumnModel response;
		
//		if (isPrimaryMeasure)
//		{
//			response = new MetadataColumn(template.getLabel(), new CatchMeasureFormat("unit", Arrays.asList("N/A")),ColumnType.MEASURE);
//		}
//		else
//		{
			String columnType = template.getColumnType().toString();
			this.logger.debug("Column type "+columnType);
			String typeDataFormatName = this.dataFormats.getProperty(columnType);
			this.logger.debug("Type data format "+typeDataFormatName);
			DataFormatGeneratorMetadata typeDataFormat = DataFormatGeneratorFactory.getDataFormatGeneratorMetadata(typeDataFormatName);
			response = new MetadataColumn(template.getLabel(), typeDataFormat.getDataFormat(template,templateBean),getColumnType(typeDataFormatName));
//		}

		return response;
		
	}

	private ColumnType getColumnType (String typeCode)
	{
		this.logger.debug("Looking for the column type for type code "+typeCode);
		String columnTypeString = this.columnTypes.getProperty(typeCode);
		this.logger.debug("Column type "+columnTypeString);
		
		if (columnTypeString == null) columnTypeString = DEFAULT_COLUMN_TYPE;
		
		ColumnType response = Enum.valueOf(ColumnType.class, columnTypeString);
		this.logger.debug("Column type "+response);
		return response;
		
	}


	
}
