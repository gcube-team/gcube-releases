//package gr.cite.geoanalytics.execution;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import gr.cite.gaap.servicelayer.ConfigurationManager;
//import gr.cite.gaap.servicelayer.ShapeManager;
//import gr.cite.gaap.servicelayer.TaxonomyManager;
//import gr.cite.geoanalytics.dataaccess.entities.layer.dao.TaxonomyLayerDao;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
//import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerBounds;
//import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyLayer;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Layer;
//import gr.cite.geoanalytics.manager.ImportManager;
//import gr.cite.geoanalytics.security.SecurityContextAccessor;
//
//import javax.inject.Inject;
//
//import org.springframework.stereotype.Component;
//
//@Component
//public class MaxAlgorithm {
//
//	private static final String ATTRIBUTE_NAME = "MIO_EUR_2014";
//	
//	private ModelSpecification modelSpecification;
//	
//	private ShapeManager shapeManager;
//	private TaxonomyLayerDao taxonomyLayerDao;
////	private TaxonomyManager taxonomyManager;
//	private AttributeFunction attributeFunction;
//	private SecurityContextAccessor securityContextAccessor;
//	private ShapeDao shapeDao;
//	private ConfigurationManager configurationManager;
//	private GeoServerBridge geoServerBridge;
//	private ImportManager importManager;
//	
////	@Inject
////	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
////		this.taxonomyManager = taxonomyManager;
////	}
//	
//	@Inject
//	public void setTaxonomyLayerDao(TaxonomyLayerDao taxonomyLayerDao) {
//		this.taxonomyLayerDao = taxonomyLayerDao;
//	}
//	
//	@Inject
//	public void setImportManager(ImportManager importManager) {
//		this.importManager = importManager;
//	}
//	
//	@Inject
//	public void setGeoServerBridge(GeoServerBridge geoServerBridge) {
//		this.geoServerBridge = geoServerBridge;
//	}
//	
//	@Inject
//	public void setShapeDao(ShapeDao shapeDao) {
//		this.shapeDao = shapeDao;
//	}
//	
//	@Inject
//	public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
//		this.securityContextAccessor = securityContextAccessor;
//	}
//	
//	@Inject
//	public void setShapeManager(ShapeManager shapeManager) {
//		this.shapeManager = shapeManager;
//	}
//	
//	@Inject
//	public void setConfigurationManager(ConfigurationManager configurationManager) {
//		this.configurationManager = configurationManager;
//	}
//	
//	public MaxAlgorithm(ModelSpecification modelSpecification, AttributeFunction attributeFunction) {
//		this.modelSpecification = modelSpecification;
//		this.attributeFunction = attributeFunction;
//	}
//
//	public Layer execute(String newLayerName) throws Exception{
//		List<Shape> shapes = this.modelSpecification.getInputLayers().get(ATTRIBUTE_NAME).getGeometry();
//		Shape maxShape = new Shape();
//		Double maxAttribute = new Double(0);
//		for (Shape shape : shapes){
//			Double temp = (Double) this.attributeFunction.compute(ATTRIBUTE_NAME, shape);
//			if ( temp >= maxAttribute){
//				maxAttribute = temp;
//				maxShape = shape;
//			}
//		}
//		
//		TaxonomyTerm templateTaxonomyTerm = this.modelSpecification.getInputLayers().get(ATTRIBUTE_NAME).getLayer();
//		
//		Shape targetShape = this.newShapeBasedOnOld(maxShape);
//		targetShape = this.shapeDao.create(targetShape);
//		shapes = new ArrayList<Shape>();
//		shapes.add(targetShape);
//		Taxonomy taxonomyOfLayer = this.modelSpecification.getInputLayers().get(ATTRIBUTE_NAME).getLayerTerm().getTaxonomy();
//		
//		gr.cite.geoanalytics.dataaccess.entities.layer.Layer layer = createLayer(newLayerName, taxonomyOfLayer);
//		
//		TaxonomyLayer tl = new TaxonomyLayer();
//		tl.setLayer(layer);
//		tl.setTaxonomy(taxonomyOfLayer);
//		this.taxonomyLayerDao.create(tl);
//		this.shapeManager.createShapeAssociationsWithLayer(layer.getId(), shapes);
//		
//		boolean newLayer = false;
//		boolean removed = false;
//		Layer l = null;
//		FeatureType ft = null;
//		LayerConfig lcfg = configurationManager.getLayerConfig(layer);
//		Map<String, String> layerStyles = null;
//		String layerDefaultStyle = null; //default style for existing layer
//		
//		LayerBounds bounds = null;
//		if(lcfg != null)
//		{
//			//defensive actions
//			l = geoServerBridge.getLayer(layer.getName());
//			layerStyles = configurationManager.getLayerStyles();
//			layerDefaultStyle = configurationManager.getDefaultTermStyle(layer.getId().toString());
//			if(l != null)
//			{
//				ft = geoServerBridge.getFeatureType(layer.getName());
//			}//else
//			//	configurationManager.removeLayerConfig(tt);
//			this.importManager.removeLayer(layer);
//			bounds = lcfg.getBoundingBox();
//			removed = true;
//		}else {
//			newLayer = true;
//			LayerConfig templateLayerConfig = configurationManager.getLayerConfig(templateTaxonomyTerm);
//			bounds = new LayerBounds(templateLayerConfig.getBoundingBox());
//		
//		this.importManager.setUpNewOrUpdatedLayer(taxonomyTerm, null, newLayer , lcfg == null ? new LayerConfig() : lcfg, "line", 
//				new Bounds(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), "EPSG:4326"));
//		securityContextAccessor.updateLayers();
//		}
//		
//		return taxonomyTerm;
//	}
//
//	private gr.cite.geoanalytics.dataaccess.entities.layer.Layer createLayer(String newLayerName, Taxonomy taxonomyOfLayers) throws Exception {
//		gr.cite.geoanalytics.dataaccess.entities.layer.Layer l = 
//				new gr.cite.geoanalytics.dataaccess.entities.layer.Layer(UUID.randomUUID(), newLayerName, securityContextAccessor.getPrincipal(), (short)1);
//		return l;
//	}
//	
//	private Shape newShapeBasedOnOld(Shape sourceShape) throws Exception {
//		Shape targetShape = new Shape();
//		targetShape.setCode(sourceShape.getCode());
//		targetShape.setCreator(securityContextAccessor.getPrincipal());
//		targetShape.setGeography(sourceShape.getGeography());
//		targetShape.setName(sourceShape.getName());
//		targetShape.setExtraData(sourceShape.getExtraData());
//		
//		return targetShape;
//	}
//	
//	
//}
