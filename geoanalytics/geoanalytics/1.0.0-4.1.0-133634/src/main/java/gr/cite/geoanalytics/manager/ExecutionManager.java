package gr.cite.geoanalytics.manager;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import gr.cite.gaap.datatransferobjects.DummyModel;
import gr.cite.gaap.geospatialbackend.GeospatialBackend;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.GeoServerBridge;
import gr.cite.geoanalytics.execution.AttributeFunction;
import gr.cite.geoanalytics.execution.MaxAlgorithm;
import gr.cite.geoanalytics.execution.ModelLayer;
import gr.cite.geoanalytics.execution.ModelLogicalLayer;
import gr.cite.geoanalytics.execution.ModelSpecification;
import gr.cite.geoanalytics.security.SecurityContextAccessor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExecutionManager {
	
	private TaxonomyManager taxonomyManager;
	private SecurityContextAccessor securityContextAccessor;
	private GeospatialBackend geospatialBackend;
	private ShapeManager shapeManager;
	private ConfigurationManager configurationManager;
	private GeoServerBridge geoServerBridge;
	private ImportManager importManager;
	private ShapeDao shapeDao;
	
	@Inject
	public void setImportManager(ImportManager importManager) {
		this.importManager = importManager;
	}
	
	@Inject
	public void setShapeDao(ShapeDao shapeDao) {
		this.shapeDao = shapeDao;
	}
	
	@Inject
	public void setGeoServerBridge(GeoServerBridge geoServerBridge) {
		this.geoServerBridge = geoServerBridge;
	}
	
	@Inject
	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}
	
	@Inject
	public void setShapeManager(ShapeManager shapeManager) {
		this.shapeManager = shapeManager;
	}
	
	@Inject
	public void setGeospatialBackend(GeospatialBackend geospatialBackend) {
		this.geospatialBackend = geospatialBackend;
	}
	
	@Inject
	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	@Inject
	public void setSecurityContextAccessor(
			SecurityContextAccessor securityContextAccessor) {
		this.securityContextAccessor = securityContextAccessor;
	}
	
	@Transactional
	public void sampleCalulateModel(DummyModel dummyModel) throws Exception{
		
		ModelLogicalLayer modelLogicalLayer = new ModelLogicalLayer(this.taxonomyManager.findTermByName(dummyModel.getLayer1(), false));
		modelLogicalLayer.setGeospatialBackend(geospatialBackend);
		
		ModelSpecification modelSpecification = new ModelSpecification();
		
		Map<String, ModelLayer> map = new HashMap<String, ModelLayer>();
		map.put("MIO_EUR_2014", modelLogicalLayer);
		modelSpecification.setInputLayers(map);
		
		AttributeFunction attributeFunction = new AttributeFunction();
		attributeFunction.setShapeManager(shapeManager);
		
		MaxAlgorithm maxAlgorithm = new MaxAlgorithm(modelSpecification, attributeFunction);
		maxAlgorithm.setSecurityContextAccessor(securityContextAccessor);
		maxAlgorithm.setConfigurationManager(configurationManager);
		maxAlgorithm.setGeoServerBridge(geoServerBridge);
		maxAlgorithm.setImportManager(importManager);
		maxAlgorithm.setShapeDao(shapeDao);
		maxAlgorithm.setTaxonomyManager(taxonomyManager);
		maxAlgorithm.setShapeManager(shapeManager);
		
		maxAlgorithm.execute(dummyModel.getNewLayerName());
	}
}
