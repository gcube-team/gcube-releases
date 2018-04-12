package org.gcube.datapublishing.sdmx.datasource.series.model;

import java.util.List;
import java.util.Map;

public interface SeriesModel 
{

	
	public List<ObservationModel> getObservations();

	public Map<String, String> getSeriesDimensions();

	public Map<String, String> getSeriesAttributes();
	


}
