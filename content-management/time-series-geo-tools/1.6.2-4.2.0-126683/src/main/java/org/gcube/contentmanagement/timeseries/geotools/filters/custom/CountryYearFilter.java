package org.gcube.contentmanagement.timeseries.geotools.filters.custom;

import java.util.List;

import org.gcube.contentmanagement.timeseries.geotools.filters.SpeciesFilter;


public class CountryYearFilter extends SpeciesFilter {

	public CountryYearFilter(String timeSeriesName, String mainColumn, String optionalColumn, String quantitiesColumn) {
		super(timeSeriesName, mainColumn, optionalColumn, quantitiesColumn);
		super.filtername = "country_and_time";
	}

	public void initFilter(){
		
	}
	
	public List<String> postFilterSquare(List<String> csquares){
		return csquares;
	}
	
}
