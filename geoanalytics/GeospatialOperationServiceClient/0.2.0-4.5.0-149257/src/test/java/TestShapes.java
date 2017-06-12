//import org.junit.Test;
//
//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import com.vividsolutions.jts.geom.LineString;
//
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.UUID;
//
//import org.geotools.geometry.jts.JTSFactoryFinder;
//
//
//public class TestShapes extends TestBase {
//
//	@Test
//	public void testBasicReadWriteShapes() throws Exception {
//		
//		assertEquals(true, sm.deleteShapesOfLayer(gosEndpoint, testLayerID));
//		//count check
//		assertEquals(0L, sm.countShapesOfLayer(gosEndpoint, testLayerID));
//		
//		//alternative count
//		assertEquals(0, sm.getShapesOfLayerID(gosEndpoint, testLayerID).size());
//		
//		//check bulk insert
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
//			shape.setCreatorID(UUID.fromString(testCreatorID));
//			shape.setLayerID(UUID.fromString(testLayerID)); //this is very important
//			shapes.add(shape);
//		}
//		assertEquals(true, sm.insertShapes(gosEndpoint, shapes));
//		//check bulk delete
//		assertEquals(true, sm.deleteShapesOfLayer(gosEndpoint, testLayerID));
//		
//		//test random
//		UUID uuid = UUID.randomUUID();
//		assertNull(sm.getShapeByID(gosEndpoint, uuid.toString()));
//		
//		//test single insert
//		shapes.get(0).setId(uuid);
//		assertTrue(sm.insertShape(gosEndpoint, shapes.get(0)));
//		//test single delete
//		assertTrue(sm.deleteShape(gosEndpoint, uuid.toString()));
//		
//	}
//	
//	@Test
//	public void testBasicFindShapes() throws Exception {
//		
//		assertEquals(true, sm.deleteShapesOfLayer(gosEndpoint, testLayerID));
//		
//		Random rand = new Random();
//		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
//		Shape shape = new Shape();
//		shape.setName("a shape for testing purposes");
//		Coordinate[] coords  =  new Coordinate[] {new Coordinate(rand.nextInt(1000), rand.nextInt(1000)), new Coordinate(rand.nextInt(1000), rand.nextInt(1000)) };
//		LineString line = geometryFactory.createLineString(coords);
//		shape.setGeography(line);
//		shape.setCode("EPSG:4326");
//		shape.setCreatorID(UUID.fromString(testCreatorID));
//		shape.setLayerID(UUID.fromString(testLayerID)); //this is very important
//		
//		//insert it
//		assertTrue(sm.insertShape(gosEndpoint, shape));
//		//find it
//		List<String> terms = new ArrayList<String>();
//		terms.add("testing");
//		List<Shape> shapes = sm.searchShapes(gosEndpoint, terms);
//		assertEquals(1, shapes.size());
//	}
//	
//	
//	
//	
//}
