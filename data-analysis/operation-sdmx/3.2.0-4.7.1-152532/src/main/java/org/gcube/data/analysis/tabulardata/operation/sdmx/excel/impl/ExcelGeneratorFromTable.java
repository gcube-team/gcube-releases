package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl;

import java.util.Iterator;
import java.util.List;

import org.gcube.data.analysis.excel.data.Column;
import org.gcube.data.analysis.excel.data.TableMetaData;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.ExcelGenerator;
import org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.types.ColumnFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelGeneratorFromTable extends ExcelGeneratorAbstractImpl implements ExcelGenerator {

	private Logger logger;
	private Table table;
	private String locale;
	private static final String DEFAULT_LOCALE = "en";
	private boolean defaultLocale;
	
	public ExcelGeneratorFromTable(Table table,String locale) {
		super ();
		this.logger = LoggerFactory.getLogger(this.getClass());
		if (locale == null || locale.equalsIgnoreCase(DEFAULT_LOCALE))
		{
			this.locale = DEFAULT_LOCALE;
			this.defaultLocale = true;
		}
		else this.defaultLocale = false;
		this.table = table;
		
	}
	public ExcelGeneratorFromTable(Table table) {
		this (table, DEFAULT_LOCALE);
	}

	private List<LocalizedText> getNamesMetadata (org.gcube.data.analysis.tabulardata.model.column.Column column)
	{
		this.logger.debug("Looking for metadata");
		List<LocalizedText> response = null;
		
		try
		{
			response = column.getMetadata(NamesMetadata.class).getTexts();
			this.logger.debug("Metadata found");
			
		} catch (NoSuchMetadataException e)
		{
			this.logger.warn("Names metadata not found the object ");
		}
		
		return response;
	}
	
	private String getDescriptionMetadata (Table table)
	{
		this.logger.debug("Looking for metadata");
		String response = null;
		
		try
		{
			TableDescriptorMetadata metadata = table.getMetadata(TableDescriptorMetadata.class); 
			response = metadata.getName();
			
			if (metadata.getVersion() != null) response = response+"_"+metadata.getVersion();
			
			this.logger.debug("Metadata found");
			
		} catch (NoSuchMetadataException e)
		{
			this.logger.warn("Names metadata not found the object ");
			response = table.getName();
		}
		
		return response;
	}
	
	private TableMetaData generateTableMetadata ()
	{
		logger.debug("Generating template columns");
		List<org.gcube.data.analysis.tabulardata.model.column.Column> columns = this.table.getColumns();
		this.logger.debug("Table name "+this.table.getName());
		String tableName = getDescriptionMetadata(this.table); 
		this.logger.debug("Table name "+tableName);
		TableMetaData tableMetadata = new TableMetaData (tableName);
		
		for (org.gcube.data.analysis.tabulardata.model.column.Column column : columns)
		{
			
			List<LocalizedText> columnNamesMetadata = getNamesMetadata(column);
			String columnName = getLocalizedName(columnNamesMetadata, column.getName());
			logger.debug("Adding column "+columnName);
			Column metadataColumn = ColumnFactory.getInstance().createColumn(columnName, column.getColumnType().getClass().getName());
			
			if (metadataColumn == null)
			{
				this.logger.debug("Column "+columnName+ " has been excluded by configuration since it is "+column.getColumnType().getClass()+" type");
			}
			else
			{
				tableMetadata.addColumn(metadataColumn);
				logger.debug("Column added");
			}
	
		}
		
		return tableMetadata;
	}
	
	private String getLocalizedName (List<LocalizedText> localizedTextList, String defaultValue)
	{
		String result = null;
		
		if (localizedTextList != null && !localizedTextList.isEmpty())
		{
			this.logger.debug("Parsing localized text");
			String defaultLocaleResult = null;
			Iterator<LocalizedText> localizedTextIterator = localizedTextList.iterator();
			
			while (result == null && localizedTextIterator.hasNext())
			{
				LocalizedText localizedText = localizedTextIterator.next();
				String locale = localizedText.getLocale();
				this.logger.debug("Found locale "+locale);
				
				if (this.locale.equalsIgnoreCase(locale)) 
				{
					result = localizedText.getValue();
					this.logger.debug("Found name "+result);
				}
				else if (!defaultLocale && DEFAULT_LOCALE.equalsIgnoreCase(locale))
				{
					this.logger.debug("Getting default locale value ");
					defaultLocaleResult = localizedText.getValue();
					this.logger.debug("Found name "+defaultLocaleResult);
					
				}
				
			}
			
			if (result == null && defaultLocaleResult != null)
			{
				this.logger.debug("Using default locale "+DEFAULT_LOCALE);
				result = defaultLocaleResult;
			}
			else if (result == null)
			{
				this.logger.debug("Locales not available, using a random one");
				LocalizedText localizedText = localizedTextList.get(0);
				this.logger.debug("Locale "+localizedText.getLocale());
				result = localizedText.getValue();			
			}
		}
		else {
			this.logger.debug("No locale found, using "+defaultValue);
			result = defaultValue;
		}
	
		this.logger.debug("Result "+result);
		return result;
	}
	
	@Override
	public void generateExcel(String fileName, String folderName) {

		this.logger.debug("Generating exel file "+fileName+ " in the folder "+folderName);
		super.setTable(this.generateTableMetadata());
		this.logger.debug("Metadata table generated");
		super.generateExcel(fileName, folderName);
	}
	

}
