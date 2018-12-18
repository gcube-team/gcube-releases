package org.gcube.contentmanagement.timeseries.geotools.filters.custom;

import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.filters.SpeciesFilter;


public class FAOYearFilter extends SpeciesFilter {

	public FAOYearFilter(String timeSeriesName, String mainColumn, String optionalColumn, String quantitiesColumn) {
		super(timeSeriesName, mainColumn, optionalColumn, quantitiesColumn);
		this.filtername = "FAO_area and_time";
	}

	public void initFilter(){
		
	}
	
	public List<String> postFilterSquare(List<String> csquares){
		return csquares;
	}
	
	public List<String> findCSquareCodes(String place, List<String> prevCsquares){
		
		List<String> csquares = geofinder.findDirectlyCSquareCodes(place, prevCsquares);
		
		return csquares;
	}
}
