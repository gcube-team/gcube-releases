package gr.cite.geoanalytics.functions.filters;

public interface CoordinateFilter {
	
	boolean exclude(double x, double y) throws Exception;
	
}
