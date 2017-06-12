//package gr.cite.geoanalytics.dataaccess.geoserverbridge.test;
//
//import gr.cite.gaap.viewbuilders.PostGISMaterializedViewBuilder;
//import gr.cite.geoanalytics.common.ViewBuilder;
//import gr.cite.geoanalytics.context.Configuration;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.GeoserverLayer;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.exception.GeoServerBridgeException;
//import gr.cite.geoanalytics.layerimport.ShapeImportUtil;
//import gr.cite.geoanalytics.manager.UserManager;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
//import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeImportDao;
//
//import java.io.File;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.UUID;
//
//import javax.inject.Inject;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml" })
///*@WebAppConfiguration*/
//@RunWith(SpringJUnit4ClassRunner.class)
//public class GeoServerBridgeTest
//{
//	private static final Logger log = LoggerFactory.getLogger(GeoServerBridgeTest.class);
//	
//	private Configuration configuration;
//	private GeoServerBridge geoServerBridge;
//	private ShapeImportDao shapeImportDao;
//	private ShapeDao shapeDao;
//	
//	private ViewBuilder vb;
//	
//	@Inject
//	public void setGeoServerBridge(GeoServerBridge geoServerBridge) {
//		this.geoServerBridge = geoServerBridge;
//	}
//	
//	@Inject
//	public void setConfiguration(Configuration configuration) {
//		this.configuration = configuration;
//	}
//	
//	@Inject
//	public void setShapeImportDao(ShapeImportDao shapeImportDao) {
//		this.shapeImportDao = shapeImportDao;
//	}
//	
//	@Inject
//	public void setShapeDao(ShapeDao shapeDao) {
//		this.shapeDao = shapeDao;
//	}
//	
//	@Inject
//	@Qualifier("builder")
//	public void setVb(PostGISMaterializedViewBuilder viewBuilder) {
//		this.vb = viewBuilder;
//	}
//	
//	public void listLayers() throws GeoServerBridgeException
//	{
//		List<String> layers = geoServerBridge.listLayers();
//		
//		for(String l : layers)
//			System.out.println(l);
//		
//	}
//	
//	public void getLayers() throws GeoServerBridgeException 
//	{
//		List<GeoserverLayer> layers = geoServerBridge.getGeoserverLayers();
//		for(GeoserverLayer l : layers)
//		{
//			System.out.println("Layer: " + l.getWorkspace() + ":" + l.getDatastore() + ":" + l.getId() + "--" +
//					l.getType() + "," + l.getResource() + "," + l.getResourceName() + "," + l.getDefaultStyle() + "," +
//					l.getFeatureTypeLink());
//		}
//	}
//	
//	public void importLayer(String filename) throws Exception
//	{
//		String gosEndpoint = ""; //TODO: get a valid one from the clustermanager
//		
//		UUID importID = ShapeImportUtil.fromShapeFile(filename, 4326, "windows-1253", true);
//		Shape s = null;
//		
//		ShapeImport si = shapeImportDao.read(importID);
//		s = new Shape();
//		s.setName(new File(filename).getName());
//		s.setCode("code");
//		s.setCreationDate(new Date());
//		s.setCreatorID(si.getId());
//		s.setExtraData(si.getData());
//		s.setGeography(si.getGeography());
//		s.setLastUpdate(new Date());
//		s.setShapeClass(1);
//		s.setShapeImport(si.getId());
//		shapeDao.create(s);
//		
//		UserManager um = new UserManager();
//		vb.forShape(s).createViewStatement().execute(gosEndpoint);
//		
//		FeatureType ft = new FeatureType();
//		ft.setDatastore(configuration.getGeoServerBridgeConfig().getDataStoreConfig().getDataStoreName());
//		ft.setEnabled(true);
//		ft.setName(s.getName());
//		ft.setNativeCRS("EPSG:4326");
//		ft.setWorkspace(configuration.getGeoServerBridgeConfig().getGeoServerBridgeWorkspace());
//		
//		GeoserverLayer l = new GeoserverLayer();
//		l.setWorkspace(configuration.getGeoServerBridgeConfig().getGeoServerBridgeWorkspace());
//		l.setDatastore(configuration.getGeoServerBridgeConfig().getDataStoreConfig().getDataStoreName());
//		l.setEnabled(true);
//		l.setDefaultStyle("polygon");
//		l.setId(s.getName());
//		l.setType("VECTOR");
//		geoServerBridge.addGeoserverLayer(l, ft, new HashMap<String, String>());
//		
//	}
//	
//	@Test
//	public void test() throws Exception {
//		
//		GeoserverLayer layer = geoServerBridge.getGeoserverLayer("sadasdasdasdsadasdasdsa");
//		
//		if (layer == null){
//			log.info("Layer is null");
//		}
//		
////		listLayers();
////		getLayers();
//		
////		importLayer("C:\\Users\\Diljin\\My Documents\\geopolis\\data\\Laconia\\kallikratis_16.shp");
//	}
//}
