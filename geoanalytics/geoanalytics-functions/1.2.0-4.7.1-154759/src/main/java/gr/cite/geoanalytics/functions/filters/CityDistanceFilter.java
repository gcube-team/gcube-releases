package gr.cite.geoanalytics.functions.filters;

import java.util.ArrayList;

import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class CityDistanceFilter implements CoordinateFilter {

	public static final int MIN_ACCEPTED_DISTANCE = 30000;

	private FeatureSource<SimpleFeatureType, SimpleFeature> citiesSource;

	private Iterable<SimpleFeature> cities;

	private CoordinateReferenceSystem crs;

	@Override
	public boolean exclude(double x, double y) throws Exception {
		boolean isCityClose = false;

		for (SimpleFeature feature : getCities()) {
			Geometry geometry = (Geometry) feature.getDefaultGeometry();
			if (Point.class.isInstance(geometry)) {
				Point city = (Point) geometry;
				if (JTS.orthodromicDistance(city.getCoordinate(), new Coordinate(x, y), crs) <= MIN_ACCEPTED_DISTANCE) {
					isCityClose = true;
					break;
				}
			}
		}

		return !isCityClose;
	}

	public Iterable<SimpleFeature> getCities() throws Exception {
		if (cities == null) {
			Query query = new Query();
			query.setCoordinateSystem(crs);
			FeatureIterator<SimpleFeature> iterator = citiesSource.getFeatures(query).features();
			try {
				ArrayList<SimpleFeature> list = new ArrayList<SimpleFeature>();
				while (iterator.hasNext()) {
					list.add((SimpleFeature) iterator.next());
				}
				cities = list;
			} finally {
				iterator.close();
			}
		}
		return cities;
	}
	
	public FeatureSource<SimpleFeatureType, SimpleFeature> getCitiesSource() {
		return citiesSource;
	}

	public void setCitiesSource(FeatureSource<SimpleFeatureType, SimpleFeature> citiesSource) {
		this.citiesSource = citiesSource;
	}

	public CoordinateReferenceSystem getCrs() {
		return crs;
	}

	public void setCrs(CoordinateReferenceSystem crs) {
		this.crs = crs;
	}
}
