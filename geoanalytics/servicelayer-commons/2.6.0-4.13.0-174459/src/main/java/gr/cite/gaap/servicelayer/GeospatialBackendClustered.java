package gr.cite.gaap.servicelayer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.vividsolutions.jts.geom.*;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.Hints;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.geometry.PositionFactory;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.Iterables;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import gr.cite.clustermanager.actuators.layers.DataCreatorGeoanalytics;
import gr.cite.clustermanager.actuators.layers.DataMonitor;
import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.clustermanager.model.layers.ZNodeData.ZNodeStatus;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.gaap.datatransferobjects.AttributeInfo;
import gr.cite.gaap.datatransferobjects.Coords;
import gr.cite.gaap.datatransferobjects.GeoLocation;
import gr.cite.gaap.datatransferobjects.GeoLocationTag;
import gr.cite.gaap.datatransferobjects.GeoSearchSelection.SearchType;
import gr.cite.gaap.datatransferobjects.NewProjectData;
import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.WfsShapeInfo;
import gr.cite.gaap.utilities.HtmlUtils;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;
import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
import gr.cite.geoanalytics.dataaccess.entities.coverage.Coverage;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.project.Project.ProjectStatus;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDocumentDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig.Type;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.definition.TaxonomyData;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.gos.client.RasterManagement;
import gr.cite.gos.client.ShapeManagement;

@Service
@Primary
public class GeospatialBackendClustered {

	private static final Logger log = LoggerFactory.getLogger(GeospatialBackendClustered.class);

	protected GeocodeManager geocodeManager;
	protected ConfigurationManager configurationManager;

	// the following 2 are part of the client to exchange information with the gos nodes
	private ShapeManagement shapeManagement;
	private RasterManagement rasterManagement;

	// these two are part of the Zookeeper Cluster management (monitoring and editing)
	private DataMonitor dataMonitor;
	private DataCreatorGeoanalytics dataCreatorGeoanalytics;

	// this is for traffic shaping
	private TrafficShaper trafficShaper;

	private GeocodeDao geocodeDao;
	private LayerDao layerDao;
	private ProjectDao projectDao;
	private static final String NoMappingKey = "\t\t\t__NoVal__\t\t\t";
	private static final String NoValueKey = "";

	@Inject
	public GeospatialBackendClustered(PrincipalDao principalDao, GeocodeManager geocodeManager, DocumentManager documentManager, ConfigurationManager configurationManager) {
		this.geocodeManager = geocodeManager;
		this.configurationManager = configurationManager;
	}

	@Inject
	public void setDataCreatorGeoanalytics(DataCreatorGeoanalytics dataCreatorGeoanalytics) {
		this.dataCreatorGeoanalytics = dataCreatorGeoanalytics;
	}

	@Inject
	public void setDataMonitor(DataMonitor dataMonitor) {
		this.dataMonitor = dataMonitor;
	}

	public DataMonitor getDataMonitor() {
		return dataMonitor;
	}

	@Inject
	public void setTrafficShaper(TrafficShaper trafficShaper) {
		this.trafficShaper = trafficShaper;
	}

	@Inject
	public void setShapeDocumentDao(ShapeDocumentDao shapeDocumentDao) {}

	@Inject
	public void setGeocodeDao(GeocodeDao geocodeDao) {
		this.geocodeDao = geocodeDao;
	}

	@Inject
	public void setLayerDao(LayerDao layerDao) {
		this.layerDao = layerDao;
	}

