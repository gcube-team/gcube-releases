package gr.cite.geoanalytics.geospatial.retrieval;

import java.util.ArrayList;

import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

public class FeatureRetrievalHelper {

	public static Iterable<SimpleFeature> getShapes(
			FeatureSource<SimpleFeatureType, SimpleFeature> source,
			String geometryProperty,
			double minimumX, double minimumY, double maximumX, double maximumY, 
			String crs) throws Exception {
		Query query = new Query();
		query.setCoordinateSystem(CRS.decode(crs));
		
		Filter filter = CQL.toFilter(String.format("BBOX(%s, %f, %f, %f, %f)", geometryProperty, minimumX, minimumY, maximumX, maximumY));
		query.setFilter(filter);
		FeatureIterator<SimpleFeature> iterator = source.getFeatures(query).features();
		try {
			ArrayList<SimpleFeature> list = new ArrayList<SimpleFeature>();
			while (iterator.hasNext()) {
				list.add((SimpleFeature) iterator.next());
			}
			return list;
		} finally {
			iterator.close();
		}
	}
}
