package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.gcube.data.analysis.excel.ColumnModel.ColumnType;
import org.gcube.data.analysis.excel.data.DataColumn;
import org.gcube.data.analysis.excel.data.StringColumn;
import org.gcube.data.analysis.excel.metadata.format.CatchMeasureFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.DataColumnBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans.TableBean;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.DataFormatGeneratorFactory;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.formats.data.DataFormatGeneratorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnFactoryData {
	
	private Logger logger;
	private static ColumnFactoryData instance;
	private Properties 	dataFormats,
						columnTypes;
	
	private String DEFAULT_COLUMN_TYPE = "ATTRIBUTE";
	
	private ColumnFactoryData ()
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.dataFormats = new Properties();
		this.columnTypes = new Properties();
		
		try {
			this.dataFormats.load(this.getClass().getResourceAsStream("/dataformatData.properties"));
		} catch (IOException e) {
			this.logger.error("Unable to find dataformat data properties",e);
		}

		try {
			this.columnTypes.load(this.getClass().getResourceAsStream("/columnTypeData.properties"));
		} catch (IOException e) {
			this.logger.error("Unable to find dataformat data properties",e);
		}
		
	}
	
	public static ColumnFactoryData getInstance ()
	{
		if (instance == null) instance = new ColumnFactoryData();
		return instance;
	}
	
	public DataColumn createColumn (DataColumnBean columnBean,TableBean tableBean,String locale)
	{

		StringColumn response;
		
//		if (columnBean.isPrimary())
//		{
//			response = new StringColumn (columnBean.getName(locale), new CatchMeasureFormat("unit", Arrays.asList("N/A")),ColumnType.MEASURE);
//		}
//		else
//		{
			Column column = columnBean.getColumn();
			String columnType = column.getColumnType().getCode();
			this.logger.debug("Column type "+columnType);
			String typeDataFormatName = this.dataFormats.getProperty(columnType);
			this.logger.debug("Type data format "+typeDataFormatName);
			DataFormatGeneratorData typeDataFormat = DataFormatGeneratorFactory.getDataFormatGeneratorData(typeDataFormatName);
			response = new StringColumn (columnBean.getName(locale),typeDataFormat.getDataFormat(columnBean,locale), getColumnType(columnType));
//		}

		response.setAllData(columnBean.getData());
		
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
