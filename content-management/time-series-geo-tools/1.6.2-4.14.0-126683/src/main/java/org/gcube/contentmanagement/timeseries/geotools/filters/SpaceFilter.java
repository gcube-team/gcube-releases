package org.gcube.contentmanagement.timeseries.geotools.filters;


public class SpaceFilter extends AFilter {

	public SpaceFilter(String timeSeriesName, String mainColumn, String quantitiesColumn) {
		super(true, timeSeriesName, mainColumn, "", quantitiesColumn);
		this.filtername = "space_distribution";
	}

}
