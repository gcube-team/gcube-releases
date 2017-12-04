package org.gcube.contentmanagement.timeseries.geotools.filters.custom;

import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.filters.SpaceFilter;


public class FAOAreaFilter extends SpaceFilter {

	
	public FAOAreaFilter(String timeSeriesName, String mainColumn, String quantitiesColumn) {
		super(timeSeriesName, mainColumn,  quantitiesColumn);
		this.filtername = "FAO_area";
	}

	public List<String> findCSquareCodes(String place, List<String> prevCsquares){
		
		List<String> csquares = geofinder.findDirectlyCSquareCodes(place, prevCsquares);
		
		return csquares;
	}
}
