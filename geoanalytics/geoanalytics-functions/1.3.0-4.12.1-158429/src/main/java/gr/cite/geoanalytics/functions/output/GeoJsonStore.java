package gr.cite.geoanalytics.functions.output;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

import gr.cite.geoanalytics.functions.output.file.FileWriterI;
import gr.cite.geoanalytics.functions.output.file.HdfsFileWriter;
import gr.cite.geoanalytics.functions.output.file.LocalFileWriter;
import gr.cite.geoanalytics.geospatial.retrieval.FeatureRetrievalHelper;


public class GeoJsonStore implements FeatureStore {

	
	private FileWriterI fileWriter;
	
	private String crs;
	
	private GeometryFactory geometryFactory;
	
	private SimpleFeatureType featureType;
	
	private SimpleFeatureBuilder featureBuilder;
	
	
	public GeoJsonStore(String crs) throws Exception {
		
		this.fileWriter = 
//				new LocalFileWriter();
				new HdfsFileWriter("hdfs://datanode1.cluster2.madgik.di.uoa.gr:50050", "sys-hcv"); 
		
		this.crs = crs; 

		this.geometryFactory = JTSFactoryFinder.getGeometryFactory();

		this.featureType = createFeatureType();

		this.featureBuilder = new SimpleFeatureBuilder(featureType);
		
	}
	
	public String getCrs() {
		return crs;
	}
	

	public void setCrs(String crs) {
		this.crs = crs;
	}
	

	public GeometryFactory getGeometryFactory() {
		return geometryFactory;
	}
	

	public void setGeometryFactory(GeometryFactory geometryFactory) {
		this.geometryFactory = geometryFactory;
	}
	

	public SimpleFeatureType getFeatureType() {
		return featureType;
	}
	

	public void setFeatureType(SimpleFeatureType featureType) {
		this.featureType = featureType;
	}
	

	public SimpleFeatureBuilder getFeatureBuilder() {
		return featureBuilder;
	}
	

	public void setFeatureBuilder(SimpleFeatureBuilder featureBuilder) {
		this.featureBuilder = featureBuilder;
	}
	

	public FileWriterI getFileWriter() {
		return fileWriter;
	}

	public void setFileWriter(FileWriterI fileWriter) {
		this.fileWriter = fileWriter;
	}

	@Override
	public void addFeature(SimpleFeature feature) {
		
	}

	@Override
	public void commit() throws Exception {
		
	}
	
	
	
	
	public static SimpleFeatureType createFeatureType() throws Exception {
		return DataUtilities.createType("Location",
                "the_geom:Point:srid=4326," + // <- the geometry attribute: Point type
                "npv:Integer"   // a number attribute
        );
	}
	
	
	
	private String formGeoJson(List<SimpleFeature> features, SimpleFeatureType simpleFeatureType) throws IOException, NoSuchAuthorityCodeException, FactoryException{
		FeatureJSON fjson = new FeatureJSON();
		GeometryJSON gjson = new GeometryJSON();
		
		DefaultFeatureCollection fc = new DefaultFeatureCollection(null, simpleFeatureType);
		for(Feature f : features)
			fc.add((SimpleFeature) f);
		
		Writer writer = new StringWriter();
		fjson.writeFeatureCollection(fc, writer);
		
		return writer.toString();
		
	}

	
	
	public void store(String fullDestPath, List<SimpleFeature> features, SimpleFeatureType simpleFeatureType) throws Exception{
		fileWriter.writeBytesAtPath(fullDestPath, formGeoJson(features, simpleFeatureType).getBytes());
	}
	
	
	
	/*
	
	public static void main (String [] args) throws Exception {
		
		String getCapabilitiesURL = "http://dl014.madgik.di.uoa.gr:8080/geoserver/wfs?REQUEST=GetCapabilities&version=1.1.0";
		String crsCode = "EPSG:4326";
		
		String parakties = "geoanalytics:b46e7afe-5906-423f-962c-fff6eedc5aeb";
		String cities = "geoanalytics:1441ba92-9823-4972-bef0-6f3d0f36f413";
		String natura2000 = "geoanalytics:262a95fb-2d88-4df8-980f-5ed4de44245b";
		
		double minimumX = 23;
		double minimumY = 34;
		double maximumX = 27;
		double maximumY = 36;
		
		Map<String, String> connectionParameters = new HashMap<String, String>();
		connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", getCapabilitiesURL);
		
		DataStore data = DataStoreFinder.getDataStore(connectionParameters);

		CoordinateReferenceSystem crs = CRS.decode(crsCode);
		
		FeatureSource<SimpleFeatureType, SimpleFeature> paraktiesSource = data.getFeatureSource(parakties);
//		FeatureSource<SimpleFeatureType, SimpleFeature> natura2000Source = data.getFeatureSource(natura2000);
//		FeatureSource<SimpleFeatureType, SimpleFeature> citiesSource = data.getFeatureSource(cities);
		
		Iterable<SimpleFeature> paraktiesFeatures = FeatureRetrievalHelper.getShapes(paraktiesSource, "SHP_Geography", minimumX, minimumY, maximumX, maximumY, crsCode);
		
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		Iterator <SimpleFeature> iter = paraktiesFeatures.iterator();
		while(iter.hasNext())
			features.add(iter.next());
		
//		new GeoJsonStore(crsCode).store("/tmp/values.json", features, createFeatureType());
		
//		new GeoJsonStore(crsCode).testWriteReadNoProperties();
		
		
		Geometry geom = (Geometry)features.get(0).getDefaultGeometry();
		
		System.out.println(geom);
		
		System.out.println(geom.getSRID());
		
//		System.out.println(features.get(0).getAttributes());
		
		
//		List<String> lst = new ArrayList<String>(); 
//		lst.add("{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[25.9409,35.1839]},\"properties\":{\"npv\":155400},\"id\":\"fid-3ce231f6_15b23fb6326_-64da\"}");
		
	}
	
	
	public void testWriteReadNoProperties() throws Exception {
	    SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
	    tb.add("geom", Point.class, CRS.decode("EPSG:4326"));
	    tb.add("name", String.class);
	    tb.add("quantity", Integer.class);
	    tb.setName("outbreak");
	    SimpleFeatureType schema = tb.buildFeatureType();
	    
	    SimpleFeatureBuilder fb = new SimpleFeatureBuilder(schema);
	    fb.add(new WKTReader().read("POINT(10 20)"));
	    SimpleFeature feature = fb.buildFeature("outbreak.1");
	    
	    FeatureJSON fj = new FeatureJSON();
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    fj.writeFeature(feature, os);
	    
	    String json = os.toString();
	    System.out.println(json);
	}
//	*/
	
}
