package org.gcube.datapublishing.sdmx.datasource.series.model.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gcube.datapublishing.sdmx.datasource.series.model.ObservationModel;
import org.gcube.datapublishing.sdmx.datasource.series.model.SeriesModel;

public class SeriesModelImpl implements SeriesModel
{
	private List<ObservationModel> observations;
	private Map<String, String> seriesDimensions;
	private Map<String, String> seriesAttributes;
	private StringBuilder seriesIdBuilder;
	
	public SeriesModelImpl() {
		this.observations = new LinkedList<ObservationModel>();
		this.seriesDimensions = new HashMap<String, String>();
		this.seriesAttributes = new HashMap<String, String>();
		this.seriesIdBuilder = new StringBuilder ();
	}

	
	@Override
	public boolean equals(Object obj) {
		
		
		if (obj != null && obj.getClass() == this.getClass())
		{
			return this.seriesIdBuilder.toString().equals(((SeriesModelImpl) obj).seriesIdBuilder.toString());
		}
		else return false;
		
	}
	

	@Override
	public int hashCode() {

		return this.seriesIdBuilder.toString().hashCode();
	}
	




	@Override
	public List<ObservationModel> getObservations() {
		return observations;
	}

	@Override
	public Map<String, String> getSeriesDimensions() {
		return seriesDimensions;
	}

	@Override
	public Map<String, String> getSeriesAttributes() {
		return seriesAttributes;
	}
	

	public void addObservation(ObservationModel observation) {

		
		this.observations.add(observation);
	}

	public void addSeriesDimension(String key, String value) {
		this.seriesIdBuilder.append(value);
		this.seriesDimensions.put(key, value);
	}

	public void addSeriesAttribute(String key, String value) {
		this.seriesAttributes.put(key, value);
	}
	

}
