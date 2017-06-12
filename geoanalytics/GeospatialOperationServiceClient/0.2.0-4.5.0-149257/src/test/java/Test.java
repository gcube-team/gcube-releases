//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.UUID;
//
//import org.geotools.geometry.jts.JTSFactoryFinder;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import com.vividsolutions.jts.geom.LineString;
//
//import gr.cite.gaap.datatransferobjects.ShapeMessenger;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
//import gr.cite.gos.client.GeoserverManagement;
//import gr.cite.gos.client.ShapeManagement;
//
//public class Test {
//
//	
//	
//	
//	
//	public static void main (String [] args) throws Exception{
//		String validToken = "8e60c44b-9ee2-44ef-915d-fd27ef89e6cd-98187548";
//		
//		String gosEndpoint = "http://dl008.madgik.di.uoa.gr:8080/GeospatialOperationService";
////		String gosEndpoint = "http://dionysus.di.uoa.gr:7070/GeospatialOperationService";
////		String gosEndpoint = "http://dionysus.di.uoa.gr:6060/GeospatialOperationService";
//		GeoserverManagement gm = new GeoserverManagement(validToken);
////		System.out.println(gm.getGeoserverLayers(gosEndpoint));
//		ShapeManagement sm = new ShapeManagement(validToken);
//		System.out.println(sm.getLayers(gosEndpoint));
//		System.out.println(sm.countShapesOfLayer(gosEndpoint, "2374e0e5-88b0-41ff-afe1-32504e0e5c2e"));
////		System.out.println(sm.getShapeByID(gosEndpoint, "043a85ef-4fe9-456c-a99f-b3db4a8019aa").toString());
////		System.out.println(sm.getShapesOfLayerID(gosEndpoint, "1b113a15-e01c-43b7-8148-11188715d9e6").size());
////		System.out.println(insertShapes(sm, gosEndpoint));
////		System.out.println(gm.workspaceExists(gosEndpoint, "geoanalytics"));
////		System.out.println(gm.listDataStores(gosEndpoint));
////		System.out.println(gm.getGeoserverLayers(gosEndpoint));
//		
//		
////		System.out.println("a");
////		System.out.println(gm.getStyle(gosEndpoint, "efbe76e6-2369-4723-816d-c1f4f0c2a00c"));
//		
////		System.out.println(sm.deleteShapesOfLayer(gosEndpoint, "22222222-cbb0-426c-859a-b83ca3b72fbc"));
//		
////		insertShapes(sm, gosEndpoint);
//	}
//	
//	
//	
//	private static boolean insertShapes(ShapeManagement sm, String gosEndpoint) throws Exception{
//		Random rand = new Random();
//		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
//		List<Shape> shapes = new ArrayList<Shape>();
//		for(int i=0;i<10;i++){
//			Shape shape = new Shape();
//			shape.setName(UUID.randomUUID().toString());
//			Coordinate[] coords  =  new Coordinate[] {new Coordinate(rand.nextInt(1000), rand.nextInt(1000)), new Coordinate(rand.nextInt(1000), rand.nextInt(1000)) };
//			LineString line = geometryFactory.createLineString(coords);
//			shape.setGeography(line);
//			shape.setCode("EPSG:4326");
//			shape.setCreatorID(UUID.fromString("33333333-3333-3333-3333-333333333333"));
//			shape.setLayerID(UUID.fromString("22222222-cbb0-426c-859a-b83ca3b72fbc")); //this is very important
//			shapes.add(shape);
//		}
//		return sm.insertShapes(gosEndpoint, shapes);
//	}
//	
//	
//	
////	public static void main (String [] args) throws Exception{
////		String gosEndpoint = "http://dionysus.di.uoa.gr:7070/GeospatialOperationService";
////		String layerID = "1b113a15-e01c-43b7-8148-11188715d9e6";
////		
////		ShapeManagement sm = new ShapeManagement();
////		System.out.println(sm.getLayers(gosEndpoint));
////		
////		Shape arbitrary = 
////				sm.getShapeByID(gosEndpoint, "fd8bcd4c-0e33-4a49-a9c7-8e3c676b19c8");
////				sm.getShapeByID(gosEndpoint, "3f6ddc79-d945-45a6-8b6b-3c8e949bc0f0");
////		ObjectMapper om = new ObjectMapper();
////		System.out.println(om.writeValueAsString(arbitrary));
////		
////		
////		GeoserverManagement geoserverManagement = new GeoserverManagement();
////		System.out.println(geoserverManagement.getGeoserverLayers(gosEndpoint));
////		System.out.println(geoserverManagement.getGeoserverLayers(gosEndpoint, "geoanalytics"));
////		System.out.println(geoserverManagement.getDataStore(gosEndpoint, "geoanalytics"));
////		System.out.println(geoserverManagement.getGeoserverLayer(gosEndpoint, "geoanalytics:8f733a8d-e442-4a9b-9c0c-d9074f5d351d"));
////		
////		
////		Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
////		Gson gson = new Gson();
////		System.out.println(gson.toJson(arbitrary));
////		
////		String shapeMessengerJSON = gson.toJson(new ShapeMessenger(arbitrary));
////		System.out.println(shapeMessengerJSON);
////		System.out.println(gson.fromJson(shapeMessengerJSON, ShapeMessenger.class).toShape().toString());
////		
////		
////		System.out.println("Layer "+layerID+" has "+sm.countShapesOfLayer(gosEndpoint, layerID)+" shapes");
////		
////		System.out.println("Layer "+layerID+" has "+sm.getShapesOfLayerID(gosEndpoint, layerID).size()+" shapes");
////		
////		sm.getShapesOfLayerID(gosEndpoint, layerID).parallelStream().forEach(s -> {
////			try {
////				List<Shape> contents = sm.findContains(gosEndpoint, arbitrary);
////				if(contents.size()>0)
////					System.out.println(s.getId().toString()+" found contains: "+sm.findContains(gosEndpoint, arbitrary).size());
////			}
////			catch(Exception ex){
////				System.out.println("EXCEPTION");
////			}
////		});
////		
////		
////		
////		System.out.println("Found within: "+sm.findWithin(gosEndpoint, arbitrary).size());
////		
////		
////		
////	}
//	
//	
//}
