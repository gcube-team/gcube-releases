package gr.cite.geoanalytics.functions.functions;

import java.util.List;
import java.util.Map;

public interface Function {
	
	List<Attribute> execute(double x, double y) throws Exception ;
	
	List<Map.Entry<String, Class>> getResultsSchema();
	
	void destroy();
}
