

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.Hints;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.GeometryFactoryFinder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;
import org.geotools.geometry.text.WKTParser;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.PositionFactory;
//import org.opengis.geometry.primitive.Point;
//import org.opengis.geometry.primitive.PrimitiveFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;


//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import com.vividsolutions.jts.geom.Point;

/**
 * This example reads data for point locations and associated attributes from a comma separated text
 * (CSV) file and exports them as a new shapefile. It illustrates how to build a feature type.
 * <p>
 * Note: to keep things simple in the code below the input file should not have additional spaces or
 * tabs between fields.
 */
public class TestSample {

	
	private static SimpleFeatureType createFeatureType() throws Exception {
		return DataUtilities.createType("Location",
                "the_geom:Point:srid=4326," + // <- the geometry attribute: Point type
                "npv:Integer"   // a number attribute
        );
	}
	
	
//	public static void main(String[] args) throws Exception {
//		GeometryBuilder builder = new GeometryBuilder( DefaultGeographicCRS.WGS84 );
//		Point point = builder.createPoint(1.45, -5.33);
//		System.out.println(point);
//	}
	
	
    public static void main(String[] args) throws Exception {
    	
        final SimpleFeatureType TYPE = DataUtilities.createType("Location",
                "location:Point:srid=4326," + // <- the geometry attribute: Point type
                        "name:String," + // <- a String attribute
                        "number:Integer" // a number attribute
        );
        
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(createFeatureType());
        


//        GeometryBuilder builder = new GeometryBuilder( DefaultGeographicCRS.WGS84 );
//        Point point = builder.createPoint( 48.44, -123.37 );
        
//        WKTReader2 wkt = new WKTReader2();
        
//        WKTParser parser = new WKTParser( new GeometryBuilder( DefaultGeographicCRS.WGS84 ));
        
//        Hints hints = new Hints( Hints.CRS, DefaultGeographicCRS.WGS84 );
//        PrimitiveFactory primitiveFactory = GeometryFactoryFinder.getPrimitiveFactory( hints );

        
        

        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        
        for(int i=0;i<1000;i++){
	        double latitude = getRandomDouble()*50;
	        double longitude = getRandomDouble()*140;
	        String name = UUID.randomUUID().toString();
	        int number = getRandomInt();
	
	        
	        System.out.println("Adding point with "+longitude+"\t"+latitude);
	        
//	        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
	        
//	        featureBuilder.add(point);
//	        featureBuilder.add(name);
//	        featureBuilder.add(number);
//	        SimpleFeature feature = featureBuilder.buildFeature(null);
	        
//	        Point point = (Point) parser.parse("POINT( "+longitude+" "+latitude+")");
//	        Point point = primitiveFactory.createPoint(  new double[]{latitude, longitude} );
	        
	        
//	        SimpleFeature feature = SimpleFeatureBuilder.build( TYPE, new Object[]{ point/*wkt.read("POINT(1,3)")*/, name, number}, null);
	        
//	        features.add(feature);
	        
        }
        
        
//        SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
//
////      // O(N) access
////      SimpleFeatureSource source = DataUtilities.source( collection );
////      SimpleFeatureCollection features = source.getFeatures( filter );
//        
//        
//        /*
//         * Get an output file name and create the new shapefile
//         */
//        File newFile = new File("/tmp/samplefiles1/outputsampleshape.shp");
//
//        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
//
//        Map<String, Serializable> params = new HashMap<String, Serializable>();
//        params.put("url", newFile.toURI().toURL());
//        params.put("create spatial index", Boolean.TRUE);
//
//        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
//        newDataStore.createSchema(TYPE);
//
//        /*
//         * You can comment out this line if you are using the createFeatureType method (at end of
//         * class file) rather than DataUtilities.createType
//         */
//        newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);
//
//        /*
//         * Write the features to the shapefile
//         */
//        Transaction transaction = new DefaultTransaction("create");
//
//        String typeName = newDataStore.getTypeNames()[0];
//        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
//
//        if (featureSource instanceof SimpleFeatureStore) {
//            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
//
//            featureStore.setTransaction(transaction);
//            try {
//                featureStore.addFeatures(collection);
//                transaction.commit();
//
//            } catch (Exception problem) {
//                problem.printStackTrace();
//                transaction.rollback();
//
//            } finally {
//                transaction.close();
//            }
//        } else {
//            System.out.println(typeName + " does not support read/write access");
//        }
    }

    public static double getRandomDouble(){
		return new Random().nextDouble()*2-1;
	}
    public static int getRandomInt(){
		return new Random().nextInt(100);
	}
    
    
//    public static void toShp(File geojson) throws IOException {     
//        File shpFile = new File("test.shp");
//        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
//
//        Map<String, Serializable> params = new HashMap<String, Serializable>();
//        params.put("url", shpFile.toURI().toURL());
//        params.put("create spatial index", Boolean.TRUE);
//
//        ShapefileDataStore shpDataStore = (ShapefileDataStore)  dataStoreFactory.createNewDataStore(params);
//
//
//        InputStream in = new FileInputStream(geojson);
//        int decimals = 15; 
//        GeometryJSON gjson = new GeometryJSON(decimals); 
//        FeatureJSON fjson = new FeatureJSON(gjson);
//
//        FeatureCollection fc = fjson.readFeatureCollection(in);
//
//        SimpleFeatureType type = (SimpleFeatureType) fc.getSchema();        
//        shpDataStore.createSchema(type);
//
//        Transaction transaction = new DefaultTransaction("create");
//
//        String typeName = shpDataStore.getTypeNames()[0];
//
//        SimpleFeatureSource featureSource = shpDataStore.getFeatureSource(typeName);
//
//        if (featureSource instanceof FeatureStore) {
//            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
//
//            featureStore.setTransaction(transaction);
//            try {
//
//                featureStore.addFeatures(fc);
//
//                transaction.commit();
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                transaction.rollback();
//
//            } finally {
//                transaction.close();
//            }
//        } else {
//            System.out.println(typeName + " does not support read/write access");
//        }
//
//
//      }
    
    
    
}