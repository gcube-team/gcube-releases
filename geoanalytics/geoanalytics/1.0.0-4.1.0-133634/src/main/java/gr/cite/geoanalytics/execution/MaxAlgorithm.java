package gr.cite.geoanalytics.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerBounds;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.FeatureType;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Layer;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

@Component
public class MaxAlgorithm {

	private static final String ATTRIBUTE_NAME = "MIO_EUR_2014";
	
	private ModelSpecification modelSpecification;
	
	private ShapeManager shapeManager;
	private TaxonomyManager taxonomyManager;
	private AttributeFunction attributeFunction;
	private SecurityContextAccessor securityContextAccessor;
	private ShapeDao shapeDao;
	private ConfigurationManager configurationManager;
	private GeoServerBridge geoServerBridge;
	private ImportManager importManager;
	
	@Inject
	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	@Inject
	public void setImportManager(ImportManager importManager) {
		this.importManager = importManager;
	}
	
	@Inject
	public void setGeoServerBridge(GeoServerBridge geoServerBridge) {
		this.geoServerBridge = geoServerBridge;
	}
	
	@Inject
	public void setShapeDao(ShapeDao shapeDao) {
		this.shapeDao = shapeDao;
	}
	
	@Inject
	public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
		this.securityContextAccessor = securityContextAccessor;
	}
	
	@Inject
	public void setShapeManager(ShapeManager shapeManager) {
		this.shapeManager = shapeManager;
	}
	
	@Inject
	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}
	
	public MaxAlgorithm(ModelSpecification modelSpecification, AttributeFunction attributeFunction) {
		this.modelSpecification = modelSpecification;
		this.attributeFunction = attributeFunction;
	}

	public TaxonomyTerm execute(String newLayerName) throws Exception{
		List<Shape> shapes = this.modelSpecification.getInputLayers().get(ATTRIBUTE_NAME).getGeometry();
		Shape maxShape = new Shape();
		Double maxAttribute = new Double(0);
		for (Shape shape : shapes){
			Double temp = (Double) this.attributeFunction.compute(ATTRIBUTE_NAME, shape);
			if ( temp >= maxAttribute){
				maxAttribute = temp;
				maxShape = shape;
			}
		}
		
		TaxonomyTerm templateTaxonomyTerm = this.modelSpecification.getInputLayers().get(ATTRIBUTE_NAME).getLayerTerm();
		
		Shape targetShape = this.newShapeBasedOnOld(maxShape);
		targetShape = this.shapeDao.create(targetShape);
		shapes = new ArrayList<Shape>();
		shapes.add(targetShape);
		Taxonomy taxonomyOfLayers = this.modelSpecification.getInputLayers().get(ATTRIBUTE_NAME).getLayerTerm().getTaxonomy();
		
		TaxonomyTerm taxonomyTerm = createTaxonomyTermofLayer(newLayerName, taxonomyOfLayers);
		
		this.taxonomyManager.updateTerm(taxonomyTerm, null, null, true);
		this.shapeManager.createShapeAssociationsWithLayerTerm(taxonomyTerm, shapes);
		
		boolean newLayer = false;
		boolean removed = false;
		Layer l = null;
		FeatureType ft = null;
		LayerConfig lcfg = configurationManager.getLayerConfig(taxonomyTerm);
		Map<String, String> layerStyles = null;
		String layerDefaultStyle = null; //default style for existing layer
		
		LayerBounds bounds = null;
		if(lcfg != null)
		{
			//defensive actions
			l = geoServerBridge.getLayer(taxonomyTerm.getName());
			layerStyles = configurationManager.getLayerStyles();
			layerDefaultStyle = configurationManager.getDefaultTermStyle(taxonomyTerm.getId().toString());
			if(l != null)
			{
				ft = geoServerBridge.getFeatureType(taxonomyTerm.getName());
			}//else
			//	configurationManager.removeLayerConfig(tt);
			this.importManager.removeLayer(taxonomyTerm);
			bounds = lcfg.getBoundingBox();
			removed = true;
		}else {
			newLayer = true;
			LayerConfig templateLayerConfig = configurationManager.getLayerConfig(templateTaxonomyTerm);
			bounds = new LayerBounds(templateLayerConfig.getBoundingBox());
		
		this.importManager.setUpNewOrUpdatedLayer(taxonomyTerm, null, newLayer , lcfg == null ? new LayerConfig() : lcfg, "line", 
				new Bounds(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), "EPSG:4326"));
		securityContextAccessor.updateLayers();
		}
		
		return taxonomyTerm;
	}

	private TaxonomyTerm createTaxonomyTermofLayer(String newLayerName, Taxonomy taxonomyOfLayers) throws Exception {
		TaxonomyTerm taxonomyTerm = new TaxonomyTerm();
		taxonomyTerm.setCreator(securityContextAccessor.getPrincipal());
		taxonomyTerm.setName(newLayerName);
		taxonomyTerm.setTaxonomy(taxonomyOfLayers);
		return taxonomyTerm;
	}
	
	private Shape newShapeBasedOnOld(Shape sourceShape) throws Exception {
		Shape targetShape = new Shape();
		targetShape.setCode(sourceShape.getCode());
		targetShape.setCreator(securityContextAccessor.getPrincipal());
		targetShape.setGeography(sourceShape.getGeography());
		targetShape.setName(sourceShape.getName());
		targetShape.setExtraData(sourceShape.getExtraData());
		
		return targetShape;
	}
	
	
}
