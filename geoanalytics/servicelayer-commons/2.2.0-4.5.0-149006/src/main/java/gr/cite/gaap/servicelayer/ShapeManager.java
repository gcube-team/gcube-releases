//package gr.cite.gaap.servicelayer;
//
//import gr.cite.commons.util.datarepository.DataRepository;
//import gr.cite.commons.util.datarepository.elements.RepositoryFile;
//import gr.cite.gaap.datatransferobjects.AttributeInfo;
//import gr.cite.gaap.datatransferobjects.Coords;
//import gr.cite.gaap.datatransferobjects.GeoLocation;
//import gr.cite.gaap.datatransferobjects.GeoLocationTag;
//import gr.cite.gaap.datatransferobjects.NewProjectData;
//import gr.cite.gaap.datatransferobjects.ShapeMessenger;
//import gr.cite.gaap.datatransferobjects.WfsShapeInfo;
//import gr.cite.gaap.datatransferobjects.GeoSearchSelection.SearchType;
//import gr.cite.gaap.geospatialbackend.GeospatialBackend;
//import gr.cite.gaap.utilities.HtmlUtils;
//import gr.cite.gaap.utilities.StringUtils;
//import gr.cite.geoanalytics.common.ShapeAttributeDataType;
//import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
//import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
//import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
//import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeDao;
//import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
//import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerDao;
//import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
//import gr.cite.geoanalytics.dataaccess.entities.project.Project;
//import gr.cite.geoanalytics.dataaccess.entities.project.Project.ProjectStatus;
//import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDao;
//import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
//import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeDocument;
//import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;
//import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
//import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDocumentDao;
//import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
//import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig.Type;
//import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.definition.TaxonomyData;
//import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Set;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import javax.inject.Inject;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//
//import org.geotools.data.DataStore;
//import org.geotools.data.DataStoreFinder;
//import org.geotools.data.simple.SimpleFeatureCollection;
//import org.geotools.data.simple.SimpleFeatureIterator;
//import org.geotools.data.simple.SimpleFeatureSource;
//import org.geotools.geometry.jts.JTS;
//import org.geotools.referencing.CRS;
//import org.opengis.feature.simple.SimpleFeature;
//import org.opengis.feature.simple.SimpleFeatureType;
//import org.opengis.feature.type.AttributeType;
//import org.opengis.referencing.crs.CoordinateReferenceSystem;
//import org.opengis.referencing.operation.MathTransform;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//
//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.geom.Envelope;
//import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.GeometryFactory;
//import com.vividsolutions.jts.geom.Point;
//import com.vividsolutions.jts.geom.PrecisionModel;
//import com.vividsolutions.jts.io.WKTReader;
//import com.vividsolutions.jts.io.WKTWriter;
//
//@Service
//@Primary
//@Deprecated
//public class ShapeManager implements GeospatialBackend {
//	private static final Logger log = LoggerFactory.getLogger(ShapeManager.class);
//	
//	protected GeocodeManager taxonomyManager;
//	private DocumentManager documentManager;
//	private DataRepository repository;
//	protected ConfigurationManager configurationManager;
//	private ShapeImportManager shapeImportManager;
//	
//	private ShapeDao shapeDao;
////	private ShapeLayerDao shapeLayerDao;
//	private ShapeDocumentDao shapeDocumentDao;
//	private GeocodeDao geocodeDao;
//	private LayerDao layerDao;
////	private GeocodeShapeDao geocodeShapeDao;
//	private ProjectDao projectDao;
//	private PrincipalDao principalDao;
//	
//	private static final String NoMappingKey = "\t\t\t__NoVal__\t\t\t";
//	private static final String NoValueKey = "";
//	
//	private class TermLinkInfo {
//		public String verb = null;
//		public Map<Geocode, Geocode> links = null;
//		
//		public TermLinkInfo(String verb)
//		{
//			this.links = new HashMap<Geocode, Geocode>();
//			this.verb = verb;
//		}
//	}
//	
////	public ShapeManager() { }
//	
///*	private void getShapeDetails(Shape s) {
//		if(s.getShapeImport() != null) s.getShapeImport().getShapeImport();
//		s.getCreator().getPrincipalData().getFullName();
//	}
//	
//	private void getTermDetails(TaxonomyTerm tt)
//	{
//		if(tt.getParent() != null) tt.getParent().getTaxonomy();
//		if(tt.getTaxonomyTermClass() != null) tt.getTaxonomyTermClass().getTaxonomy();
//		tt.getTaxonomy().getName();
//	}*/
//	
//	@Inject
//	public ShapeManager(PrincipalDao principalDao, GeocodeManager taxonomyManager, 
//			DocumentManager documentManager, DataRepository repository,
//			ConfigurationManager configurationManager) {
//		this.principalDao = principalDao;
//		this.taxonomyManager = taxonomyManager;
//		this.documentManager = documentManager;
//		this.repository = repository;
//		this.configurationManager = configurationManager;
//	}
//	
//	@Inject
//	public void setShapeImportManager(ShapeImportManager shapeImportManager) {
//		this.shapeImportManager = shapeImportManager;
//	}
//	
//	
//	@Inject
//	public void setShapeDao(ShapeDao shapeDao) {
//		this.shapeDao = shapeDao;
//	}
//	
////	@Inject
////	public void setShapeTermDao(ShapeLayerDao shapeLayerDao) {
////		this.shapeLayerDao = shapeLayerDao;
////	}
//	
//	@Inject
//	public void setShapeDocumentDao(ShapeDocumentDao shapeDocumentDao) {
//		this.shapeDocumentDao = shapeDocumentDao;
//	}
//	
//	@Inject
//	public void setGeocodeDao(GeocodeDao geocodeDao) {
//		this.geocodeDao = geocodeDao;
//	}
//	
//	@Inject
//	public void setLayerDao(LayerDao layerDao) {
//		this.layerDao = layerDao;
//	}
//		
//	
////	@Inject
////	public void setGeocodeShapeDao(GeocodeShapeDao geocodeShapeDao) {
////		this.geocodeShapeDao = geocodeShapeDao;
////	}
//	
//	@Inject
//	public void setProjectDao(ProjectDao projectDao) {
//		this.projectDao = projectDao;
//	}
//	
//	@Transactional(readOnly = true)
//	public Shape findShapeById(UUID id) throws IOException {
//		return shapeDao.read(id);
//	}
//	
//	/** Retrieves the layerID of this shape 
//	 * @see gr.cite.gaap.geospatialbackend.GeospatialBackend#findShapeByIdInfo(java.util.UUID)
//	 */
//	@Transactional(readOnly = true)
//	public ShapeInfo findShapeByIdInfo(UUID shapeID) throws Exception {
//		Shape s = shapeDao.read(shapeID);
//		ShapeInfo si = new ShapeInfo();
//		si.setShape(s);
//		UUID layerID = shapeDao.findLayerIDOfShape(s);
//		si.setLayerID(layerID);
//		return si;
//	}
//	
//	@Override
//	@Transactional
//	public void createShapesOfLayer(Collection<Shape> shapes) {		
//		for (Shape shape : shapes) {
//			this.shapeDao.create(shape);
//		}		
//	}
//	
//	public String retrieveShapeAttributeValue(Shape s, String attribute) throws Exception {
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
//		
//		NodeList els = d.getElementsByTagName(attribute);
//		if(els == null || els.getLength() == 0)
//			return null;
//		
//		Element el = (Element)els.item(0);
//		return el.getFirstChild().getNodeValue();
//	}
//	
//	public AttributeInfo retrieveShapeAttribute(Shape s, String attribute) throws Exception
//	{
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
//		
//		NodeList els = d.getElementsByTagName(attribute);
//		if(els == null || els.getLength() == 0)
//			return null;
//		
//		Element el = (Element)els.item(0);
//		AttributeInfo ai = new AttributeInfo();
//		ai.setName(attribute);
//		ai.setValue(el.getFirstChild().getNodeValue());
//		ai.setTaxonomy(el.getAttribute("taxonomy"));
//		ai.setType(ShapeAttributeDataType.valueOf(el.getAttribute("type").toUpperCase()).toString());
//		ai.setPresentable(Boolean.valueOf(el.getAttribute("presentable")));
//		return ai;
//	}
//	
//	@Transactional(readOnly = true)
//	public AttributeInfo retrieveShapeAttributeByTaxonomy(Shape s, String taxonomy) throws Exception
//	{
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
//		
//		NodeList els = d.getElementsByTagName("extraData").item(0).getChildNodes();
//		for(int i=0; i<els.getLength(); i++)
//		{
//			Element el = (Element)els.item(i);
//			if(el.getAttribute("taxonomy").equals(taxonomy))
//			{
//				AttributeInfo ai = new AttributeInfo();
//				ai.setName(el.getNodeName());
//				ai.setValue(el.getFirstChild().getNodeValue());
//				ai.setTaxonomy(taxonomy);
//				ai.setType(ShapeAttributeDataType.valueOf(el.getAttribute("type").toUpperCase()).toString());
//				ai.setPresentable(Boolean.valueOf(el.getAttribute("presentable")));
//				return ai;
//			}
//		}
//		return null;
//	}
//	
//	@Transactional(readOnly = true)
//	private Map<String, AttributeInfo> retrieveRawShapeAttributes(Shape s) throws Exception
//	{
//		Map<String, AttributeInfo> attributes = new HashMap<String, AttributeInfo>();
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
//		
//		NodeList els = d.getElementsByTagName("extraData").item(0).getChildNodes();
//		for(int i=0; i<els.getLength(); i++)
//		{
//			Element el = (Element)els.item(i);
//			
//			String taxon = el.getAttribute("taxonomy");
//			if(taxon == null || taxon.trim().isEmpty())
//				continue;
//			AttributeInfo ai = new AttributeInfo();
//			ai.setName(el.getNodeName());
//			if(el.getFirstChild() != null) ai.setValue(el.getFirstChild().getNodeValue());
//			ai.setTaxonomy(taxon);
//			ai.setType(ShapeAttributeDataType.valueOf(el.getAttribute("type").toUpperCase()).toString());
//			ai.setPresentable(Boolean.valueOf(el.getAttribute("presentable")));
//			attributes.put(taxon, ai);
//		}
//		return attributes;
//	}
//	
//	@Transactional
//	public void addShapeAttribute(Shape s, String attrName, String attrValue, GeocodeSystem taxonomy) throws Exception
//	{
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
//		
//		Element root = d.getDocumentElement();
//		NodeList els = d.getElementsByTagName(attrName);
//		if(els != null && els.getLength() != 0)
//			throw new Exception("Attribute " + attrName + " already exists");
//		
//		Element el = d.createElement(attrName);
//		el.setAttribute("type", ShapeAttributeDataType.STRING.toString());
//		
//		if (taxonomy != null){
//			el.setAttribute("taxonomy", taxonomy.getId().toString());
//		}
//		el.appendChild(d.createTextNode(attrValue));
//		root.appendChild(el);
//		
//		s.setExtraData(transformDocToString(d));
//		
//		shapeDao.update(s);
//	}
//	
//	private String transformDocToString(org.w3c.dom.Document document) throws TransformerException{
//		TransformerFactory tf = TransformerFactory.newInstance();
//		Transformer transformer = tf.newTransformer();
//		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//		StringWriter writer = new StringWriter();
//		transformer.transform(new DOMSource(document), new StreamResult(writer));
//		return writer.getBuffer().toString().replaceAll("\n|\r", "");
//	}
//	
//	@Transactional
//	public void setShapeAttributes(Shape s, Map<String, AttributeInfo> attrs) throws Exception
//	{
//		StringBuilder xml = new StringBuilder();
//		xml.append("<extraData>");
//		for(Map.Entry<String, AttributeInfo> attrE : attrs.entrySet())
//		{
//			AttributeInfo attr = attrE.getValue();
//			xml.append("<"+attr.getName() + " type=\"" + ShapeAttributeDataType.valueOf(attr.getType().toUpperCase()).toString() + "\" " + 
//					"taxonomy=\""+attr.getTaxonomy()+"\" " + (attr.getTerm() != null ? "term=\""+attr.getTerm()+"\"" : "") +">"); 
//			xml.append(attr.getValue());
//			xml.append("</"+attr.getName()+">");
//		}
//		xml.append("</extraData>");
//		
//		s.setExtraData(xml.toString());
//		
//		shapeDao.update(s);
//	}
//	
//	@Transactional
//	public void updateShapeAttribute(Shape s, String attrName, String attrValue) throws Exception
//	{
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
//		
//		NodeList els = d.getElementsByTagName(attrName);
//		if(els == null || els.getLength() == 0)
//			throw new Exception("Attribute " + attrName + " not found");
//		
//		Element el = (Element)els.item(0);
//		el.getFirstChild().setNodeValue(attrValue);
//		
//		shapeDao.update(s);
//	}
//	
//	@Transactional
//	public void removeShapeAttribute(Shape s, String attrName) throws Exception
//	{
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
//		
//		NodeList els = d.getElementsByTagName(attrName);
//		if(els == null || els.getLength() == 0)
//			throw new Exception("Attribute " + attrName + " not found");
//		
//		Element el = (Element)els.item(0);
//		d.removeChild(el);
//		
//		shapeDao.update(s);
//	}
//	
//	@Override
//	@Transactional
//	public Set<String> getAttributeValuesOfShapesByLayer(UUID layerID, Attribute attr) throws Exception {
//		return shapeDao.getAttributeValuesOfShapesByLayer(layerID, attr);
//	}
//	
//	private Map<String, AttributeInfo> filterAttributes(Map<String, AttributeInfo> attributes, List<String> taxonomies)
//	{
//		Map<String, AttributeInfo> filteredAttrs = new HashMap<String, AttributeInfo>();
//		for(Map.Entry<String, AttributeInfo> aie : attributes.entrySet())
//		{
//			for(String t : taxonomies)
//			{
//				if(aie.getValue().getTaxonomy().equals(t))
//				{
//					filteredAttrs.put(aie.getKey(), aie.getValue());
//					break;
//				}
//			}
//		}
//		return filteredAttrs;
//	}
//	
////	private void linkAttributeDocument(Shape s, GeocodeShape tts) throws Exception
////	{
////		gr.cite.geoanalytics.dataaccess.entities.document.Document d = shapeDocumentDao.findUniqueByGeocodeShape(tts);
////		if(d != null)
////		{
////			GeocodeShape thisTts = geocodeShapeDao.find(tts.getGeocode(), s);
////			if(thisTts == null)
////			{
////				thisTts = new GeocodeShape();
////				thisTts.setCreator(principalDao.systemPrincipal());
////				thisTts.setShape(s);
////				thisTts.setGeocode(tts.getGeocode());
////				geocodeShapeDao.create(thisTts);
////			}
////			
////			gr.cite.geoanalytics.dataaccess.entities.document.Document thisD = 
////					shapeDocumentDao.findUniqueByGeocodeShape(thisTts);
////			if(thisD == null)
////			{
////				ShapeDocument sd = new ShapeDocument();
////				sd.setCreator(principalDao.systemPrincipal());
////				sd.setGeocodeShape(thisTts);
////				sd.setDocument(d);
////				shapeDocumentDao.create(sd);
////			}
////		}
////	}
//	
//	@Transactional(readOnly = true)
//	public Map<String, AttributeInfo> consolidateAttributes(Shape s) throws Exception
//	{
//		Map<String, AttributeInfo> attrs = new HashMap<String, AttributeInfo>();
//		
//		Set<TaxonomyConfig> extraTaxonIds = new HashSet<TaxonomyConfig>();
//		List<TaxonomyConfig> infoCategories = configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.PROJECTINFOCATEGORYTAXONOMY);
//		for(TaxonomyConfig infoCfg : infoCategories)
//			extraTaxonIds.addAll(configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.valueOf(infoCfg.getType())));
//		
//		Point centroid = s.getGeography().getCentroid();
//		List<Geocode> geoLocation = geoLocate(centroid.getX(), centroid.getY());
//		
//		String geographyTaxonId = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY).get(0).getId();
//		
//		Set<Geocode> attrTerms = new HashSet<Geocode>();
//		
//		Geocode tt = null;
//		Iterator<Geocode> ttIt = geoLocation.iterator();
//		List<String> attrTaxonomies = new ArrayList<String>();
//		for(TaxonomyConfig tcfg : extraTaxonIds)
//			attrTaxonomies.add(tcfg.getId());
//		
//		//geographic info
//		boolean foundWidestGeographyTerm = false;
//		while(ttIt.hasNext()) //geoLocate returns terms by geographic order, e.g. country->prefecture->...
//		{
//			tt = ttIt.next();
//			if(tt.getGeocodeSystem().getId().toString().equals(geographyTaxonId))
//				foundWidestGeographyTerm = true; //found the term representing the widest area in which there exist useful attributes
//		
///*
//REMOVED TEMPORARILY
//			List<TaxonomyTerm> linkedTerms = taxonomyTermDao.getActiveLinkedTerms(tt, TaxonomyTermLink.Verb.AttrFor);
//			attrTerms.addAll(linkedTerms);
//			List<TaxonomyTerm> toCheck = new ArrayList<TaxonomyTerm>(linkedTerms);
//			while(true)
//			{
//				List<TaxonomyTerm> descendants = new ArrayList<TaxonomyTerm>();
//				for(TaxonomyTerm tc : toCheck)
//					descendants.addAll(taxonomyManager.getChildrenOfTerm(tc.getId().toString(), true, false));
//				attrTerms.addAll(descendants);
//				toCheck = descendants;
//				if(descendants.isEmpty())
//					break;
//			}
//*/			
//		/*	for(TaxonomyTerm lt : linkedTerms)
//				attrTaxonomies.add(lt.getTaxonomy().getName());*/
//			if(foundWidestGeographyTerm == true)
//				attrTaxonomies.add(tt.getGeocodeSystem().getId().toString()); //geographic info
//		}
//		
////		ttIt = geoLocation.iterator();
////		foundWidestGeographyTerm = false;
////		while(ttIt.hasNext())
////		{
////			tt = ttIt.next();
////			if(tt.getGeocodeSystem().getId().toString().equals(geographyTaxonId))
////				foundWidestGeographyTerm = true;
////			if(foundWidestGeographyTerm == false)
////				continue;
////			GeocodeShape tts = geocodeShapeDao.findUniqueByGeocode(tt);
////			if(tts != null)
////			{
////				Map<String, AttributeInfo> shapeAttrs = retrieveRawShapeAttributes(tts.getShape());
////				Map<String, AttributeInfo> filteredAttrs = filterAttributes(shapeAttrs, attrTaxonomies);
////				attrs.putAll(filteredAttrs);
////				for(AttributeInfo fa : filteredAttrs.values())
////				{
////					String termIdStr = null;
////					if(fa.getTerm() != null)
////						termIdStr = fa.getTerm();
////					else
////					{
////						List<AttributeMappingConfig> valCfgs = configurationManager.getAttributeMappings(fa.getName(), fa.getValue());
////						if(valCfgs != null)
////						{
////							for(AttributeMappingConfig valCfg : valCfgs)
////							{
////								if(valCfg.getTermId() != null)
////								{
////									termIdStr = valCfg.getTermId();
////									break;
////								}
////							}
////						}
////					}
////					if(termIdStr == null) continue;
////					Geocode attrT = taxonomyManager.findTermById(termIdStr, false);
////					List<GeocodeShape> attrTtss = geocodeShapeDao.findNonProjectByGeocode(attrT);
////					GeocodeShape attrTts = null;
////					if(attrT != null)
////					{
////						for(GeocodeShape aTts : attrTtss)
////						{
////							if(shapeDao.within(s, aTts.getShape()))
////							{
////								attrTts = aTts;
////								break;		
////							}
////						}
////						/*try
////						{
////							attrTts = taxonomyTermShapeDao.findUByTerm(attrT);
////						}catch(NonUniqueResultException e)
////						{
////							log.error("Non unique tts");
////							throw e;
////						}*/
////					}
////					if(attrTts != null)
////						linkAttributeDocument(s, attrTts);
////				}
////			}
////		}
////		for(Geocode t : attrTerms)
////		{
////			List<GeocodeShape> ttss = geocodeShapeDao.findByGeocode(t);
////			GeocodeShape tts = null;
////			for(GeocodeShape ttShape : ttss)
////			{
////				if(shapeDao.within(s, ttShape.getShape()))
////				{
////					tts = ttShape;
////					break;
////				}
////			}
////			if(tts != null)
////			{
////				AttributeInfo attr = retrieveShapeAttributeByTaxonomy(tts.getShape(), t.getGeocodeSystem().getId().toString());
////				attrs.put(attr.getTaxonomy(), attr);
////				
////				linkAttributeDocument(s, tts);
////			}
////		}
//		
//		return attrs;
//		
//	}
//	
//	@Transactional(readOnly = true)
//	public Map<String, AttributeInfo> computeAttributes(Shape s) throws Exception
//	{
//		Map<String, AttributeInfo> attrs = new HashMap<String, AttributeInfo>();
//		
//		Point centroid = s.getGeography().getCentroid();
//		
//		AttributeInfo ai = new AttributeInfo();
//		ai.setName("location");
//		ai.setPresentable(true);
//		ai.setTaxonomy(configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.LOCATIONTAXONOMY).get(0).getId());
//		ai.setType(ShapeAttributeDataType.STRING.toString());
//		ai.setValue(centroid.getX() + "," + centroid.getY());
//		attrs.put(TaxonomyConfig.Type.LOCATIONTAXONOMY.toString(), ai);
//		
//		//TODO hibernate spatial area
//		double area = shapeDao.area(s);
//		ai = new AttributeInfo();
//		ai.setName("area");
//		ai.setPresentable(true);
//		ai.setTaxonomy(configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.AREATAXONOMY).get(0).getId());
//		ai.setType(ShapeAttributeDataType.DOUBLE.toString());
//		ai.setValue(new Double(area).toString());
//		attrs.put(TaxonomyConfig.Type.AREATAXONOMY.toString(), ai);
//		
//		return attrs;
//	}
//	
//	@Transactional(readOnly = true)
//	public Map<String, AttributeInfo> retrieveShapeAttributes(Shape s) throws Exception
//	{
//		Map<String, AttributeInfo> res = new HashMap<String, AttributeInfo>();
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		if(s.getExtraData() == null || s.getExtraData().trim().isEmpty())
//			return res;
//		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
//		
//		GeographyHierarchy hier = getDefaultGeographyHierarchy();
//		
//		NodeList els = d.getChildNodes().item(0).getChildNodes();
//		for(int i=0; i<els.getLength(); i++)
//		{
//			String geogTaxonomyName = null;
//			String geogTaxonomyId = null;
//			Element el = (Element)els.item(i);
//			String taxonomyId = el.getAttribute("taxonomy");
//			if(taxonomyId == null || taxonomyId.trim().isEmpty())
//				continue;
//			TaxonomyConfig tcfg = configurationManager.retrieveTaxonomyConfigById(taxonomyId, true);
//			if(tcfg != null && (tcfg.getType().equals(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY.toString()) ||
//					tcfg.getType().equals(TaxonomyConfig.Type.ALTGEOGRAPHYTAXONOMY.toString())))
//				tcfg = null; //trigger geographic mode for those taxonomies that mappings are present
//			if(tcfg == null)
//			{
//				boolean foundGeog = false;
//				for(GeocodeSystem geogT : hier.getMainHierarchy())
//				{
//					if(geogT.getId().toString().equals(taxonomyId))
//					{
//						foundGeog = true;
//						geogTaxonomyName = geogT.getName();
//						geogTaxonomyId = geogT.getId().toString();
//						break;
//					}
//				}
//				if(!foundGeog)
//				{
//					for(List<GeocodeSystem> altHier : hier.getAlternativeHierarchies())
//					{
//						for(GeocodeSystem geogT : altHier)
//						{
//							if(geogT.getId().toString().equals(taxonomyId))
//							{
//								foundGeog = true;
//								geogTaxonomyName = geogT.getName();
//								geogTaxonomyId = geogT.getId().toString();
//								break;
//							}
//						}
//						if(foundGeog) break;
//					}
//				}
//				if(!foundGeog)
//					continue;
//			}
//			
//			List<AttributeMappingConfig> taxonMcfgs = configurationManager.getAttributeMappings(el.getNodeName(), null);
//			/*if(taxonMcfgs == null || taxonMcfgs.isEmpty()) //TODO check if needed
//				continue;*/
//			List<AttributeMappingConfig> valMcfgs = null;
//			if(el.getFirstChild() != null)
//				valMcfgs = configurationManager.getAttributeMappings(el.getNodeName(), el.getFirstChild().getNodeValue());
//			
//			boolean presentable = true;
//			boolean mapValue = false;
//			if(taxonMcfgs != null)
//			{
//				for(AttributeMappingConfig mcfg : taxonMcfgs)
//				{
//					if(!mcfg.isPresentable())
//					{
//						presentable = false;
//						break;
//					}
//				}
//				if(!presentable)
//					continue;
//			}
//			
//			String ttStr = null;
//			if(valMcfgs != null)
//			{
//				for(AttributeMappingConfig mcfg : valMcfgs) //iterate over all value mappings for this value (one for each layer). check if at least one specifies mapped value
//				{
//					if(mcfg.isMapValue())
//						mapValue = true;
//					if(mcfg.getTermId() != null)
//						ttStr = mcfg.getTermId();
//				}
//			}
//			
//			Geocode tt = null;
//			gr.cite.geoanalytics.dataaccess.entities.document.Document shapeDocument = null;
//			
//			
//			//String ttStr = el.getAttribute("term");
//			if(ttStr != null && !ttStr.trim().isEmpty())
//				tt =  taxonomyManager.findTermById(ttStr, false);
//			if(tt != null)
//			{
////				GeocodeShape tts = geocodeShapeDao.find(tt, s);
////				if(tts != null)
////					shapeDocument = shapeDocumentDao.findUniqueByGeocodeShape(tts);
//			}
//			String val = null;
//			if(!mapValue)
//			{
//				if(el.getFirstChild() != null)
//				{
//					val = el.getFirstChild().getNodeValue();
//					//if(val == null || val.equals(""))
//					//	continue;
//				}
//			}
//			else {
//				if(tt == null)
//				{
//					log.error("Could not find mapped taxonomy term: " + el.getAttribute("term") + ". Skipping");
//					continue;
//				}
//				val = tt.getName();
//			}
//			
//			AttributeInfo ai = new AttributeInfo();
//			ai.setName(el.getNodeName());
//			ai.setValue(val);
//			ai.setType(tcfg != null ? tcfg.getId() : geogTaxonomyName);
//			ai.setTaxonomy(tcfg != null ? tcfg.getId() : geogTaxonomyName);
//			if(shapeDocument != null)
//				ai.setDocument(shapeDocument.getId().toString());
//			res.put(tcfg != null ? tcfg.getId().toString() : geogTaxonomyName, ai);
//			
//		}
//		return res;
//	}
//	
////	@Transactional(readOnly = true)
////	public Set<String> getShapeAttributeValues(Taxonomy t) throws Exception
////	{
////		List<AttributeMappingConfig> mcfgs = configurationManager.getAttributeMappingsForTermId(t.getId().toString());
////		String layerId = null;
////		Attribute attr = null;
////		for(AttributeMappingConfig mcfg : mcfgs)
////		{
////			if(mcfg.getAttributeValue() == null)
////			{
////				if(mcfg.isPresentable() == false)
////					throw new Exception("Not a presentable attribute");
////				layerId = mcfg.getLayerTermId();
////				attr = new Attribute(mcfg.getAttributeName(), mcfg.getAttributeType(), mcfg.getTermId(), null);
////				break;
////			}
////		}
////		
////		if(layerId == null)
////			return new HashSet<String>();
////		
////		return shapeDao.getAttributeValuesOfShapesByLayer(taxonomyManager.findTermById(layerId, false), attr);
////	}
//	
//	public class GeocodeInsertionPoint {
//		private List<Geocode> over;
//		private Geocode under;
//		
//		public GeocodeInsertionPoint() { }
//		
//		public GeocodeInsertionPoint(List<Geocode> over, Geocode under) {
//			this.over = over;
//			this.under = under;
//		}
//		
//		public List<Geocode> getParent() {
//			return over;
//		}
//		public void setOver(List<Geocode> over) {
//			this.over = over;
//		}
//		public Geocode getUnder() {
//			return under;
//		}
//		public void setUnder(Geocode under) {
//			this.under = under;
//		}
//		
//	}
//	
//	private int termLevel(Geocode term) {
//		Geocode curTerm = term;
//		int level = 0;
//		while(curTerm.getParent() != null)
//			level++;
//		return level;
//	}
//	
////	private TaxonomyTerm locateShape(Shape s, Taxonomy parentTaxonomy) throws Exception
////	{
////		
////		Point centroid = s.getGeography().getCentroid();
////		centroid.setSRID(4326);
////		Shape pointShape = new Shape();
////	    pointShape.setGeography(centroid);
////	    
////		List<TaxonomyTerm> parentTerms = taxonomyManager.getTermsOfTaxonomy(parentTaxonomy.getId().toString(), true, false);
////		
////		for(TaxonomyTerm parentTerm : parentTerms)
////		{
////			List<Shape> parentShapes = taxonomyManager.getShapesOfTerm(parentTerm);
////			for(Shape parentShape : parentShapes)
////			{
////				if(shapeDao.within(pointShape, parentShape))
////					return parentTerm;
////			}
////		}
////		return null;
////	}
//	
////	private TaxonomyTermInsertionPoint locateShapeInsertionPoint(Shape s, Taxonomy parentTaxonomy) throws Exception
////	{
////		
//////		Point centroid = s.getGeography().getCentroid();
//////		centroid.setSRID(4326);
////		Shape pointShape = new Shape();
//////	    pointShape.setGeography(centroid);
////	    pointShape.setGeography(s.getGeography());
////	    
////		List<TaxonomyTerm> parentTerms = taxonomyManager.getTermsOfTaxonomy(parentTaxonomy.getId().toString(), true, false);
////		
////		List<TaxonomyTerm> bottomTerms = taxonomyManager.getBottomTermsOfTaxonomy(parentTaxonomy.getId().toString(), false);
////		List<TaxonomyTerm> topTerms = taxonomyManager.getTopmostTermsOfTaxonomy(parentTaxonomy.getId().toString(), false);
////		
////		//the following is bottom up search, then top down starting from closest parent that the shape is within
////		//TaxonomyTermInsertionPoint insertionPoint = bottomUpSearchWithin(pointShape, bottomTerms);
////		//if(insertionPoint != null)
////		//	return treeTopDownSearchWithin(pointShape, insertionPoint.under);
////		
////		//the previous is replaced with top down starting from the top nodes in the forest,
////		//in the hope that it's faster
////		TaxonomyTermInsertionPoint insertionPoint = topDownSearchWithin(pointShape, topTerms);
////		if(insertionPoint != null)
////			return insertionPoint;
////
////		//not within current hierarchy, if shape is pshould be set as a parent of one of the topmost nodes
////		List<TaxonomyTerm> over = new ArrayList<>();
////		for(TaxonomyTerm topTerm : topTerms) {
////			List<Shape> checkShapes = taxonomyManager.getShapesOfTerm(topTerm);
////			for(Shape checkShape : checkShapes) {
////				if(shapeDao.within(checkShape, s))
////					over.add(topTerm);
////			}
////		}
////		if(!over.isEmpty())
////			return new TaxonomyTermInsertionPoint(over, null);
////		return new TaxonomyTermInsertionPoint(new ArrayList<>(), null);
////	}
//
////	private TaxonomyTermInsertionPoint bottomUpSearchWithin(Shape pointShape, List<TaxonomyTerm> bottomTerms) throws Exception {
////		
////		for(TaxonomyTerm bottomTerm : bottomTerms) {
////			TaxonomyTerm child = null;
////			
////			TaxonomyTerm curTerm = bottomTerm;
////			do {
////				if(shapeWithinShapeOfTaxonomyTerm(pointShape, curTerm))
////					return new TaxonomyTermInsertionPoint(new ArrayList<>(), curTerm);
////				TaxonomyTerm tmp = curTerm;
////				curTerm = curTerm.getParent();
////				if(curTerm != null)
////					child = tmp;
////				
////			}while(curTerm != null);
////		}
////		return null;
////	}
////	
////	private TaxonomyTermInsertionPoint topDownSearchWithin(Shape checkShape, List<TaxonomyTerm> topTerms) throws Exception {
////		
////		for(TaxonomyTerm topTerm : topTerms) {
////			TaxonomyTermInsertionPoint insertionPoint = treeTopDownSearchWithin(checkShape, topTerm);
////			if(insertionPoint != null)
////				return insertionPoint;
////		}
////		return null;
////	}
////
////	private TaxonomyTermInsertionPoint treeTopDownSearchWithin(Shape checkShape, TaxonomyTerm top) throws Exception {
////		
////		boolean withinTop = shapeWithinShapeOfTaxonomyTerm(checkShape, top);
////		if(!withinTop)
////			return null;
////		
////		List<TaxonomyTerm> children = taxonomyManager.getChildrenOfTerm(top.getId().toString(), true, false);
////		for(TaxonomyTerm child : children) {
////			TaxonomyTermInsertionPoint insertionPoint = treeTopDownSearchWithin(checkShape, child);
////			if(insertionPoint != null)
////				return insertionPoint;
////		}
////		if(withinTop) {
////			List<TaxonomyTerm> over = new ArrayList<>();
////			for(TaxonomyTerm child : children) {
////				if(shapeOfTaxonomyTermWithinShape(child, checkShape))
////					over.add(child);
////			}
////			return new TaxonomyTermInsertionPoint(over, top);
////		}
////		return null;
////	}
////
////	private boolean shapeWithinShapeOfTaxonomyTerm(Shape checkShape, TaxonomyTerm curTerm) throws Exception {
////		List<Shape> shapes = taxonomyManager.getShapesOfTerm(curTerm);
////		for(Shape shape : shapes) {
////			if(shapeDao.within(checkShape, shape))
////				return true;
////		}
////		return false;
////	}
////	
////	private boolean shapeOfTaxonomyTermWithinShape(TaxonomyTerm term, Shape checkShape) throws Exception {
////		List<Shape> shapes = taxonomyManager.getShapesOfTerm(term);
////		for(Shape shape : shapes) {
////			if(shapeDao.within(shape, checkShape))
////				return true;
////		}
////		return false;
////	}
//	
//	private GeocodeSystem findSourceTaxonomy(Map<String, Map<String, AttributeInfo>> attrInfo, Map<String, Set<String>> valueMappingValues) throws Exception
//	{
//		String sourceTaxonomyName = null;
//		for(Map<String, AttributeInfo> aie : attrInfo.values())
//		{
//			for(AttributeInfo ai : aie.values())
//			{
//				if(ai.getValue() == null && ai.isAutoValueMapping())
//				{
//					sourceTaxonomyName = ai.getTaxonomy();
//					break;
//				}
//			}
//			if(sourceTaxonomyName != null)
//				break;
//		}
//		if(sourceTaxonomyName == null)
//		{
//			for(Map.Entry<String, Map<String, AttributeInfo>> aie: attrInfo.entrySet())
//			{
//				Set<String> mappedValues = new HashSet<String>();
//				String taxon = null;
//				for(AttributeInfo ai : aie.getValue().values())
//				{
//					if(ai.getValue() != null)
//					{
//						mappedValues.add(ai.getValue());
//						taxon = ai.getTaxonomy();
//					}
//				}
//				if(mappedValues.containsAll(valueMappingValues.get(aie.getKey())))
//				{
//					sourceTaxonomyName = taxon;
//					break;
//				}
//			}
//		}
//		if(sourceTaxonomyName == null)
//			return null;
//		
//		GeocodeSystem sourceTaxonomy = taxonomyManager.findGeocodeSystemByName(sourceTaxonomyName, false);
//		return sourceTaxonomy;
//	}
//	
//	private boolean checkGeographic(GeocodeSystem termTaxonomy, GeographyHierarchy geographyHierarchy)
//	{
//		List<List<GeocodeSystem>> hier = new ArrayList<List<GeocodeSystem>>(geographyHierarchy.getAlternativeHierarchies());
//		hier.add(geographyHierarchy.getMainHierarchy());
//		for(List<GeocodeSystem> ts : hier)
//		{
//			for(GeocodeSystem t : ts)
//			{
//				if(t.getId().equals(termTaxonomy.getId()))
//					return true;
//			}
//		}
//		return false;
//	}
//	
////	private Map<Taxonomy, TermLinkInfo> locateLinked(Map<String, Map<String, AttributeInfo>> attrInfo, Taxonomy sourceTaxonomy, GeographyHierarchy geographyHierarchy) throws Exception
////	{
////		Map<Taxonomy, TermLinkInfo> linkedLocationInfo = new HashMap<Taxonomy, TermLinkInfo>();
////		
////		List<Taxonomy> hier = new ArrayList<Taxonomy>();
////		Collections.reverse(geographyHierarchy.getMainHierarchy());
////		hier = geographyHierarchy.getMainHierarchy(); //TODO should alts come into play?
////		
////		for(Map.Entry<String, Map<String, AttributeInfo>> aie : attrInfo.entrySet())
////		{
////			AttributeInfo ai = aie.getValue().get("");
////			if(ai == null) continue;
////			String verb = ai.getLinkVerb();
////			if(verb == null) continue;
////			
////			Taxonomy linkTaxonomy = taxonomyManager.findTaxonomyByName(ai.getTaxonomy(), false);
////			
////			Iterator<Taxonomy> currentGeogTaxonomyIt = hier.iterator();
////			boolean located = false;
////			while(currentGeogTaxonomyIt.hasNext())
////			{
////				linkedLocationInfo.put(linkTaxonomy, new TermLinkInfo(verb));
////				Taxonomy currentGeogTaxonomy = currentGeogTaxonomyIt.next();
////				List<TaxonomyTerm> sourceTerms = taxonomyManager.getTermsOfTaxonomy(sourceTaxonomy.getId().toString(), true, false);
////				
////				located = true;
////				for(TaxonomyTerm tt : sourceTerms)
////				{
////					TaxonomyTerm destTerm = locateShape(taxonomyTermDao.getShape(tt), currentGeogTaxonomy);
////					if(destTerm == null)
////					{
////						located = false;
////						break;
////					}
////					else
////						linkedLocationInfo.get(linkTaxonomy).links.put(tt, destTerm);
////				}
////				
////				if(located == false)
////					continue;
////				else
////					break;
////			}
////			if(located == false)
////			{
////				log.error("Could not locate linked terms of taxonomy " + linkTaxonomy.getName() + " within the geography hierarchy");
////				throw new Exception("Could not locate linked terms of taxonomy " + linkTaxonomy.getName() + " within the geography hierarchy");
////			}
////		}
////		
////		return linkedLocationInfo;
////	}
//	
//	//TODO move all MappingConfig creation logic from ShapeImportManager.createDataXML() to this method
//	@Transactional
//	public String generateShapesOfImport(UUID layerID, Map<String, Map<String,AttributeInfo>> attrInfo, Map<String, Set<String>> valueMappingValues, 
//			UUID importId, String layerTermId, GeographyHierarchy geographyHierarchy, Principal principal) throws Exception
//	{
//		DocumentBuilderFactory dbf = null;
//		DocumentBuilder db = null;
//		TransformerFactory tf = null;
//		Transformer transformer = null;
//		
//		Map<String, Map<String, AttributeMappingConfig>> cfgCache = new HashMap<String, Map<String, AttributeMappingConfig>>();
//		
////		ShapeImportManager importMan = new ShapeImportManager(userDao, taxonomyManager, 
////				this, configurationManager);
//		List<ShapeImport> result = shapeImportManager.getImport(importId);		
//		
////		Map<String, Map<String, AttributeInfo>> geographyInfo = new HashMap<String, Map<String, AttributeInfo>>();
////		for(Map<String, AttributeInfo> aim : attrInfo.values())
////		{
////			for(AttributeInfo ai : aim.values())
////			{
////				if(ai.getTaxonomy() != null && ai.getTaxonomy().equals(geographyTaxonomy.getName()))
////				{
////					if(geographyInfo.get(ai.getName()) == null)
////						geographyInfo.put(ai.getName(), new HashMap<String, AttributeInfo>());
////					if(ai.getValue() != null && !ai.getValue().isEmpty()) 
////						geographyInfo.get(ai.getName()).put(ai.getValue(), ai);
////				}
////			}
////		}
////		
////		if(!geographyInfo.isEmpty())
////		{
////			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
////			db = dbf.newDocumentBuilder();
////		}
//		boolean valueMappings = false;
//		boolean autoValueMappings = false;
//		boolean autoDocMappings = false;
//		boolean linked = false;
//		for(Map<String, AttributeInfo> aim : attrInfo.values())
//		{				
//			for(AttributeInfo ai : aim.values())
//			{
//				if(ai.getValue() != null)
//					valueMappings = true;
//				else
//				{
//					if(ai.isAutoValueMapping())
//						autoValueMappings = true;
//					if(ai.isAutoDocumentMapping())
//						autoDocMappings = true;
//					if(ai.getLinkVerb() != null)
//						linked = true;
//				}
//			}
//		}
//		
//		GeocodeSystem sourceTaxonomy = null;
//		if(linked == true)
//		{
//			sourceTaxonomy = findSourceTaxonomy(attrInfo, valueMappingValues);
//			if(sourceTaxonomy == null)
//			{
//				log.error("Unable to find a source taxonomy for linked term mapping");
//				throw new Exception("Unable to find a source taxonomy for linked term mapping");
//			}
//		}
//		//if(!geographyInfo.isEmpty())
//		
//		if(valueMappings || autoValueMappings)
//		{
//			dbf = DocumentBuilderFactory.newInstance();
//			db = dbf.newDocumentBuilder();
//		}
//		if(autoValueMappings)
//		{
//			tf = TransformerFactory.newInstance();
//			tf.setAttribute("indent-number", 2);
//			transformer = tf.newTransformer();
//			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//		}
//		
//		int cnt = 0;
//		log.info("Import " + importId + " contains " + result.size() + " shapes");
//        String identity = null;
//        
//		Map<String, Map<String, Integer>> createdMappingCounts = new HashMap<String, Map<String, Integer>>(); //term name -> attr value -> count
//		
//		long count = 0;
//        for ( ShapeImport si : (List<ShapeImport>) result ) {
//        	Long start = System.currentTimeMillis();
//			//System.out.println( "Shape (" + si.getShapeIdentity() + ") : "); //TODO remove
//			//System.out.println( "Shape (" + si.getGeography() + ") : "); //TODO remove
//			identity = si.getShapeIdentity(); //equal for all shapes of same import
//			if(importId.equals(si.getShapeImport()))
//			{
//				Shape s = new Shape();
//				s.setName(si.getShapeIdentity()+"_"+(cnt++));
//				s.setCreationDate(si.getCreationDate());
//				s.setLastUpdate(si.getLastUpdate());
//				s.setCreatorID(principal.getId());
//				s.setExtraData(si.getData());
//				s.setGeography(si.getGeography());
//				s.setShapeImport(si.getId());
//				//s.setCode(); //TODO sysconfig
//				s.setShapeClass(1); //TODO sysconfig
//				s.setLayerID(layerID);
//				shapeDao.create(s);
//				
////				ShapeLayer sl = new ShapeLayer();
////				sl.setCreatorID(si.getCreatorID());
////				sl.setShape(s);
////				sl.setLayerID(layerID); //refresh to prevent erroneous creation attempt
////				shapeLayerDao.create(sl);
//				
//				//if(!geographyInfo.isEmpty())
//				if(valueMappings)
//					createTermsForShapeAttributes(attrInfo, principal, db, s);
//				
//				if(autoValueMappings || autoDocMappings)
//				{
//					Document d = db.parse(new InputSource(new StringReader(s.getExtraData())));
//					
//					for(Map.Entry<String, Map<String, AttributeInfo>> ai : attrInfo.entrySet())
//					{
//						AttributeInfo attr = ai.getValue().get("");
//						if(attr == null || attr.isStore() == false) continue;
//						if(attr.isAutoValueMapping() == false && attr.isAutoDocumentMapping() == false) continue;
//						if(attr.getTaxonomy() == null)
//							throw new Exception("Taxonomy of auto-created terms is not defined");
//						
//						NodeList els = d.getElementsByTagName(ai.getKey());
//						
//						Node el = els.item(0);
//						Set<String> vals = valueMappingValues.get(attr.getName());
//						if(vals == null) continue;
//						
//						GeocodeSystem termTaxonomy = taxonomyManager.findGeocodeSystemByName(attr.getTaxonomy(), false);
//						boolean geographicTaxonomy = checkGeographic(termTaxonomy, geographyHierarchy);
//						if(geographicTaxonomy == true)
//						{
//							setTaxonomyDataGeographic(termTaxonomy);
//							taxonomyManager.updateTaxonomy(termTaxonomy, termTaxonomy.getName(), false);
//						}
//						
//						if(el.getFirstChild() != null) {
//							String nodeValue = el.getFirstChild().getNodeValue();
//							//DEACTIVATED (NIKOLAS)
////							generateAutoValuedTermsForShapeAttributes(layerTermId, principal, cfgCache, createdMappingCounts, s, attr, vals,termTaxonomy, geographicTaxonomy, nodeValue);
//							generateAutoDocumentMappingsForShapeAttributes(layerTermId, principal, cfgCache, s, attr, vals, termTaxonomy, nodeValue);
//							System.out.println("Inserted shape and generated geography terms. Count: " + count);
//							count++;
//						}
//					}
//				}
//			}
//			System.out.println("Count: " + count + " millis: " + (System.currentTimeMillis() - start));
//        }
//    	attrInfo.values().stream().
//		map(x -> x.get("")).
//		filter(attr -> attr != null && attr.isAutoValueMapping() && attr.isStore()).
//		forEach(this::createTaxonomiesOfTermLevels);
//    	
//    	
//    	//DEACTIVATED (NIKOLAS)
////        if(linked)
////		{
////			Map<Taxonomy, TermLinkInfo> linkedLocationInfo = locateLinked(attrInfo, sourceTaxonomy, geographyHierarchy);
////			for(TermLinkInfo lli : linkedLocationInfo.values())
////			{
////				for(Map.Entry<TaxonomyTerm, TaxonomyTerm> link : lli.links.entrySet()) {
////					TaxonomyTermLink l = new TaxonomyTermLink();
////					l.setSourceTerm(link.getKey());
////					l.setDestinationTerm(link.getValue());
////					l.setCreator(principal);
////					l.setVerb(TaxonomyTermLink.Verb.valueOf(lli.verb));
////					taxonomyTermLinkDao.create(l);
////				}
////			}
////		}
//        
//        if(autoValueMappings) {
//        	
//        }
//        return identity;
//	}
//	
//	private void createTaxonomiesOfTermLevels(AttributeInfo attr) {
//		
//		GeocodeSystem geocodeSystem = taxonomyManager.findGeocodeSystemByName(attr.getTermParentTaxonomy(), false);
//			
//		String topTaxonomyName = geocodeSystem.getName();
//		int level = 0;
//		List<Geocode> terms = taxonomyManager.getTopmostTermsOfTaxonomy(geocodeSystem.getId().toString(), false);//Changes occured here as well the name of the attribute was passed instead of the UUID
//		level++;
//		while(!terms.isEmpty()) {
//			terms = terms.stream().
//					flatMap(t -> taxonomyManager.getChildrenOfGeocode(t.getId().toString(), true, false).stream()).
//					collect(Collectors.toList());
//			
//			GeocodeSystem parent = geocodeSystem;
//			geocodeSystem = taxonomyManager.findGeocodeSystemByName(topTaxonomyName + " " + level, false);
//			
//			if(geocodeSystem == null) {
//				geocodeSystem = new GeocodeSystem();
//				geocodeSystem.setCreator(parent.getCreator());
//				geocodeSystem.setIsActive(true);
//				geocodeSystem.setName(topTaxonomyName + " " + level);
//				TaxonomyData taxonomyData = new TaxonomyData();
//				taxonomyData.setGeographic(true);
//				taxonomyData.setParent(parent.getId());
//				geocodeSystem.setExtraData(taxonomyManager.marshalTaxonomyData(taxonomyData));
//				taxonomyManager.updateTaxonomy(geocodeSystem, null, true);
//			}
//			
//			final GeocodeSystem createdGeocodeSystem = geocodeSystem;
//			terms.forEach(term -> {
//				term.setGeocodeSystem(createdGeocodeSystem);
//				taxonomyManager.updateTerm(term, term.getName(), term.getGeocodeSystem().getName(), false);
//			});
//			level++;
//		}
//	}
//
//	private void generateAutoValuedTermsForShapeAttributes(String layerTermId, Principal principal,
//			Map<String, Map<String, AttributeMappingConfig>> cfgCache,
//			Map<String, Map<String, Integer>> createdMappingCounts, Shape s, AttributeInfo attr, Set<String> vals,
//			GeocodeSystem geocodeSystem, boolean geographicTaxonomy, String nodeValue) throws Exception {
//		for(String val : vals) {
//			
//			if(val.equals(nodeValue)) {
//				
//				if(geographicTaxonomy == true && attr.getTermParentTaxonomy() == null)
//					throw new Exception("Taxonomy of auto-created term parent terms is not defined");
//				
//				if(attr.isAutoValueMapping()) {
//					
//					GeocodeInsertionPoint insertionPoint = null;
//					//DEACTIVATED (NIKOLAS)
////					if(attr.getTermParentTaxonomy() != null)
////						insertionPoint = locateShapeInsertionPoint(s, taxonomyManager.findTaxonomyByName(attr.getTermParentTaxonomy(), false));
//					
//				//	String termName = createdMappings.get(val);
//					String termName = StringUtils.normalizeEntityName(new String(new char[]{val.charAt(0)}).toUpperCase() + val.substring(1).toLowerCase());
//					
//					if(!createdMappingCounts.containsKey(termName))
//						createdMappingCounts.put(termName, new HashMap<String, Integer>());
//					Integer termCnt = null;
//					
//					if(createdMappingCounts.get(termName).containsKey(val))
//						termCnt = createdMappingCounts.get(termName).get(val);
//					else {
//						createdMappingCounts.get(termName).put(val, 0);
//						termCnt = 0;
//					}
//					
//					Geocode ttstt = new Geocode();
//					ttstt.setCreator(principal);
//					ttstt.setIsActive(true);
//					ttstt.setName(termName);
//					//DEACTIVATED (NIKOLAS)
////					ttstt.setParent(insertionPoint.under);
//					ttstt.setExtraData("auto " + attr.getTermParentTaxonomy());
//					ttstt.setGeocodeSystem(geocodeSystem);
//					Geocode existingTtstt = taxonomyManager.findTermByName(termName, false); 
//					
//					if(existingTtstt != null) {
//						List<Shape> shapesOfExisting = taxonomyManager.getShapesOfTerm(existingTtstt);
//						for(Shape existingS : shapesOfExisting) {
//							
//							if(s.getId().equals(existingS.getId())) {
//								taxonomyManager.deleteGeocode(existingTtstt); //should never happen
//								throw new Exception("Duplicate shape of term " + existingTtstt.getName());
//								//break;
//							}
//						}
//						if(termCnt == 0) {
//							
//							//TaxonomyTerm existingTT = taxonomyManager.findTermByName(termName, false);
//							existingTtstt.setName(termName+" "+0);
//							taxonomyManager.updateTerm(existingTtstt, existingTtstt.getName(), existingTtstt.getGeocodeSystem().getName(), false);
//						}
//						ttstt.setName(termName + " " + (++termCnt));
//							
//					}
//
//					taxonomyManager.updateTerm(ttstt, null, null, true);
//					
//					updateParentOfChildrenOfInsertedTerm(insertionPoint, ttstt);
//					
//					AttributeMappingConfig mcfg = new AttributeMappingConfig();
//					mcfg.setAttributeName(attr.getName());
//					mcfg.setAttributeType(attr.getType());
//					mcfg.setAttributeValue(val);
//					mcfg.setLayerTermId(layerTermId);
//					mcfg.setTermId(ttstt.getId().toString());
//					mcfg.setPresentable(attr.isPresentable());
//					mcfg.setMapValue(attr.isMapValue());
//					
//					addMappingConfig(mcfg, cfgCache);
//					
//					//createdMappings.put(val, termName);
//					createdMappingCounts.get(termName).put(val, termCnt);
//					
////					GeocodeShape tts = new GeocodeShape();
////					tts.setGeocode(taxonomyManager.findTermByName(ttstt.getName(), false));
////					tts.setShape(s);
////					tts.setCreator(principal);
////					geocodeShapeDao.create(tts);
////					
////					//if autoValueMappings && autoDocMappings, mapping is driven by auto value mappings
////					if(attr.isAutoDocumentMapping()) {
////						
////						gr.cite.geoanalytics.dataaccess.entities.document.Document doc = findMapDocumentByValue(val);
////						if(doc != null)
////							mapDocumentByValue(doc, tts, principal);
////					}
//					
//				}
//			}
//		}
//	}
//
//	private void updateParentOfChildrenOfInsertedTerm(GeocodeInsertionPoint insertionPoint, Geocode ttstt)
//			throws Exception {
//		//update child's parent in case ttstt has been inserted between two levels in the hierarchy
//		if(!insertionPoint.over.isEmpty()) {
//			for(Geocode over : insertionPoint.over) {
//				over.setParent(ttstt);
//				taxonomyManager.updateTerm(over, null, null, false);
//			}
//		}
//	}
//	
//	private void generateAutoDocumentMappingsForShapeAttributes(String layerTermId, Principal principal,
//			Map<String, Map<String, AttributeMappingConfig>> cfgCache, Shape s, AttributeInfo attr, Set<String> vals,
//			GeocodeSystem geocodeSystem, String nodeValue) throws Exception {
//		for(String val : vals) {
//			if(val.equals(nodeValue) && attr.isAutoDocumentMapping())
//			{
//				gr.cite.geoanalytics.dataaccess.entities.document.Document doc = findMapDocumentByValue(val);
//				if(doc != null)
//				{
//					String termName = attr.getTaxonomy() + StringUtils.normalizeEntityName(new String(new char[]{val.charAt(0)}).toUpperCase() + val.substring(1).toLowerCase());
//					Geocode ttstt = taxonomyManager.findTermByName(termName, false);
//					if(ttstt == null)
//					{
//						ttstt = new Geocode();
//						ttstt.setName(termName);
//						ttstt.setCreator(principal);
//						ttstt.setIsActive(true);
//						ttstt.setGeocodeSystem(geocodeSystem);
//						taxonomyManager.updateTerm(ttstt, null, null, true);
//						
//						AttributeMappingConfig mcfg = new AttributeMappingConfig();
//						mcfg.setAttributeName(attr.getName());
//						mcfg.setAttributeType(attr.getType());
//						mcfg.setAttributeValue(val);
//						mcfg.setLayerTermId(layerTermId);
//						mcfg.setTermId(ttstt.getId().toString());
//						
//						addMappingConfig(mcfg, cfgCache);
//					}
////					GeocodeShape tts = geocodeShapeDao.find(ttstt, s);
////					
////					if(tts == null)
////					{
////						tts = new GeocodeShape();
////						tts.setCreator(principal);
////						tts.setShape(s);
////						tts.setGeocode(ttstt);
////						geocodeShapeDao.create(tts);
////					}
////					mapDocumentByValue(doc, tts, principal);
//				}
//				
//			}
//		}
//	}
//
//	private void createTermsForShapeAttributes(Map<String, Map<String, AttributeInfo>> attrInfo, Principal principal,
//			DocumentBuilder db, Shape s) throws SAXException, IOException, Exception {
//		Document d = db.parse(new InputSource(new StringReader(s.getExtraData())));
//		//for(Map.Entry<String, Map<String, AttributeInfo>> gi : geographyInfo.entrySet())
//		for(Map.Entry<String, Map<String, AttributeInfo>> ai : attrInfo.entrySet())
//		{
//			if(ai.getValue().get("") != null && ai.getValue().get("").isStore() == false) continue;
//			NodeList els = d.getElementsByTagName(ai.getKey());
//			if(els.getLength() == 1)
//			{
//				Node el = els.item(0);
//				for(Map.Entry<String, AttributeInfo> aie : ai.getValue().entrySet())
//				{
//					if(el.getFirstChild() != null && aie.getKey().equals(el.getFirstChild().getNodeValue()))
//					{
//						Geocode ttstt = taxonomyManager.findTermByNameAndTaxonomy(aie.getValue().getTerm(), aie.getValue().getTaxonomy(), false);
////						GeocodeShape tts = new GeocodeShape();
////						tts.setGeocode(ttstt);
////						tts.setShape(s);
////						tts.setCreator(principal);
////						geocodeShapeDao.create(tts);
//						
//						if(aie.getValue().getDocument() != null)
//						{
//							gr.cite.geoanalytics.dataaccess.entities.document.Document document = 
//									documentManager.findById(aie.getValue().getDocument(), false);
//							ShapeDocument sd = new ShapeDocument();
//							sd.setCreator(principal);
////							sd.setGeocodeShape(tts);
//							sd.setDocument(document);
//							shapeDocumentDao.create(sd);
//						}
//					}
//				}
//			}
//		}
//	}
//	
//	private void addMappingConfig(AttributeMappingConfig mcfg, Map<String, Map<String, AttributeMappingConfig>> cfgCache) throws Exception
//	{
//		if(cfgCache == null)
//		{
//			configurationManager.updateMappingConfig(mcfg);
//			return;
//		}
//		String key = null;
//		if(mcfg.getAttributeName() == null) return;
//		if(mcfg.getTermId() == null && mcfg.getAttributeValue() == null)
//			key = NoMappingKey;
//		else if(mcfg.getAttributeValue() == null)
//			key = NoValueKey;
//		else
//			key = mcfg.getAttributeValue();
//
//		if(cfgCache.get(mcfg.getAttributeName()) == null)
//			cfgCache.put(mcfg.getAttributeName(), new HashMap<String, AttributeMappingConfig>());
//		if(cfgCache.get(mcfg.getAttributeName()).get(key) == null)
//		{
//			cfgCache.get(mcfg.getAttributeName()).put(key, mcfg);
//			configurationManager.updateMappingConfig(mcfg);
//		}
//	}
//	
//	private void setTaxonomyDataGeographic(GeocodeSystem taxonomy) {
//		TaxonomyData taxonomyData = new TaxonomyData();
//		taxonomyData.setGeographic(true);
//		taxonomy.setExtraData(taxonomyManager.marshalTaxonomyData(taxonomyData));
//	}
//	
//	private gr.cite.geoanalytics.dataaccess.entities.document.Document findMapDocumentByValue(String val) throws Exception
//	{
//		List<gr.cite.geoanalytics.dataaccess.entities.document.Document> ds = 
//				documentManager.searchDocuments(Collections.singletonList(val)); //TODO pattern match value to doc name/description, keep for now
//		if(ds != null && !ds.isEmpty())
//		{
//			if(ds.size() == 1)
//			{
//				gr.cite.geoanalytics.dataaccess.entities.document.Document doc = ds.get(0);
//				RepositoryFile rf = repository.retrieve(doc.getId().toString());
//				if(rf == null)
//				{
//					log.error("Could not locate doc " + doc.getId() + " for value " + val + " in data repository");
//					throw new Exception("Could not locate doc " + doc.getId() + " for value " + val + " in data repository");
//				}
//				return doc;
//			}
//			else
//			{
//				log.warn("Multiple documents matching " + val + " were found during auto document mapping");
//				return null;
//			}
//		}else
//		{
//			log.warn("Could not find document for value " + val + ".");
//			return null;
//		}
//	}
////	private void mapDocumentByValue(gr.cite.geoanalytics.dataaccess.entities.document.Document doc, GeocodeShape tts, Principal principal) throws Exception {
////		ShapeDocument sd = new ShapeDocument();
////		sd.setCreator(principal);
////		sd.setGeocodeShape(tts);
////		sd.setDocument(doc);
////		shapeDocumentDao.create(sd);
////	}
//	
//	@Transactional
//	public void generateShapeBoundary(UUID layerID, String layerName, Geocode boundaryTerm, Principal principal) throws Exception
//	{
//		List<Shape> shapes = getShapesOfLayer(layerID);
//		
//		Shape boundary = new Shape();
//		boundary.setCreatorID(principal.getId());
//		boundary.setGeography(shapes.get(0).getGeography());
//		boundary.setName(layerName+"_boundary");
//		shapeDao.create(boundary);
//		
//		for(int i=1; i<shapes.size(); i++)
//		{
//			Shape buffer = shapeDao.buffer(shapes.get(i), 10.0f);
//			shapeDao.create(buffer);
//			
//			Shape union = shapeDao.union(boundary, buffer);
//			boundary.setGeography(union.getGeography());
//			shapeDao.update(boundary);
//			
//			shapeDao.delete(buffer);
//		}
//		//Shape b = shapeDao.boundary(boundary);
//		//boundary.setGeography(b.getGeography());
//		//shapeDao.update(boundary);
//		
////		GeocodeShape tts = new GeocodeShape();
////		tts.setCreator(principal);
////		tts.setGeocode(boundaryTerm);
////		tts.setShape(boundary);
////		geocodeShapeDao.create(tts);
//	}
//	
//	@Transactional(readOnly = true)
//	public List<Shape> getShapesOfImport(UUID importId) throws Exception
//	{
//		ShapeImport si = new ShapeImport();
//		si.setShapeImport(importId);
//		List<Shape> shapes = shapeDao.findShapesByImport(si);
//		if(shapes == null) return null;
//		
///*		for(Shape s : shapes)
//			getShapeDetails(s);*/
//		return shapes;
//	}
//	
//	@Transactional(readOnly = true)
//	public List<Shape> getShapesOfLayer(UUID layerID) throws Exception
//	{
//		List<Shape> shapes = shapeDao.findShapesOfLayer(layerID);
//		if(shapes == null) return null;
//		
///*		for(Shape s : shapes)
//			getShapeDetails(s);*/
//		return shapes;
//	}
//	
//	
//	
//	@Transactional(readOnly = true)
//	public List<ShapeInfo> getShapesInfoForLayer(String layerID) throws Exception{
//		return getShapesInfoForLayer(UUID.fromString(layerID));
//	}
//	
//	
//	@Transactional(readOnly = true)
//	public List<ShapeInfo> getShapesInfoForLayer(UUID layerID) throws Exception
//	{
//				
//		List<Shape> shapes = shapeDao.findShapesOfLayer(layerID);
//		if(shapes == null) return null;
//		
//		List<ShapeInfo> res = new ArrayList<ShapeInfo>();
//		for(Shape s : shapes)
//		{
//			//getShapeDetails(s);
//			ShapeInfo si = new ShapeInfo();
//			si.setShape(s);
//			si.setLayerID(layerID);
//			res.add(si);
//		}
//		return res;
//	}
//	
//	@Transactional
//	public void deleteShapesOfLayer(UUID layerID) throws Exception
//	{
//		shapeDao.findShapesOfLayer(layerID)
//				.forEach(shape -> shapeDao.delete(shape));
//	}
//	
////	@Transactional(readOnly = true)
////	public List<TaxonomyTermShape> findTermMappingsOfLayerShapes(TaxonomyTerm tt) throws Exception
////	{
////		return shapeDao.findTermMappingsOfLayerShapes(tt);
////	}
//	
//	
//	@Transactional(readOnly = true)
//	public UUID findLayerIDOfShape(Shape s) throws Exception {
//		return shapeDao.findLayerIDOfShape(s);
//	}
//	
//	@Transactional(readOnly = true)
//	public List<Shape> findShapesOfImport(ShapeImport shapeImport) throws Exception
//	{
//		return shapeDao.findShapesByImport(shapeImport);
//	}
//	
//	@Transactional(readOnly = true)
//	public long countShapesOfImport(UUID shapeImport) throws Exception
//	{
//		return shapeDao.countShapesByImport(shapeImport);
//	}
//	
//	@Transactional(readOnly = true)
//	public List<ShapeInfo> findShapesOfImport(UUID shapeImport) throws Exception
//	{
//		List<Shape> shapes = shapeDao.findShapesByImport(shapeImport);
//		List<ShapeInfo> res = new ArrayList<ShapeInfo>();
//		for(Shape s : shapes)
//		{
//			//getShapeDetails(s);
//			ShapeInfo si = new ShapeInfo();
//			si.setShape(s);
//			UUID layerID = shapeDao.findLayerIDOfShape(s);
//			si.setLayerID(layerID);
//			res.add(si);
//		}
//		return res;
//	}
//	
//	@Transactional(readOnly = true)
//	public List<ShapeInfo> findShapeWithinBounds(String bounds) throws Exception
//	{
//		Geometry geom = new WKTReader().read(bounds);
//		geom.setSRID(4326);
//		Shape sh = new Shape();
//		sh.setId(UUIDGenerator.randomUUID());
//		sh.setGeography(geom);
//		
//		List<Shape> shapes = shapeDao.findContains(sh);
//		List<ShapeInfo> res = new ArrayList<ShapeInfo>();
//		for(Shape s : shapes)
//		{
//			//getShapeDetails(s);
//			ShapeInfo si = new ShapeInfo();
//			si.setShape(s);
//			UUID layerID = shapeDao.findLayerIDOfShape(s);
//			si.setLayerID(layerID);
//			res.add(si);
//		}
//		return res;
//	}
//	
//	@Transactional(readOnly = true)
//	public boolean existShapesOfLayer(UUID layerID) throws Exception
//	{
//		return shapeDao.existShapesOfLayer(layerID);
//	}
//	
//	@Transactional(readOnly = true)
//	public ShapeInfo getShape(UUID id) throws Exception
//	{
//		ShapeInfo si = new ShapeInfo();
//		Shape s = shapeDao.read(id);
//		if(s == null) return null;
////		getShapeDetails(s);
//		si.setShape(s);
//		UUID layerID = shapeDao.findLayerIDOfShape(s);
//		si.setLayerID(layerID);
//		return si;
//	}
//	
////	@Transactional(readOnly = true)
////	public Bounds getShapeBounds(UUID id) throws Exception
////	{
////		Shape s = shapeDao.read(id);
////		if(s == null) throw new Exception("Shape " + id + " not found");
////		
////		Shape env = shapeDao.envelope(s);
////		Geometry geom = env.getGeography();
////		return null;
////	}
//	
//	@Transactional
//	public Shape createFromGeometry(Principal principal,String shapeName, String geometry) throws Exception {
//		Geometry geom = new WKTReader().read(geometry);
//		geom.setSRID(4326);
//		Shape s = new Shape();
//		s.setGeography(geom);
//		s.setName(shapeName);
//		s.setCreatorID(principal.getId());
//		shapeDao.create(s);
//		return s;
//	}
//	
//	@Transactional
//	public Shape createFromGeometry(Project project, String geometry) throws Exception {
//		return createFromGeometry(project.getCreator(), project.getName(), geometry);
//	}
//	
//	@Transactional
//	public Shape createFromGeometryPolygon(Project project, NewProjectData npd, Principal principal) throws Exception {
//		 WKTReader reader = new WKTReader();
//		 String polygon =
//				    "POLYGON(("+ npd.getCoords().getCoord0()[0] + " " + npd.getCoords().getCoord0()[1] + ","
//							+ npd.getCoords().getCoord1()[0] + " " + npd.getCoords().getCoord1()[1] +","
//							+ npd.getCoords().getCoord2()[0] + " " + npd.getCoords().getCoord2()[1] +","
//							+ npd.getCoords().getCoord3()[0] + " " + npd.getCoords().getCoord3()[1]+","
//							+ npd.getCoords().getCoord0()[0] + " " + npd.getCoords().getCoord0()[1] + "))";
//		 
//		 Geometry g = reader.read(polygon);
//		 g.setSRID(4326);
//			
////			if ( !geo.isRectangle() ) {
////		        geo = geo.getEnvelope();
////		        WKTWriter writer = new WKTWriter();
////		        String bbox = writer.write( geo );
////		        wkt = bbox;
////		    }
//
//		Shape s = new Shape();
//		s.setGeography(g);
//		s.setCreatorID(principal.getId());
////		s.setCode(npd.getCoords().toString());
//		shapeDao.create(s);
//		return s;
//	}
//	
//	
//	@Transactional
//	public void updateGeometry(UUID id, String geometry) throws Exception {
//		Shape ex = shapeDao.read(id);
//		if(ex == null) throw new Exception("Shape " + id + " not found");
//		Geometry geom = new WKTReader().read(geometry);
//		ex.setGeography(geom);
//		shapeDao.update(ex);
//	}
//	
//	@Transactional(readOnly = true)
//	public String getGeometry(UUID id) throws Exception {
//		Shape s = shapeDao.read(id);
//		if(s == null) throw new Exception("Shape " + id + " not found");
//		return new WKTWriter().write(s.getGeography());
//	}
//	
//	@Transactional(readOnly = true)
//	public String getBoundingBoxByProjectNameAndTenant(String projectName, String tenantName) throws Exception{
//		List<Project> projects = projectDao.findByNameAndTenant(projectName, tenantName);
//		if(projects != null && projects.size() > 1){
//			throw new Exception("Multiple projects with name " + projectName);
//		} 
//		
//		Project project = null;
//		if( projects != null && !projects.isEmpty()){
//			project = projects.get(0);
//		}
//		
////		Shape s = shapeDao.read(project.getShape());
////		if(s == null) throw new Exception("Shape " + project.getShape() + " not found");
////		Coordinate[] coords = s.getGeography().getCoordinates();
////		List<String> coordinates = new ArrayList<String>();
////		for(int i=0;i<coords.length-1;i++){
////			coordinates.add(coords[i].toString());
////		}
////		return new WKTWriter().write(s.getGeography().getCoordinates());
//		return project.getExtent();
//	}
//	
//	
//	@Override
//	public String getBoundingBoxByProjectName(String projectName) throws Exception {
//		List<Project> projects = projectDao.findByName(projectName);
//		if(projects != null && projects.size() > 1){
//			throw new Exception("Multiple projects with name " + projectName);
//		} 
//		
//		Project project = null;
//		if( projects != null && !projects.isEmpty()){
//			project = projects.get(0);
//		}
//		
////		Shape s = shapeDao.read(project.getShape());
////		if(s == null) throw new Exception("Shape " + project.getShape() + " not found");
////		Coordinate[] coords = s.getGeography().getCoordinates();
////		List<String> coordinates = new ArrayList<String>();
////		for(int i=0;i<coords.length-1;i++){
////			coordinates.add(coords[i].toString());
////		}
////		return new WKTWriter().write(s.getGeography().getCoordinates());
//		return project.getExtent();
//	}
//	
//	
//	
//	@Transactional
//	public void update(Shape s) throws Exception {
//		Shape ex = shapeDao.read(s.getId());
//		if(ex == null) throw new Exception("Shape " + s.getId() + " not found");
//		if(s.getCode() != null) ex.setCode(s.getCode());
//		if(s.getExtraData() != null) ex.setExtraData(s.getExtraData());
//		if(s.getName() != null) ex.setName(s.getName());
//		if(s.getShapeClass() > -1) ex.setShapeClass(s.getShapeClass());
//		//if(s.getGeography() != null) ex.setGeography(s.getGeography()); TODO support in the future
//		shapeDao.update(ex);
//	}
//	
//	@Transactional(rollbackFor={Exception.class})
//	public void delete(List<String> shapeIDs) throws Exception
//	{
//		shapeIDs.forEach(shapeID -> {
//			shapeDao.deleteByShapeID(UUID.fromString(shapeID));
//			Shape sh = shapeDao.read(UUID.fromString(shapeID));
//			shapeDao.delete(sh);
//		});
//	}
//	
//	@Transactional(readOnly = true)
//	public List<Shape> findShapesEnclosingGeometry(Shape s) throws Exception {
//		return shapeDao.findWithin(s);
//	}
//	
////	@Transactional(readOnly = true)
////	public List<Shape> findShapesOfLayerEnclosingGeometry(Shape s /*, TaxonomyTerm layerTerm*/) throws Exception {
////		return shapeDao.findWithin(s/*, layerTerm, null*/);
////	}
//	
////	@Transactional(readOnly = true)
////	public List<Shape> findShapesOfLayerEnclosingGeometry(Shape s, TaxonomyTerm layerTerm, TaxonomyTerm term) throws Exception {
////		return shapeDao.findWithin(s, layerTerm, term);
////	}
//	
//	@Transactional(readOnly = true)
//	public List<Shape> findShapesEnclosingGeometry(Geometry geometry) throws Exception {
//		Shape s = new Shape();
//		s.setGeography(geometry);
//		return findShapesEnclosingGeometry(s);
//	}
//	
////	@Transactional(readOnly = true)
////	public List<Shape> findShapesOfLayerEnclosingGeometry(Geometry geometry, UUID layerID) throws Exception {	
////		Shape s = new Shape();
////		s.setGeography(geometry);
////		return findShapesOfLayerEnclosingGeometry(s, layerTerm);
////	}
//	
////	@Transactional(readOnly = true)
////	public List<Shape> findShapesOfLayerEnclosingGeometry(Geometry geometry, TaxonomyTerm layerTerm, TaxonomyTerm term) throws Exception {
////		Shape s = new Shape();
////		s.setGeography(geometry);
////		return findShapesOfLayerEnclosingGeometry(s, layerTerm, term);
////	}
//
//	private List<List<GeocodeSystem>> getAlternativeHierarchies(List<GeocodeSystem> mainHierarchy, List<List<GeocodeSystem>> currentAlts, int index, List<GeocodeSystem> allTaxonomies, Map<UUID, TaxonomyData> taxonomyData) throws Exception
//	{
//		List<List<GeocodeSystem>> currHierarchies = new ArrayList<List<GeocodeSystem>>(currentAlts);
//		currHierarchies.add(mainHierarchy);
//		
//		List<List<GeocodeSystem>> altHierarchies = new ArrayList<List<GeocodeSystem>>();
//		GeocodeSystem altTaxonomy = mainHierarchy.get(index);
//		
//		TaxonomyData altTaxonomyData = taxonomyData.get(altTaxonomy.getId());
//		
//		for(List<GeocodeSystem> hier : currHierarchies) {
//			for(UUID alt : altTaxonomyData.getAlternatives()) {
//				List<GeocodeSystem> altHierarchy = new ArrayList<GeocodeSystem>();
//				
//				for(GeocodeSystem t : hier) {
//					if(t.getId().equals(altTaxonomy.getId()))
//						break;
//					altHierarchy.add(t);
//				}
//				
//				altHierarchies.add(altHierarchy);
//			}
//		}
//		
//		for(int i=0; i<altTaxonomyData.getAlternatives().size(); i++) {
//			for(List<GeocodeSystem> ah : altHierarchies) {
//				
//				GeocodeSystem child = null;
//				int ind = i;
//				List<GeocodeSystem> children = allTaxonomies.stream().
//						filter(t -> taxonomyData.get(t.getId()).getParent().equals(altTaxonomyData.getAlternatives().get(ind))).
//						collect(Collectors.toList());
//				if(!children.isEmpty()) {
//					if(children.size() > 1)
//						throw new Exception("Branched taxonomy hierarchies not supported");
//					child = children.get(0);
//				}
//				
//				if(child == null)
//					break;
//				ah.add(child);
//			}
//		}
//		
//		List<GeocodeSystem> rest = mainHierarchy.subList(index+1, mainHierarchy.size());
//		
//		for(List<GeocodeSystem> hier : altHierarchies)
//			hier.addAll(rest);
//		
//		return altHierarchies;
//	}
//	
//	@Transactional(readOnly=true)
//	public GeographyHierarchy getDefaultGeographyHierarchy() throws Exception {
//		return getGeographyHierarchy(taxonomyManager.findGeocodeSystemById(
//				configurationManager.retrieveTaxonomyConfig(Type.GEOGRAPHYTAXONOMY).get(0).getId(), false));
//	}
//	
//	@Transactional(readOnly=true)
//	public GeographyHierarchy getGeographyHierarchy(GeocodeSystem geogTaxonomy) throws Exception {
//		GeographyHierarchy hierarchy = new GeographyHierarchy();
//		
//		List<GeocodeSystem> allTaxonomies = taxonomyManager.allGeocodeSystems(false);
//		Map<UUID, TaxonomyData> taxonomyData = allTaxonomies.stream().
//				filter(t -> t.getExtraData() != null).
//				collect(Collectors.toMap(GeocodeSystem::getId, t -> taxonomyManager.unmarshalTaxonomyData(t.getExtraData())));
//		
//		GeocodeSystem reloadedGeoTax = allTaxonomies.stream().filter(t -> t.getId().equals(geogTaxonomy.getId())).findFirst().get();
//		hierarchy.setMainHierarchy(constructMainHierarchy(reloadedGeoTax, allTaxonomies, taxonomyData));
//		hierarchy.setAlternativeHierarchies(constructAlternativeHierarchies(hierarchy, allTaxonomies, taxonomyData));
//				
///*		hierarchy.getMainHierarchy().
//			forEach(t -> taxonomyDao.loadDetails(t));
//		
//		hierarchy.getAlternativeHierarchies().
//			forEach(alt ->
//				alt.forEach(t -> taxonomyDao.loadDetails(t)));*/
//		
//		return hierarchy;
//		
//	}
//
//	private List<List<GeocodeSystem>> constructAlternativeHierarchies(GeographyHierarchy hierarchy,
//			List<GeocodeSystem> allTaxonomies, Map<UUID, TaxonomyData> taxonomyData) throws Exception {
//		List<Integer> altIndexes = new ArrayList<Integer>();
//		
//		int i = 0;
//		for(GeocodeSystem currTaxonomy : hierarchy.getMainHierarchy()) {
//			if(!taxonomyData.get(currTaxonomy.getId()).getAlternatives().isEmpty())
//				altIndexes.add(i);
//			i++;
//		}
//		
//		List<List<GeocodeSystem>> altHierarchies = new ArrayList<>();
//		for(Integer index : altIndexes)
//			altHierarchies.addAll(getAlternativeHierarchies(hierarchy.getMainHierarchy(), altHierarchies, index, allTaxonomies, taxonomyData));
//		return altHierarchies;
//	}
//
//	/**
//	 * 
//	 * @param geogTaxonomy a taxonomy within the hierarchy that is to be returned. Not necessarily the top taxonomy.
//	 * @return
//	 * @throws Exception
//	 */
//	private List<GeocodeSystem> constructMainHierarchy(GeocodeSystem geogTaxonomy, List<GeocodeSystem> allTaxonomies, Map<UUID, TaxonomyData> taxonomyData) throws Exception {
//		//		List<TaxonomyTerm> terms = taxonomyManager.getTermsOfTaxonomy(geogTaxonomy.getId().toString(), true, false);
//		//		if(terms.size() == 0) throw new Exception("No geographic data");
//		//		TaxonomyTerm term = terms.get(0);
//		if(geogTaxonomy == null)
//			throw new IllegalArgumentException("Geography taxonomy cannot be null");
//		
//		LinkedList<GeocodeSystem> hier = new LinkedList<GeocodeSystem>();
//		hier.add(geogTaxonomy);
//		
//		GeocodeSystem currTaxonomy = geogTaxonomy;
//		while(currTaxonomy != null) {
//			TaxonomyData taxData = taxonomyData.get(currTaxonomy.getId());
//			if(taxData == null || taxData.getParent() == null)
//				break;
//			
//			GeocodeSystem parent = taxonomyManager.findGeocodeSystemById(taxData.getParent().toString(), false);
//			if(taxData.getParent() != null)
//				hier.push(parent);
//			currTaxonomy = parent;
//		}
//		
//		currTaxonomy = hier.peekLast();
//		while(true) {
//			final GeocodeSystem ct = currTaxonomy;
//			GeocodeSystem child = null;
//			List<GeocodeSystem> children = allTaxonomies.stream().
//					filter(t -> {
//						TaxonomyData td = taxonomyData.get(t.getId());
//						return td != null && td.getParent() != null && td.getParent().equals(ct.getId());
//					}).
//					collect(Collectors.toList());
//			if(!children.isEmpty()) {
//				if(children.size() > 1)
//					throw new Exception("Branched taxonomy hierarchies not supported");
//				child = children.get(0);
//			}
//			
//			if(child == null)
//				break;
//			
//			hier.add(child);
//			currTaxonomy = child;
//		}
//		return hier;
//	}
//	
//	@Transactional(readOnly=true)
//	public List<Geocode> geoLocate(double x, double y) throws Exception
//	{
//		List<Geocode> res = new ArrayList<Geocode>();
//		
//		GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
//
//        Point point = gf.createPoint(new Coordinate(x, y));
//        Shape pointShape = new Shape();
//        pointShape.setGeography(point);
//        
//        TaxonomyConfig tcfg = null;
//		List<TaxonomyConfig> tcfgs = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY);
//		if (tcfgs != null){
//			tcfg = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY).get(0);
//		}
//		//Taxonomy geoTaxonomy = taxonomyManager.findTaxonomyById(tcfg.getId(), false);
//		List<Geocode> terms = taxonomyManager.getTopmostTermsOfTaxonomy(tcfg.getId(), false);
//		
//		if(terms == null || terms.isEmpty())
//			return res;
//		
//		while(true)
//		{
//			boolean located = false;
//			for(Geocode geocode : terms)
//			{
//				List<Shape> termShapes = taxonomyManager.getShapesOfTerm(geocode);
//				if(termShapes == null || termShapes.isEmpty())
//				{
//					log.error("Could not find shapes of taxonomy term " + geocode.getId());
//					throw new Exception("Could not find shapes of taxonomy term " + geocode.getId());
//				}
//				for(Shape termShape : termShapes)
//				{
//					if(shapeDao.within(pointShape, termShape))
//					{
//						if(geocode.getParent() != null) geocode.getParent().getName();
//						if(geocode.getGeocodeClass() != null) geocode.getGeocodeClass().getName();
//						geocode.getGeocodeSystem().getName();
//						geocode.getCreator().getName();
//						res.add(geocode);
//						terms = taxonomyManager.getChildrenOfGeocode(geocode.getId().toString(), true, false);
//						located = true;
//						break;
//					}
//				}
//				if(located)
//					break;
//			}
//			if(terms == null || terms.isEmpty() || located == false)
//				break;
//		}
//		return res;
//	}
//	
////  TODO starting from bottom terms - lower perfomance, keep until top terms method is tested
////	@Transactional(readOnly=true)
////	public List<TaxonomyTerm> geoLocate(double x, double y) throws Exception
////	{
////		List<TaxonomyTerm> res = new ArrayList<TaxonomyTerm>();
////		
////		GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
////
////        Point point = gf.createPoint(new Coordinate(x, y));
////        Shape pointShape = new Shape();
////        pointShape.setGeography(point);
////        
////		TaxonomyConfig tcfg = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY);
////		//Taxonomy geoTaxonomy = taxonomyManager.findTaxonomyById(tcfg.getId(), false);
////		List<TaxonomyTerm> terms = taxonomyManager.getBottomTermsOfTaxonomy(tcfg.getId(), false);
////		
////		TaxonomyTerm locatedTerm = null;
////		for(TaxonomyTerm term : terms)
////		{
////			Shape termShape = taxonomyManager.getShapeOfTerm(term);
////			if(termShape == null)
////			{
////				log.error("Could not find shape of taxonomy term " + term.getId());
////				throw new Exception("Could not find shape of taxonomy term " + term.getId());
////			}
////			if(shapeDao.within(pointShape, termShape))
////			{
////				locatedTerm = term;
////				break;
////			}
////		}
////		if(terms == null || terms.isEmpty() || locatedTerm == null)
////			return res;
////		
////		TaxonomyTerm term = locatedTerm;
////		while(term != null)
////		{
////			if(term.getParent() != null) term.getParent().getName();
////			if(term.getTaxonomyTermClass() != null) term.getTaxonomyTermClass().getName();
////			term.getTaxonomy().getName();
////			term.getCreator().getSystemName();
////			res.add(term);
////			term = term.getParent();
////		}
////		Collections.reverse(res); //reverse term list so that the hierarchy begins with the widest area
////		return res;
////	}
//	
//	@Transactional(readOnly=true)
//	public List<GeoLocation> termLocate(SearchType searchType, String term, Principal principal) throws Exception {
//		List<GeoLocation> res = new ArrayList<GeoLocation>();
//		List<Shape> shapes = shapeDao.searchShapes(Collections.singletonList(term));
//		Map<String, Project> projectShapeMappings = new HashMap<String, Project>();
//		shapes = filterBySearchType(searchType, shapes, principal, projectShapeMappings);
//		
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = dbf.newDocumentBuilder();
//		
//		for(Shape s : shapes)
//		{
//			boolean nonGeographic = false;
////			List<GeocodeShape> ttss = geocodeShapeDao.findByShape(s);
////			for(GeocodeShape tts : ttss)
////			{
////				GeocodeSystem tax = tts.getGeocode().getGeocodeSystem();
////				if(tax.getExtraData() == null || tax.getExtraData().isEmpty())
////				{
////					nonGeographic = true;
////					break;
////				}else
////				{
////					Document ed = db.parse(tax.getExtraData());
////					if(!ed.getDocumentElement().hasAttribute("geographic") || Boolean.parseBoolean(ed.getDocumentElement().getAttribute("geographic").trim()) == false)
////					{
////						nonGeographic = true;
////						break;
////					}
////				}
////			}
//			if(nonGeographic == false)
//				continue;
//			Point centroid = s.getGeography().getCentroid();
//			List<Geocode> terms = geoLocate(centroid.getX(), centroid.getY());
//			if(terms == null || terms.isEmpty()) continue;
//			
//			List<GeoLocationTag> tags = new ArrayList<GeoLocationTag>();
//			for(Geocode t : terms)
//			{
////				Shape tts = geocodeShapeDao.findUniqueByGeocode(t).getShape();
////				Point ttsCentroid = tts.getGeography().getCentroid();
////				AttributeInfo tagInfo = retrieveShapeAttributeByTaxonomy(geocodeShapeDao.findUniqueByGeocode(t).getShape(), t.getGeocodeSystem().getId().toString());
////				GeocodeSystem tax = taxonomyManager.findGeocodeSystemById(tagInfo.getTaxonomy(), false);
////				Geometry b = tts.getGeography().getEnvelope();
////				Bounds bounds = new Bounds(b.getCoordinates()[0].x, b.getCoordinates()[0].y, b.getCoordinates()[2].x, b.getCoordinates()[2].y, null);
////				tags.add(new GeoLocationTag(t.getId().toString(), tagInfo.getValue(), tax.getId().toString(), tax.getName(), ttsCentroid.getX(), ttsCentroid.getY(), bounds));
//			}
//			Geometry b = s.getGeography().getEnvelope();
//			Bounds bounds = new Bounds(b.getCoordinates()[0].x, b.getCoordinates()[0].y, b.getCoordinates()[2].x, b.getCoordinates()[2].y, null);
//			if(searchType == SearchType.MAP)
//				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds));
//			else if(searchType == SearchType.PROJECTS)
//			{
//				Project shapeProject = projectShapeMappings.get(s.getId().toString());
//				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds, shapeProject.getName(), shapeProject.getId().toString()));
//			}
//		}
//		return res;
//	}
//
//	/** TAXONOMIES WILL NOT BE ACCOMPANIED BY SHAPES ANYMORE, RIGHT? **/
//	@Override
//	@Transactional
//	@Deprecated
//	public List<String> getBreadcrumbs(Coords coords) throws Exception {
////	public Map<UUID, List<Geocode>> getBreadcrumbs(Coords coords) throws Exception {
//		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
//		Point point = geometryFactory.createPoint(new Coordinate(coords.getLon(), coords.getLat()));
//		List<Shape> shapes = findShapesEnclosingGeometry(point);
//		
//		Map<UUID, Geocode> termsById = new HashMap<>();
////		for(Shape shape : shapes) {
////			List<TaxonomyTerm> taxonomyTermsOfShape = findTaxonomyTermShapes(shape);
////			removeTermsWhichAreAncestorsOfIncoming(termsById, taxonomyTermsOfShape);
////			addTermsWhichAreNotAncestorsOfExisting(termsById, taxonomyTermsOfShape);
////		}
//		
//		List<Geocode> geocodes = geocodeDao.getGeocodesByShapes(shapes);
//		geocodes.sort(Comparator.comparing(Geocode::getName));
//		List<String> geocodesStr = geocodes.stream().map(g -> g.getName()).collect(Collectors.toList());
//		
//		return geocodesStr;
//		
////		return termsById.values().stream().
////			filter(term -> ExceptionUtils.wrap(() -> getGeographyHierarchy(term.getGeocodeSystem())).get() != null).
////			map(term -> {
////				List<Geocode> breadcrumb = new ArrayList<>();
////				do {
////					breadcrumb.add(term);
////					term = term.getParent();
////				}while(term != null);
////				Collections.reverse(breadcrumb);
////				return breadcrumb;
////			}).
////			collect(Collectors.toMap(
////					breadcrumb -> ExceptionUtils.wrap(() -> getGeographyHierarchy(breadcrumb.get(0).getGeocodeSystem())).get().getMainHierarchy().get(0).getId(), 
////					breadcrumb -> breadcrumb));
//		
//		
//	}
//	
//	/** ?? are these going deprecated? **/
//	@Deprecated
//	private void removeTermsWhichAreAncestorsOfIncoming(Map<UUID, Geocode> termsById, List<Geocode> taxonomyTermsOfShape) {
//		termsById.keySet().removeAll(
//				termsById.values().stream().
//					filter(t -> taxonomyTermsOfShape.stream().
//							anyMatch(tts -> {
//								do {
//									if(tts.getParent() != null && tts.getParent().getId().equals(t.getId()))
//										return true;
//									tts = tts.getParent();
//								}while(tts != null);
//								return false;
//							})).
//					map(Geocode::getId).
//					collect(Collectors.toSet())
//		);
//	}
//	
//	/** ?? are these going deprecated? **/
//	@Deprecated
//	private void addTermsWhichAreNotAncestorsOfExisting(Map<UUID, Geocode> termsById,
//			List<Geocode> taxonomyTermsOfShape) {
//		termsById.putAll(
//				taxonomyTermsOfShape.stream().
//					filter(tts -> termsById.values().stream().
//									allMatch(t -> {
//										do {
//											if(t.getParent() != null && t.getParent().getId().equals(tts.getId()))
//												return false;
//											t = t.getParent();
//										}while(t != null);
//										return true;
//									})
//					).
//					collect(Collectors.toMap(Geocode::getId, x -> x))
//		);
//	}
//	
//	private List<Shape> filterBySearchType(SearchType searchType, List<Shape> shapes, Principal principal, Map<String, Project> projectShapeMappings /*out*/) throws Exception
//	{
//		List<Shape> filtered = new ArrayList<Shape>();
//		List<Project> projects = projectDao.findByCreator(principal);
//		List<Shape> projectShapes = new ArrayList<Shape>();
//		Set<UUID> filteredIds = new HashSet<UUID>();
//		for(Shape s : shapes)
//		{
//			for(Project p : projects)
//			{
//				if(s.getId().equals(p.getShape()))
//				{
//					if(p.getStatus() != ProjectStatus.DELETED)
//					{
//						projectShapeMappings.put(s.getId().toString(), p);
//						projectShapes.add(s);
//					}
//				}
//				else
//				{
//					if(!filteredIds.contains(s.getId()))
//					{
//						filtered.add(s);
//						filteredIds.add(s.getId());
//					}
//				}
//			}
//		}
//		if(searchType == SearchType.PROJECTS)
//			return projectShapes;
//		else
//			return filtered;
//	}
//	
//	private Map<String, Map<String, Attribute>> partitionAttributes(Map<String, String> attributes, GeographyHierarchy geographyHierarchy) throws Exception
//	{
//		Set<String> geographicNames = new HashSet<String>();
//		List<List<GeocodeSystem>> hier = new ArrayList<List<GeocodeSystem>>(geographyHierarchy.getAlternativeHierarchies());
//		hier.add(geographyHierarchy.getMainHierarchy());
//		for(List<GeocodeSystem> h : hier)
//		{
//			for(GeocodeSystem t : h)
//				geographicNames.add(t.getName());
//		}
//		
//		hier.add(geographyHierarchy.getMainHierarchy());
//		Map<String, String> toProcess = new HashMap<String, String>(attributes);
//		Map<String, Map<String, Attribute>> partition = new HashMap<String, Map<String, Attribute>>();
//		
//		while(!toProcess.isEmpty())
//		{
//			Set<String> toDelete = new HashSet<String>();
//			for(Map.Entry<String, String> attr : toProcess.entrySet())
//			{
//				if(geographicNames.contains(attr.getKey()))
//				{
//					toDelete.add(attr.getKey());
//					continue;
//				}
//				
//				GeocodeSystem t = taxonomyManager.findGeocodeSystemByName(attr.getKey(), false);
//				if(t == null)
//				{
//					toDelete.add(attr.getKey());
//					continue;
//				}
//				
//				TaxonomyConfig tcfg = configurationManager.retrieveTaxonomyConfigById(t.getId().toString());
//				if(tcfg == null)
//				{
//					toDelete.add(attr.getKey());
//					continue;
//				}
//				
//				List<AttributeMappingConfig> mcfgs = configurationManager.getAttributeMappingsForTermId(t.getId().toString());
//				String layer = null;
//				for(AttributeMappingConfig mcfg : mcfgs)
//				{
//					if(mcfg.getAttributeValue() == null)
//					{
//						layer = mcfg.getLayerTermId();
//						//Attribute ai = new Attribute(mcfg.getAttributeName(), mcfg.getAttributeType(), t.getId().toString(), attr.getValue());
//						break;
//					}
//				}
//				if(layer == null)
//				{
//					toDelete.add(attr.getKey());
//					continue;
//				}
//				
//				Map<String, Attribute> layerAttrs = new HashMap<String, Attribute>();
//				mcfgs = configurationManager.getMappingConfigsForLayer(layer);
//				for(AttributeMappingConfig mcfg : mcfgs)
//				{
//					if(mcfg.getTermId() == null)
//						continue;
//					
//					if(mcfg.getAttributeValue() == null)
//					{
//						GeocodeSystem mT = taxonomyManager.findGeocodeSystemById(mcfg.getTermId(), false);
//						if(mT == null)
//							continue;
//						if(!mT.getName().equals(attr.getKey()))
//							continue;
//						toDelete.add(mT.getName());
//						if(!geographicNames.contains(mT.getName()))
//						{
//							if(!layerAttrs.containsKey(mcfg.getAttributeName()))
//								layerAttrs.put(mcfg.getAttributeName(), new Attribute(mcfg.getAttributeName(), mcfg.getAttributeType(), mcfg.getTermId(), attr.getValue()));					
//						}
//					}
//				}
//				
//				if(!layerAttrs.isEmpty())
//				{
//					if(!partition.containsKey(layer))
//						partition.put(layer, new HashMap<String, Attribute>());
//					partition.get(layer).putAll(layerAttrs);
//				}
//				
//			}
//			for(String td : toDelete)
//				toProcess.remove(td);
//		}
//		
//		return partition;
//	}
//	
//	@Transactional(readOnly=true)
//	public List<GeoLocation> attributeLocate(SearchType searchType, Map<String, String> attributes, Principal principal) throws Exception {
//		GeographyHierarchy hier = getDefaultGeographyHierarchy();
//		String mostSpecificGeogTerm = null;
//		Iterator<GeocodeSystem> hierIt = hier.getMainHierarchy().iterator();
//		int i=0;
//		int tIndex = -1;
//		while(hierIt.hasNext())
//		{
//			GeocodeSystem t = hierIt.next();
//			if(attributes.containsKey(t.getName()))
//			{
//				mostSpecificGeogTerm = attributes.get(t.getName());
//				attributes.remove(t.getName());
//				tIndex =  i;
//			}
//			i++;
//		}
//		
//		int maxIndex = tIndex;
//		for(List<GeocodeSystem> alt : hier.getAlternativeHierarchies())
//		{
//			Iterator<GeocodeSystem> altIt = alt.iterator();
//			i=0;
//			int altIndex = 0;
//			while(altIt.hasNext())
//			{
//				GeocodeSystem t = altIt.next();
//				if(attributes.containsKey(t.getName()))
//				{
//					altIndex = i;
//					if(altIndex > maxIndex)
//					{
//						maxIndex = altIndex;
//						mostSpecificGeogTerm = attributes.get(t.getName());
//						altIndex = i;
//					}
//					attributes.remove(t.getName());
//				}
//				i++;
//			}
//		}
//		
//		Geocode tt = taxonomyManager.findTermByName(mostSpecificGeogTerm, false);
//		Shape shapeTerm = taxonomyManager.getShapeOfTerm(tt);
//		
//		Map<String, Project> projectShapeMappings = new HashMap<String, Project>();
//		List<Shape> foundShapes = new ArrayList<Shape>();
//		Map<String, Map<String, Attribute>> partition = partitionAttributes(attributes, hier);
//		/*if(searchType == SearchType.MAP && partition.isEmpty())
//			throw new Exception("No attributes were specified");*/
//		
//		if(!partition.isEmpty())
//		{
//			Map.Entry<String, Map<String, Attribute>> first = partition.entrySet().iterator().next();
//			foundShapes = shapeDao.searchShapesWithinByAttributes(first.getValue(), shapeTerm);
//			foundShapes = filterBySearchType(searchType, foundShapes, principal, projectShapeMappings);
//			partition.remove(first.getKey());
//		}else
//		{
//			foundShapes = shapeDao.searchShapesWithinByAttributes(new HashMap<String, Attribute>(), shapeTerm);
//			foundShapes = filterBySearchType(searchType, foundShapes, principal, projectShapeMappings);
//		}
//		
//		for(Map.Entry<String, Map<String, Attribute>> layerAttrs : partition.entrySet())
//		{
//			List<Shape> res = shapeDao.searchShapesWithinByAttributes(layerAttrs.getValue(), shapeTerm);
//			Map<String, Shape> toAdd = new HashMap<String, Shape>();
//			for(Shape rs : res)
//			{
//				for(Shape fs : foundShapes)
//				{
//					if(shapeDao.within(fs, rs))
//					{
//						if(!toAdd.containsKey(fs.getId().toString()))
//							toAdd.put(fs.getId().toString(), fs);
//					}
//					else if(shapeDao.within(rs, fs))
//					{
//						if(!toAdd.containsKey(rs.getId().toString()))
//							toAdd.put(rs.getId().toString(), rs);
//					}
//				}
//			}
//			foundShapes = new ArrayList<Shape>(toAdd.values());
//			foundShapes = filterBySearchType(searchType, foundShapes, principal, projectShapeMappings);
//		}
//		
//		List<GeoLocation> res = new ArrayList<GeoLocation>();
//		
//		for(Shape s : foundShapes)
//		{
//			/*boolean nonGeographic = false;
//			List<TaxonomyTermShape> ttss = taxonomyTermShapeDao.findByShape(s);
//			for(TaxonomyTermShape tts : ttss)
//			{
//				Taxonomy tax = tts.getTerm().getTaxonomy();
//				if(tax.getExtraData() == null || tax.getExtraData().isEmpty() || !tax.getExtraData().contains("geographic=\"true\""))
//				{
//					nonGeographic = true;
//					break;
//				}
//			}
//			if(nonGeographic == false && ttss.size() > 0)
//				continue;*/
//			Point centroid = s.getGeography().getCentroid();
//			List<Geocode> terms = geoLocate(centroid.getX(), centroid.getY());
//			if(terms == null || terms.isEmpty()) continue;
//			
//			List<GeoLocationTag> tags = new ArrayList<GeoLocationTag>();
//			for(Geocode t : terms)
//			{
////				Shape tts = geocodeShapeDao.findUniqueByGeocode(t).getShape();
////				Point ttsCentroid = tts.getGeography().getCentroid();
////				AttributeInfo tagInfo = retrieveShapeAttributeByTaxonomy(geocodeShapeDao.findUniqueByGeocode(t).getShape(), t.getGeocodeSystem().getId().toString());
////				GeocodeSystem tax = taxonomyManager.findGeocodeSystemById(tagInfo.getTaxonomy(), false);
////				Geometry b = tts.getGeography().getEnvelope();
////				Bounds bounds = new Bounds(b.getCoordinates()[0].x, b.getCoordinates()[0].y, b.getCoordinates()[2].x, b.getCoordinates()[2].y, null);
////				tags.add(new GeoLocationTag(t.getId().toString(), tagInfo.getValue(), tax.getId().toString(), tax.getName(), ttsCentroid.getX(), ttsCentroid.getY(), bounds));
//			}
//			Geometry b = s.getGeography().getEnvelope();
//			Bounds bounds = new Bounds(b.getCoordinates()[0].x, b.getCoordinates()[0].y, b.getCoordinates()[2].x, b.getCoordinates()[2].y, null);
//			if(searchType == SearchType.MAP)
//				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds));
//			else if(searchType == SearchType.PROJECTS)
//			{
//				Project shapeProject = projectShapeMappings.get(s.getId().toString());
//				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds, shapeProject.getName(), shapeProject.getId().toString()));
//			}
//		}
//		return res;
//	}
//	
//	@Override
//	public List<Shape> getShapesOfLayerID(UUID layerID) throws Exception {
//		return shapeDao.findShapesOfLayer(layerID);
//	}
//
//	
//	@Override
//	public List<ShapeMessenger> getShapeMessengerForLayer(UUID layerID) throws Exception {
//		
//		List<Shape> shapes = shapeDao.findShapesOfLayer(layerID);
//		
//		if(shapes == null) return null;
//		
//		//getTermDetails(tt);
//		
//		List<ShapeMessenger> res = new ArrayList<ShapeMessenger>();
//		for(Shape s : shapes)
//		{
//			//getShapeDetails(s);
//			ShapeMessenger sm = new ShapeMessenger();
//			sm.setId(s.getId().toString());
//			sm.setCode(s.getCode());
//			sm.setExtraData(s.getExtraData());
//			sm.setGeometry(s.getGeography().toText());
//			if(s.getShapeImport() != null)
//				sm.setImportId(s.getShapeImport().toString());
//			sm.setName(s.getName());
//			sm.setShapeClass(s.getShapeClass());
//			Layer layer = layerDao.getLayerById(layerID);
//			
//			if(layer != null)
//			{
//				sm.setLayerId(layer.getId().toString());
//				if(layer.getGeocodeSystem()!=null)
//					sm.setLayerGeocodeSystem(layer.getGeocodeSystem().getName());
//			}
//			res.add(sm);
//		}
//		return res;		
//	}
//	
//	@Override
//	public List<ShapeInfo> getShapeInfoForLayer(UUID layerID) throws Exception {
//		List<Shape> shapes = shapeDao.findShapesOfLayer(layerID);
//		
//		if(shapes == null) return null;
//		
//		//getTermDetails(tt);
//		
//		List<ShapeInfo> res = new ArrayList<ShapeInfo>();
//		for(Shape s : shapes)
//		{
//			//getShapeDetails(s);
//			ShapeInfo si = new ShapeInfo();
//			si.setShape(s);
//			si.setLayerID(layerID);
//			res.add(si);
//		}
//		return res;	
//		
//	}
//
//	@Override
//	public Map<String, Shape> getShapesOfLayer(Layer layer) throws Exception {
//		List<Geocode> geocodes = geocodeDao.findByGeocodeSystem(layer.getGeocodeSystem());
//
//		Map<String, Shape> shapes = new HashMap<>();		
//		for(Geocode geocode : geocodes){
//			shapes.put(geocode.getName(), geocode.getShape());
//		}
//		
//		return shapes;
//	}
//	
//	@Override
//	@Transactional
//	public WfsShapeInfo getShapesFromShapefile(String pathName, String termId, int srid, String charset, boolean forceLonLat, 
//			Map<String, Map<String,AttributeInfo>> attrInfo, Principal principal, boolean forceOverwriteMappings) throws Exception {
//		
//		if(srid < 0 && srid != -1) throw new IllegalArgumentException("Illegal srid code");
//		if(principal == null) throw new IllegalArgumentException("Creator not provided");
//
//		Map<String, String> map = new HashMap<String, String>();
//
//		File file = new File(pathName);
//
//		map.put("url", file.toURI().toString());
//		map.put("charset", charset);
//		
//		SimpleFeatureSource featureSource = null;
//		DataStore dataStore = null;
//		try 
//		{
//			dataStore = DataStoreFinder.getDataStore(map);
//			featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
//		} 
//		catch (IOException e) 
//		{
//			log.error("Error while reading shape file", e);
//			throw e;
//		}
//		
//		
//		return fromFeatureSource(dataStore, featureSource, termId, srid, forceLonLat, attrInfo, principal, forceOverwriteMappings);
//
//	}
//	
//	@Transactional
//	private WfsShapeInfo fromFeatureSource(DataStore dataStore, SimpleFeatureSource featureSource, 
//			String termId, int srid, boolean forceLonLat, Map<String, Map<String,AttributeInfo>> attrInfo, 
//			Principal principal, boolean forceOverwriteMappings) throws Exception {
//		
//		Map<String, GeocodeSystem> taxonomyCache = new HashMap<String, GeocodeSystem>();
//		SimpleFeatureCollection collection = featureSource.getFeatures();
//		SimpleFeatureType schema = featureSource.getSchema();
//		SimpleFeatureIterator iterator = collection.features();
//		WfsShapeInfo wfsShapeInfo = new WfsShapeInfo();
//		List<Shape> listShape = new ArrayList<Shape>();
//		
//		// EPSG:GGRS87 / Greek Grid - instead of  2100
//		// GCS_WGS_1984 / EPSG:2100
//		
//		String sourceCode = null, targetCode;
//		
//		if(srid != -1) sourceCode = "EPSG:" + new Integer(srid).toString();
//		targetCode = "EPSG:4326";
//		
//		CoordinateReferenceSystem sourceCRS = null;
//		if(schema.getCoordinateReferenceSystem() != null) sourceCRS = schema.getCoordinateReferenceSystem();
//		else if(sourceCode != null) sourceCRS = CRS.decode(sourceCode);
//		
//		if(sourceCRS == null) throw new Exception("No coordinate system provided nor found in shape file definition");
//		
//		CoordinateReferenceSystem targetCRS = CRS.decode(targetCode, forceLonLat);
//		
//        UUID importUUID = UUIDGenerator.randomUUID();
//        
//        boolean lenient = false;
//        String wkt = sourceCRS.toWKT();
//        if(!wkt.toLowerCase().contains("towgs"))
//        {
//        	if(CRS.lookupEpsgCode(sourceCRS, true) == 2100) //Greek Grid)
//        	{
//        		double[] bursaWolf = {-199.87, 74.79, 246.62, 0, 0, 0, 0};
//        		log.warn("No transformation parameters were found within source CRS data." + 
//        				 "Automatically applying: " + Arrays.toString(bursaWolf));
//        		wkt = insertBursaWolfToWKT(wkt, bursaWolf);
//        		sourceCRS = CRS.parseWKT(wkt);
//        	}
//        	else
//        	{
//        		log.warn("No transformation parameters were found within source CRS data. Transformation may contain errors");
//        		lenient = true;
//        	}
//        }
//        
//        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, lenient);
//        
//        Envelope b = featureSource.getBounds();
//        b = JTS.transform(b, transform);
//        Bounds bounds = new Bounds();
//        bounds.setCrs("EPSG:4326");
//        bounds.setMinx(b.getMinX());
//        bounds.setMiny(b.getMinY());
//        bounds.setMaxx(b.getMaxX());
//        bounds.setMaxy(b.getMaxY());
//        wfsShapeInfo.setBounds(bounds);
//        
//    	Geometry g = null;
//    	
//		try {
//		   //speed up updates by executing them only when needed.
//	       //assumes that mappings are not different among features (features can
//	       //contain different subset of attributes, but when a mapping is present, it
//	       //is assumed to be the same as the corresponding mapping of all other features)
//		   //this assumption is valid and does not pose any limitations to the mapping
//		   //configuration of the system, which only supports dataset-wide mappings as well
//			Map<String, Map<String, AttributeMappingConfig>> cfgCache = new HashMap<String, Map<String, AttributeMappingConfig>>(); 
//			 
//			int cnt=0;
//			while (iterator.hasNext()) 
//			{
//				// read a shape file feature
//				SimpleFeature feature = iterator.next();
//				
//				// get its geometry
//				g = (Geometry) feature.getDefaultGeometry();
//
//				g = JTS.transform( g, transform);
//				g.setSRID(4326);
//				
//
//				String data = createDataXML(feature, attrInfo, taxonomyCache, termId, cfgCache, forceOverwriteMappings);
//				
//				Shape shape = new Shape();
//
//				shape.setCreationDate(Calendar.getInstance().getTime());
//				shape.setCreatorID(principal.getId());
//
//				shape.setExtraData(data);
//				shape.setId(UUIDGenerator.randomUUID());
//				shape.setLastUpdate(Calendar.getInstance().getTime());
//				shape.setLayerID(UUID.fromString(termId));
//				shape.setGeography(g);
//				shape.setShapeClass(1);
//				shape.setName(importUUID+"_"+(cnt++));
//				
//				listShape.add(shape);
//				
//			}
//			wfsShapeInfo.setListShape(listShape);
//		} 
//		finally 
//		{
//			iterator.close();
//		}
//			
//		return wfsShapeInfo;
//	}
//	
//	private String createDataXML(SimpleFeature feature, Map<String, Map<String,AttributeInfo>> attrInfo, Map<String, GeocodeSystem> taxonomyCache, String layerTermId,
//			Map<String, Map<String, AttributeMappingConfig>> cfgCache, boolean forceOverwriteMappings) throws Exception
//	{
//		StringBuilder xml = new StringBuilder();
//		xml.append("<extraData>");
//		List <AttributeType> types = feature.getType().getTypes();
//		
//		for(AttributeType t : (List<AttributeType>) types) {
//			Object val = feature.getAttribute(t.getName());
//			if(val != null)
//			{
//				String type = null;
//				if(attrInfo == null) type = "double";
//				else type = attrInfo.get(t.getName().toString()).get("").getType();
//				boolean setTaxonomy = false;
//				boolean setValue = false;
//				String taxonomyId = null;
//				String layerId = null;
//				String attrValue = null;
//				Boolean presentable = true;
//				Boolean mapValue = true;
//				if(attrInfo != null)
//				{
//					
//					AttributeInfo ai = attrInfo.get(t.getName().toString()).get(val);
//					if(!val.equals("") && attrInfo.get(t.getName().toString()).get(val.toString()) != null)
//					{
//						setValue = true;
//					}else
//					{
//						if(attrInfo.get(t.getName().toString()).get("").isStore() == false)
//							continue; //ignore attribute that is marked as non-storeable
//						presentable = attrInfo.get(t.getName().toString()).get("").isPresentable();
//					}
//
//				}
//				
//				AttributeMappingConfig mcfg = new AttributeMappingConfig();
//				mcfg.setAttributeName(t.getName().toString());
//				mcfg.setAttributeType(type);
//				mcfg.setLayerTermId(layerTermId);
//				mcfg.setPresentable(presentable);
//				
//			
//				if(setValue)
//				{
//					mcfg.setAttributeValue(attrValue);
//					mcfg.setMapValue(mapValue);
//					mcfg.setTermId(layerId);
//				}			
//				
//				addMappingConfig(mcfg, cfgCache);
//				
//				String processedVal = HtmlUtils.htmlEscape(discardIllegalValues(type, feature.getAttribute(t.getName()).toString().trim()));
//				
//				xml.append("<"+t.getName() + " type=\"" + type + "\" " + 
//						(setTaxonomy ? "geocodeSystem=\""+taxonomyId+"\" " : "") + (setValue ? ("layer=\""+layerId+"\""): "") + ">"); 
//				xml.append(processedVal);
//				xml.append("</"+t.getName()+">");
//			}
//		}
//		xml.append("</extraData>");
//		
//		return xml.toString();
//	}
//	
//	private String insertBursaWolfToWKT(String wkt, double[] bursaWolf)
//	{
//		String[] defs = wkt.split("DATUM\\[");
//		if(defs.length != 2)
//		{
//			log.warn("Could not insert Bursa-Wolf Parameters to CRS WKT");
//			return wkt;
//		}
//		int bracketCount = 1;
//		int index = 0;
//		int prevClose = -1;
//		while(bracketCount != 0)
//		{
//			int close = defs[1].indexOf(']', index);
//			if(prevClose == -1) prevClose = close;
//			int open = defs[1].indexOf('[', index);
//			if(close == -1)
//			{
//				log.warn("Invalid wkt");
//				return null;
//			}
//			if(open < close)
//			{
//				bracketCount++;
//				index = open + 1;
//			}else
//			{
//				bracketCount--;
//				index = close + 1;
//			}
//			if(bracketCount != 0) prevClose = close;
//		}
//		
//		StringBuilder formattedBursaWolf = new StringBuilder();
//		for(int i=0; i<bursaWolf.length; i++)
//		{
//			formattedBursaWolf.append(String.format(Locale.US, "%.2f",  bursaWolf[i]));
//			if(i != bursaWolf.length-1) formattedBursaWolf.append(", ");
//		}
//		
//		String res = defs[0] + "DATUM[" +
//				 defs[1].substring(0, prevClose+1) +
//				", TOWGS84[" + formattedBursaWolf.toString() + "]" +
//				defs[1].substring(prevClose+1);
//		return res;
//	}
//	
//	private String discardIllegalValues(String type, String value)
//	{
//		try
//		{
//			if(type.equals("short"))
//				Short.parseShort(value);
//			else if(type.equals("integer"))
//				Integer.parseInt(value);
//			else if(type.equals("long"))
//				Long.parseLong(value);
//			else if(type.equals("float"))
//				Float.parseFloat(value);
//			else if(type.equals("double"))
//				Double.parseDouble(value);
//		}catch(NumberFormatException e)
//		{
//			return "";
//		}
//		return value;
//	}
//	
//}
