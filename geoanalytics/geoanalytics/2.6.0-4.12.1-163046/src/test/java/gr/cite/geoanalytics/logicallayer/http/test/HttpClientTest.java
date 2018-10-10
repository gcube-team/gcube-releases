/*package gr.cite.geoanalytics.logicallayer.http.test;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.geotools.util.Comparators;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import gr.cite.gaap.servicelayer.ShapeInfo;
import gr.cite.gaap.servicelayer.ShapeManager;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.GeocodeShape;
import gr.cite.geoanalytics.geospatialbackend.ClusteredGeospatialBackend;
import gr.cite.geoanalytics.logicallayer.HttpLayerOperations;
import gr.cite.geoanalytics.logicallayer.LogicalLayerBroker;

@ContextConfiguration(locations = { "classpath:WEB-INF/applicationContext.xml", "classpath:WEB-INF/geoanalytics-security.xml" })
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class HttpClientTest {

	private ShapeManager shapeManager;
	private HttpLayerOperations httpClient;
	private LogicalLayerBroker llBroker;
	private ClusteredGeospatialBackend clusteredGeospatialBackend;
	private TaxonomyManager taxonomyManager;
	
	@Inject
	public void setShapeManager(ShapeManager shapeManager) {
		this.shapeManager = shapeManager;
	}
	
	@Inject
	public void setHttpClient(HttpLayerOperations httpClient) {
		this.httpClient = httpClient;
	}
	
	@Inject
	public void setLlBroker(LogicalLayerBroker llBroker) {
		this.llBroker = llBroker;
	}
	
	@Inject
	public void setTaxonomyManager(TaxonomyManager taxonomyManager) {
		this.taxonomyManager = taxonomyManager;
	}
	
	@Inject
	public void setClusteredGeospatialBackend(ClusteredGeospatialBackend clusteredGeospatialBackend) {
		this.clusteredGeospatialBackend = clusteredGeospatialBackend;
	}
	
	@Test
	public void testGetShapesOfTerm() throws Exception {
		Set<Shape> cShapes = new HashSet<>(shapeManager.getShapesOfLayer("Taxon1Term1", "Taxon1"));
		Set<Shape> lShapes = new HashSet<>(clusteredGeospatialBackend.getShapesOfLayer("Taxon1Term1", "Taxon1"));
		
		Assert.assertEquals("Shapes not equal", cShapes, lShapes);
	}
	
	@Test
	public void testGetShapeInfoForTerm() throws Exception {
		List<ShapeInfo> cShapes = clusteredGeospatialBackend.getShapeInfoForTerm("Taxon1Term1", "Taxon1");
		List<ShapeInfo> lShapes = shapeManager.getShapeInfoForTerm("Taxon1Term1", "Taxon1");
		
		Assert.assertEquals("Shapes not equal", cShapes, lShapes);
	}
	
	@Test
	public void testGetAttributeValuesOfShapesByTerm() throws Exception {
		TaxonomyTerm layerTerm = taxonomyManager.findTermByNameAndTaxonomy("Taxon1Term1", "Taxon1", false);
		Attribute attr = new Attribute("NAME_GR", "STRING", "2bffe580-1d5a-45ab-8190-2817dae73ed0", null);
		Set<String> cAttrs = clusteredGeospatialBackend.getAttributeValuesOfShapesByTerm(layerTerm, attr);
		Set<String> lAttrs = shapeManager.getAttributeValuesOfShapesByTerm(layerTerm, attr);
		
		Assert.assertEquals("Attributes not equal", cAttrs, lAttrs);
	}
	
	@Test
	public void testFindTermMappingsForLayerShapes() throws Exception {
		TaxonomyTerm layerTerm = taxonomyManager.findTermByNameAndTaxonomy("Taxon1Term1", "Taxon1", false);
		Set<GeocodeShape> cTts = new HashSet<>(clusteredGeospatialBackend.findTermMappingsOfLayerShapes(layerTerm));
		Set<GeocodeShape> lTts = new HashSet<>(shapeManager.findTermMappingsOfLayerShapes(layerTerm));
		
		Assert.assertEquals("Taxonomy term shapes not equal", cTts, lTts);
		
		
	}
}*/