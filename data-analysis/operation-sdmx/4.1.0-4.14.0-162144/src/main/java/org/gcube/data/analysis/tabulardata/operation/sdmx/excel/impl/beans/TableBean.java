package org.gcube.data.analysis.tabulardata.operation.sdmx.excel.impl.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.metadata.common.TableDescriptorMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public class TableBean {

	private Table table;
	private Map<String, DataColumnBean> columnMap;
	private List<DataColumnBean> measureColumns,
						dimensionColumns,
						attributeColumns;
	private DataColumnBean timeDimensionColumn;
	private DataColumnBean primaryMeasure;
	private String tableName;
	
	public TableBean (Table table)
	{
		this.table = table;
		this.measureColumns = new ArrayList<>();
		this.dimensionColumns = new ArrayList<>();
		this.attributeColumns = new ArrayList<>();
		this.columnMap = new HashMap<>();
	}

	public Table getTable() {
		return table;
	}

	public String getTableName ()
	{
		if (this.tableName == null)
		{
			this.tableName = getDescriptionMetadata(this.table);
		}
		
		return this.tableName;
	}


	public void setTimeDimension (DataColumnBean timeDimension)
	{
		this.timeDimensionColumn = timeDimension;
		
		if (timeDimension != null) this.columnMap.put(timeDimension.getColumn().getName(), timeDimension);
	}


	public void setPrimaryMeasure (DataColumnBean primaryMeasure)
	{
		this.primaryMeasure = primaryMeasure;
		
		if (primaryMeasure != null) this.columnMap.put(primaryMeasure.getColumn().getName(), primaryMeasure);
	}
	
	public void addMeasureColumn (DataColumnBean measure)
	{
		this.measureColumns.add(measure);
		
		if (measure != null) this.columnMap.put(measure.getColumn().getName(), measure);
	}
	
	public void addDimensionColumn (DataColumnBean dimension)
	{
		this.dimensionColumns.add(dimension);
		
		if (dimension != null) this.columnMap.put(dimension.getColumn().getName(), dimension);
	}
	
	public void addAttributeColumn (DataColumnBean attribute)
	{
		this.attributeColumns.add(attribute);
		
		if (attribute != null) this.columnMap.put(attribute.getColumn().getName(), attribute);
	}

	public List<DataColumnBean> getMeasureColumns() {
		return measureColumns;
	}

	public List<DataColumnBean> getDimensionColumns() {
		return dimensionColumns;
	}

	public List<DataColumnBean> getAttributeColumns() {
		return attributeColumns;
	}

	public DataColumnBean getTimeDimensionColumn() {
		return timeDimensionColumn;
	}

	public DataColumnBean getPrimaryMeasure() {
		return primaryMeasure;
	}
	
	private String getDescriptionMetadata (Table table)
	{

		String response = null;
		
		try
		{
			TableDescriptorMetadata metadata = table.getMetadata(TableDescriptorMetadata.class); 
			response = metadata.getName();
			
			if (metadata.getVersion() != null) response = response+"_"+metadata.getVersion();
			
			
		} catch (NoSuchMetadataException e)
		{
			response = table.getName();
		}
		
		return response;
	}
	
	public DataColumnBean getColumnByName (String columnName)
	{
		return this.columnMap.get(columnName);
	}


}
