//package gr.cite.gos.services;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import javax.inject.Inject;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import gr.cite.gaap.datatransferobjects.ShapeMessenger;
//import gr.cite.gaap.datatransferobjects.GeocodeMessenger;
//import gr.cite.gaap.datatransferobjects.PrincipalMessenger;
//import gr.cite.gaap.geospatialbackend.GeospatialBackend;
//import gr.cite.gaap.servicelayer.ShapeInfo;
//import gr.cite.gaap.servicelayer.ShapeInfo.ShapeInfoMessenger;
//import gr.cite.gaap.servicelayer.TaxonomyManager;
//import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
//import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
//import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Geocode;
//import gr.cite.geoanalytics.dataaccess.entities.user.dao.UserDaoOld;
//import gr.cite.geoanalytics.logicallayer.LayerOperations;
//
//@Service
//public class LayerOperationsImpl implements LayerOperations{
//	
//	private GeospatialBackend geospatialBackend;
////	private TaxonomyManager taxonomyManager;
//	private PrincipalDao principalDao;
//	
//	@Inject
//	public void setPrincipalDao(PrincipalDao principalDao) {
//		this.principalDao = principalDao;
//	}
//	
//	@Inject
//	public void setGeospatialialBackend(GeospatialBackend geospatialBackend) {
//		this.geospatialBackend = geospatialBackend;
//	}
//	
////	@Inject
////	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
////		this.taxonomyManager = taxonomyManager;
////	}
////
////	@Override
////	@Transactional
////	public Set<String> getAttributeValuesOfShapesByTerm(TaxonomyTermMessenger taxonomyTermMessenger, Attribute attr) throws Exception {
////		Layer layer = taxonomyManager.findLayerByNameAndTaxonomy(taxonomyTermMessenger.getName(), taxonomyTermMessenger.getTaxonomy(), false);
////		return this.geospatialBackend.getAttributeValuesOfShapesByLayer(layer.getId(), attr);
////	}
////
////	@Override
////	@Transactional
////	public void generateShapeBoundary(TaxonomyTermMessenger layerTermMessenger, TaxonomyTermMessenger boundaryTermMessenger, PrincipalMessenger principalMessenger) throws Exception {
////		Layer layer = this.taxonomyManager.findLayerByNameAndTaxonomy(layerTermMessenger.getName(), layerTermMessenger.getTaxonomy(), false);
////		TaxonomyTerm boundaryTerm = this.taxonomyManager.findTermByNameAndTaxonomy(boundaryTermMessenger.getName(), boundaryTermMessenger.getTaxonomy(), false);
////		Principal principal = this.principalDao.findActivePrincipalByName(principalMessenger.getSystemName());
////		this.geospatialBackend.generateShapeBoundary(layer.getId(), layer.getName(), boundaryTerm, principal);
////	}
//
//	@Override
//	public List<ShapeMessenger> getShapesOfTerm(String termName, String termTaxonomy) throws Exception {
//		List<Shape> shapes = this.geospatialBackend.getShapesOfLayer(termName, termTaxonomy);
//		List<ShapeMessenger> shapeMessengers = shapes.stream().map(x -> new ShapeMessenger(x)).collect(Collectors.toList());
//		return shapeMessengers;
//	}
//
//
////	@Override
////	@Transactional
////	public List<TaxonomyTermShapeMessenger> findTermMappingsOfLayerShapes(TaxonomyTermMessenger layerTermMessenger) throws Exception {
////		
////		List<TaxonomyTermShapeMessenger> taxonomyTermShapeMessenger = new ArrayList<TaxonomyTermShapeMessenger>();
////		TaxonomyTerm layerTerm = this.taxonomyManager.findTermByNameAndTaxonomy(layerTermMessenger.getName(), layerTermMessenger.getTaxonomy(), false);
////		List<TaxonomyTermShape> taxonomyTermShapes = this.geospatialialBackend.findTermMappingsOfLayerShapes(layerTerm);
////		
////		for (TaxonomyTermShape taxonomyTermShape : taxonomyTermShapes){
////			
////			TaxonomyTermMessenger taxonomyTermMessenger = new TaxonomyTermMessenger(taxonomyTermShape.getTerm());
////			ShapeMessenger shapeMessenger = new ShapeMessenger(taxonomyTermShape.getShape());
////			PrincipalMessenger principalMessenger = new PrincipalMessenger(taxonomyTermShape.getCreator());
////			
////			taxonomyTermShapeMessenger.add(new TaxonomyTermShapeMessenger(taxonomyTermShape.getId().toString(), taxonomyTermMessenger, shapeMessenger, principalMessenger));
////		}
////		
////		return taxonomyTermShapeMessenger;
////	}
////
////	@Override
////	public List<ShapeInfoMessenger> getShapeInfoForTerm(String termName, String termTaxonomy) throws Exception {
////		
////		List<ShapeInfo> shapeInfoList = this.geospatialialBackend.getShapeInfoForTerm(termName, termTaxonomy);
////		List<ShapeInfoMessenger> shapeInfoMessengers = new ArrayList<ShapeInfo.ShapeInfoMessenger>();
////		
////		for (ShapeInfo shapeInfo : shapeInfoList){
////			shapeInfoMessengers.add(new ShapeInfoMessenger(shapeInfo.getShape(), shapeInfo.getTerm()));
////		}
////		return shapeInfoMessengers;
////	}
//
//}
