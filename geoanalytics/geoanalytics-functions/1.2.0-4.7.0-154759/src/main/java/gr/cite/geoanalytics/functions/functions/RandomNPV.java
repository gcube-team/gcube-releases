package gr.cite.geoanalytics.functions.functions;

import java.util.*;
import java.util.Map.Entry;

public class RandomNPV implements Function {

	private static Random random = new Random();
	
	public List<Attribute> execute(double x, double y) {
		Attribute attribute = new Attribute("random", new Double(random.nextInt(100)));
		
		List<Attribute> result = new ArrayList<Attribute>();
		result.add(attribute);
		return result;
	}
	
	public void destroy() {
		
	}

	@Override
	public List<Entry<String, Class>> getResultsSchema() {
		List<Map.Entry<String, Class>> schema = new ArrayList<Map.Entry<String, Class>>();
		schema.add(new AbstractMap.SimpleEntry<String, Class>("random", Double.class));
		return schema;
	}
}
