package org.gcube.contentmanagement.timeseries.geotools.filters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.finder.GeoAreaFinder;
import org.gcube.contentmanagement.timeseries.geotools.finder.TimeSeriesAggregator;
import org.gcube.contentmanagement.timeseries.geotools.representations.GISLayer;

public class AFilter {

	
	protected boolean isSpaceFilter;
	protected String timeSeriesName;
	protected String ref_country = "ref_country";
	protected String ref_species = "ref_species";
	protected String aggregationColumn;
	protected String informationColumn;
	protected String quantitiesColumn;
	protected GeoAreaFinder geofinder;
	public String valuesColumnName = "maxspeciescountinacell";
	public static final String defaultvaluesColumnName = "maxspeciescountinacell";
	protected float status;
	protected String filtername = "distribution";
	
	public AFilter(boolean isspacefilter,String timeSeriesName, String mainColumn,String optionalColumn,String quantitiesColumn){
		
		this.timeSeriesName = timeSeriesName;
		this.aggregationColumn = mainColumn;
		this.informationColumn = optionalColumn;
		this.quantitiesColumn = quantitiesColumn;
		this.isSpaceFilter = isspacefilter;
		
	}
	
	//NOTE: Overwrite this method for getting csquare from other tables or data source
	public List<String> findCSquareCodes(String place, List<String> prevCsquares) throws Exception{
		List<String> csquares = geofinder.findCSquareCodes(place, prevCsquares);
		return csquares;
	}
	
	public String getRef_country() {
		return ref_country;
	}

	public void setRef_country(String ref_country) {
		if (ref_country!=null)
			this.ref_country = ref_country;
	}
	
	public String getRef_species() {
		return ref_species;
	}

	public void setRef_species(String ref_species) {
		if (ref_species!=null)
			this.ref_species = ref_species;
	}
	
	
	public void setSpaceFilter(boolean isSpaceFilter) {
		this.isSpaceFilter = isSpaceFilter;
	}

	public boolean isSpaceFilter() {
		return isSpaceFilter;
	}
	
	public String getTimeSeriesName() {
		return timeSeriesName;
	}
	public void setTimeSeriesName(String timeSeriesName) {
		this.timeSeriesName = timeSeriesName;
	}
	public String getAggregationColumn() {
		return aggregationColumn;
	}
	public void setAggregationColumn(String aggregationColumn) {
		this.aggregationColumn = aggregationColumn;
	}
	public String getInformationColumn() {
		return informationColumn;
	}
	public void setInformationColumn(String informationColumn) {
		this.informationColumn = informationColumn;
	}
	public String getQuantitiesColumn() {
		return quantitiesColumn;
	}
	public void setQuantitiesColumn(String quantitiesColumn) {
		this.quantitiesColumn = quantitiesColumn;
	}
	
	public void initFilter(){
		
	}
	
	public List<String> postFilterSquare(List<String> csquares){
		return csquares;
	}
	
	public List<GISLayer> filter(GISLayer previousGisLayer, ConnectionsManager connectionsManager, GeoAreaFinder geofind) throws Exception {

		String name = this.getAggregationColumn();
		String timeseriesname = this.getTimeSeriesName();
		String placesColumn = this.getAggregationColumn();
		String quantitiesColumn = this.getQuantitiesColumn();
		if ( (timeseriesname == null) || (placesColumn == null) || (quantitiesColumn == null))
		{
			throw new Exception("Inconsistent Values");
		}
			
		if (previousGisLayer != null)
			name = previousGisLayer.getLayerName() + "_" + name;

		// create a new gis layer with the name of the place
		
		String layername = (placesColumn.substring(0,Math.min(10, placesColumn.length())) + "_" + UUID.randomUUID()).replace("-", "_");
		GISLayer layer = new GISLayer(layername);
		layer.setLayerTitle(filtername);
		
		// get the squares by means of the finder
		if (geofind == null)
			geofinder = new GeoAreaFinder(connectionsManager,ref_country);
		else
			geofinder = geofind;

		// aggregate the Time Series
		TimeSeriesAggregator aggregator = new TimeSeriesAggregator(connectionsManager);

		Map<String, String> aggregation = aggregator.aggregateTimeSeries(placesColumn, quantitiesColumn, timeseriesname);
		// aggregation result: place1->(quant1),(quant2) ...
		double max = -Double.MAX_VALUE;
		double min = Double.MAX_VALUE;
		float totalNumber = aggregation.size();
		int counter = 0;
		for (String place : aggregation.keySet()) {
			AnalysisLogger.getLogger().warn("FAOAreaFilter->PLACE:" + place);
			String quant = aggregation.get(place);
			AnalysisLogger.getLogger().trace("FAOAreaFilter->QUANTITY:" + quant);

			// get the associated value
			Double value = 0.00;
			try {
				value = Double.parseDouble(quant);
			} catch (Exception e1) {
			}
			if (value > max)
				max = value;
			if (value < min)
				min = value;
			
			// transform the place to csquares
			List<String> prevCsquares = null;
			if (previousGisLayer != null)
				prevCsquares = previousGisLayer.getCsquareCodes();
			
			List<String> csquares = findCSquareCodes(place, prevCsquares);
			if (csquares.size() > 0) {
				
				// add the csquare codes and value to the layer
				layer.appendListofSquares(csquares, value,place);
			}
			counter++;
			status = (float)counter/totalNumber;
			// setup the layer with the geometries
			// List<String> geometries = geofinder.getGeometries(layer.getCsquareCodes());
			// layer.setGeometries(geometries);
			
		}
		if (max == min)
			max = max+1;
		
		layer.setMax(max);
		layer.setMin(min);
		layer.setValuesColumnName(valuesColumnName);
		AnalysisLogger.getLogger().trace("FAOAreaFilter->VALUES - MIN:" + min+" MAX:"+max);
		
		List<GISLayer> singletonlayers = new ArrayList<GISLayer>();
		singletonlayers.add(layer);
		status = 100f;
		return singletonlayers;
	}

	public void setStatus(float status) {
		this.status = status;
	}

	public float getStatus() {
		return status;
	}
	
}
