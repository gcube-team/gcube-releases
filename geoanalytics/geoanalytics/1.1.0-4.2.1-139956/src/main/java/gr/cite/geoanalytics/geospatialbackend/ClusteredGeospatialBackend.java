package gr.cite.geoanalytics.geospatialbackend;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.io.WKTReader;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.commons.util.datarepository.DataRepository;
import gr.cite.gaap.datatransferobjects.PrincipalMessenger;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.ConfigurationManager.AttributeLayerIdPair;
import gr.cite.gaap.servicelayer.DocumentManager;
import gr.cite.gaap.utilities.ExceptionUtils;
import gr.cite.gaap.servicelayer.ShapeInfo;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;
import gr.cite.geoanalytics.dataaccess.entities.user.dao.UserDaoOld;
import gr.cite.geoanalytics.logicallayer.LogicalLayerBroker;

@Service
public class ClusteredGeospatialBackend extends ShapeManager {
	
	@Inject
	public ClusteredGeospatialBackend(PrincipalDao principalDao, TaxonomyManager taxonomyManager, DocumentManager documentManager,
			DataRepository repository, ConfigurationManager configurationManager) {
		super(principalDao, taxonomyManager, documentManager, repository, configurationManager);
	}

	private LogicalLayerBroker logicalLayerBroker = null;
	private WKTReader wktReader = null;
	
	@Inject
	public void setLogicalLayerBroker(LogicalLayerBroker logicalLayerBroker) {
		this.logicalLayerBroker = logicalLayerBroker;
	}
	
	@Inject
	public void setWktReader(WKTReader wktReader) {
		this.wktReader = wktReader;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<String> getShapeAttributeValues(Taxonomy t) throws Exception {
		AttributeLayerIdPair attrLayer = configurationManager.findAttributeByTermId(t.getId().toString());
		
		if(attrLayer.layerId == null)
			return new HashSet<String>();
		return logicalLayerBroker.getAttributeValuesOfShapesByTerm(new TaxonomyTermMessenger(taxonomyManager.findTermById(attrLayer.layerId, false)), attrLayer.attr);
	}
	
	@Override
	@Transactional
	public void generateShapeBoundary(TaxonomyTerm layerTerm, TaxonomyTerm boundaryTerm, Principal principal) throws Exception {
		logicalLayerBroker.generateShapeBoundary(new TaxonomyTermMessenger(layerTerm), new TaxonomyTermMessenger(boundaryTerm), new PrincipalMessenger(principal));
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Shape> getShapesOfLayer(String termName, String termTaxonomy) throws Exception {
		return logicalLayerBroker.getShapesOfTerm(termName, termTaxonomy).stream().
			map(x -> shapeMessengerToShape(x)).
			collect(Collectors.toList());
	}

	private Shape shapeMessengerToShape(ShapeMessenger x) {
		Shape s = new Shape();
		s.setCode(x.getCode());
		//s.setCreationDate(x.get);
		//s.setCreator(x.get);
		s.setExtraData(x.getExtraData());
		try {
			s.setGeography(wktReader.read(x.getGeometry()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		s.setId(UUID.fromString(x.getId()));
//	s.setLastUpdate(x.get);
		s.setName(x.getName());
		s.setShapeClass(x.getShapeClass());
//	s.setShapeImport(x.get);
//	s.setShapeTerm(x.get);
		return s;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ShapeInfo> getShapeInfoForTerm(String termName, String termTaxonomy) throws Exception {
		return logicalLayerBroker.getShapeInfoForTerm(termName, termTaxonomy).stream().
				map(x -> {
					ShapeInfo si = new ShapeInfo();
					si.setShape(shapeMessengerToShape(x.getShapeMessenger()));
					si.setTerm(ExceptionUtils.wrap(() -> taxonomyManager.findTermByNameAndTaxonomy(x.getTaxonomyTermMessenger().getName(), x.getTaxonomyTermMessenger().getTaxonomy(), false)).get());
					return si;
				}).
				collect(Collectors.toList());
	}
	
	@Override
	@Transactional
	public List<TaxonomyTermShape> findTermMappingsOfLayerShapes(TaxonomyTerm layerTerm) throws Exception {
		return logicalLayerBroker.findTermMappingsOfLayerShapes(new TaxonomyTermMessenger(layerTerm)).stream().
				map(x -> {
					TaxonomyTermShape tts = new TaxonomyTermShape();
					tts.setId(UUID.fromString(x.getId()));
					tts.setShape(shapeMessengerToShape(x.getShapeMessenger()));
					tts.setTerm(ExceptionUtils.wrap(() -> taxonomyManager.findTermByNameAndTaxonomy(x.getTaxonomyTermMessenger().getName(), x.getTaxonomyTermMessenger().getTaxonomy(), false)).get());
					return tts;
				}).
				collect(Collectors.toList());

	}
}
