package org.n52.wps.demo;

import org.n52.wps.server.AbstractSelfDescribingAlgorithm;

public class ConvexHullDemo {
	/*
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollections;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.n52.wps.io.GTHelper;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.n52.wps.server.ExceptionReport;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

//org.n52.wps.demo.ConvexHullDemo



	@Override
	public Class<?> getInputDataType(String identifier) {
		if (identifier.equalsIgnoreCase("FEATURES")) {
			return GTVectorDataBinding.class;
		}
		return null;
	}

	@Override
	public Class<?> getOutputDataType(String identifier) {
		if (identifier.equalsIgnoreCase("polygons")) {
			return GTVectorDataBinding.class;
		}
		return null;
	}


	@Override
	public Map<String, IData> run(Map<String, List<IData>> inputData)
			throws ExceptionReport {
		if (inputData == null || !inputData.containsKey("FEATURES")) {
			throw new RuntimeException(
					"Error while allocating input parameters");
		}
		List<IData> dataList = inputData.get("FEATURES");
		if (dataList == null || dataList.size() != 1) {
			throw new RuntimeException(
					"Error while allocating input parameters");
		}
		IData firstInputData = dataList.get(0);
		FeatureCollection featureCollection = ((GTVectorDataBinding) firstInputData).getPayload();
		
		FeatureIterator iter = featureCollection.features();
		List<Coordinate> coordinateList = new ArrayList<Coordinate>();
		int counter = 0;
		while (iter.hasNext()) {
		                SimpleFeature feature = (SimpleFeature) iter.next();
		                if (feature.getDefaultGeometry() == null) {
		                        throw new NullPointerException("defaultGeometry is null in feature id: "+ feature.getID());
		                }
		                Geometry geom = (Geometry) feature.getDefaultGeometry();
		                Coordinate[] coordinateArray = geom.getCoordinates();
		                for(Coordinate coordinate : coordinateArray){
		                        coordinateList.add(coordinate);
		                }
		}
		iter.close();
		
		Coordinate[] coordinateArray = new Coordinate[coordinateList.size()];
		for(int i = 0; i<coordinateList.size(); i++){
		        coordinateArray[i] = coordinateList.get(i);
		}
		
		
		com.vividsolutions.jts.algorithm.ConvexHull convexHull = new com.vividsolutions.jts.algorithm.ConvexHull(coordinateArray, new GeometryFactory());
		Geometry geometry = convexHull.getConvexHull();
		
		String uuid = UUID.randomUUID().toString();
		SimpleFeatureType featureType = GTHelper.createFeatureType(geometry, uuid, featureCollection.getSchema().getCoordinateReferenceSystem());
		GTHelper.createGML3SchemaForFeatureType(featureType);
		Feature feature = GTHelper.createFeature("0", geometry, featureType);
		
		SimpleFeatureCollection fOut = DefaultFeatureCollections.newCollection();
		fOut.add((SimpleFeature) feature);
		
		HashMap<String, IData> result = new HashMap<String, IData>();
		result.put("polygons", new GTVectorDataBinding(fOut));
		return result;
	}

	@Override
	public List<String> getInputIdentifiers() {
		List<String> list = new ArrayList();
		list.add("FEATURES");
		return list;

	}

	@Override
	public List<String> getOutputIdentifiers() {
		List<String> list = new ArrayList();
		list.add("polygons");
		return list;
	}
*/
}
