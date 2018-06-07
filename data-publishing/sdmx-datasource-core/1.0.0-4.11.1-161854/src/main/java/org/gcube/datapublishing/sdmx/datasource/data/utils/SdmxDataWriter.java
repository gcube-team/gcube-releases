package org.gcube.datapublishing.sdmx.datasource.data.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.datapublishing.sdmx.datasource.series.model.ObservationModel;
import org.gcube.datapublishing.sdmx.datasource.series.model.SeriesModel;
import org.sdmxsource.sdmx.api.engine.DataWriterEngine;
import org.sdmxsource.sdmx.api.model.data.query.DataQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * Class retrieves data from a SQL database
 */
public class SdmxDataWriter 
{

	private Logger logger;
	private DataQuery dataQuery;
	private DataWriterEngine dataWriter;
	
	/**
	 * 
	 * @param dataQuery
	 */
	public SdmxDataWriter (DataQuery dataQuery)
	{
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.dataQuery = dataQuery;
	}
	
	
	/**
	 * 
	 * @param dataWriter
	 */
	public void setDataWriter (DataWriterEngine dataWriter)
	{
		this.dataWriter = dataWriter;
	}
	

	public void writeData (List<SeriesModel> seriesModelList)
	{
		this.logger.debug("Writing data");
		this.dataWriter.startDataset(null, this.dataQuery.getDataflow(), this.dataQuery.getDataStructure(),null);
		
		for (SeriesModel seriesModel : seriesModelList)
		{
			this.dataWriter.startSeries();
			this.logger.debug("Writing series...");
			writeSeriesDimension( seriesModel.getSeriesDimensions());
			writeAttributes(seriesModel.getSeriesAttributes());
			writeObservations( seriesModel.getObservations());
			logger.debug("Series written");
		}
		
		dataWriter.close();

	}
	
	/**
	 * 
	 * @param dataWriter
	 * @param dataMap
	 */
	private void writeSeriesDimension (Map<String, String> dataMap)
	{
		this.logger.debug("Writing series data");
		Iterator<String> keysIterator = dataMap.keySet().iterator();
		
		while (keysIterator.hasNext())
		{
			String key = keysIterator.next();
			String value = dataMap.get(key);
			this.logger.debug("Writing series "+key+ " "+value);
			this.dataWriter.writeSeriesKeyValue(key, value);
		}

	}
	
	/**
	 * 
	 * @param dataWriter
	 * @param attributesMap
	 */
	private void writeAttributes (Map<String, String> attributesMap)
	{
		this.logger.debug("Writing series data");
		Iterator<String> keysIterator = attributesMap.keySet().iterator();
		
		while (keysIterator.hasNext())
		{
			String key = keysIterator.next();
			String value = attributesMap.get(key);
			this.logger.debug("Writing series "+key+ " "+value);
			this.dataWriter.writeAttributeValue(key, value);
		}

	}
	
	/**
	 * 
	 * @param dataWriter
	 * @param observations
	 */
	private void  writeObservations (List<ObservationModel> observations)
	{
		this.logger.debug("Writing observations");
		
		for (ObservationModel observation: observations)
		{
			this.logger.debug("Observation Dimension value "+observation.getObservationDimension()+" Observation value "+observation.getValue());
			this.dataWriter.writeObservation(observation.getObservationDimension(), observation.getValue());
			Map<String, String> observationAttributes = observation.getAttributes();
			writeAttributes(observationAttributes);
			logger.debug("Observation written");
		}
	}
	
}


