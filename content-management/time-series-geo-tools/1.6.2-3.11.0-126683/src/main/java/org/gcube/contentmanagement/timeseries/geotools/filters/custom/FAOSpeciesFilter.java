package org.gcube.contentmanagement.timeseries.geotools.filters.custom;

import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.filters.SpeciesFilter;


public class FAOSpeciesFilter extends SpeciesFilter {

	public FAOSpeciesFilter(String timeSeriesName, String mainColumn, String optionalColumn, String quantitiesColumn) {
		super(timeSeriesName, mainColumn, optionalColumn, quantitiesColumn);
		this.filtername = "FAO_area_and_species";
	}

	
	public List<String> findCSquareCodes(String place, List<String> prevCsquares){
		
		List<String> csquares = geofinder.findDirectlyCSquareCodes(place, prevCsquares);
		
		return csquares;
	}
}