	@Inject
	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}

	@Inject
	public void setShapeManagement(ShapeManagement shapeManagement) {
		this.shapeManagement = shapeManagement;
	}

	@Inject
	public void setRasterManagement(RasterManagement rasterManagement) {
		this.rasterManagement = rasterManagement;
	}

	public ShapeManagement getShapeManagement() {
		return shapeManagement;
	}

	@Transactional(readOnly = true)
	public Shape findShapeById(String gosEndpoint, final UUID id) throws IOException {
		try {
			return shapeManagement.getShapeByID(gosEndpoint, id.toString());
		} catch (Exception e) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public ShapeInfo findShapeByIdInfo(String gosEndpoint, UUID shapeID) throws Exception {
		Shape s = findShapeById(gosEndpoint, shapeID);
		ShapeInfo si = new ShapeInfo();
		si.setShape(s);
		si.setLayerID(s.getLayerID());
		return si;
	}

	@Transactional(readOnly = true)
	public Shape findShapeById(final UUID id) throws IOException {
		Set<Shape> shapes = dataMonitor.getAllGosEndpoints().parallelStream().map(gosEndpoint -> {
			try {
				return shapeManagement.getShapeByID(gosEndpoint.getGosEndpoint(), id.toString());
			} catch (Exception e) {
				return null;
			}
		}).filter(shape -> shape != null).collect(Collectors.toSet());
		return shapes.iterator().next();
	}

	/**
	 * Retrieves the layerID of this shape
	 * 
	 * @see gr.cite.gaap.geospatialbackend.GeospatialBackend#findShapeByIdInfo(java.util.UUID)
	 */
	@Transactional(readOnly = true)
	public ShapeInfo findShapeByIdInfo(UUID shapeID) throws Exception {
		Shape s = findShapeById(shapeID);
		ShapeInfo si = new ShapeInfo();
		si.setShape(s);
		si.setLayerID(s.getLayerID());
		return si;
	}

	@Transactional
	public void createCoverageOfLayer(GosDefinition gosDefinition, Coverage coverage) throws Exception {
		dataCreatorGeoanalytics.addLayer(coverage.getLayerID().toString(), ZNodeStatus.ACTIVE, gosDefinition.getGosIdentifier());
		rasterManagement.createCoverage(gosDefinition.getGosEndpoint(), coverage);
	}

	@Transactional
	public boolean createShapesOfLayer(GosDefinition gosDefinition, Collection<Shape> shapes) throws Exception {
		if (shapes == null || shapes.isEmpty())
			return false;
		String layerID = shapes.iterator().next().getLayerID().toString();
		log.info("Performing shape insertion on GOS: " + gosDefinition.getGosEndpoint());
		dataCreatorGeoanalytics.addLayer(layerID, ZNodeStatus.ACTIVE, gosDefinition.getGosIdentifier());
		boolean status = shapeManagement.insertShapes(gosDefinition.getGosEndpoint(), shapes);
		return status;
	}

	public String retrieveShapeAttributeValue(Shape s, String attribute) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));

		NodeList els = d.getElementsByTagName(attribute);
		if (els == null || els.getLength() == 0)
			return null;

		Element el = (Element) els.item(0);
		return el.getFirstChild().getNodeValue();
	}

	public AttributeInfo retrieveShapeAttribute(Shape s, String attribute) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));

		NodeList els = d.getElementsByTagName(attribute);
		if (els == null || els.getLength() == 0)
			return null;

		Element el = (Element) els.item(0);
		AttributeInfo ai = new AttributeInfo();
		ai.setName(attribute);
		ai.setValue(el.getFirstChild().getNodeValue());
		ai.setTaxonomy(el.getAttribute("taxonomy"));
		ai.setType(ShapeAttributeDataType.valueOf(el.getAttribute("type").toUpperCase()).toString());
		ai.setPresentable(Boolean.valueOf(el.getAttribute("presentable")));
		return ai;
	}

	@Transactional(readOnly = true)
	public AttributeInfo retrieveShapeAttributeByTaxonomy(Shape s, String taxonomy) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));

		NodeList els = d.getElementsByTagName("extraData").item(0).getChildNodes();
		for (int i = 0; i < els.getLength(); i++) {
			Element el = (Element) els.item(i);
			if (el.getAttribute("taxonomy").equals(taxonomy)) {
				AttributeInfo ai = new AttributeInfo();
				ai.setName(el.getNodeName());
				ai.setValue(el.getFirstChild().getNodeValue());
				ai.setTaxonomy(taxonomy);
				ai.setType(ShapeAttributeDataType.valueOf(el.getAttribute("type").toUpperCase()).toString());
				ai.setPresentable(Boolean.valueOf(el.getAttribute("presentable")));
				return ai;
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	private Map<String, AttributeInfo> retrieveRawShapeAttributes(Shape s) throws Exception {
		Map<String, AttributeInfo> attributes = new HashMap<String, AttributeInfo>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));

		NodeList els = d.getElementsByTagName("extraData").item(0).getChildNodes();
		for (int i = 0; i < els.getLength(); i++) {
			Element el = (Element) els.item(i);

			String taxon = el.getAttribute("taxonomy");
			if (taxon == null || taxon.trim().isEmpty())
				continue;
			AttributeInfo ai = new AttributeInfo();
			ai.setName(el.getNodeName());
			if (el.getFirstChild() != null)
				ai.setValue(el.getFirstChild().getNodeValue());
			ai.setTaxonomy(taxon);
			ai.setType(ShapeAttributeDataType.valueOf(el.getAttribute("type").toUpperCase()).toString());
			ai.setPresentable(Boolean.valueOf(el.getAttribute("presentable")));
			attributes.put(taxon, ai);
		}
		return attributes;
	}

	@Transactional
	public List<String> addShapeAttribute(Shape s, String attrName, String attrValue, GeocodeSystem taxonomy) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));

		Element root = d.getDocumentElement();
		NodeList els = d.getElementsByTagName(attrName);
		if (els != null && els.getLength() != 0)
			throw new Exception("Attribute " + attrName + " already exists");

		Element el = d.createElement(attrName);
		el.setAttribute("type", ShapeAttributeDataType.STRING.toString());

		if (taxonomy != null) {
			el.setAttribute("taxonomy", taxonomy.getId().toString());
		}
		el.appendChild(d.createTextNode(attrValue));
		root.appendChild(el);

		s.setExtraData(transformDocToString(d));

		List<String> endpoints = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				boolean status = shapeManagement.updateShape(gosDefinition.getGosEndpoint(), s);
				if (status)
					return gosDefinition.getGosEndpoint();
				else
					return null;
			} catch (Exception e) {
				log.error("Could not update shape with id: " + s.getId().toString() + " on endpoint: " + gosDefinition.getGosEndpoint());
				return null;
			}
		}).filter(endpoint -> endpoint != null).collect(Collectors.toList());

		return endpoints;
	}

	private String transformDocToString(org.w3c.dom.Document document) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		return writer.getBuffer().toString().replaceAll("\n|\r", "");
	}

	@Transactional
	public List<String> setShapeAttributes(Shape s, Map<String, AttributeInfo> attrs) throws Exception {
		StringBuilder xml = new StringBuilder();
		xml.append("<extraData>");
		for (Map.Entry<String, AttributeInfo> attrE : attrs.entrySet()) {
			AttributeInfo attr = attrE.getValue();
			xml.append("<" + attr.getName() + " type=\"" + ShapeAttributeDataType.valueOf(attr.getType().toUpperCase()).toString() + "\" " + "taxonomy=\"" + attr.getTaxonomy()
					+ "\" " + (attr.getTerm() != null ? "term=\"" + attr.getTerm() + "\"" : "") + ">");
			xml.append(attr.getValue());
			xml.append("</" + attr.getName() + ">");
		}
		xml.append("</extraData>");

		s.setExtraData(xml.toString());

		List<String> endpoints = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				boolean status = shapeManagement.updateShape(gosDefinition.getGosEndpoint(), s);
				if (status)
					return gosDefinition.getGosEndpoint();
				else
					return null;
			} catch (Exception e) {
				log.error("Could not update shape with id: " + s.getId().toString() + " on endpoint: " + gosDefinition.getGosEndpoint());
				return null;
			}
		}).filter(endpoint -> endpoint != null).collect(Collectors.toList());

		return endpoints;
	}

	@Transactional
	public List<String> updateShapeAttribute(Shape s, String attrName, String attrValue) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));

		NodeList els = d.getElementsByTagName(attrName);
		if (els == null || els.getLength() == 0)
			throw new Exception("Attribute " + attrName + " not found");

		Element el = (Element) els.item(0);
		el.getFirstChild().setNodeValue(attrValue);

		List<String> endpoints = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				boolean status = shapeManagement.updateShape(gosDefinition.getGosEndpoint(), s);
				if (status)
					return gosDefinition.getGosEndpoint();
				else
					return null;
			} catch (Exception e) {
				log.error("Could not update shape with id: " + s.getId().toString() + " on endpoint: " + gosDefinition.getGosEndpoint());
				return null;
			}
		}).filter(endpoint -> endpoint != null).collect(Collectors.toList());

		return endpoints;

	}

	@Transactional
	public List<String> removeShapeAttribute(Shape s, String attrName) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));

		NodeList els = d.getElementsByTagName(attrName);
		if (els == null || els.getLength() == 0)
			throw new Exception("Attribute " + attrName + " not found");

		Element el = (Element) els.item(0);
		d.removeChild(el);

		List<String> endpoints = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				boolean status = shapeManagement.updateShape(gosDefinition.getGosEndpoint(), s);
				if (status)
					return gosDefinition.getGosEndpoint();
				else
					return null;
			} catch (Exception e) {
				log.error("Could not update shape with id: " + s.getId().toString() + " on endpoint: " + gosDefinition.getGosEndpoint());
				return null;
			}
		}).filter(endpoint -> endpoint != null).collect(Collectors.toList());

		return endpoints;

	}

	@Transactional
	public Set<String> getAttributeValuesOfShapesByLayer(UUID layerID, Attribute attr) throws Exception {
		GosDefinition gosDefinition = trafficShaper.getAppropriateGosForLayer(layerID.toString());
		return shapeManagement.getAttributeValuesOfShapesByLayer(gosDefinition.getGosEndpoint(), layerID.toString(), attr);
	}

	@Transactional(readOnly = true)
	public Map<String, AttributeInfo> consolidateAttributes(Shape s) throws Exception {
		Map<String, AttributeInfo> attrs = new HashMap<String, AttributeInfo>();

		Set<TaxonomyConfig> extraTaxonIds = new HashSet<TaxonomyConfig>();
		List<TaxonomyConfig> infoCategories = configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.PROJECTINFOCATEGORYTAXONOMY);
		for (TaxonomyConfig infoCfg : infoCategories)
			extraTaxonIds.addAll(configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.valueOf(infoCfg.getType())));

		Point centroid = s.getGeography().getCentroid();
		List<Geocode> geoLocation = geoLocate(centroid.getX(), centroid.getY());

		String geographyTaxonId = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY).get(0).getId();

		new HashSet<Geocode>();

		Geocode tt = null;
		Iterator<Geocode> ttIt = geoLocation.iterator();
		List<String> attrTaxonomies = new ArrayList<String>();
		for (TaxonomyConfig tcfg : extraTaxonIds)
			attrTaxonomies.add(tcfg.getId());

		// geographic info
		boolean foundWidestGeographyTerm = false;
		while (ttIt.hasNext()) // geoLocate returns terms by geographic order, e.g. country->prefecture->...
		{
			tt = ttIt.next();
			if (tt.getGeocodeSystem().getId().toString().equals(geographyTaxonId))
				foundWidestGeographyTerm = true; // found the term representing the widest area in which there exist useful attributes

			if (foundWidestGeographyTerm == true)
				attrTaxonomies.add(tt.getGeocodeSystem().getId().toString()); // geographic info
		}

		return attrs;

	}

	@Transactional(readOnly = true)
	public Map<String, AttributeInfo> computeAttributes(Shape s) throws Exception {
		Map<String, AttributeInfo> attrs = new HashMap<String, AttributeInfo>();

		Point centroid = s.getGeography().getCentroid();

		AttributeInfo ai = new AttributeInfo();
		ai.setName("location");
		ai.setPresentable(true);
		ai.setTaxonomy(configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.LOCATIONTAXONOMY).get(0).getId());
		ai.setType(ShapeAttributeDataType.STRING.toString());
		ai.setValue(centroid.getX() + "," + centroid.getY());
		attrs.put(TaxonomyConfig.Type.LOCATIONTAXONOMY.toString(), ai);

		double area = s.getGeography().getArea();
		ai = new AttributeInfo();
		ai.setName("area");
		ai.setPresentable(true);
		ai.setTaxonomy(configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.AREATAXONOMY).get(0).getId());
		ai.setType(ShapeAttributeDataType.DOUBLE.toString());
		ai.setValue(new Double(area).toString());
		attrs.put(TaxonomyConfig.Type.AREATAXONOMY.toString(), ai);

		return attrs;
	}

	@Transactional(readOnly = true)
	public Map<String, AttributeInfo> retrieveShapeAttributes(Shape s) throws Exception {
		Map<String, AttributeInfo> res = new HashMap<String, AttributeInfo>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		if (s.getExtraData() == null || s.getExtraData().trim().isEmpty())
			return res;
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));

		GeographyHierarchy hier = getDefaultGeographyHierarchy();

		NodeList els = d.getChildNodes().item(0).getChildNodes();
		for (int i = 0; i < els.getLength(); i++) {
			String geogTaxonomyName = null;
			Element el = (Element) els.item(i);
			String taxonomyId = el.getAttribute("taxonomy");
			if (taxonomyId == null || taxonomyId.trim().isEmpty())
				continue;
			TaxonomyConfig tcfg = configurationManager.retrieveTaxonomyConfigById(taxonomyId, true);
			if (tcfg != null
					&& (tcfg.getType().equals(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY.toString()) || tcfg.getType().equals(TaxonomyConfig.Type.ALTGEOGRAPHYTAXONOMY.toString())))
				tcfg = null; // trigger geographic mode for those taxonomies that mappings are present
			if (tcfg == null) {
				boolean foundGeog = false;
				for (GeocodeSystem geogT : hier.getMainHierarchy()) {
					if (geogT.getId().toString().equals(taxonomyId)) {
						foundGeog = true;
						geogTaxonomyName = geogT.getName();
						geogT.getId().toString();
						break;
					}
				}
				if (!foundGeog) {
					for (List<GeocodeSystem> altHier : hier.getAlternativeHierarchies()) {
						for (GeocodeSystem geogT : altHier) {
							if (geogT.getId().toString().equals(taxonomyId)) {
								foundGeog = true;
								geogTaxonomyName = geogT.getName();
								geogT.getId().toString();
								break;
							}
						}
						if (foundGeog)
							break;
					}
				}
				if (!foundGeog)
					continue;
			}

			List<AttributeMappingConfig> taxonMcfgs = configurationManager.getAttributeMappings(el.getNodeName(), null);
			/*
			 * if(taxonMcfgs == null || taxonMcfgs.isEmpty()) //TODO check if needed continue;
			 */
			List<AttributeMappingConfig> valMcfgs = null;
			if (el.getFirstChild() != null)
				valMcfgs = configurationManager.getAttributeMappings(el.getNodeName(), el.getFirstChild().getNodeValue());

			boolean presentable = true;
			boolean mapValue = false;
			if (taxonMcfgs != null) {
				for (AttributeMappingConfig mcfg : taxonMcfgs) {
					if (!mcfg.isPresentable()) {
						presentable = false;
						break;
					}
				}
				if (!presentable)
					continue;
			}

			String ttStr = null;
			if (valMcfgs != null) {
				for (AttributeMappingConfig mcfg : valMcfgs) // iterate over all value mappings for this value (one for each layer). check if at least
																// one specifies mapped value
				{
					if (mcfg.isMapValue())
						mapValue = true;
					if (mcfg.getTermId() != null)
						ttStr = mcfg.getTermId();
				}
			}

			Geocode tt = null;

			if (ttStr != null && !ttStr.trim().isEmpty())
				tt = geocodeManager.findTermById(ttStr, false);

			String val = null;
			if (!mapValue) {
				if (el.getFirstChild() != null) {
					val = el.getFirstChild().getNodeValue();
				}
			} else {
				if (tt == null) {
					log.error("Could not find mapped taxonomy term: " + el.getAttribute("term") + ". Skipping");
					continue;
				}
				val = tt.getName();
			}

			AttributeInfo ai = new AttributeInfo();
			ai.setName(el.getNodeName());
			ai.setValue(val);
			ai.setType(tcfg != null ? tcfg.getId() : geogTaxonomyName);
			ai.setTaxonomy(tcfg != null ? tcfg.getId() : geogTaxonomyName);
			res.put(tcfg != null ? tcfg.getId().toString() : geogTaxonomyName, ai);

		}
		return res;
	}

	public class GeocodeInsertionPoint {

		private List<Geocode> over;
		private Geocode under;

		public GeocodeInsertionPoint() {}

		public GeocodeInsertionPoint(List<Geocode> over, Geocode under) {
			this.over = over;
			this.under = under;
		}

		public List<Geocode> getParent() {
			return over;
		}

		public void setOver(List<Geocode> over) {
			this.over = over;
		}

		public Geocode getUnder() {
			return under;
		}

		public void setUnder(Geocode under) {
			this.under = under;
		}

	}

	private void addMappingConfig(AttributeMappingConfig mcfg, Map<String, Map<String, AttributeMappingConfig>> cfgCache) throws Exception {
		if (cfgCache == null) {
			configurationManager.updateMappingConfig(mcfg);
			return;
		}
		String key = null;
		if (mcfg.getAttributeName() == null)
			return;
		if (mcfg.getTermId() == null && mcfg.getAttributeValue() == null)
			key = NoMappingKey;
		else if (mcfg.getAttributeValue() == null)
			key = NoValueKey;
		else
			key = mcfg.getAttributeValue();

		if (cfgCache.get(mcfg.getAttributeName()) == null)
			cfgCache.put(mcfg.getAttributeName(), new HashMap<String, AttributeMappingConfig>());
		if (cfgCache.get(mcfg.getAttributeName()).get(key) == null) {
			cfgCache.get(mcfg.getAttributeName()).put(key, mcfg);
			configurationManager.updateMappingConfig(mcfg);
		}
	}

	/**
	 * 
	 * @param layerID
	 * @param layerName
	 * @param boundaryTerm
	 * @param principal
	 * @return The GOS endpoints on which a modification was done!
	 * @throws Exception
	 */
	@Transactional
	public List<String> generateShapeBoundary(UUID layerID, String layerName, Geocode boundaryTerm, Principal principal) throws Exception {
		List<Shape> shapes = getShapesOfLayer(layerID);

		GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
		GeometryCollection geometryCollection = (GeometryCollection) factory.buildGeometry(shapes.parallelStream().map(shape -> shape.getGeography()).collect(Collectors.toList()));

		final Shape boundary = new Shape();
		boundary.setCreatorID(principal.getId());
		boundary.setGeography(geometryCollection.union());
		boundary.setName(layerName + "_boundary");

		Set<GosDefinition> gosDefinitions = dataMonitor.getAvailableGosFor(layerID.toString());
		gosDefinitions.addAll(dataMonitor.getNotAvailableGosFor(layerID.toString()));

		List<String> endpoints = gosDefinitions.parallelStream().map(gosDefinition -> {
			try {
				boolean status = shapeManagement.insertShape(gosDefinition.getGosEndpoint(), boundary);
				if (status)
					return gosDefinition.getGosEndpoint();
				else
					return null;
			} catch (Exception e) {
				log.error("Could not insert (boundary) shape with id: " + boundary.getId().toString() + " on endpoint: " + gosDefinition.getGosEndpoint());
				return null;
			}
		}).filter(e -> e != null).collect(Collectors.toList());
		return endpoints;
	}

	@Transactional(readOnly = true)
	public List<Shape> getShapesOfLayer(UUID layerID) throws Exception {
		GosDefinition gosDefinition = trafficShaper.getAppropriateGosForLayer(layerID.toString());
		return shapeManagement.getShapesOfLayerID(gosDefinition.getGosEndpoint(), layerID.toString());
	}

	@Transactional(readOnly = true)
	@Deprecated
	public List<ShapeInfo> getShapesInfoForLayer(String layerID) throws Exception {
		return getShapesInfoForLayer(UUID.fromString(layerID));
	}

	@Transactional(readOnly = true)
	@Deprecated
	public List<ShapeInfo> getShapesInfoForLayer(UUID layerID) throws Exception {
		return getShapesOfLayer(layerID).parallelStream().map(shape -> {
			ShapeInfo si = new ShapeInfo();
			si.setShape(shape);
			si.setLayerID(layerID);
			return si;
		}).collect(Collectors.toList());
	}

	/**
	 * 
	 * @param layerID
	 * @return The GOS endpoints on which a modification was done!
	 * @throws Exception
	 */
	@Transactional
	public List<String> deleteShapesOfLayer(UUID layerID) throws Exception {
		Set<GosDefinition> gosDefinitions = dataMonitor.getAvailableGosFor(layerID.toString());
		gosDefinitions.addAll(dataMonitor.getNotAvailableGosFor(layerID.toString()));

		List<String> endpoints = gosDefinitions.parallelStream().map(gosDefinition -> {
			boolean status = shapeManagement.deleteShapesOfLayer(gosDefinition.getGosEndpoint(), layerID.toString());
			if (status)
				return gosDefinition.getGosEndpoint();
			else
				return null;
		}).filter(e -> e != null).collect(Collectors.toList());

		return endpoints;
	}

	@Transactional(readOnly = true)
	@Deprecated
	public UUID findLayerIDOfShape(Shape s) throws Exception {
		return s.getLayerID();
	}

	@Transactional(readOnly = true)
	public List<ShapeInfo> findShapeWithinBounds(String bounds) throws Exception {
		Geometry geom = new WKTReader().read(bounds);
		geom.setSRID(4326);
		Shape sh = new Shape();
		sh.setId(UUIDGenerator.randomUUID());
		sh.setGeography(geom);

		List<Shape> shapes = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				return shapeManagement.findContains(gosDefinition.getGosEndpoint(), sh);
			} catch (Exception e) {
				return new ArrayList<Shape>();
			}
		}).filter(list -> !list.isEmpty()).flatMap(l -> l.stream()).collect(Collectors.toList());

		List<ShapeInfo> res = new ArrayList<ShapeInfo>();
		for (Shape s : shapes) {
			ShapeInfo si = new ShapeInfo();
			si.setShape(s);
			si.setLayerID(s.getId());
			res.add(si);
		}
		return res;
	}

	@Transactional(readOnly = true)
	public boolean existShapesOfLayer(UUID layerID) throws Exception {
		GosDefinition gosDefinition = trafficShaper.getAppropriateGosForLayer(layerID.toString());
		if (gosDefinition == null)
			return false;
		// //just an additional check (but it might be a little slow)
		long count = shapeManagement.countShapesOfLayer(gosDefinition.getGosEndpoint(), layerID.toString());
		if (count == 0)
			return false;
		return true;
	}

	@Transactional(readOnly = true)
	public ShapeInfo getShape(UUID id) throws Exception {
		List<Shape> shapes = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				return shapeManagement.getShapeByID(gosDefinition.getGosEndpoint(), id.toString());
			} catch (Exception ex) {
				return null;
			}
		}).filter(shape -> shape != null).collect(Collectors.toList());
		if (shapes != null && shapes.isEmpty()) {
			ShapeInfo si = new ShapeInfo();
			si.setShape(shapes.iterator().next());
			si.setLayerID(shapes.iterator().next().getLayerID());
			return si;
		}
		return null;
	}

	@Transactional(readOnly = true)
	public Bounds getShapeBounds(UUID id) throws Exception {
		Shape s = findShapeById(id);
		if (s == null)
			throw new Exception("Shape " + id + " not found");

		Geometry envelope = s.getGeography().getEnvelope();
		envelope.setSRID(4326);

		// TODO: compute shape bounds from envelope
		// Bounds bounds = new Bounds(minx, miny, maxx, maxy, crs)
		// return bounds;

		return null;

	}

	public Shape generateShapeFromGeometry(Principal principal, String shapeName, String geometry) throws Exception {
		Geometry geom = new WKTReader().read(geometry);
		geom.setSRID(4326);
		Shape s = new Shape();
		s.setGeography(geom);
		s.setName(shapeName);
		s.setCreatorID(principal.getId());
		s.setId(UUID.randomUUID());
		return s;
	}

	@Transactional
	public List<String> createFromGeometry(Principal principal, String shapeName, String geometry) throws Exception {

		Shape s = generateShapeFromGeometry(principal, shapeName, geometry);

		List<String> endpoints = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				boolean status = shapeManagement.insertShape(gosDefinition.getGosEndpoint(), s);
				if (status)
					return gosDefinition.getGosEndpoint();
				else
					return null;
			} catch (Exception e) {
				log.error("Could not insert shape with id: " + s.getId().toString() + " on endpoint: " + gosDefinition.getGosEndpoint());
				return null;
			}
		}).filter(e -> e != null).collect(Collectors.toList());

		return endpoints;
	}

	@Transactional
	public List<String> createFromGeometry(Project project, String geometry) throws Exception {
		return createFromGeometry(project.getCreator(), project.getName(), geometry);
	}

	public Shape generateShapeFromGeometryPolygon(Project project, NewProjectData npd, Principal principal) throws Exception {
		WKTReader reader = new WKTReader();
		String polygon = "POLYGON((" + npd.getCoords().getCoord0()[0] + " " + npd.getCoords().getCoord0()[1] + "," + npd.getCoords().getCoord1()[0] + " "
				+ npd.getCoords().getCoord1()[1] + "," + npd.getCoords().getCoord2()[0] + " " + npd.getCoords().getCoord2()[1] + "," + npd.getCoords().getCoord3()[0] + " "
				+ npd.getCoords().getCoord3()[1] + "," + npd.getCoords().getCoord0()[0] + " " + npd.getCoords().getCoord0()[1] + "))";

		Geometry g = reader.read(polygon);
		g.setSRID(4326);

		// if ( !geo.isRectangle() ) {
		// geo = geo.getEnvelope();
		// WKTWriter writer = new WKTWriter();
		// String bbox = writer.write( geo );
		// wkt = bbox;
		// }

		Shape s = new Shape();
		s.setGeography(g);
		s.setCreatorID(principal.getId());
		// s.setCode(npd.getCoords().toString());

		s.setId(UUID.randomUUID());

		return s;
	}

	@Transactional
	public List<String> createFromGeometryPolygon(Project project, NewProjectData npd, Principal principal) throws Exception {

		Shape s = generateShapeFromGeometryPolygon(project, npd, principal);

		List<String> endpoints = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				boolean status = shapeManagement.insertShape(gosDefinition.getGosEndpoint(), s);
				if (status)
					return gosDefinition.getGosEndpoint();
				else
					return null;
			} catch (Exception e) {
				log.error("Could not insert shape with id: " + s.getId().toString() + " on endpoint: " + gosDefinition.getGosEndpoint());
				return null;
			}
		}).filter(e -> e != null).collect(Collectors.toList());

		return endpoints;
	}

	@Transactional
	public List<String> updateGeometry(UUID id, String geometry) throws Exception {

		ShapeInfo si = getShape(id);
		if (si == null)
			throw new Exception("Shape with id: " + id + " was not found");
		Shape ex = si.getShape();
		Geometry geom = new WKTReader().read(geometry);
		ex.setGeography(geom);

		return update(ex);
	}

	@Transactional(readOnly = true)
	public String getGeometry(UUID id) throws Exception {
		ShapeInfo si = getShape(id);
		if (si == null)
			throw new Exception("Shape with id: " + id + " was not found");
		return new WKTWriter().write(si.getShape().getGeography());
	}

	@Transactional(readOnly = true)
	public String getBoundingBoxByProjectNameAndTenant(String projectName, String tenantName) throws Exception {
		List<Project> projects = projectDao.findByNameAndTenant(projectName, tenantName);
		if (projects != null && projects.size() > 1) {
			throw new Exception("Multiple projects with name " + projectName);
		}

		Project project = null;
		if (projects != null && !projects.isEmpty()) {
			project = projects.get(0);
		}

		return project.getExtent();
	}

	public String getBoundingBoxByProjectName(String projectName) throws Exception {
		List<Project> projects = projectDao.findByName(projectName);
		if (projects != null && projects.size() > 1) {
			throw new Exception("Multiple projects with name " + projectName);
		}

		Project project = null;
		if (projects != null && !projects.isEmpty()) {
			project = projects.get(0);
		}

		return project.getExtent();
	}

	@Transactional
	public List<String> update(Shape s) throws Exception {

		Set<GosDefinition> gosDefinitions = dataMonitor.getAvailableGosFor(s.getLayerID().toString());
		gosDefinitions.addAll(dataMonitor.getNotAvailableGosFor(s.getLayerID().toString()));

		List<String> endpoints = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				boolean status = shapeManagement.updateShape(gosDefinition.getGosEndpoint(), s);
				if (status)
					return gosDefinition.getGosEndpoint();
				else
					return null;
			} catch (Exception e) {
				log.error("Could not update shape with id: " + s.getId().toString() + " on endpoint: " + gosDefinition.getGosEndpoint());
				return null;
			}
		}).filter(e -> e != null).collect(Collectors.toList());

		return endpoints;

	}

	@Transactional(rollbackFor = { Exception.class })
	public List<String> delete(List<String> shapeIDs) throws Exception {

		List<String> endpoints = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				boolean status = shapeManagement.deleteShapes(gosDefinition.getGosEndpoint(), shapeIDs);
				if (status)
					return gosDefinition.getGosEndpoint();
				else
					return null;

			} catch (Exception e) {
				log.error("Could not delete shapes with ids: " + shapeIDs.toString() + " on endpoint: " + gosDefinition.getGosEndpoint());
				return null;
			}
		}).filter(e -> e != null).collect(Collectors.toList());

		return endpoints;
	}

	@Transactional(readOnly = true)
	public List<Shape> findShapesEnclosingGeometry(Shape s) throws Exception {
		return dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				return shapeManagement.findWithin(gosDefinition.getGosEndpoint(), s);
			} catch (IOException e) {
				return new ArrayList<Shape>();
			}
		}).filter(list -> !list.isEmpty()).flatMap(l -> l.stream()).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Deprecated
	public List<Shape> findShapesEnclosingGeometry(Geometry geometry) throws Exception {
		return null;
	}

	private List<List<GeocodeSystem>> getAlternativeHierarchies(List<GeocodeSystem> mainHierarchy, List<List<GeocodeSystem>> currentAlts, int index,
			List<GeocodeSystem> allTaxonomies, Map<UUID, TaxonomyData> taxonomyData) throws Exception {
		List<List<GeocodeSystem>> currHierarchies = new ArrayList<List<GeocodeSystem>>(currentAlts);
		currHierarchies.add(mainHierarchy);

		List<List<GeocodeSystem>> altHierarchies = new ArrayList<List<GeocodeSystem>>();
		GeocodeSystem altTaxonomy = mainHierarchy.get(index);

		TaxonomyData altTaxonomyData = taxonomyData.get(altTaxonomy.getId());

		for (int i = 0; i < altTaxonomyData.getAlternatives().size(); i++) {
			for (List<GeocodeSystem> ah : altHierarchies) {

				GeocodeSystem child = null;
				int ind = i;
				List<GeocodeSystem> children = allTaxonomies.stream().filter(t -> taxonomyData.get(t.getId()).getParent().equals(altTaxonomyData.getAlternatives().get(ind)))
						.collect(Collectors.toList());
				if (!children.isEmpty()) {
					if (children.size() > 1)
						throw new Exception("Branched taxonomy hierarchies not supported");
					child = children.get(0);
				}

				if (child == null)
					break;
				ah.add(child);
			}
		}

		List<GeocodeSystem> rest = mainHierarchy.subList(index + 1, mainHierarchy.size());

		for (List<GeocodeSystem> hier : altHierarchies)
			hier.addAll(rest);

		return altHierarchies;
	}

	@Transactional(readOnly = true)
	public GeographyHierarchy getDefaultGeographyHierarchy() throws Exception {
		return getGeographyHierarchy(geocodeManager.findGeocodeSystemById(configurationManager.retrieveTaxonomyConfig(Type.GEOGRAPHYTAXONOMY).get(0).getId(), false));
	}

	@Transactional(readOnly = true)
	public GeographyHierarchy getGeographyHierarchy(GeocodeSystem geogTaxonomy) throws Exception {
		GeographyHierarchy hierarchy = new GeographyHierarchy();

		List<GeocodeSystem> allTaxonomies = geocodeManager.allGeocodeSystems(false);
		Map<UUID, TaxonomyData> taxonomyData = allTaxonomies.stream().filter(t -> t.getExtraData() != null)
				.collect(Collectors.toMap(GeocodeSystem::getId, t -> geocodeManager.unmarshalTaxonomyData(t.getExtraData())));

		GeocodeSystem reloadedGeoTax = allTaxonomies.stream().filter(t -> t.getId().equals(geogTaxonomy.getId())).findFirst().get();
		hierarchy.setMainHierarchy(constructMainHierarchy(reloadedGeoTax, allTaxonomies, taxonomyData));
		hierarchy.setAlternativeHierarchies(constructAlternativeHierarchies(hierarchy, allTaxonomies, taxonomyData));

		return hierarchy;

	}

	private List<List<GeocodeSystem>> constructAlternativeHierarchies(GeographyHierarchy hierarchy, List<GeocodeSystem> allTaxonomies, Map<UUID, TaxonomyData> taxonomyData)
			throws Exception {
		List<Integer> altIndexes = new ArrayList<Integer>();

		int i = 0;
		for (GeocodeSystem currTaxonomy : hierarchy.getMainHierarchy()) {
			if (!taxonomyData.get(currTaxonomy.getId()).getAlternatives().isEmpty())
				altIndexes.add(i);
			i++;
		}

		List<List<GeocodeSystem>> altHierarchies = new ArrayList<>();
		for (Integer index : altIndexes)
			altHierarchies.addAll(getAlternativeHierarchies(hierarchy.getMainHierarchy(), altHierarchies, index, allTaxonomies, taxonomyData));
		return altHierarchies;
	}

	/**
	 * 
	 * @param geogTaxonomy
	 *            a taxonomy within the hierarchy that is to be returned. Not necessarily the top taxonomy.
	 * @return
	 * @throws Exception
	 */
	private List<GeocodeSystem> constructMainHierarchy(GeocodeSystem geogTaxonomy, List<GeocodeSystem> allTaxonomies, Map<UUID, TaxonomyData> taxonomyData) throws Exception {
		if (geogTaxonomy == null)
			throw new IllegalArgumentException("Geography taxonomy cannot be null");

		LinkedList<GeocodeSystem> hier = new LinkedList<GeocodeSystem>();
		hier.add(geogTaxonomy);

		GeocodeSystem currTaxonomy = geogTaxonomy;
		while (currTaxonomy != null) {
			TaxonomyData taxData = taxonomyData.get(currTaxonomy.getId());
			if (taxData == null || taxData.getParent() == null)
				break;

			GeocodeSystem parent = geocodeManager.findGeocodeSystemById(taxData.getParent().toString(), false);
			if (taxData.getParent() != null)
				hier.push(parent);
			currTaxonomy = parent;
		}

		currTaxonomy = hier.peekLast();
		while (true) {
			final GeocodeSystem ct = currTaxonomy;
			GeocodeSystem child = null;
			List<GeocodeSystem> children = allTaxonomies.stream().filter(t -> {
				TaxonomyData td = taxonomyData.get(t.getId());
				return td != null && td.getParent() != null && td.getParent().equals(ct.getId());
			}).collect(Collectors.toList());
			if (!children.isEmpty()) {
				if (children.size() > 1)
					throw new Exception("Branched taxonomy hierarchies not supported");
				child = children.get(0);
			}

			if (child == null)
				break;

			hier.add(child);
			currTaxonomy = child;
		}
		return hier;
	}

	@Transactional(readOnly = true)
	public List<Geocode> geoLocate(double x, double y) throws Exception {
		List<Geocode> res = new ArrayList<Geocode>();

		GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);

		Point point = gf.createPoint(new Coordinate(x, y));
		Shape pointShape = new Shape();
		pointShape.setGeography(point);

		TaxonomyConfig tcfg = null;
		List<TaxonomyConfig> tcfgs = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY);
		if (tcfgs != null) {
			tcfg = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY).get(0);
		}

		List<Geocode> terms = geocodeManager.getTopmostTermsOfTaxonomy(tcfg.getId(), false);

		if (terms == null || terms.isEmpty())
			return res;

		while (true) {
			boolean located = false;
			for (Geocode geocode : terms) {
				List<Shape> termShapes = geocodeManager.getShapesOfTerm(geocode);
				if (termShapes == null || termShapes.isEmpty()) {
					log.error("Could not find shapes of taxonomy term " + geocode.getId());
					throw new Exception("Could not find shapes of taxonomy term " + geocode.getId());
				}
				for (Shape termShape : termShapes) {
					if (pointShape.getGeography().within(termShape.getGeography())) {
						if (geocode.getParent() != null)
							geocode.getParent().getName();
						if (geocode.getGeocodeClass() != null)
							geocode.getGeocodeClass().getName();
						geocode.getGeocodeSystem().getName();
						geocode.getCreator().getName();
						res.add(geocode);
						terms = geocodeManager.getChildrenOfGeocode(geocode.getId().toString(), true, false);
						located = true;
						break;
					}
				}
				if (located)
					break;
			}
			if (terms == null || terms.isEmpty() || located == false)
				break;
		}
		return res;
	}

	@Transactional(readOnly = true)
	public List<GeoLocation> termLocate(SearchType searchType, String term, Principal principal) throws Exception {
		List<GeoLocation> res = new ArrayList<GeoLocation>();

		List<Shape> shapes = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
			try {
				return shapeManagement.searchShapes(gosDefinition.getGosEndpoint(), Collections.singletonList(term));
			} catch (IOException e) {
				return new ArrayList<Shape>();
			}
		}).filter(list -> !list.isEmpty()).flatMap(l -> l.stream()).collect(Collectors.toList());

		Map<String, Project> projectShapeMappings = new HashMap<String, Project>();
		shapes = filterBySearchType(searchType, shapes, principal, projectShapeMappings);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.newDocumentBuilder();

		for (Shape s : shapes) {
			boolean nonGeographic = false;

			if (nonGeographic == false)
				continue;
			Point centroid = s.getGeography().getCentroid();
			List<Geocode> terms = geoLocate(centroid.getX(), centroid.getY());
			if (terms == null || terms.isEmpty())
				continue;

			List<GeoLocationTag> tags = new ArrayList<GeoLocationTag>();

			Geometry b = s.getGeography().getEnvelope();
			Bounds bounds = new Bounds(b.getCoordinates()[0].x, b.getCoordinates()[0].y, b.getCoordinates()[2].x, b.getCoordinates()[2].y, null);
			if (searchType == SearchType.MAP)
				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds));
			else if (searchType == SearchType.PROJECTS) {
				Project shapeProject = projectShapeMappings.get(s.getId().toString());
				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds, shapeProject.getName(), shapeProject.getId().toString()));
			}
		}
		return res;
	}

	/** TAXONOMIES WILL NOT BE ACCOMPANIED BY SHAPES ANYMORE, RIGHT? **/

	@Transactional
	@Deprecated
	public List<String> getBreadcrumbs(Coords coords) throws Exception {
		// public Map<UUID, List<Geocode>> getBreadcrumbs(Coords coords) throws Exception {
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		Point point = geometryFactory.createPoint(new Coordinate(coords.getLon(), coords.getLat()));
		List<Shape> shapes = findShapesEnclosingGeometry(point);

		List<Geocode> geocodes = geocodeDao.getGeocodesByShapes(shapes);
		geocodes.sort(Comparator.comparing(Geocode::getName));
		List<String> geocodesStr = geocodes.stream().map(g -> g.getName()).collect(Collectors.toList());

		return geocodesStr;

		// return termsById.values().stream().
		// filter(term -> ExceptionUtils.wrap(() -> getGeographyHierarchy(term.getGeocodeSystem())).get() != null).
		// map(term -> {
		// List<Geocode> breadcrumb = new ArrayList<>();
		// do {
		// breadcrumb.add(term);
		// term = term.getParent();
		// }while(term != null);
		// Collections.reverse(breadcrumb);
		// return breadcrumb;
		// }).
		// collect(Collectors.toMap(
		// breadcrumb -> ExceptionUtils.wrap(() ->
		// getGeographyHierarchy(breadcrumb.get(0).getGeocodeSystem())).get().getMainHierarchy().get(0).getId(),
		// breadcrumb -> breadcrumb));

	}

	private List<Shape> filterBySearchType(SearchType searchType, List<Shape> shapes, Principal principal, Map<String, Project> projectShapeMappings /* out */) throws Exception {
		List<Shape> filtered = new ArrayList<Shape>();
		List<Project> projects = projectDao.findByCreator(principal);
		List<Shape> projectShapes = new ArrayList<Shape>();
		Set<UUID> filteredIds = new HashSet<UUID>();
		for (Shape s : shapes) {
			for (Project p : projects) {
				if (s.getId().equals(p.getShape())) {
					if (p.getStatus() != ProjectStatus.DELETED) {
						projectShapeMappings.put(s.getId().toString(), p);
						projectShapes.add(s);
					}
				} else {
					if (!filteredIds.contains(s.getId())) {
						filtered.add(s);
						filteredIds.add(s.getId());
					}
				}
			}
		}
		if (searchType == SearchType.PROJECTS)
			return projectShapes;
		else
			return filtered;
	}

	private Map<String, Map<String, Attribute>> partitionAttributes(Map<String, String> attributes, GeographyHierarchy geographyHierarchy) throws Exception {
		Set<String> geographicNames = new HashSet<String>();
		List<List<GeocodeSystem>> hier = new ArrayList<List<GeocodeSystem>>(geographyHierarchy.getAlternativeHierarchies());
		hier.add(geographyHierarchy.getMainHierarchy());
		for (List<GeocodeSystem> h : hier) {
			for (GeocodeSystem t : h)
				geographicNames.add(t.getName());
		}

		hier.add(geographyHierarchy.getMainHierarchy());
		Map<String, String> toProcess = new HashMap<String, String>(attributes);
		Map<String, Map<String, Attribute>> partition = new HashMap<String, Map<String, Attribute>>();

		while (!toProcess.isEmpty()) {
			Set<String> toDelete = new HashSet<String>();
			for (Map.Entry<String, String> attr : toProcess.entrySet()) {
				if (geographicNames.contains(attr.getKey())) {
					toDelete.add(attr.getKey());
					continue;
				}

				GeocodeSystem t = geocodeManager.findGeocodeSystemByName(attr.getKey(), false);
				if (t == null) {
					toDelete.add(attr.getKey());
					continue;
				}

				TaxonomyConfig tcfg = configurationManager.retrieveTaxonomyConfigById(t.getId().toString());
				if (tcfg == null) {
					toDelete.add(attr.getKey());
					continue;
				}

				List<AttributeMappingConfig> mcfgs = configurationManager.getAttributeMappingsForTermId(t.getId().toString());
				String layer = null;
				for (AttributeMappingConfig mcfg : mcfgs) {
					if (mcfg.getAttributeValue() == null) {
						layer = mcfg.getLayerTermId();
						break;
					}
				}
				if (layer == null) {
					toDelete.add(attr.getKey());
					continue;
				}

				Map<String, Attribute> layerAttrs = new HashMap<String, Attribute>();
				mcfgs = configurationManager.getMappingConfigsForLayer(layer);
				for (AttributeMappingConfig mcfg : mcfgs) {
					if (mcfg.getTermId() == null)
						continue;

					if (mcfg.getAttributeValue() == null) {
						GeocodeSystem mT = geocodeManager.findGeocodeSystemById(mcfg.getTermId(), false);
						if (mT == null)
							continue;
						if (!mT.getName().equals(attr.getKey()))
							continue;
						toDelete.add(mT.getName());
						if (!geographicNames.contains(mT.getName())) {
							if (!layerAttrs.containsKey(mcfg.getAttributeName()))
								layerAttrs.put(mcfg.getAttributeName(), new Attribute(mcfg.getAttributeName(), mcfg.getAttributeType(), mcfg.getTermId(), attr.getValue()));
						}
					}
				}

				if (!layerAttrs.isEmpty()) {
					if (!partition.containsKey(layer))
						partition.put(layer, new HashMap<String, Attribute>());
					partition.get(layer).putAll(layerAttrs);
				}

			}
			for (String td : toDelete)
				toProcess.remove(td);
		}

		return partition;
	}

	@Transactional(readOnly = true)
	public List<GeoLocation> attributeLocate(SearchType searchType, Map<String, String> attributes, Principal principal) throws Exception {
		GeographyHierarchy hier = getDefaultGeographyHierarchy();
		String mostSpecificGeogTerm = null;
		Iterator<GeocodeSystem> hierIt = hier.getMainHierarchy().iterator();
		int i = 0;
		int tIndex = -1;
		while (hierIt.hasNext()) {
			GeocodeSystem t = hierIt.next();
			if (attributes.containsKey(t.getName())) {
				mostSpecificGeogTerm = attributes.get(t.getName());
				attributes.remove(t.getName());
				tIndex = i;
			}
			i++;
		}

		int maxIndex = tIndex;
		for (List<GeocodeSystem> alt : hier.getAlternativeHierarchies()) {
			Iterator<GeocodeSystem> altIt = alt.iterator();
			i = 0;
			int altIndex = 0;
			while (altIt.hasNext()) {
				GeocodeSystem t = altIt.next();
				if (attributes.containsKey(t.getName())) {
					altIndex = i;
					if (altIndex > maxIndex) {
						maxIndex = altIndex;
						mostSpecificGeogTerm = attributes.get(t.getName());
						altIndex = i;
					}
					attributes.remove(t.getName());
				}
				i++;
			}
		}

		Geocode tt = geocodeManager.findTermByName(mostSpecificGeogTerm, false);
		Shape shapeTerm = geocodeManager.getShapeOfTerm(tt);

		Map<String, Project> projectShapeMappings = new HashMap<String, Project>();
		List<Shape> foundShapes = new ArrayList<Shape>();
		Map<String, Map<String, Attribute>> partition = partitionAttributes(attributes, hier);
		/*
		 * if(searchType == SearchType.MAP && partition.isEmpty()) throw new Exception("No attributes were specified");
		 */

		if (!partition.isEmpty()) {
			Map.Entry<String, Map<String, Attribute>> first = partition.entrySet().iterator().next();
			foundShapes = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
				try {
					return shapeManagement.searchShapesWithinByAttributes(gosDefinition.getGosEndpoint(), first.getValue(), shapeTerm);
				} catch (IOException e) {
					return new ArrayList<Shape>();
				}
			}).filter(list -> !list.isEmpty()).flatMap(l -> l.stream()).collect(Collectors.toList());
			foundShapes = filterBySearchType(searchType, foundShapes, principal, projectShapeMappings);
			partition.remove(first.getKey());
		} else {
			foundShapes = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
				try {
					return shapeManagement.searchShapesWithinByAttributes(gosDefinition.getGosEndpoint(), new HashMap<String, Attribute>(), shapeTerm);
				} catch (IOException e) {
					return new ArrayList<Shape>();
				}
			}).filter(list -> !list.isEmpty()).flatMap(l -> l.stream()).collect(Collectors.toList());
			foundShapes = filterBySearchType(searchType, foundShapes, principal, projectShapeMappings);
		}

		for (Map.Entry<String, Map<String, Attribute>> layerAttrs : partition.entrySet()) {
			List<Shape> res = dataMonitor.getAllGosEndpoints().parallelStream().map(gosDefinition -> {
				try {
					return shapeManagement.searchShapesWithinByAttributes(gosDefinition.getGosEndpoint(), layerAttrs.getValue(), shapeTerm);
				} catch (IOException e) {
					return new ArrayList<Shape>();
				}
			}).filter(list -> !list.isEmpty()).flatMap(l -> l.stream()).collect(Collectors.toList());
			Map<String, Shape> toAdd = new HashMap<String, Shape>();
			for (Shape rs : res) {
				for (Shape fs : foundShapes) {
					if (fs.getGeography().within(rs.getGeography())) {
						if (!toAdd.containsKey(fs.getId().toString()))
							toAdd.put(fs.getId().toString(), fs);
					} else if (rs.getGeography().within(fs.getGeography())) {
						if (!toAdd.containsKey(rs.getId().toString()))
							toAdd.put(rs.getId().toString(), rs);
					}
				}
			}
			foundShapes = new ArrayList<Shape>(toAdd.values());
			foundShapes = filterBySearchType(searchType, foundShapes, principal, projectShapeMappings);
		}

		List<GeoLocation> res = new ArrayList<GeoLocation>();

		for (Shape s : foundShapes) {
			Point centroid = s.getGeography().getCentroid();
			List<Geocode> terms = geoLocate(centroid.getX(), centroid.getY());
			if (terms == null || terms.isEmpty())
				continue;

			List<GeoLocationTag> tags = new ArrayList<GeoLocationTag>();

			Geometry b = s.getGeography().getEnvelope();
			Bounds bounds = new Bounds(b.getCoordinates()[0].x, b.getCoordinates()[0].y, b.getCoordinates()[2].x, b.getCoordinates()[2].y, null);
			if (searchType == SearchType.MAP)
				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds));
			else if (searchType == SearchType.PROJECTS) {
				Project shapeProject = projectShapeMappings.get(s.getId().toString());
				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds, shapeProject.getName(), shapeProject.getId().toString()));
			}
		}
		return res;
	}

	public List<Shape> getShapesOfLayerID(UUID layerID) throws Exception {
		return getShapesOfLayer(layerID);
	}

	public List<ShapeMessenger> getShapeMessengerForLayer(UUID layerID) throws Exception {

		List<Shape> shapes = getShapesOfLayer(layerID);

		if (shapes == null)
			return null;

		// getTermDetails(tt);

		List<ShapeMessenger> res = new ArrayList<ShapeMessenger>();
		for (Shape s : shapes) {
			// getShapeDetails(s);
			ShapeMessenger sm = new ShapeMessenger();
			sm.setId(s.getId().toString());
			sm.setCode(s.getCode());
			sm.setExtraData(s.getExtraData());
			sm.setGeometry(s.getGeography().toText());
			// if(s.getShapeImport() != null)
			// sm.setImportId(s.getShapeImport().toString());
			sm.setName(s.getName());
			sm.setShapeClass(s.getShapeClass());
			Layer layer = layerDao.getLayerById(layerID);

			if (layer != null) {
				sm.setLayerId(layer.getId().toString());
				if (layer.getGeocodeSystem() != null)
					sm.setLayerGeocodeSystem(layer.getGeocodeSystem().getName());
			}
			res.add(sm);
		}
		return res;
	}

	public List<ShapeInfo> getShapeInfoForLayer(UUID layerID) throws Exception {
		List<Shape> shapes = getShapesOfLayer(layerID);

		if (shapes == null)
			return null;

		// getTermDetails(tt);

		List<ShapeInfo> res = new ArrayList<ShapeInfo>();
		for (Shape s : shapes) {
			// getShapeDetails(s);
			ShapeInfo si = new ShapeInfo();
			si.setShape(s);
			si.setLayerID(layerID);
			res.add(si);
		}
		return res;

	}

	@Deprecated
	public Map<String, Shape> getShapesOfLayer(Layer layer) throws Exception {
		List<Geocode> geocodes = geocodeDao.findByGeocodeSystem(layer.getGeocodeSystem());
		Map<String, Shape> shapes = new HashMap<>();
		// TODO: findShapeById() is very very slow. please find another way of fetching the shapes
		for (Geocode geocode : geocodes) {
			shapes.put(geocode.getName().toLowerCase(), findShapeById(geocode.getShapeID()));
		}
		return shapes;
	}

	@Transactional
	public WfsShapeInfo getShapesFromShapefile(String pathName, String termId, int srid, String charset, boolean forceLonLat, Map<String, Map<String, AttributeInfo>> attrInfo,
			Principal principal, boolean forceOverwriteMappings) throws Exception {

		if (srid < 0 && srid != -1)
			throw new IllegalArgumentException("Illegal srid code");
		if (principal == null)
			throw new IllegalArgumentException("Creator not provided");

		Map<String, String> map = new HashMap<String, String>();

		File file = new File(pathName);

		map.put("url", file.toURI().toString());
		map.put("charset", charset);

		SimpleFeatureSource featureSource = null;
		DataStore dataStore = null;
		try {
			dataStore = DataStoreFinder.getDataStore(map);
			featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
		} catch (IOException e) {
			log.error("Error while reading shape file", e);
			throw e;
		}

		return fromFeatureSource(dataStore, featureSource, termId, srid, forceLonLat, attrInfo, principal, forceOverwriteMappings);

	}

	@Transactional
	private WfsShapeInfo fromFeatureSource(DataStore dataStore, SimpleFeatureSource featureSource, String termId, int srid, boolean forceLonLat,
			Map<String, Map<String, AttributeInfo>> attrInfo, Principal principal, boolean forceOverwriteMappings) throws Exception {

		Map<String, GeocodeSystem> taxonomyCache = new HashMap<String, GeocodeSystem>();
		SimpleFeatureCollection collection = featureSource.getFeatures();
		SimpleFeatureType schema = featureSource.getSchema();
		SimpleFeatureIterator iterator = collection.features();
		WfsShapeInfo wfsShapeInfo = new WfsShapeInfo();

		// EPSG:GGRS87 / Greek Grid - instead of 2100
		// GCS_WGS_1984 / EPSG:2100

		String sourceCode = null, targetCode;

		if (srid != -1)
			sourceCode = "EPSG:" + new Integer(srid).toString();
		targetCode = "EPSG:4326";

		CoordinateReferenceSystem sourceCRS = null;
		if (schema.getCoordinateReferenceSystem() != null)
			sourceCRS = schema.getCoordinateReferenceSystem();
		else if (sourceCode != null)
			sourceCRS = CRS.decode(sourceCode);

		System.out.println("soureCode:"+sourceCRS.toString());

		if (sourceCRS == null)
			throw new Exception("No coordinate system provided nor found in shape file definition");

		CoordinateReferenceSystem targetCRS = CRS.decode(targetCode, forceLonLat);

		Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
		CRSAuthorityFactory factory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
//		CoordinateReferenceSystem
		sourceCRS = factory.createCoordinateReferenceSystem(sourceCode);

		UUID importUUID = UUIDGenerator.randomUUID();

		boolean lenient = false;
		String wkt = sourceCRS.toWKT();
		if (!wkt.toLowerCase().contains("towgs")) {
			if (CRS.lookupEpsgCode(sourceCRS, true) == 2100) // Greek Grid)
			{
				double[] bursaWolf = { -199.87, 74.79, 246.62, 0, 0, 0, 0 };
				log.warn("No transformation parameters were found within source CRS data." + "Automatically applying: " + Arrays.toString(bursaWolf));
				wkt = insertBursaWolfToWKT(wkt, bursaWolf);
				sourceCRS = CRS.parseWKT(wkt);
			} else {
				log.warn("No transformation parameters were found within source CRS data. Transformation may contain errors");
				lenient = true;
			}
		}

		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, lenient);

		Envelope b = featureSource.getBounds();
		b = JTS.transform(b, transform);
		Bounds bounds = new Bounds();
		bounds.setCrs("EPSG:4326");
		bounds.setMinx(b.getMinX());
		bounds.setMiny(b.getMinY());
		bounds.setMaxx(b.getMaxX());
		bounds.setMaxy(b.getMaxY());
		wfsShapeInfo.setBounds(bounds);

		Geometry g = null;

		try {
			// speed up updates by executing them only when needed.
			// assumes that mappings are not different among features (features can
			// contain different subset of attributes, but when a mapping is present, it
			// is assumed to be the same as the corresponding mapping of all other features)
			// this assumption is valid and does not pose any limitations to the mapping
			// configuration of the system, which only supports dataset-wide mappings as well
			Map<String, Map<String, AttributeMappingConfig>> cfgCache = new HashMap<String, Map<String, AttributeMappingConfig>>();
			
			String tempDir = System.getProperty("java.io.tmpdir");
			if(tempDir==null || tempDir.isEmpty())
				tempDir = "/tmp";
			
			List<Object> listShape = DBMaker.fileDB(tempDir+"/"+termId)
											.closeOnJvmShutdown()
											.fileMmapEnableIfSupported()
											.fileDeleteAfterClose()
											.make()
											.indexTreeList(termId).createOrOpen();
			listShape.clear();
			
			int cnt = 0;
			while (iterator.hasNext()) {
				// read a shape file feature
				SimpleFeature feature = iterator.next();

				// get its geometry
				g = (Geometry) feature.getDefaultGeometry();

				g = JTS.transform(g, transform);
				g.setSRID(4326);

				String data = createDataXML(feature, attrInfo, taxonomyCache, termId, cfgCache, forceOverwriteMappings);

				Shape shape = new Shape();

				shape.setCreationDate(Calendar.getInstance().getTime());
				shape.setCreatorID(principal.getId());

				shape.setExtraData(data);
				shape.setId(UUIDGenerator.randomUUID());
				shape.setLastUpdate(Calendar.getInstance().getTime());
				shape.setLayerID(UUID.fromString(termId));
				shape.setGeography(g);
				shape.setShapeClass(1);
				shape.setName(importUUID + "_" + (cnt++));
				
				listShape.add(shape);

			}
			
			wfsShapeInfo.setListShape((List<Shape>)(Object)listShape); //because we know that list only contains Shape objects
		} finally {
			iterator.close();
		}

		return wfsShapeInfo;
	}

	private String createDataXML(SimpleFeature feature, Map<String, Map<String, AttributeInfo>> attrInfo, Map<String, GeocodeSystem> taxonomyCache, String layerTermId,
			Map<String, Map<String, AttributeMappingConfig>> cfgCache, boolean forceOverwriteMappings) throws Exception {
		StringBuilder xml = new StringBuilder();
		xml.append("<extraData>");
		List<AttributeType> types = feature.getType().getTypes();

		for (AttributeType t : types) {
			Object val = feature.getAttribute(t.getName());
			if (val != null) {
				String type = null;
				if (attrInfo == null)
					type = "double";
				else
					type = attrInfo.get(t.getName().toString()).get("").getType();
				boolean setTaxonomy = false;
				boolean setValue = false;
				String taxonomyId = null;
				String layerId = null;
				String attrValue = null;
				Boolean presentable = true;
				Boolean mapValue = true;
				if (attrInfo != null) {

					if (!val.equals("") && attrInfo.get(t.getName().toString()).get(val.toString()) != null) {
						setValue = true;
					} else {
						if (attrInfo.get(t.getName().toString()).get("").isStore() == false)
							continue; // ignore attribute that is marked as non-storeable
						presentable = attrInfo.get(t.getName().toString()).get("").isPresentable();
					}

				}

				AttributeMappingConfig mcfg = new AttributeMappingConfig();
				mcfg.setAttributeName(t.getName().toString());
				mcfg.setAttributeType(type);
				mcfg.setLayerTermId(layerTermId);
				mcfg.setPresentable(presentable);

				if (setValue) {
					mcfg.setAttributeValue(attrValue);
					mcfg.setMapValue(mapValue);
					mcfg.setTermId(layerId);
				}

				addMappingConfig(mcfg, cfgCache);

				String processedVal = HtmlUtils.htmlEscape(discardIllegalValues(type, feature.getAttribute(t.getName()).toString().trim()));

				xml.append("<" + t.getName() + " type=\"" + type + "\" " + (setTaxonomy ? "geocodeSystem=\"" + taxonomyId + "\" " : "")
						+ (setValue ? ("layer=\"" + layerId + "\"") : "") + ">");
				xml.append(processedVal);
				xml.append("</" + t.getName() + ">");
			}
		}
		xml.append("</extraData>");

		return xml.toString();
	}

	private String insertBursaWolfToWKT(String wkt, double[] bursaWolf) {
		String[] defs = wkt.split("DATUM\\[");
		if (defs.length != 2) {
			log.warn("Could not insert Bursa-Wolf Parameters to CRS WKT");
			return wkt;
		}
		int bracketCount = 1;
		int index = 0;
		int prevClose = -1;
		while (bracketCount != 0) {
			int close = defs[1].indexOf(']', index);
			if (prevClose == -1)
				prevClose = close;
			int open = defs[1].indexOf('[', index);
			if (close == -1) {
				log.warn("Invalid wkt");
				return null;
			}
			if (open < close) {
				bracketCount++;
				index = open + 1;
			} else {
				bracketCount--;
				index = close + 1;
			}
			if (bracketCount != 0)
				prevClose = close;
		}

		StringBuilder formattedBursaWolf = new StringBuilder();
		for (int i = 0; i < bursaWolf.length; i++) {
			formattedBursaWolf.append(String.format(Locale.US, "%.2f", bursaWolf[i]));
			if (i != bursaWolf.length - 1)
				formattedBursaWolf.append(", ");
		}

		String res = defs[0] + "DATUM[" + defs[1].substring(0, prevClose + 1) + ", TOWGS84[" + formattedBursaWolf.toString() + "]" + defs[1].substring(prevClose + 1);
		return res;
	}

	private String discardIllegalValues(String type, String value) {
		try {
			if (type.equals("short"))
				Short.parseShort(value);
			else if (type.equals("integer"))
				Integer.parseInt(value);
			else if (type.equals("long"))
				Long.parseLong(value);
			else if (type.equals("float"))
				Float.parseFloat(value);
			else if (type.equals("double"))
				Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return "";
		}
		return value;
	}
}
/*
 * 
 * what other functions are needed:
 * 
 * shapeMan.findShapeById() shapeManager.getDefaultGeographyHierarchy() shapeManager.getBoundingBoxByProjectName( shapeManager.geoLocate(x, y)
 * shapeManager.termLocate( shapeManager.attributeLocate( shapeManager.retrieveShapeAttributeByTaxonomy( shapeManager.getDefaultGeographyHierarchy()
 * shapeManager.getGeographyHierarchy( shapeManager.getShapeBounds shapeManager.getShape( shapeManager.getShapeMessengerForLayer(
 * shapeManager.getShapeInfoForLayer( shapeManager.findShapesOfImport( shapeManager.findShapeByIdInfo( shapeManager.findShapeWithinBounds(
 * shapeManager.update shapeManager.delete shapeManager.getBreadcrumbs(coords) shapeManager.getShapesFromShapefile( shapeManager.createShapesOfLayer(
 * shapeManager.getShapesOfLayer( shapeManager.generateShapesOfImport shapeManager.generateShapeBoundary shapeManager.deleteShapesOfLayer
 * shapeManager.getGeometry( shapeManager.retrieveShapeAttributes shapeManager.consolidateAttributes shapeManager.computeAttributes
 * shapeManager.setShapeAttributes shapeManager.createFromGeometry shapeManager.updateGeometry shapeManager.getGeometry
 * 
 * 
 * ----- geoserver stuff --- geoServerBridge.getFeatureType( geoServerBridge.setDefaultLayerStyle geoServerBridge.getGeoserverLayer
 * geoServerBridge.addGeoserverLayer geoServerBridge.addLayerStyle geoServerBridge.removeLayerStyle geoServerBridge.setDefaultLayerStyle
 * geoServerBridge.addStyle geoServerBridge.removeStyle geoServerBridge.deleteLayer
 */