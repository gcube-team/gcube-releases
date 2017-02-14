package gr.cite.geoanalytics.util.test;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import gr.cite.gaap.servicelayer.ShapeImportManager;
import gr.cite.geoanalytics.common.ViewBuilder;
import gr.cite.geoanalytics.context.Configuration;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Layer;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.manager.UserManager;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;

@Component
public class LayerFromShapeFileTest
{
	
	private ShapeImportManager shapeImportManager;
	
	private ImportManager importManager;
	
	@Inject
	public void setShapeImportManager(ShapeImportManager shapeImportManager) {
		this.shapeImportManager = shapeImportManager;
	}
	
	@Inject
	public void setImportManager(ImportManager importManager) {
		this.importManager = importManager;
	}
	
	public String importLayer(String filename, int srid, String charset, boolean axisInvert) throws Exception
	{
		return charset;
		
		/*Shape s = ShapeImportUtilTest.importShape(filename, srid, charset, axisInvert);
		UserManager um = new UserManager();
		ViewBuilder builder = new PostGISRegularViewBuilder(new ShapeManager(), new ShapeImportManager(), new TaxonomyManager(), new ConfigurationManager());
		builder.forShape(s).createViewStatement().execute();
		
		FeatureType featureType = new FeatureType();
		featureType.setDatastore(Configuration.getGeoServerBridgeDataStore());
		featureType.setWorkspace(Configuration.getGeoServerBridgeWorkspace());
		featureType.setEnabled(true);
		featureType.setName(s.getName());
		featureType.setTitle(s.getName());
		featureType.setNativeCRS("EPSG:4326");
		
		Layer l = new Layer();
		l.setWorkspace(Configuration.getGeoServerBridgeWorkspace());
		l.setDatastore(Configuration.getGeoServerBridgeDataStore());
		l.setEnabled(true);
		l.setDefaultStyle("polygon");
		l.setName(s.getName());
		l.setType("VECTOR");
		
		GeoServerBridgeFacade.addLayer(l, featureType, new HashMap<String, String>());
		return l.getName();*/
		
//		Layer layer = new Layer();
//		layer.setDatastore(Context.getGeoServerBridgeDataStore());
//		layer.setWorkspace(Context.getGeoServerBridgeWorkspace());
//		layer.setEnabled(true);
//		layer.setName(s.getName());
//		layer.setDefaultStyle("polygon");
//		layer.setType("VECTOR");
//		
//		GeoServerBridge.addLayer(layer);
	
	}
	
	public static void main(String[] args) throws Exception
	{
		ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		LayerFromShapeFileTest t = (LayerFromShapeFileTest)appContext.getBean("layerFromShapeFileTest");
		t.importLayer("C:\\Users\\diljin\\Documents\\geopolis\\data\\Laconia\\kallikratis_16.shp", 2100, "windows-1253", true);
	}
}
