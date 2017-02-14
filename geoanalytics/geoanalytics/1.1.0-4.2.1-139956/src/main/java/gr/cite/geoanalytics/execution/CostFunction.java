package gr.cite.geoanalytics.execution;

import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

public interface CostFunction<T> {
	T compute(String attribute, Shape shape) throws Exception;
}
