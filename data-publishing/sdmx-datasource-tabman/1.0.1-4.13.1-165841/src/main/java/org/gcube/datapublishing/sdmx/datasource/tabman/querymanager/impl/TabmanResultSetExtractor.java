package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl;

import java.util.LinkedList;
import java.util.List;

import org.gcube.datapublishing.sdmx.datasource.data.ResultSetExtractor;
import org.gcube.datapublishing.sdmx.datasource.data.ResultSetExtractorAbstractImpl;
import org.gcube.datapublishing.sdmx.datasource.data.beans.AttributeColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.ColumnBean;
import org.gcube.datapublishing.sdmx.datasource.data.beans.DimensionColumnBean;
import org.gcube.datapublishing.sdmx.datasource.series.model.SeriesModel;
import org.gcube.datapublishing.sdmx.datasource.series.model.impl.ObservationModelImpl;
import org.gcube.datapublishing.sdmx.datasource.series.model.impl.SeriesModelImpl;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.RowModel;
import org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.json.Rows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabmanResultSetExtractor extends ResultSetExtractorAbstractImpl  implements ResultSetExtractor {

	private Logger logger;
	private List<SeriesModel> seriesModelList;
	

	
	public TabmanResultSetExtractor() {
		super ();
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.seriesModelList = new LinkedList<>();
	}

	public void processRows(Rows rows) 
	{
		
		List<RowModel> rowModels = rows.getRows();
		
		int observationDimensionColumnIdPosition = getDataPosition(this.metadataProvider.getObservationDimension());
		int primaryMeasureColumnIdPosition = getDataPosition(this.metadataProvider.getPrimaryMeasure());
		
		try
		{
			for (RowModel row : rowModels)
			{
				this.logger.debug("Parsing row");
				this.logger.debug("Loading Series");
				
				
				SeriesModelImpl seriesModel = getCurrentSeriesModel(row);
				
				if (checkIfAddObservation (seriesModel,observationDimensionColumnIdPosition,primaryMeasureColumnIdPosition)) addObservations(row, seriesModel, observationDimensionColumnIdPosition, primaryMeasureColumnIdPosition);
		
			}
		} catch (ObservationExceeded e)
		{
			this.logger.debug(e.getMaxObservationLogMessage());
		}
		


	}
	
	private short getDataPosition (ColumnBean column)
	{
		short response = -1;
		
		try
		{
			response = this.metadataProvider.getDataPosition(column.getId());
		} catch (Exception e)
		{
			this.logger.debug("Data position not available");
		}
		
		return response;
	}
	
	
	private boolean checkIfAddObservation (SeriesModel seriesModel, int observationDimensionColumnIdPosition, int primaryMeasureColumnIdPosition)
	{
		return (observationDimensionColumnIdPosition != -1 && primaryMeasureColumnIdPosition != -1 && 
				(!this.metadataProvider.timeBasedObservations() || this.metadataProvider.getNObservations()<=0 || seriesModel.getObservations().size()< this.metadataProvider.getNObservations()));
	}

	private void addObservations (RowModel rowModel, SeriesModelImpl seriesModel,int observationDimensionColumnIdPosition, int primaryMeasureColumnIdPosition)
	{
		logger.debug("Processing new row");
		ObservationModelImpl observation = new ObservationModelImpl();
		String observationDimension = rowModel.getElement(observationDimensionColumnIdPosition);
		String observationValue = rowModel.getElement(primaryMeasureColumnIdPosition);
		logger.debug("Adding observation of "+observationDimension+ " = "+observationValue);
		observation.setObservationDimension(observationDimension);
		observation.setValue(observationValue);
		//Write Observation
		logger.debug("Adding observation attributes");
		//Write Observation Attributes
		
		List<AttributeColumnBean> attributes = this.metadataProvider.getObservationAttributes();
		
		for (AttributeColumnBean attribute : attributes)
		{
			
			String attributeColumnId = attribute.getId();
			String attributeColumnLabel = attribute.getConcept();
			String attributeValue = rowModel.getElement(this.metadataProvider.getDataPosition(attributeColumnId));
			this.logger.debug("Adding observation attribute column "+attributeColumnId+" called "+attributeColumnLabel+" value "+attributeValue);
			
			if (attributeValue != null) observation.addAttribute(attributeColumnLabel,attributeValue);	
	
			else logger.warn("Observation attribute not found");
		}
		
		if (this.metadataProvider.getNObservations()<=0 || seriesModel.getObservations().size()< this.metadataProvider.getNObservations()) seriesModel.addObservation(observation);

		
	}

	private boolean checkIfAddSeriesModel ()
	{
		return (this.metadataProvider.timeBasedObservations() || this.metadataProvider.getNObservations()<=0 || this.seriesModelList.size()< this.metadataProvider.getNObservations());
	}

	
	private SeriesModelImpl getCurrentSeriesModel (RowModel rowModel) throws ObservationExceeded
	{
		logger.debug("Getting the current series model");
		List<DimensionColumnBean> dimensions = this.metadataProvider.getDimensions();
		SeriesModelImpl temp = new SeriesModelImpl();
		SeriesModelImpl response = null;
		
		for (DimensionColumnBean dimension : dimensions)
		{
			
			String dimensionColumnId = dimension.getId();
			String dimensionColumnLabel = dimension.getConcept();
			String dimensionValue = rowModel.getElement(this.metadataProvider.getDataPosition(dimensionColumnId));
			this.logger.debug("Adding dimension column "+dimensionColumnId+" called "+dimensionColumnLabel+" value "+dimensionValue);
			
			if (dimensionValue != null) temp.addSeriesDimension(dimensionColumnLabel, dimensionValue);
				
			
			else logger.warn("Dimension not found");
		}

		int seriesModeIndex = this.seriesModelList.indexOf(temp);
		
		if (seriesModeIndex == -1 && checkIfAddSeriesModel())
		{
			this.logger.debug("Creating new series model");
			this.seriesModelList.add(temp);
			response = temp;
		}
		else if (seriesModeIndex == -1) // series model cannot be added
		{
			this.logger.debug("The number of series allowed for this series group has been exceeded");
			throw new ObservationExceeded(this.metadataProvider.getNObservations());
			
		}
		else // The series model exists
		{
			this.logger.debug("Series model found");
			response = (SeriesModelImpl) this.seriesModelList.get(seriesModeIndex);
		}
		
		
		
		List<AttributeColumnBean> attributes = this.metadataProvider.getDimensionAttributes();
		
		for (AttributeColumnBean attribute : attributes)
		{
			
			String attributeColumnId = attribute.getId();
			String attributeColumnLabel = attribute.getConcept();
			String attributeValue = rowModel.getElement(this.metadataProvider.getDataPosition(attributeColumnId));
			this.logger.debug("Adding dimension attribute column "+attributeColumnId+" associated to concept "+attributeColumnLabel+" value "+attributeValue);
			
			if (attributeValue != null) response.addSeriesAttribute(attributeColumnLabel,attributeValue);	
	
			else logger.warn("Dimension attribute not found");
		}
		
		return response;
	}

	@Override
	public List<SeriesModel> getSeriesList() {

		return this.seriesModelList;
	}
	
//	private String getColumnLabel (String componentId, Map<String, String> map)
//	{
//		logger.debug("Getting column value for "+componentId);
//		String columnValue = map.get(componentId);
//		
//		if (columnValue == null)
//		{
//			logger.warn("Column value not found, using component id");
//			columnValue = componentId;
//		}
//		
//		logger.debug("Response value "+columnValue);
//		return columnValue;
//	}


}
