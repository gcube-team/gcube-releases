package gr.cite.gaap.servicelayer;

import gr.cite.commons.util.datarepository.DataRepository;
import gr.cite.commons.util.datarepository.elements.RepositoryFile;
import gr.cite.gaap.datatransferobjects.AttributeInfo;
import gr.cite.gaap.datatransferobjects.Coords;
import gr.cite.gaap.datatransferobjects.GeoLocation;
import gr.cite.gaap.datatransferobjects.GeoLocationTag;
import gr.cite.gaap.datatransferobjects.NewProjectData;
import gr.cite.gaap.datatransferobjects.GeoSearchSelection.SearchType;
import gr.cite.gaap.geospatialbackend.GeospatialBackend;
import gr.cite.gaap.utilities.ExceptionUtils;
import gr.cite.gaap.utilities.StringUtils;
import gr.cite.geoanalytics.common.ShapeAttributeDataType;
import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.project.Project.ProjectStatus;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDao;
import gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape.Attribute;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeDocument;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeImport;
import gr.cite.geoanalytics.dataaccess.entities.shape.ShapeTerm;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDocumentDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeTermDao;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.global.TaxonomyConfig.Type;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.mapping.AttributeMappingConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyTermDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyTermLinkDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyTermShapeDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.definition.TaxonomyData;
import gr.cite.geoanalytics.dataaccess.entities.user.dao.UserDaoOld;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

@Service
@Primary
public class ShapeManager implements GeospatialBackend {
	private static final Logger log = LoggerFactory.getLogger(ShapeManager.class);
	
	protected TaxonomyManager taxonomyManager;
	private DocumentManager documentManager;
	private DataRepository repository;
	protected ConfigurationManager configurationManager;
	private ShapeImportManager shapeImportManager;
	
	private ShapeDao shapeDao;
	private ShapeTermDao shapeTermDao;
	private ShapeDocumentDao shapeDocumentDao;
	private TaxonomyDao taxonomyDao;
	private TaxonomyTermDao taxonomyTermDao;
	private TaxonomyTermLinkDao taxonomyTermLinkDao;
	private TaxonomyTermShapeDao taxonomyTermShapeDao;
	private ProjectDao projectDao;
	private PrincipalDao principalDao;
	
	private static final String NoMappingKey = "\t\t\t__NoVal__\t\t\t";
	private static final String NoValueKey = "";
	
	public class GeographyHierarchy {
		private List<Taxonomy> mainHierarchy = null;
		private List<List<Taxonomy>> alternativeHierarchies = new ArrayList<List<Taxonomy>>();
		
		public List<Taxonomy> getMainHierarchy() {
			return mainHierarchy;
		}
		
		public void setMainHierarchy(List<Taxonomy> mainHierarchy) {
			this.mainHierarchy = mainHierarchy;
		}
		
		public List<List<Taxonomy>> getAlternativeHierarchies() {
			return alternativeHierarchies;
		}
		
		public void setAlternativeHierarchies(List<List<Taxonomy>> alternativeHierarchies) {
			this.alternativeHierarchies = alternativeHierarchies;
		}
		
		public void addAlternativeHierarchy(List<Taxonomy> hierarchy) {
			this.alternativeHierarchies.add(hierarchy);
		}
	}
	
	private class TermLinkInfo {
		public String verb = null;
		public Map<TaxonomyTerm, TaxonomyTerm> links = null;
		
		public TermLinkInfo(String verb)
		{
			this.links = new HashMap<TaxonomyTerm, TaxonomyTerm>();
			this.verb = verb;
		}
	}
	
//	public ShapeManager() { }
	
	private void getShapeDetails(Shape s) {
		if(s.getShapeImport() != null) s.getShapeImport().getShapeImport();
		s.getCreator().getPrincipalData().getFullName();
	}
	
	private void getTermDetails(TaxonomyTerm tt)
	{
		if(tt.getParent() != null) tt.getParent().getTaxonomy();
		if(tt.getTaxonomyTermClass() != null) tt.getTaxonomyTermClass().getTaxonomy();
		tt.getTaxonomy().getName();
	}
	
	@Inject
	public ShapeManager(PrincipalDao principalDao, TaxonomyManager taxonomyManager, 
			DocumentManager documentManager, DataRepository repository,
			ConfigurationManager configurationManager) {
		this.principalDao = principalDao;
		this.taxonomyManager = taxonomyManager;
		this.documentManager = documentManager;
		this.repository = repository;
		this.configurationManager = configurationManager;
	}
	
	@Inject
	public void setShapeImportManager(ShapeImportManager shapeImportManager) {
		this.shapeImportManager = shapeImportManager;
	}
	
	@Inject
	public void setShapeDao(ShapeDao shapeDao) {
		this.shapeDao = shapeDao;
	}
	
	@Inject
	public void setShapeTermDao(ShapeTermDao shapeTermDao) {
		this.shapeTermDao = shapeTermDao;
	}
	
	@Inject
	public void setShapeDocumentDao(ShapeDocumentDao shapeDocumentDao) {
		this.shapeDocumentDao = shapeDocumentDao;
	}
	
	@Inject
	public void setTaxonomyDao(TaxonomyDao taxonomyDao) {
		this.taxonomyDao = taxonomyDao;
	}
	
	@Inject
	public void setTaxonomyTermDao(TaxonomyTermDao taxonomyTermDao) {
		this.taxonomyTermDao = taxonomyTermDao;
	}
	
	@Inject
	public void setTaxonomyTermLinkDao(TaxonomyTermLinkDao taxonomyTermLinkDao) {
		this.taxonomyTermLinkDao = taxonomyTermLinkDao;
	}
	
	@Inject
	public void setTaxonomyTermShapeDao(TaxonomyTermShapeDao taxonomyTermShapeDao) {
		this.taxonomyTermShapeDao = taxonomyTermShapeDao;
	}
	
	@Inject
	public void setProjectDao(ProjectDao projectDao) {
		this.projectDao = projectDao;
	}
	
	@Transactional(readOnly = true)
	public Shape findShapeById(UUID id) throws IOException {
		return shapeDao.read(id);
	}
	
	@Transactional(readOnly = true)
	public ShapeInfo findShapeByIdInfo(UUID id) throws Exception {
		Shape s = shapeDao.read(id);
		getShapeDetails(s);
		ShapeInfo si = new ShapeInfo();
		si.setShape(s);
		TaxonomyTerm t = shapeDao.findTermOfShape(s);
		if(t != null) getTermDetails(t);
		si.setTerm(t);
		
		return si;
	}
	
	public String retrieveShapeAttributeValue(Shape s, String attribute) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
		
		NodeList els = d.getElementsByTagName(attribute);
		if(els == null || els.getLength() == 0)
			return null;
		
		Element el = (Element)els.item(0);
		return el.getFirstChild().getNodeValue();
	}
	
	public AttributeInfo retrieveShapeAttribute(Shape s, String attribute) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
		
		NodeList els = d.getElementsByTagName(attribute);
		if(els == null || els.getLength() == 0)
			return null;
		
		Element el = (Element)els.item(0);
		AttributeInfo ai = new AttributeInfo();
		ai.setName(attribute);
		ai.setValue(el.getFirstChild().getNodeValue());
		ai.setTaxonomy(el.getAttribute("taxonomy"));
		ai.setType(ShapeAttributeDataType.valueOf(el.getAttribute("type").toUpperCase()).toString());
		ai.setPresentable(Boolean.valueOf(el.getAttribute("presentable")));
		return ai;
	}
	
	@Transactional(readOnly = true)
	public AttributeInfo retrieveShapeAttributeByTaxonomy(Shape s, String taxonomy) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
		
		NodeList els = d.getElementsByTagName("extraData").item(0).getChildNodes();
		for(int i=0; i<els.getLength(); i++)
		{
			Element el = (Element)els.item(i);
			if(el.getAttribute("taxonomy").equals(taxonomy))
			{
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
	private Map<String, AttributeInfo> retrieveRawShapeAttributes(Shape s) throws Exception
	{
		Map<String, AttributeInfo> attributes = new HashMap<String, AttributeInfo>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
		
		NodeList els = d.getElementsByTagName("extraData").item(0).getChildNodes();
		for(int i=0; i<els.getLength(); i++)
		{
			Element el = (Element)els.item(i);
			
			String taxon = el.getAttribute("taxonomy");
			if(taxon == null || taxon.trim().isEmpty())
				continue;
			AttributeInfo ai = new AttributeInfo();
			ai.setName(el.getNodeName());
			if(el.getFirstChild() != null) ai.setValue(el.getFirstChild().getNodeValue());
			ai.setTaxonomy(taxon);
			ai.setType(ShapeAttributeDataType.valueOf(el.getAttribute("type").toUpperCase()).toString());
			ai.setPresentable(Boolean.valueOf(el.getAttribute("presentable")));
			attributes.put(taxon, ai);
		}
		return attributes;
	}
	
	@Transactional
	public void addShapeAttribute(Shape s, String attrName, String attrValue, Taxonomy taxonomy) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
		
		Element root = d.getDocumentElement();
		NodeList els = d.getElementsByTagName(attrName);
		if(els != null && els.getLength() != 0)
			throw new Exception("Attribute " + attrName + " already exists");
		
		Element el = d.createElement(attrName);
		el.setAttribute("type", ShapeAttributeDataType.STRING.toString());
		
		if (taxonomy != null){
			el.setAttribute("taxonomy", taxonomy.getId().toString());
		}
		el.appendChild(d.createTextNode(attrValue));
		root.appendChild(el);
		
		s.setExtraData(transformDocToString(d));
		
		shapeDao.update(s);
	}
	
	private String transformDocToString(org.w3c.dom.Document document) throws TransformerException{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		return writer.getBuffer().toString().replaceAll("\n|\r", "");
	}
	
	@Transactional
	public void setShapeAttributes(Shape s, Map<String, AttributeInfo> attrs) throws Exception
	{
		StringBuilder xml = new StringBuilder();
		xml.append("<extraData>");
		for(Map.Entry<String, AttributeInfo> attrE : attrs.entrySet())
		{
			AttributeInfo attr = attrE.getValue();
			xml.append("<"+attr.getName() + " type=\"" + ShapeAttributeDataType.valueOf(attr.getType().toUpperCase()).toString() + "\" " + 
					"taxonomy=\""+attr.getTaxonomy()+"\" " + (attr.getTerm() != null ? "term=\""+attr.getTerm()+"\"" : "") +">"); 
			xml.append(attr.getValue());
			xml.append("</"+attr.getName()+">");
		}
		xml.append("</extraData>");
		
		s.setExtraData(xml.toString());
		
		shapeDao.update(s);
	}
	
	@Transactional
	public void updateShapeAttribute(Shape s, String attrName, String attrValue) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
		
		NodeList els = d.getElementsByTagName(attrName);
		if(els == null || els.getLength() == 0)
			throw new Exception("Attribute " + attrName + " not found");
		
		Element el = (Element)els.item(0);
		el.getFirstChild().setNodeValue(attrValue);
		
		shapeDao.update(s);
	}
	
	@Transactional
	public void removeShapeAttribute(Shape s, String attrName) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
		
		NodeList els = d.getElementsByTagName(attrName);
		if(els == null || els.getLength() == 0)
			throw new Exception("Attribute " + attrName + " not found");
		
		Element el = (Element)els.item(0);
		d.removeChild(el);
		
		shapeDao.update(s);
	}
	
	@Override
	@Transactional
	public Set<String> getAttributeValuesOfShapesByTerm(TaxonomyTerm layerTerm, Attribute attr) throws Exception {
		TaxonomyTerm tt = taxonomyManager.findTermById(layerTerm.getId().toString(), false);
		if(tt == null)
			throw new Exception("Taxonomy term " + layerTerm.getId() + " not found");
		return shapeDao.getAttributeValuesOfShapesByTerm(tt, attr);
	}
	
	private Map<String, AttributeInfo> filterAttributes(Map<String, AttributeInfo> attributes, List<String> taxonomies)
	{
		Map<String, AttributeInfo> filteredAttrs = new HashMap<String, AttributeInfo>();
		for(Map.Entry<String, AttributeInfo> aie : attributes.entrySet())
		{
			for(String t : taxonomies)
			{
				if(aie.getValue().getTaxonomy().equals(t))
				{
					filteredAttrs.put(aie.getKey(), aie.getValue());
					break;
				}
			}
		}
		return filteredAttrs;
	}
	
	private void linkAttributeDocument(Shape s, TaxonomyTermShape tts) throws Exception
	{
		gr.cite.geoanalytics.dataaccess.entities.document.Document d = shapeDocumentDao.findUniqueByTaxonomyTermShape(tts);
		if(d != null)
		{
			TaxonomyTermShape thisTts = taxonomyTermShapeDao.find(tts.getTerm(), s);
			if(thisTts == null)
			{
				thisTts = new TaxonomyTermShape();
				thisTts.setCreator(principalDao.systemPrincipal());
				thisTts.setShape(s);
				thisTts.setTerm(tts.getTerm());
				taxonomyTermShapeDao.create(thisTts);
			}
			
			gr.cite.geoanalytics.dataaccess.entities.document.Document thisD = 
					shapeDocumentDao.findUniqueByTaxonomyTermShape(thisTts);
			if(thisD == null)
			{
				ShapeDocument sd = new ShapeDocument();
				sd.setCreator(principalDao.systemPrincipal());
				sd.setTaxonomyTermShape(thisTts);
				sd.setDocument(d);
				shapeDocumentDao.create(sd);
			}
		}
	}
	
	@Transactional(readOnly = true)
	public Map<String, AttributeInfo> consolidateAttributes(Shape s) throws Exception
	{
		Map<String, AttributeInfo> attrs = new HashMap<String, AttributeInfo>();
		
		Set<TaxonomyConfig> extraTaxonIds = new HashSet<TaxonomyConfig>();
		List<TaxonomyConfig> infoCategories = configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.PROJECTINFOCATEGORYTAXONOMY);
		for(TaxonomyConfig infoCfg : infoCategories)
			extraTaxonIds.addAll(configurationManager.retrieveTaxonomyConfigByClass(TaxonomyConfig.Type.valueOf(infoCfg.getType())));
		
		Point centroid = s.getGeography().getCentroid();
		List<TaxonomyTerm> geoLocation = geoLocate(centroid.getX(), centroid.getY());
		
		String geographyTaxonId = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY).get(0).getId();
		
		Set<TaxonomyTerm> attrTerms = new HashSet<TaxonomyTerm>();
		
		TaxonomyTerm tt = null;
		Iterator<TaxonomyTerm> ttIt = geoLocation.iterator();
		List<String> attrTaxonomies = new ArrayList<String>();
		for(TaxonomyConfig tcfg : extraTaxonIds)
			attrTaxonomies.add(tcfg.getId());
		
		//geographic info
		boolean foundWidestGeographyTerm = false;
		while(ttIt.hasNext()) //geoLocate returns terms by geographic order, e.g. country->prefecture->...
		{
			tt = ttIt.next();
			if(tt.getTaxonomy().getId().toString().equals(geographyTaxonId))
				foundWidestGeographyTerm = true; //found the term representing the widest area in which there exist useful attributes
			
			List<TaxonomyTerm> linkedTerms = taxonomyTermDao.getActiveLinkedTerms(tt, TaxonomyTermLink.Verb.AttrFor);
			attrTerms.addAll(linkedTerms);
			List<TaxonomyTerm> toCheck = new ArrayList<TaxonomyTerm>(linkedTerms);
			while(true)
			{
				List<TaxonomyTerm> descendants = new ArrayList<TaxonomyTerm>();
				for(TaxonomyTerm tc : toCheck)
					descendants.addAll(taxonomyManager.getChildrenOfTerm(tc.getId().toString(), true, false));
				attrTerms.addAll(descendants);
				toCheck = descendants;
				if(descendants.isEmpty())
					break;
			}
			
		/*	for(TaxonomyTerm lt : linkedTerms)
				attrTaxonomies.add(lt.getTaxonomy().getName());*/
			if(foundWidestGeographyTerm == true)
				attrTaxonomies.add(tt.getTaxonomy().getId().toString()); //geographic info
		}
		
		ttIt = geoLocation.iterator();
		foundWidestGeographyTerm = false;
		while(ttIt.hasNext())
		{
			tt = ttIt.next();
			if(tt.getTaxonomy().getId().toString().equals(geographyTaxonId))
				foundWidestGeographyTerm = true;
			if(foundWidestGeographyTerm == false)
				continue;
			TaxonomyTermShape tts = taxonomyTermShapeDao.findUniqueByTerm(tt);
			if(tts != null)
			{
				Map<String, AttributeInfo> shapeAttrs = retrieveRawShapeAttributes(tts.getShape());
				Map<String, AttributeInfo> filteredAttrs = filterAttributes(shapeAttrs, attrTaxonomies);
				attrs.putAll(filteredAttrs);
				for(AttributeInfo fa : filteredAttrs.values())
				{
					String termIdStr = null;
					if(fa.getTerm() != null)
						termIdStr = fa.getTerm();
					else
					{
						List<AttributeMappingConfig> valCfgs = configurationManager.getAttributeMappings(fa.getName(), fa.getValue());
						if(valCfgs != null)
						{
							for(AttributeMappingConfig valCfg : valCfgs)
							{
								if(valCfg.getTermId() != null)
								{
									termIdStr = valCfg.getTermId();
									break;
								}
							}
						}
					}
					if(termIdStr == null) continue;
					TaxonomyTerm attrT = taxonomyManager.findTermById(termIdStr, false);
					List<TaxonomyTermShape> attrTtss = taxonomyTermShapeDao.findNonProjectByTerm(attrT);
					TaxonomyTermShape attrTts = null;
					if(attrT != null)
					{
						for(TaxonomyTermShape aTts : attrTtss)
						{
							if(shapeDao.within(s, aTts.getShape()))
							{
								attrTts = aTts;
								break;		
							}
						}
						/*try
						{
							attrTts = taxonomyTermShapeDao.findUByTerm(attrT);
						}catch(NonUniqueResultException e)
						{
							log.error("Non unique tts");
							throw e;
						}*/
					}
					if(attrTts != null)
						linkAttributeDocument(s, attrTts);
				}
			}
		}
		for(TaxonomyTerm t : attrTerms)
		{
			List<TaxonomyTermShape> ttss = taxonomyTermShapeDao.findByTerm(t);
			TaxonomyTermShape tts = null;
			for(TaxonomyTermShape ttShape : ttss)
			{
				if(shapeDao.within(s, ttShape.getShape()))
				{
					tts = ttShape;
					break;
				}
			}
			if(tts != null)
			{
				AttributeInfo attr = retrieveShapeAttributeByTaxonomy(tts.getShape(), t.getTaxonomy().getId().toString());
				attrs.put(attr.getTaxonomy(), attr);
				
				linkAttributeDocument(s, tts);
			}
		}
		
		return attrs;
		
	}
	
	@Transactional(readOnly = true)
	public Map<String, AttributeInfo> computeAttributes(Shape s) throws Exception
	{
		Map<String, AttributeInfo> attrs = new HashMap<String, AttributeInfo>();
		
		Point centroid = s.getGeography().getCentroid();
		
		AttributeInfo ai = new AttributeInfo();
		ai.setName("location");
		ai.setPresentable(true);
		ai.setTaxonomy(configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.LOCATIONTAXONOMY).get(0).getId());
		ai.setType(ShapeAttributeDataType.STRING.toString());
		ai.setValue(centroid.getX() + "," + centroid.getY());
		attrs.put(TaxonomyConfig.Type.LOCATIONTAXONOMY.toString(), ai);
		
		//TODO hibernate spatial area
		double area = shapeDao.area(s);
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
	public Map<String, AttributeInfo> retrieveShapeAttributes(Shape s) throws Exception
	{
		Map<String, AttributeInfo> res = new HashMap<String, AttributeInfo>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		if(s.getExtraData() == null || s.getExtraData().trim().isEmpty())
			return res;
		org.w3c.dom.Document d = db.parse(new ByteArrayInputStream(s.getExtraData().getBytes("UTF-8")));
		
		GeographyHierarchy hier = getDefaultGeographyHierarchy();
		
		NodeList els = d.getChildNodes().item(0).getChildNodes();
		for(int i=0; i<els.getLength(); i++)
		{
			String geogTaxonomyName = null;
			String geogTaxonomyId = null;
			Element el = (Element)els.item(i);
			String taxonomyId = el.getAttribute("taxonomy");
			if(taxonomyId == null || taxonomyId.trim().isEmpty())
				continue;
			TaxonomyConfig tcfg = configurationManager.retrieveTaxonomyConfigById(taxonomyId, true);
			if(tcfg != null && (tcfg.getType().equals(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY.toString()) ||
					tcfg.getType().equals(TaxonomyConfig.Type.ALTGEOGRAPHYTAXONOMY.toString())))
				tcfg = null; //trigger geographic mode for those taxonomies that mappings are present
			if(tcfg == null)
			{
				boolean foundGeog = false;
				for(Taxonomy geogT : hier.getMainHierarchy())
				{
					if(geogT.getId().toString().equals(taxonomyId))
					{
						foundGeog = true;
						geogTaxonomyName = geogT.getName();
						geogTaxonomyId = geogT.getId().toString();
						break;
					}
				}
				if(!foundGeog)
				{
					for(List<Taxonomy> altHier : hier.getAlternativeHierarchies())
					{
						for(Taxonomy geogT : altHier)
						{
							if(geogT.getId().toString().equals(taxonomyId))
							{
								foundGeog = true;
								geogTaxonomyName = geogT.getName();
								geogTaxonomyId = geogT.getId().toString();
								break;
							}
						}
						if(foundGeog) break;
					}
				}
				if(!foundGeog)
					continue;
			}
			
			List<AttributeMappingConfig> taxonMcfgs = configurationManager.getAttributeMappings(el.getNodeName(), null);
			/*if(taxonMcfgs == null || taxonMcfgs.isEmpty()) //TODO check if needed
				continue;*/
			List<AttributeMappingConfig> valMcfgs = null;
			if(el.getFirstChild() != null)
				valMcfgs = configurationManager.getAttributeMappings(el.getNodeName(), el.getFirstChild().getNodeValue());
			
			boolean presentable = true;
			boolean mapValue = false;
			if(taxonMcfgs != null)
			{
				for(AttributeMappingConfig mcfg : taxonMcfgs)
				{
					if(!mcfg.isPresentable())
					{
						presentable = false;
						break;
					}
				}
				if(!presentable)
					continue;
			}
			
			String ttStr = null;
			if(valMcfgs != null)
			{
				for(AttributeMappingConfig mcfg : valMcfgs) //iterate over all value mappings for this value (one for each layer). check if at least one specifies mapped value
				{
					if(mcfg.isMapValue())
						mapValue = true;
					if(mcfg.getTermId() != null)
						ttStr = mcfg.getTermId();
				}
			}
			
			TaxonomyTerm tt = null;
			gr.cite.geoanalytics.dataaccess.entities.document.Document shapeDocument = null;
			
			
			//String ttStr = el.getAttribute("term");
			if(ttStr != null && !ttStr.trim().isEmpty())
				tt =  taxonomyManager.findTermById(ttStr, false);
			if(tt != null)
			{
				TaxonomyTermShape tts = taxonomyTermShapeDao.find(tt, s);
				if(tts != null)
					shapeDocument = shapeDocumentDao.findUniqueByTaxonomyTermShape(tts);
			}
			String val = null;
			if(!mapValue)
			{
				if(el.getFirstChild() != null)
				{
					val = el.getFirstChild().getNodeValue();
					//if(val == null || val.equals(""))
					//	continue;
				}
			}
			else {
				if(tt == null)
				{
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
			if(shapeDocument != null)
				ai.setDocument(shapeDocument.getId().toString());
			res.put(tcfg != null ? tcfg.getId().toString() : geogTaxonomyName, ai);
			
		}
		return res;
	}
	
	@Transactional(readOnly = true)
	public Set<String> getShapeAttributeValues(Taxonomy t) throws Exception
	{
		List<AttributeMappingConfig> mcfgs = configurationManager.getAttributeMappingsForTermId(t.getId().toString());
		String layerId = null;
		Attribute attr = null;
		for(AttributeMappingConfig mcfg : mcfgs)
		{
			if(mcfg.getAttributeValue() == null)
			{
				if(mcfg.isPresentable() == false)
					throw new Exception("Not a presentable attribute");
				layerId = mcfg.getLayerTermId();
				attr = new Attribute(mcfg.getAttributeName(), mcfg.getAttributeType(), mcfg.getTermId(), null);
				break;
			}
		}
		
		if(layerId == null)
			return new HashSet<String>();
		
		return shapeDao.getAttributeValuesOfShapesByTerm(taxonomyManager.findTermById(layerId, false), attr);
	}
	
	public class TaxonomyTermInsertionPoint {
		private List<TaxonomyTerm> over;
		private TaxonomyTerm under;
		
		public TaxonomyTermInsertionPoint() { }
		
		public TaxonomyTermInsertionPoint(List<TaxonomyTerm> over, TaxonomyTerm under) {
			this.over = over;
			this.under = under;
		}
		
		public List<TaxonomyTerm> getParent() {
			return over;
		}
		public void setOver(List<TaxonomyTerm> over) {
			this.over = over;
		}
		public TaxonomyTerm getUnder() {
			return under;
		}
		public void setUnder(TaxonomyTerm under) {
			this.under = under;
		}
		
	}
	
	private int termLevel(TaxonomyTerm term) {
		TaxonomyTerm curTerm = term;
		int level = 0;
		while(curTerm.getParent() != null)
			level++;
		return level;
	}
	
	private TaxonomyTerm locateShape(Shape s, Taxonomy parentTaxonomy) throws Exception
	{
		
		Point centroid = s.getGeography().getCentroid();
		centroid.setSRID(4326);
		Shape pointShape = new Shape();
	    pointShape.setGeography(centroid);
	        
		List<TaxonomyTerm> parentTerms = taxonomyManager.getTermsOfTaxonomy(parentTaxonomy.getId().toString(), true, false);
		
		for(TaxonomyTerm parentTerm : parentTerms)
		{
			List<Shape> parentShapes = taxonomyManager.getShapesOfTerm(parentTerm);
			for(Shape parentShape : parentShapes)
			{
				if(shapeDao.within(pointShape, parentShape))
					return parentTerm;
			}
		}
		return null;
	}
	
	private TaxonomyTermInsertionPoint locateShapeInsertionPoint(Shape s, Taxonomy parentTaxonomy) throws Exception
	{
		
//		Point centroid = s.getGeography().getCentroid();
//		centroid.setSRID(4326);
		Shape pointShape = new Shape();
//	    pointShape.setGeography(centroid);
	    pointShape.setGeography(s.getGeography());
	    
		List<TaxonomyTerm> parentTerms = taxonomyManager.getTermsOfTaxonomy(parentTaxonomy.getId().toString(), true, false);
		
		List<TaxonomyTerm> bottomTerms = taxonomyManager.getBottomTermsOfTaxonomy(parentTaxonomy.getId().toString(), false);
		List<TaxonomyTerm> topTerms = taxonomyManager.getTopmostTermsOfTaxonomy(parentTaxonomy.getId().toString(), false);
		
		//the following is bottom up search, then top down starting from closest parent that the shape is within
		//TaxonomyTermInsertionPoint insertionPoint = bottomUpSearchWithin(pointShape, bottomTerms);
		//if(insertionPoint != null)
		//	return treeTopDownSearchWithin(pointShape, insertionPoint.under);
		
		//the previous is replaced with top down starting from the top nodes in the forest,
		//in the hope that it's faster
		TaxonomyTermInsertionPoint insertionPoint = topDownSearchWithin(pointShape, topTerms);
		if(insertionPoint != null)
			return insertionPoint;

		//not within current hierarchy, if shape is pshould be set as a parent of one of the topmost nodes
		List<TaxonomyTerm> over = new ArrayList<>();
		for(TaxonomyTerm topTerm : topTerms) {
			List<Shape> checkShapes = taxonomyManager.getShapesOfTerm(topTerm);
			for(Shape checkShape : checkShapes) {
				if(shapeDao.within(checkShape, s))
					over.add(topTerm);
			}
		}
		if(!over.isEmpty())
			return new TaxonomyTermInsertionPoint(over, null);
		return new TaxonomyTermInsertionPoint(new ArrayList<>(), null);
	}

	private TaxonomyTermInsertionPoint bottomUpSearchWithin(Shape pointShape, List<TaxonomyTerm> bottomTerms) throws Exception {
		
		for(TaxonomyTerm bottomTerm : bottomTerms) {
			TaxonomyTerm child = null;
			
			TaxonomyTerm curTerm = bottomTerm;
			do {
				if(shapeWithinShapeOfTaxonomyTerm(pointShape, curTerm))
					return new TaxonomyTermInsertionPoint(new ArrayList<>(), curTerm);
				TaxonomyTerm tmp = curTerm;
				curTerm = curTerm.getParent();
				if(curTerm != null)
					child = tmp;
				
			}while(curTerm != null);
		}
		return null;
	}
	
	private TaxonomyTermInsertionPoint topDownSearchWithin(Shape checkShape, List<TaxonomyTerm> topTerms) throws Exception {
		
		for(TaxonomyTerm topTerm : topTerms) {
			TaxonomyTermInsertionPoint insertionPoint = treeTopDownSearchWithin(checkShape, topTerm);
			if(insertionPoint != null)
				return insertionPoint;
		}
		return null;
	}

	private TaxonomyTermInsertionPoint treeTopDownSearchWithin(Shape checkShape, TaxonomyTerm top) throws Exception {
		
		boolean withinTop = shapeWithinShapeOfTaxonomyTerm(checkShape, top);
		if(!withinTop)
			return null;
		
		List<TaxonomyTerm> children = taxonomyManager.getChildrenOfTerm(top.getId().toString(), true, false);
		for(TaxonomyTerm child : children) {
			TaxonomyTermInsertionPoint insertionPoint = treeTopDownSearchWithin(checkShape, child);
			if(insertionPoint != null)
				return insertionPoint;
		}
		if(withinTop) {
			List<TaxonomyTerm> over = new ArrayList<>();
			for(TaxonomyTerm child : children) {
				if(shapeOfTaxonomyTermWithinShape(child, checkShape))
					over.add(child);
			}
			return new TaxonomyTermInsertionPoint(over, top);
		}
		return null;
	}

	private boolean shapeWithinShapeOfTaxonomyTerm(Shape checkShape, TaxonomyTerm curTerm) throws Exception {
		List<Shape> shapes = taxonomyManager.getShapesOfTerm(curTerm);
		for(Shape shape : shapes) {
			if(shapeDao.within(checkShape, shape))
				return true;
		}
		return false;
	}
	
	private boolean shapeOfTaxonomyTermWithinShape(TaxonomyTerm term, Shape checkShape) throws Exception {
		List<Shape> shapes = taxonomyManager.getShapesOfTerm(term);
		for(Shape shape : shapes) {
			if(shapeDao.within(shape, checkShape))
				return true;
		}
		return false;
	}
	
	private Taxonomy findSourceTaxonomy(Map<String, Map<String, AttributeInfo>> attrInfo, Map<String, Set<String>> valueMappingValues) throws Exception
	{
		String sourceTaxonomyName = null;
		for(Map<String, AttributeInfo> aie : attrInfo.values())
		{
			for(AttributeInfo ai : aie.values())
			{
				if(ai.getValue() == null && ai.isAutoValueMapping())
				{
					sourceTaxonomyName = ai.getTaxonomy();
					break;
				}
			}
			if(sourceTaxonomyName != null)
				break;
		}
		if(sourceTaxonomyName == null)
		{
			for(Map.Entry<String, Map<String, AttributeInfo>> aie: attrInfo.entrySet())
			{
				Set<String> mappedValues = new HashSet<String>();
				String taxon = null;
				for(AttributeInfo ai : aie.getValue().values())
				{
					if(ai.getValue() != null)
					{
						mappedValues.add(ai.getValue());
						taxon = ai.getTaxonomy();
					}
				}
				if(mappedValues.containsAll(valueMappingValues.get(aie.getKey())))
				{
					sourceTaxonomyName = taxon;
					break;
				}
			}
		}
		if(sourceTaxonomyName == null)
			return null;
		
		Taxonomy sourceTaxonomy = taxonomyManager.findTaxonomyByName(sourceTaxonomyName, false);
		return sourceTaxonomy;
	}
	
	private boolean checkGeographic(Taxonomy termTaxonomy, GeographyHierarchy geographyHierarchy)
	{
		List<List<Taxonomy>> hier = new ArrayList<List<Taxonomy>>(geographyHierarchy.getAlternativeHierarchies());
		hier.add(geographyHierarchy.getMainHierarchy());
		for(List<Taxonomy> ts : hier)
		{
			for(Taxonomy t : ts)
			{
				if(t.getId().equals(termTaxonomy.getId()))
					return true;
			}
		}
		return false;
	}
	
	private Map<Taxonomy, TermLinkInfo> locateLinked(Map<String, Map<String, AttributeInfo>> attrInfo, Taxonomy sourceTaxonomy, GeographyHierarchy geographyHierarchy) throws Exception
	{
		Map<Taxonomy, TermLinkInfo> linkedLocationInfo = new HashMap<Taxonomy, TermLinkInfo>();
		
		List<Taxonomy> hier = new ArrayList<Taxonomy>();
		Collections.reverse(geographyHierarchy.getMainHierarchy());
		hier = geographyHierarchy.getMainHierarchy(); //TODO should alts come into play?
		
		for(Map.Entry<String, Map<String, AttributeInfo>> aie : attrInfo.entrySet())
		{
			AttributeInfo ai = aie.getValue().get("");
			if(ai == null) continue;
			String verb = ai.getLinkVerb();
			if(verb == null) continue;
			
			Taxonomy linkTaxonomy = taxonomyManager.findTaxonomyByName(ai.getTaxonomy(), false);
			
			Iterator<Taxonomy> currentGeogTaxonomyIt = hier.iterator();
			boolean located = false;
			while(currentGeogTaxonomyIt.hasNext())
			{
				linkedLocationInfo.put(linkTaxonomy, new TermLinkInfo(verb));
				Taxonomy currentGeogTaxonomy = currentGeogTaxonomyIt.next();
				List<TaxonomyTerm> sourceTerms = taxonomyManager.getTermsOfTaxonomy(sourceTaxonomy.getId().toString(), true, false);
				
				located = true;
				for(TaxonomyTerm tt : sourceTerms)
				{
					TaxonomyTerm destTerm = locateShape(taxonomyTermDao.getShape(tt), currentGeogTaxonomy);
					if(destTerm == null)
					{
						located = false;
						break;
					}
					else
						linkedLocationInfo.get(linkTaxonomy).links.put(tt, destTerm);
				}
				
				if(located == false)
					continue;
				else
					break;
			}
			if(located == false)
			{
				log.error("Could not locate linked terms of taxonomy " + linkTaxonomy.getName() + " within the geography hierarchy");
				throw new Exception("Could not locate linked terms of taxonomy " + linkTaxonomy.getName() + " within the geography hierarchy");
			}
		}
		
		return linkedLocationInfo;
	}
	
	//TODO move all MappingConfig creation logic from ShapeImportManager.createDataXML() to this method
	@Transactional
	public String generateShapesOfImport(TaxonomyTerm tt, Map<String, Map<String,AttributeInfo>> attrInfo, Map<String, Set<String>> valueMappingValues, 
			UUID importId, String layerTermId, GeographyHierarchy geographyHierarchy, Principal principal) throws Exception
	{
		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		TransformerFactory tf = null;
		Transformer transformer = null;
		
		Map<String, Map<String, AttributeMappingConfig>> cfgCache = new HashMap<String, Map<String, AttributeMappingConfig>>();
		
//		ShapeImportManager importMan = new ShapeImportManager(userDao, taxonomyManager, 
//				this, configurationManager);
		List<ShapeImport> result = shapeImportManager.getImport(importId);		
		
//		Map<String, Map<String, AttributeInfo>> geographyInfo = new HashMap<String, Map<String, AttributeInfo>>();
//		for(Map<String, AttributeInfo> aim : attrInfo.values())
//		{
//			for(AttributeInfo ai : aim.values())
//			{
//				if(ai.getTaxonomy() != null && ai.getTaxonomy().equals(geographyTaxonomy.getName()))
//				{
//					if(geographyInfo.get(ai.getName()) == null)
//						geographyInfo.put(ai.getName(), new HashMap<String, AttributeInfo>());
//					if(ai.getValue() != null && !ai.getValue().isEmpty()) 
//						geographyInfo.get(ai.getName()).put(ai.getValue(), ai);
//				}
//			}
//		}
//		
//		if(!geographyInfo.isEmpty())
//		{
//			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//			db = dbf.newDocumentBuilder();
//		}
		boolean valueMappings = false;
		boolean autoValueMappings = false;
		boolean autoDocMappings = false;
		boolean linked = false;
		for(Map<String, AttributeInfo> aim : attrInfo.values())
		{				
			for(AttributeInfo ai : aim.values())
			{
				if(ai.getValue() != null)
					valueMappings = true;
				else
				{
					if(ai.isAutoValueMapping())
						autoValueMappings = true;
					if(ai.isAutoDocumentMapping())
						autoDocMappings = true;
					if(ai.getLinkVerb() != null)
						linked = true;
				}
			}
		}
		
		Taxonomy sourceTaxonomy = null;
		if(linked == true)
		{
			sourceTaxonomy = findSourceTaxonomy(attrInfo, valueMappingValues);
			if(sourceTaxonomy == null)
			{
				log.error("Unable to find a source taxonomy for linked term mapping");
				throw new Exception("Unable to find a source taxonomy for linked term mapping");
			}
		}
		//if(!geographyInfo.isEmpty())
		
		if(valueMappings || autoValueMappings)
		{
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
		}
		if(autoValueMappings)
		{
			tf = TransformerFactory.newInstance();
			tf.setAttribute("indent-number", 2);
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		}
		
		int cnt = 0;
		log.info("Import " + importId + " contains " + result.size() + " shapes");
        String identity = null;
        
		Map<String, Map<String, Integer>> createdMappingCounts = new HashMap<String, Map<String, Integer>>(); //term name -> attr value -> count
		
		long count = 0;
        for ( ShapeImport si : (List<ShapeImport>) result ) {
        	Long start = System.currentTimeMillis();
			//System.out.println( "Shape (" + si.getShapeIdentity() + ") : "); //TODO remove
			//System.out.println( "Shape (" + si.getGeography() + ") : "); //TODO remove
			identity = si.getShapeIdentity(); //equal for all shapes of same import
			if(importId.equals(si.getShapeImport()))
			{
				Shape s = new Shape();
				s.setName(si.getShapeIdentity()+"_"+(cnt++));
				s.setCreationDate(si.getCreationDate());
				s.setLastUpdate(si.getLastUpdate());
				s.setCreator(principal);
				s.setExtraData(si.getData());
				s.setGeography(si.getGeography());
				s.setShapeImport(si);
				//s.setCode(); //TODO sysconfig
				s.setShapeClass(1); //TODO sysconfig
				shapeDao.create(s);
				
				ShapeTerm st = new ShapeTerm();
				st.setCreator(si.getCreator());
				st.setShape(s);
				st.setTerm(taxonomyTermDao.read(tt.getId())); //refresh to prevent erroneous creation attempt
				shapeTermDao.create(st);
				
				//if(!geographyInfo.isEmpty())
				if(valueMappings)
					createTermsForShapeAttributes(attrInfo, principal, db, s);
				
				if(autoValueMappings || autoDocMappings)
				{
					Document d = db.parse(new InputSource(new StringReader(s.getExtraData())));
					
					for(Map.Entry<String, Map<String, AttributeInfo>> ai : attrInfo.entrySet())
					{
						AttributeInfo attr = ai.getValue().get("");
						if(attr == null || attr.isStore() == false) continue;
						if(attr.isAutoValueMapping() == false && attr.isAutoDocumentMapping() == false) continue;
						if(attr.getTaxonomy() == null)
							throw new Exception("Taxonomy of auto-created terms is not defined");
						
						NodeList els = d.getElementsByTagName(ai.getKey());
						
						Node el = els.item(0);
						Set<String> vals = valueMappingValues.get(attr.getName());
						if(vals == null) continue;
						
						Taxonomy termTaxonomy = taxonomyManager.findTaxonomyByName(attr.getTaxonomy(), false);
						boolean geographicTaxonomy = checkGeographic(termTaxonomy, geographyHierarchy);
						if(geographicTaxonomy == true)
						{
							setTaxonomyDataGeographic(termTaxonomy);
							taxonomyManager.updateTaxonomy(termTaxonomy, termTaxonomy.getName(), false);
						}
						
						if(el.getFirstChild() != null) {
							String nodeValue = el.getFirstChild().getNodeValue();
							generateAutoValuedTermsForShapeAttributes(layerTermId, principal, cfgCache, createdMappingCounts, s, attr, vals,
									termTaxonomy, geographicTaxonomy, nodeValue);
							generateAutoDocumentMappingsForShapeAttributes(layerTermId, principal, cfgCache, s, attr, vals, termTaxonomy,
									nodeValue);
							System.out.println("Inserted shape and generated geography terms. Count: " + count);
							count++;
						}
					}
				}
			}
			System.out.println("Count: " + count + " millis: " + (System.currentTimeMillis() - start));
        }
    	attrInfo.values().stream().
		map(x -> x.get("")).
		filter(attr -> attr != null && attr.isAutoValueMapping() && attr.isStore()).
		forEach(this::createTaxonomiesOfTermLevels);
    	
        if(linked)
		{
			Map<Taxonomy, TermLinkInfo> linkedLocationInfo = locateLinked(attrInfo, sourceTaxonomy, geographyHierarchy);
			for(TermLinkInfo lli : linkedLocationInfo.values())
			{
				for(Map.Entry<TaxonomyTerm, TaxonomyTerm> link : lli.links.entrySet()) {
					TaxonomyTermLink l = new TaxonomyTermLink();
					l.setSourceTerm(link.getKey());
					l.setDestinationTerm(link.getValue());
					l.setCreator(principal);
					l.setVerb(TaxonomyTermLink.Verb.valueOf(lli.verb));
					taxonomyTermLinkDao.create(l);
				}
			}
		}
        
        if(autoValueMappings) {
        	
        }
        return identity;
	}
	
	private void createTaxonomiesOfTermLevels(AttributeInfo attr) {
		
		Taxonomy taxonomy = taxonomyManager.findTaxonomyByName(attr.getTermParentTaxonomy(), false);
			
		String topTaxonomyName = taxonomy.getName();
		int level = 0;
		List<TaxonomyTerm> terms = taxonomyManager.getTopmostTermsOfTaxonomy(taxonomy.getId().toString(), false);//Changes occured here as well the name of the attribute was passed instead of the UUID
		level++;
		while(!terms.isEmpty()) {
			terms = terms.stream().
					flatMap(t -> taxonomyManager.getChildrenOfTerm(t.getId().toString(), true, false).stream()).
					collect(Collectors.toList());
			
			Taxonomy parent = taxonomy;
			taxonomy = taxonomyManager.findTaxonomyByName(topTaxonomyName + " " + level, false);
			
			if(taxonomy == null) {
				taxonomy = new Taxonomy();
				taxonomy.setCreator(parent.getCreator());
				taxonomy.setIsActive(true);
				taxonomy.setName(topTaxonomyName + " " + level);
				TaxonomyData taxonomyData = new TaxonomyData();
				taxonomyData.setGeographic(true);
				taxonomyData.setParent(parent.getId());
				taxonomy.setExtraData(taxonomyManager.marshalTaxonomyData(taxonomyData));
				taxonomyManager.updateTaxonomy(taxonomy, null, true);
			}
			
			final Taxonomy createdTaxonomy = taxonomy;
			terms.forEach(term -> {
				term.setTaxonomy(createdTaxonomy);
				taxonomyManager.updateTerm(term, term.getName(), term.getTaxonomy().getName(), false);
			});
			level++;
		}
	}

	private void generateAutoValuedTermsForShapeAttributes(String layerTermId, Principal principal,
			Map<String, Map<String, AttributeMappingConfig>> cfgCache,
			Map<String, Map<String, Integer>> createdMappingCounts, Shape s, AttributeInfo attr, Set<String> vals,
			Taxonomy termTaxonomy, boolean geographicTaxonomy, String nodeValue) throws Exception {
		for(String val : vals) {
			
			if(val.equals(nodeValue)) {
				
				if(geographicTaxonomy == true && attr.getTermParentTaxonomy() == null)
					throw new Exception("Taxonomy of auto-created term parent terms is not defined");
				
				if(attr.isAutoValueMapping()) {
					
					TaxonomyTermInsertionPoint insertionPoint = null;
					if(attr.getTermParentTaxonomy() != null)
						insertionPoint = locateShapeInsertionPoint(s, taxonomyManager.findTaxonomyByName(attr.getTermParentTaxonomy(), false));
					
				//	String termName = createdMappings.get(val);
					String termName = StringUtils.normalizeEntityName(new String(new char[]{val.charAt(0)}).toUpperCase() + val.substring(1).toLowerCase());
					
					if(!createdMappingCounts.containsKey(termName))
						createdMappingCounts.put(termName, new HashMap<String, Integer>());
					Integer termCnt = null;
					
					if(createdMappingCounts.get(termName).containsKey(val))
						termCnt = createdMappingCounts.get(termName).get(val);
					else {
						createdMappingCounts.get(termName).put(val, 0);
						termCnt = 0;
					}
					
					TaxonomyTerm ttstt = new TaxonomyTerm();
					ttstt.setCreator(principal);
					ttstt.setIsActive(true);
					ttstt.setName(termName);
					ttstt.setParent(insertionPoint.under);
					ttstt.setExtraData("auto " + attr.getTermParentTaxonomy());
					ttstt.setTaxonomy(termTaxonomy);
					TaxonomyTerm existingTtstt = taxonomyManager.findTermByName(termName, false); 
					
					if(existingTtstt != null) {
						List<Shape> shapesOfExisting = taxonomyManager.getShapesOfTerm(existingTtstt);
						for(Shape existingS : shapesOfExisting) {
							
							if(s.getId().equals(existingS.getId())) {
								taxonomyManager.deleteTerm(existingTtstt); //should never happen
								throw new Exception("Duplicate shape of term " + existingTtstt.getName());
								//break;
							}
						}
						if(termCnt == 0) {
							
							//TaxonomyTerm existingTT = taxonomyManager.findTermByName(termName, false);
							existingTtstt.setName(termName+" "+0);
							taxonomyManager.updateTerm(existingTtstt, existingTtstt.getName(), existingTtstt.getTaxonomy().getName(), false);
						}
						ttstt.setName(termName + " " + (++termCnt));
							
					}

					taxonomyManager.updateTerm(ttstt, null, null, true);
					
					updateParentOfChildrenOfInsertedTerm(insertionPoint, ttstt);
					
					AttributeMappingConfig mcfg = new AttributeMappingConfig();
					mcfg.setAttributeName(attr.getName());
					mcfg.setAttributeType(attr.getType());
					mcfg.setAttributeValue(val);
					mcfg.setLayerTermId(layerTermId);
					mcfg.setTermId(ttstt.getId().toString());
					mcfg.setPresentable(attr.isPresentable());
					mcfg.setMapValue(attr.isMapValue());
					
					addMappingConfig(mcfg, cfgCache);
					
					//createdMappings.put(val, termName);
					createdMappingCounts.get(termName).put(val, termCnt);
					
					TaxonomyTermShape tts = new TaxonomyTermShape();
					tts.setTerm(taxonomyManager.findTermByName(ttstt.getName(), false));
					tts.setShape(s);
					tts.setCreator(principal);
					taxonomyTermShapeDao.create(tts);
					
					//if autoValueMappings && autoDocMappings, mapping is driven by auto value mappings
					if(attr.isAutoDocumentMapping()) {
						
						gr.cite.geoanalytics.dataaccess.entities.document.Document doc = findMapDocumentByValue(val);
						if(doc != null)
							mapDocumentByValue(doc, tts, principal);
					}
					
				}
			}
		}
	}

	private void updateParentOfChildrenOfInsertedTerm(TaxonomyTermInsertionPoint insertionPoint, TaxonomyTerm ttstt)
			throws Exception {
		//update child's parent in case ttstt has been inserted between two levels in the hierarchy
		if(!insertionPoint.over.isEmpty()) {
			for(TaxonomyTerm over : insertionPoint.over) {
				over.setParent(ttstt);
				taxonomyManager.updateTerm(over, null, null, false);
			}
		}
	}
	
	private void generateAutoDocumentMappingsForShapeAttributes(String layerTermId, Principal principal,
			Map<String, Map<String, AttributeMappingConfig>> cfgCache, Shape s, AttributeInfo attr, Set<String> vals,
			Taxonomy termTaxonomy, String nodeValue) throws Exception {
		for(String val : vals) {
			if(val.equals(nodeValue) && attr.isAutoDocumentMapping())
			{
				gr.cite.geoanalytics.dataaccess.entities.document.Document doc = findMapDocumentByValue(val);
				if(doc != null)
				{
					String termName = attr.getTaxonomy() + StringUtils.normalizeEntityName(new String(new char[]{val.charAt(0)}).toUpperCase() + val.substring(1).toLowerCase());
					TaxonomyTerm ttstt = taxonomyManager.findTermByName(termName, false);
					if(ttstt == null)
					{
						ttstt = new TaxonomyTerm();
						ttstt.setName(termName);
						ttstt.setCreator(principal);
						ttstt.setIsActive(true);
						ttstt.setTaxonomy(termTaxonomy);
						taxonomyManager.updateTerm(ttstt, null, null, true);
						
						AttributeMappingConfig mcfg = new AttributeMappingConfig();
						mcfg.setAttributeName(attr.getName());
						mcfg.setAttributeType(attr.getType());
						mcfg.setAttributeValue(val);
						mcfg.setLayerTermId(layerTermId);
						mcfg.setTermId(ttstt.getId().toString());
						
						addMappingConfig(mcfg, cfgCache);
					}
					TaxonomyTermShape tts = taxonomyTermShapeDao.find(ttstt, s);
					
					if(tts == null)
					{
						tts = new TaxonomyTermShape();
						tts.setCreator(principal);
						tts.setShape(s);
						tts.setTerm(ttstt);
						taxonomyTermShapeDao.create(tts);
					}
					mapDocumentByValue(doc, tts, principal);
				}
				
			}
		}
	}

	private void createTermsForShapeAttributes(Map<String, Map<String, AttributeInfo>> attrInfo, Principal principal,
			DocumentBuilder db, Shape s) throws SAXException, IOException, Exception {
		Document d = db.parse(new InputSource(new StringReader(s.getExtraData())));
		//for(Map.Entry<String, Map<String, AttributeInfo>> gi : geographyInfo.entrySet())
		for(Map.Entry<String, Map<String, AttributeInfo>> ai : attrInfo.entrySet())
		{
			if(ai.getValue().get("") != null && ai.getValue().get("").isStore() == false) continue;
			NodeList els = d.getElementsByTagName(ai.getKey());
			if(els.getLength() == 1)
			{
				Node el = els.item(0);
				for(Map.Entry<String, AttributeInfo> aie : ai.getValue().entrySet())
				{
					if(el.getFirstChild() != null && aie.getKey().equals(el.getFirstChild().getNodeValue()))
					{
						TaxonomyTerm ttstt = taxonomyManager.findTermByNameAndTaxonomy(aie.getValue().getTerm(), aie.getValue().getTaxonomy(), false);
						TaxonomyTermShape tts = new TaxonomyTermShape();
						tts.setTerm(ttstt);
						tts.setShape(s);
						tts.setCreator(principal);
						taxonomyTermShapeDao.create(tts);
						
						if(aie.getValue().getDocument() != null)
						{
							gr.cite.geoanalytics.dataaccess.entities.document.Document document = 
									documentManager.findById(aie.getValue().getDocument(), false);
							ShapeDocument sd = new ShapeDocument();
							sd.setCreator(principal);
							sd.setTaxonomyTermShape(tts);
							sd.setDocument(document);
							shapeDocumentDao.create(sd);
						}
					}
				}
			}
		}
	}
	
	private void addMappingConfig(AttributeMappingConfig mcfg, Map<String, Map<String, AttributeMappingConfig>> cfgCache) throws Exception
	{
		if(cfgCache == null)
		{
			configurationManager.updateMappingConfig(mcfg);
			return;
		}
		String key = null;
		if(mcfg.getAttributeName() == null) return;
		if(mcfg.getTermId() == null && mcfg.getAttributeValue() == null)
			key = NoMappingKey;
		else if(mcfg.getAttributeValue() == null)
			key = NoValueKey;
		else
			key = mcfg.getAttributeValue();

		if(cfgCache.get(mcfg.getAttributeName()) == null)
			cfgCache.put(mcfg.getAttributeName(), new HashMap<String, AttributeMappingConfig>());
		if(cfgCache.get(mcfg.getAttributeName()).get(key) == null)
		{
			cfgCache.get(mcfg.getAttributeName()).put(key, mcfg);
			configurationManager.updateMappingConfig(mcfg);
		}
	}
	
	private void setTaxonomyDataGeographic(Taxonomy taxonomy) {
		TaxonomyData taxonomyData = new TaxonomyData();
		taxonomyData.setGeographic(true);
		taxonomy.setExtraData(taxonomyManager.marshalTaxonomyData(taxonomyData));
	}
	
	private gr.cite.geoanalytics.dataaccess.entities.document.Document findMapDocumentByValue(String val) throws Exception
	{
		List<gr.cite.geoanalytics.dataaccess.entities.document.Document> ds = 
				documentManager.searchDocuments(Collections.singletonList(val)); //TODO pattern match value to doc name/description, keep for now
		if(ds != null && !ds.isEmpty())
		{
			if(ds.size() == 1)
			{
				gr.cite.geoanalytics.dataaccess.entities.document.Document doc = ds.get(0);
				RepositoryFile rf = repository.retrieve(doc.getId().toString());
				if(rf == null)
				{
					log.error("Could not locate doc " + doc.getId() + " for value " + val + " in data repository");
					throw new Exception("Could not locate doc " + doc.getId() + " for value " + val + " in data repository");
				}
				return doc;
			}
			else
			{
				log.warn("Multiple documents matching " + val + " were found during auto document mapping");
				return null;
			}
		}else
		{
			log.warn("Could not find document for value " + val + ".");
			return null;
		}
	}
	private void mapDocumentByValue(gr.cite.geoanalytics.dataaccess.entities.document.Document doc, TaxonomyTermShape tts, Principal principal) throws Exception {
		ShapeDocument sd = new ShapeDocument();
		sd.setCreator(principal);
		sd.setTaxonomyTermShape(tts);
		sd.setDocument(doc);
		shapeDocumentDao.create(sd);
	}
	
	@Transactional
	public void generateShapeBoundary(TaxonomyTerm layerTerm, TaxonomyTerm boundaryTerm, Principal principal) throws Exception
	{
		List<Shape> shapes = getShapesOfLayer(layerTerm);
		
		Shape boundary = new Shape();
		boundary.setCreator(principal);
		boundary.setGeography(shapes.get(0).getGeography());
		boundary.setName(layerTerm.getName()+"_boundary");
		shapeDao.create(boundary);
		
		for(int i=1; i<shapes.size(); i++)
		{
			Shape buffer = shapeDao.buffer(shapes.get(i), 10.0f);
			shapeDao.create(buffer);
			
			Shape union = shapeDao.union(boundary, buffer);
			boundary.setGeography(union.getGeography());
			shapeDao.update(boundary);
			
			shapeDao.delete(buffer);
		}
		//Shape b = shapeDao.boundary(boundary);
		//boundary.setGeography(b.getGeography());
		//shapeDao.update(boundary);
		
		TaxonomyTermShape tts = new TaxonomyTermShape();
		tts.setCreator(principal);
		tts.setTerm(boundaryTerm);
		tts.setShape(boundary);
		taxonomyTermShapeDao.create(tts);
	}
	
	@Transactional(readOnly = true)
	public List<Shape> getShapesOfImport(UUID importId) throws Exception
	{
		ShapeImport si = new ShapeImport();
		si.setShapeImport(importId);
		List<Shape> shapes = shapeDao.findShapesByImport(si);
		if(shapes == null) return null;
		
		for(Shape s : shapes)
			getShapeDetails(s);
		return shapes;
	}
	
	@Transactional(readOnly = true)
	public List<Shape> getShapesOfLayer(TaxonomyTerm tt) throws Exception
	{
		List<Shape> shapes = shapeDao.findShapesByTerm(tt);
		if(shapes == null) return null;
		
		for(Shape s : shapes)
			getShapeDetails(s);
		return shapes;
	}
	
	@Transactional(readOnly = true)
	public List<Shape> getShapesOfLayer(String termName, String termTaxonomy) throws Exception
	{
		TaxonomyTerm tt = taxonomyManager.findTermByNameAndTaxonomy(termName, termTaxonomy, false);
		return getShapesOfLayer(tt);
	}
	
	@Transactional(readOnly = true)
	public Shape getShapeFromLayerTermAndShapeTerm(TaxonomyTerm layerTerm, TaxonomyTerm termForShape) {
		return getShapeFromLayerTermAndShapeTerm(layerTerm, termForShape, false);
	}
	
	@Transactional(readOnly = true)
	public Shape getShapeFromLayerTermAndShapeTerm(TaxonomyTerm layerTerm, TaxonomyTerm termForShape, boolean loadDetails) {
		Shape shape = shapeDao.getShapeFromLayerTermAndShapeTerm(layerTerm, termForShape);
		if(loadDetails)
			getShapeDetails(shape);
		return shape;
		
	}
	
	@Transactional(readOnly = true)
	public Map<String, Shape> getShapesFromLayerTerm(TaxonomyTerm layerTerm) {
		return shapeDao.getShapesFromLayerTerm(layerTerm);		
	}
	
	@Transactional(readOnly = true)
	public TaxonomyTerm getTermFromLayerTermAndShape(TaxonomyTerm layerTerm, Shape shape) {
		return getTermFromLayerTermAndShape(layerTerm, shape, false);
	}
	
	@Transactional(readOnly = true)
	public TaxonomyTerm getTermFromLayerTermAndShape(TaxonomyTerm layerTerm, Shape shape, boolean loadDetails) {
		TaxonomyTerm t = shapeDao.getTermFromLayerTermAndShape(layerTerm, shape);
		if(loadDetails)
			taxonomyManager.getTermDetails(t);
		return t;
	}
	
	@Transactional(readOnly = true)
	public List<ShapeInfo> getShapeInfoForTerm(String termName, String termTaxonomy) throws Exception
	{
		TaxonomyTerm tt = taxonomyManager.findTermByNameAndTaxonomy(termName, termTaxonomy, false);
				
		List<Shape> shapes = shapeDao.findShapesByTerm(tt);
		if(shapes == null) return null;
		
		getTermDetails(tt);
		
		List<ShapeInfo> res = new ArrayList<ShapeInfo>();
		for(Shape s : shapes)
		{
			getShapeDetails(s);
			ShapeInfo si = new ShapeInfo();
			si.setShape(s);
			si.setTerm(tt);
			res.add(si);
		}
		return res;
	}
	
	@Transactional
	public void deleteShapesOfTerm(TaxonomyTerm tt) throws Exception
	{
		List<Shape> shapes = shapeDao.findShapesByTerm(tt);
		shapeTermDao.deleteByTerm(tt);
		for(Shape s : shapes)
			shapeDao.delete(s);
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTermShape> findTermMappingsOfLayerShapes(TaxonomyTerm tt) throws Exception
	{
		return shapeDao.findTermMappingsOfLayerShapes(tt);
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTerm> findTaxonomyTermShapes(Shape s) throws Exception {
		return findTaxonomyTermShapes(s, false);
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTerm> findTaxonomyTermShapes(Shape s, boolean loadDetails) throws Exception {
		List<TaxonomyTerm> result = shapeDao.findTaxononyTermShapes(s);
		if(loadDetails)
			result.forEach(tt -> taxonomyTermDao.loadDetails(tt));
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<Shape> findShapesOfImport(ShapeImport shapeImport) throws Exception
	{
		return shapeDao.findShapesByImport(shapeImport);
	}
	
	@Transactional(readOnly = true)
	public long countShapesOfImport(UUID shapeImport) throws Exception
	{
		return shapeDao.countShapesByImport(shapeImport);
	}
	
	@Transactional(readOnly = true)
	public List<ShapeInfo> findShapesOfImport(UUID shapeImport) throws Exception
	{
		List<Shape> shapes = shapeDao.findShapesByImport(shapeImport);
		List<ShapeInfo> res = new ArrayList<ShapeInfo>();
		for(Shape s : shapes)
		{
			getShapeDetails(s);
			ShapeInfo si = new ShapeInfo();
			si.setShape(s);
			TaxonomyTerm t = shapeDao.findTermOfShape(s);
			if(t != null) getTermDetails(t);
			si.setTerm(t);
			res.add(si);
		}
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<ShapeInfo> findShapeWithinBounds(String bounds) throws Exception
	{
		Geometry geom = new WKTReader().read(bounds);
		geom.setSRID(4326);
		Shape sh = new Shape();
		sh.setId(UUIDGenerator.randomUUID());
		sh.setGeography(geom);
		
		List<Shape> shapes = shapeDao.findContains(sh);
		List<ShapeInfo> res = new ArrayList<ShapeInfo>();
		for(Shape s : shapes)
		{
			getShapeDetails(s);
			ShapeInfo si = new ShapeInfo();
			si.setShape(s);
			TaxonomyTerm t = shapeDao.findTermOfShape(s);
			if(t != null) getTermDetails(t);
			si.setTerm(t);
			res.add(si);
		}
		return res;
	}
	
	@Transactional(readOnly = true)
	public boolean existShapesOfTerm(TaxonomyTerm tt) throws Exception
	{
		return shapeDao.existShapesOfTerm(tt);
	}
	
	@Transactional(readOnly = true)
	public ShapeInfo getShape(UUID id) throws Exception
	{
		ShapeInfo si = new ShapeInfo();
		Shape s = shapeDao.read(id);
		if(s == null) return null;
		getShapeDetails(s);
		si.setShape(s);
		TaxonomyTerm t = shapeDao.findTermOfShape(s);
		if(t != null) getTermDetails(t);
		si.setTerm(t);
		return si;
	}
	
	@Transactional(readOnly = true)
	public Bounds getShapeBounds(UUID id) throws Exception
	{
		Shape s = shapeDao.read(id);
		if(s == null) throw new Exception("Shape " + id + " not found");
		
		Shape env = shapeDao.envelope(s);
		Geometry geom = env.getGeography();
		return null;
	}
	
	@Transactional
	public Shape createFromGeometry(Principal principal,String shapeName, String geometry) throws Exception {
		Geometry geom = new WKTReader().read(geometry);
		geom.setSRID(4326);
		Shape s = new Shape();
		s.setGeography(geom);
		s.setName(shapeName);
		s.setCreator(principal);
		shapeDao.create(s);
		return s;
	}
	
	@Transactional
	public Shape createFromGeometry(Project project, String geometry) throws Exception {
		return createFromGeometry(project.getCreator(), project.getName(), geometry);
	}
	
	@Transactional
	public Shape createFromGeometryPolygon(Project project, NewProjectData npd, Principal principal) throws Exception {
		 WKTReader reader = new WKTReader();
		 String polygon =
				    "POLYGON(("+ npd.getCoords().getCoord0()[0] + " " + npd.getCoords().getCoord0()[1] + ","
							+ npd.getCoords().getCoord1()[0] + " " + npd.getCoords().getCoord1()[1] +","
							+ npd.getCoords().getCoord2()[0] + " " + npd.getCoords().getCoord2()[1] +","
							+ npd.getCoords().getCoord3()[0] + " " + npd.getCoords().getCoord3()[1]+","
							+ npd.getCoords().getCoord0()[0] + " " + npd.getCoords().getCoord0()[1] + "))";
		 
		 Geometry g = reader.read(polygon);
		 g.setSRID(4326);
			
//			if ( !geo.isRectangle() ) {
//		        geo = geo.getEnvelope();
//		        WKTWriter writer = new WKTWriter();
//		        String bbox = writer.write( geo );
//		        wkt = bbox;
//		    }

		Shape s = new Shape();
		s.setGeography(g);
		s.setCreator(principal);
//		s.setCode(npd.getCoords().toString());
		shapeDao.create(s);
		return s;
	}
	
	
	@Transactional
	public void updateGeometry(UUID id, String geometry) throws Exception {
		Shape ex = shapeDao.read(id);
		if(ex == null) throw new Exception("Shape " + id + " not found");
		Geometry geom = new WKTReader().read(geometry);
		ex.setGeography(geom);
		shapeDao.update(ex);
	}
	
	@Transactional(readOnly = true)
	public String getGeometry(UUID id) throws Exception {
		Shape s = shapeDao.read(id);
		if(s == null) throw new Exception("Shape " + id + " not found");
		return new WKTWriter().write(s.getGeography());
	}
	
	@Transactional(readOnly = true)
	public String getBoundingBoxByProjectName(String projectName) throws Exception{
		List<Project> projects = projectDao.findByName(projectName);
		if(projects != null && projects.size() > 1){
			throw new Exception("Multiple projects with name " + projectName);
		} 
		
		Project project = null;
		if( projects != null && !projects.isEmpty()){
			project = projects.get(0);
		}
		
//		Shape s = shapeDao.read(project.getShape());
//		if(s == null) throw new Exception("Shape " + project.getShape() + " not found");
//		Coordinate[] coords = s.getGeography().getCoordinates();
//		List<String> coordinates = new ArrayList<String>();
//		for(int i=0;i<coords.length-1;i++){
//			coordinates.add(coords[i].toString());
//		}
//		return new WKTWriter().write(s.getGeography().getCoordinates());
		return project.getClient();
	}
	
	@Transactional
	public void update(Shape s) throws Exception {
		Shape ex = shapeDao.read(s.getId());
		if(ex == null) throw new Exception("Shape " + s.getId() + " not found");
		if(s.getCode() != null) ex.setCode(s.getCode());
		if(s.getExtraData() != null) ex.setExtraData(s.getExtraData());
		if(s.getName() != null) ex.setName(s.getName());
		if(s.getShapeClass() > -1) ex.setShapeClass(s.getShapeClass());
		//if(s.getGeography() != null) ex.setGeography(s.getGeography()); TODO support in the future
		shapeDao.update(ex);
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void delete(List<String> shapes) throws Exception
	{
		for(String s : shapes)
		{
			Shape sh = shapeDao.read(UUID.fromString(s));
			if(sh == null) throw new Exception("Shape " + s + " not found");
			
			TaxonomyTerm tt = shapeDao.findTermOfShape(sh);
			
			if(tt != null)
			{
				ShapeTerm st = shapeTermDao.find(tt, sh);
				if(st == null) throw new Exception("Could not find shape term for shape " + s);
				shapeTermDao.delete(st);
			}
			List<TaxonomyTermShape> ttss = taxonomyTermShapeDao.findByShape(sh);
			for(TaxonomyTermShape tts : ttss)
			{
				shapeDocumentDao.deleteByTaxonomyTermShape(tts);
				taxonomyTermShapeDao.delete(tts);
			}
			
			shapeDao.delete(sh);
		}
	}
	
	@Transactional(readOnly = true)
	public List<Shape> findShapesEnclosingGeometry(Shape s) throws Exception {
		return shapeDao.findWithin(s);
	}
	
	@Transactional(readOnly = true)
	public List<Shape> findShapesOfLayerEnclosingGeometry(Shape s, TaxonomyTerm layerTerm) throws Exception {
		return shapeDao.findWithin(s, layerTerm, null);
	}
	
	@Transactional(readOnly = true)
	public List<Shape> findShapesOfLayerEnclosingGeometry(Shape s, TaxonomyTerm layerTerm, TaxonomyTerm term) throws Exception {
		return shapeDao.findWithin(s, layerTerm, term);
	}
	
	@Transactional(readOnly = true)
	public List<Shape> findShapesEnclosingGeometry(Geometry geometry) throws Exception {
		Shape s = new Shape();
		s.setGeography(geometry);
		return findShapesEnclosingGeometry(s);
	}
	
	@Transactional(readOnly = true)
	public List<Shape> findShapesOfLayerEnclosingGeometry(Geometry geometry, TaxonomyTerm layerTerm) throws Exception {	
		Shape s = new Shape();
		s.setGeography(geometry);
		return findShapesOfLayerEnclosingGeometry(s, layerTerm);
	}
	
	@Transactional(readOnly = true)
	public List<Shape> findShapesOfLayerEnclosingGeometry(Geometry geometry, TaxonomyTerm layerTerm, TaxonomyTerm term) throws Exception {
		Shape s = new Shape();
		s.setGeography(geometry);
		return findShapesOfLayerEnclosingGeometry(s, layerTerm, term);
	}

	private List<List<Taxonomy>> getAlternativeHierarchies(List<Taxonomy> mainHierarchy, List<List<Taxonomy>> currentAlts, int index, List<Taxonomy> allTaxonomies, Map<UUID, TaxonomyData> taxonomyData) throws Exception
	{
		List<List<Taxonomy>> currHierarchies = new ArrayList<List<Taxonomy>>(currentAlts);
		currHierarchies.add(mainHierarchy);
		
		List<List<Taxonomy>> altHierarchies = new ArrayList<List<Taxonomy>>();
		Taxonomy altTaxonomy = mainHierarchy.get(index);
		
		TaxonomyData altTaxonomyData = taxonomyData.get(altTaxonomy.getId());
		
		for(List<Taxonomy> hier : currHierarchies) {
			for(UUID alt : altTaxonomyData.getAlternatives()) {
				List<Taxonomy> altHierarchy = new ArrayList<Taxonomy>();
				
				for(Taxonomy t : hier) {
					if(t.getId().equals(altTaxonomy.getId()))
						break;
					altHierarchy.add(t);
				}
				
				altHierarchies.add(altHierarchy);
			}
		}
		
		for(int i=0; i<altTaxonomyData.getAlternatives().size(); i++) {
			for(List<Taxonomy> ah : altHierarchies) {
				
				Taxonomy child = null;
				int ind = i;
				List<Taxonomy> children = allTaxonomies.stream().
						filter(t -> taxonomyData.get(t.getId()).getParent().equals(altTaxonomyData.getAlternatives().get(ind))).
						collect(Collectors.toList());
				if(!children.isEmpty()) {
					if(children.size() > 1)
						throw new Exception("Branched taxonomy hierarchies not supported");
					child = children.get(0);
				}
				
				if(child == null)
					break;
				ah.add(child);
			}
		}
		
		List<Taxonomy> rest = mainHierarchy.subList(index+1, mainHierarchy.size());
		
		for(List<Taxonomy> hier : altHierarchies)
			hier.addAll(rest);
		
		return altHierarchies;
	}
	
	@Transactional(readOnly=true)
	public GeographyHierarchy getDefaultGeographyHierarchy() throws Exception {
		return getGeographyHierarchy(taxonomyManager.findTaxonomyById(
				configurationManager.retrieveTaxonomyConfig(Type.GEOGRAPHYTAXONOMY).get(0).getId(), false));
	}
	
	@Transactional(readOnly=true)
	public GeographyHierarchy getGeographyHierarchy(Taxonomy geogTaxonomy) throws Exception {
		GeographyHierarchy hierarchy = new GeographyHierarchy();
		
		List<Taxonomy> allTaxonomies = taxonomyManager.allTaxonomies(false);
		Map<UUID, TaxonomyData> taxonomyData = allTaxonomies.stream().
				filter(t -> t.getExtraData() != null).
				collect(Collectors.toMap(Taxonomy::getId, t -> taxonomyManager.unmarshalTaxonomyData(t.getExtraData())));
		
		Taxonomy reloadedGeoTax = allTaxonomies.stream().filter(t -> t.getId().equals(geogTaxonomy.getId())).findFirst().get();
		hierarchy.setMainHierarchy(constructMainHierarchy(reloadedGeoTax, allTaxonomies, taxonomyData));
		hierarchy.setAlternativeHierarchies(constructAlternativeHierarchies(hierarchy, allTaxonomies, taxonomyData));
				
		hierarchy.getMainHierarchy().
			forEach(t -> taxonomyDao.loadDetails(t));
		
		hierarchy.getAlternativeHierarchies().
			forEach(alt ->
				alt.forEach(t -> taxonomyDao.loadDetails(t)));
		
		return hierarchy;
		
	}

	private List<List<Taxonomy>> constructAlternativeHierarchies(GeographyHierarchy hierarchy,
			List<Taxonomy> allTaxonomies, Map<UUID, TaxonomyData> taxonomyData) throws Exception {
		List<Integer> altIndexes = new ArrayList<Integer>();
		
		int i = 0;
		for(Taxonomy currTaxonomy : hierarchy.getMainHierarchy()) {
			if(!taxonomyData.get(currTaxonomy.getId()).getAlternatives().isEmpty())
				altIndexes.add(i);
			i++;
		}
		
		List<List<Taxonomy>> altHierarchies = new ArrayList<>();
		for(Integer index : altIndexes)
			altHierarchies.addAll(getAlternativeHierarchies(hierarchy.getMainHierarchy(), altHierarchies, index, allTaxonomies, taxonomyData));
		return altHierarchies;
	}

	/**
	 * 
	 * @param geogTaxonomy a taxonomy within the hierarchy that is to be returned. Not necessarily the top taxonomy.
	 * @return
	 * @throws Exception
	 */
	private List<Taxonomy> constructMainHierarchy(Taxonomy geogTaxonomy, List<Taxonomy> allTaxonomies, Map<UUID, TaxonomyData> taxonomyData) throws Exception {
		//		List<TaxonomyTerm> terms = taxonomyManager.getTermsOfTaxonomy(geogTaxonomy.getId().toString(), true, false);
		//		if(terms.size() == 0) throw new Exception("No geographic data");
		//		TaxonomyTerm term = terms.get(0);
		if(geogTaxonomy == null)
			throw new IllegalArgumentException("Geography taxonomy cannot be null");
		
		LinkedList<Taxonomy> hier = new LinkedList<Taxonomy>();
		hier.add(geogTaxonomy);
		
		Taxonomy currTaxonomy = geogTaxonomy;
		while(currTaxonomy != null) {
			TaxonomyData taxData = taxonomyData.get(currTaxonomy.getId());
			if(taxData == null || taxData.getParent() == null)
				break;
			
			Taxonomy parent = taxonomyManager.findTaxonomyById(taxData.getParent().toString(), false);
			if(taxData.getParent() != null)
				hier.push(parent);
			currTaxonomy = parent;
		}
		
		currTaxonomy = hier.peekLast();
		while(true) {
			final Taxonomy ct = currTaxonomy;
			Taxonomy child = null;
			List<Taxonomy> children = allTaxonomies.stream().
					filter(t -> {
						TaxonomyData td = taxonomyData.get(t.getId());
						return td != null && td.getParent() != null && td.getParent().equals(ct.getId());
					}).
					collect(Collectors.toList());
			if(!children.isEmpty()) {
				if(children.size() > 1)
					throw new Exception("Branched taxonomy hierarchies not supported");
				child = children.get(0);
			}
			
			if(child == null)
				break;
			
			hier.add(child);
			currTaxonomy = child;
		}
		return hier;
	}
	
	@Transactional(readOnly=true)
	public List<TaxonomyTerm> geoLocate(double x, double y) throws Exception
	{
		List<TaxonomyTerm> res = new ArrayList<TaxonomyTerm>();
		
		GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);

        Point point = gf.createPoint(new Coordinate(x, y));
        Shape pointShape = new Shape();
        pointShape.setGeography(point);
        
        TaxonomyConfig tcfg = null;
		List<TaxonomyConfig> tcfgs = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY);
		if (tcfgs != null){
			tcfg = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY).get(0);
		}
		//Taxonomy geoTaxonomy = taxonomyManager.findTaxonomyById(tcfg.getId(), false);
		List<TaxonomyTerm> terms = taxonomyManager.getTopmostTermsOfTaxonomy(tcfg.getId(), false);
		
		if(terms == null || terms.isEmpty())
			return res;
		
		while(true)
		{
			boolean located = false;
			for(TaxonomyTerm term : terms)
			{
				List<Shape> termShapes = taxonomyManager.getShapesOfTerm(term);
				if(termShapes == null || termShapes.isEmpty())
				{
					log.error("Could not find shapes of taxonomy term " + term.getId());
					throw new Exception("Could not find shapes of taxonomy term " + term.getId());
				}
				for(Shape termShape : termShapes)
				{
					if(shapeDao.within(pointShape, termShape))
					{
						if(term.getParent() != null) term.getParent().getName();
						if(term.getTaxonomyTermClass() != null) term.getTaxonomyTermClass().getName();
						term.getTaxonomy().getName();
						term.getCreator().getName();
						res.add(term);
						terms = taxonomyManager.getChildrenOfTerm(term.getId().toString(), true, false);
						located = true;
						break;
					}
				}
				if(located)
					break;
			}
			if(terms == null || terms.isEmpty() || located == false)
				break;
		}
		return res;
	}
	
//  TODO starting from bottom terms - lower perfomance, keep until top terms method is tested
//	@Transactional(readOnly=true)
//	public List<TaxonomyTerm> geoLocate(double x, double y) throws Exception
//	{
//		List<TaxonomyTerm> res = new ArrayList<TaxonomyTerm>();
//		
//		GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
//
//        Point point = gf.createPoint(new Coordinate(x, y));
//        Shape pointShape = new Shape();
//        pointShape.setGeography(point);
//        
//		TaxonomyConfig tcfg = configurationManager.retrieveTaxonomyConfig(TaxonomyConfig.Type.GEOGRAPHYTAXONOMY);
//		//Taxonomy geoTaxonomy = taxonomyManager.findTaxonomyById(tcfg.getId(), false);
//		List<TaxonomyTerm> terms = taxonomyManager.getBottomTermsOfTaxonomy(tcfg.getId(), false);
//		
//		TaxonomyTerm locatedTerm = null;
//		for(TaxonomyTerm term : terms)
//		{
//			Shape termShape = taxonomyManager.getShapeOfTerm(term);
//			if(termShape == null)
//			{
//				log.error("Could not find shape of taxonomy term " + term.getId());
//				throw new Exception("Could not find shape of taxonomy term " + term.getId());
//			}
//			if(shapeDao.within(pointShape, termShape))
//			{
//				locatedTerm = term;
//				break;
//			}
//		}
//		if(terms == null || terms.isEmpty() || locatedTerm == null)
//			return res;
//		
//		TaxonomyTerm term = locatedTerm;
//		while(term != null)
//		{
//			if(term.getParent() != null) term.getParent().getName();
//			if(term.getTaxonomyTermClass() != null) term.getTaxonomyTermClass().getName();
//			term.getTaxonomy().getName();
//			term.getCreator().getSystemName();
//			res.add(term);
//			term = term.getParent();
//		}
//		Collections.reverse(res); //reverse term list so that the hierarchy begins with the widest area
//		return res;
//	}
	
	@Transactional(readOnly=true)
	public List<GeoLocation> termLocate(SearchType searchType, String term, Principal principal) throws Exception {
		List<GeoLocation> res = new ArrayList<GeoLocation>();
		List<Shape> shapes = shapeDao.searchShapes(Collections.singletonList(term));
		Map<String, Project> projectShapeMappings = new HashMap<String, Project>();
		shapes = filterBySearchType(searchType, shapes, principal, projectShapeMappings);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		for(Shape s : shapes)
		{
			boolean nonGeographic = false;
			List<TaxonomyTermShape> ttss = taxonomyTermShapeDao.findByShape(s);
			for(TaxonomyTermShape tts : ttss)
			{
				Taxonomy tax = tts.getTerm().getTaxonomy();
				if(tax.getExtraData() == null || tax.getExtraData().isEmpty())
				{
					nonGeographic = true;
					break;
				}else
				{
					Document ed = db.parse(tax.getExtraData());
					if(!ed.getDocumentElement().hasAttribute("geographic") || Boolean.parseBoolean(ed.getDocumentElement().getAttribute("geographic").trim()) == false)
					{
						nonGeographic = true;
						break;
					}
				}
			}
			if(nonGeographic == false)
				continue;
			Point centroid = s.getGeography().getCentroid();
			List<TaxonomyTerm> terms = geoLocate(centroid.getX(), centroid.getY());
			if(terms == null || terms.isEmpty()) continue;
			
			List<GeoLocationTag> tags = new ArrayList<GeoLocationTag>();
			for(TaxonomyTerm t : terms)
			{
				Shape tts = taxonomyTermShapeDao.findUniqueByTerm(t).getShape();
				Point ttsCentroid = tts.getGeography().getCentroid();
				AttributeInfo tagInfo = retrieveShapeAttributeByTaxonomy(taxonomyTermShapeDao.findUniqueByTerm(t).getShape(), t.getTaxonomy().getId().toString());
				Taxonomy tax = taxonomyManager.findTaxonomyById(tagInfo.getTaxonomy(), false);
				Geometry b = tts.getGeography().getEnvelope();
				Bounds bounds = new Bounds(b.getCoordinates()[0].x, b.getCoordinates()[0].y, b.getCoordinates()[2].x, b.getCoordinates()[2].y, null);
				tags.add(new GeoLocationTag(t.getId().toString(), tagInfo.getValue(), tax.getId().toString(), tax.getName(), ttsCentroid.getX(), ttsCentroid.getY(), bounds));
			}
			Geometry b = s.getGeography().getEnvelope();
			Bounds bounds = new Bounds(b.getCoordinates()[0].x, b.getCoordinates()[0].y, b.getCoordinates()[2].x, b.getCoordinates()[2].y, null);
			if(searchType == SearchType.MAP)
				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds));
			else if(searchType == SearchType.PROJECTS)
			{
				Project shapeProject = projectShapeMappings.get(s.getId().toString());
				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds, shapeProject.getName(), shapeProject.getId().toString()));
			}
		}
		return res;
	}

	@Override
	@Transactional
	public Map<UUID, List<TaxonomyTerm>> getBreadcrumbs(Coords coords) throws Exception {
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
		Point point = geometryFactory.createPoint(new Coordinate(coords.getLon(), coords.getLat()));
		List<Shape> shapes = findShapesEnclosingGeometry(point);
		
		Map<UUID, TaxonomyTerm> termsById = new HashMap<>();
		for(Shape shape : shapes) {
			List<TaxonomyTerm> taxonomyTermsOfShape = findTaxonomyTermShapes(shape);
		
			removeTermsWhichAreAncestorsOfIncoming(termsById, taxonomyTermsOfShape);
			addTermsWhichAreNotAncestorsOfExisting(termsById, taxonomyTermsOfShape);
		}
		
		return termsById.values().stream().
			filter(term -> ExceptionUtils.wrap(() -> getGeographyHierarchy(term.getTaxonomy())).get() != null).
			map(term -> {
				List<TaxonomyTerm> breadcrumb = new ArrayList<>();
				do {
					breadcrumb.add(term);
					term = term.getParent();
				}while(term != null);
				Collections.reverse(breadcrumb);
				return breadcrumb;
			}).
			collect(Collectors.toMap(
					breadcrumb -> ExceptionUtils.wrap(() -> getGeographyHierarchy(breadcrumb.get(0).getTaxonomy())).get().getMainHierarchy().get(0).getId(), 
					breadcrumb -> breadcrumb));
	}
	
	private void removeTermsWhichAreAncestorsOfIncoming(Map<UUID, TaxonomyTerm> termsById, List<TaxonomyTerm> taxonomyTermsOfShape) {
		termsById.keySet().removeAll(
				termsById.values().stream().
					filter(t -> taxonomyTermsOfShape.stream().
							anyMatch(tts -> {
								do {
									if(tts.getParent() != null && tts.getParent().getId().equals(t.getId()))
										return true;
									tts = tts.getParent();
								}while(tts != null);
								return false;
							})).
					map(TaxonomyTerm::getId).
					collect(Collectors.toSet())
		);
	}

	private void addTermsWhichAreNotAncestorsOfExisting(Map<UUID, TaxonomyTerm> termsById,
			List<TaxonomyTerm> taxonomyTermsOfShape) {
		termsById.putAll(
				taxonomyTermsOfShape.stream().
					filter(tts -> termsById.values().stream().
									allMatch(t -> {
										do {
											if(t.getParent() != null && t.getParent().getId().equals(tts.getId()))
												return false;
											t = t.getParent();
										}while(t != null);
										return true;
									})
					).
					collect(Collectors.toMap(TaxonomyTerm::getId, x -> x))
		);
	}
	
	private List<Shape> filterBySearchType(SearchType searchType, List<Shape> shapes, Principal principal, Map<String, Project> projectShapeMappings /*out*/) throws Exception
	{
		List<Shape> filtered = new ArrayList<Shape>();
		List<Project> projects = projectDao.findByCreator(principal);
		List<Shape> projectShapes = new ArrayList<Shape>();
		Set<UUID> filteredIds = new HashSet<UUID>();
		for(Shape s : shapes)
		{
			for(Project p : projects)
			{
				if(s.getId().equals(p.getShape()))
				{
					if(p.getStatus() != ProjectStatus.DELETED)
					{
						projectShapeMappings.put(s.getId().toString(), p);
						projectShapes.add(s);
					}
				}
				else
				{
					if(!filteredIds.contains(s.getId()))
					{
						filtered.add(s);
						filteredIds.add(s.getId());
					}
				}
			}
		}
		if(searchType == SearchType.PROJECTS)
			return projectShapes;
		else
			return filtered;
	}
	
	private Map<String, Map<String, Attribute>> partitionAttributes(Map<String, String> attributes, GeographyHierarchy geographyHierarchy) throws Exception
	{
		Set<String> geographicNames = new HashSet<String>();
		List<List<Taxonomy>> hier = new ArrayList<List<Taxonomy>>(geographyHierarchy.getAlternativeHierarchies());
		hier.add(geographyHierarchy.getMainHierarchy());
		for(List<Taxonomy> h : hier)
		{
			for(Taxonomy t : h)
				geographicNames.add(t.getName());
		}
		
		hier.add(geographyHierarchy.getMainHierarchy());
		Map<String, String> toProcess = new HashMap<String, String>(attributes);
		Map<String, Map<String, Attribute>> partition = new HashMap<String, Map<String, Attribute>>();
		
		while(!toProcess.isEmpty())
		{
			Set<String> toDelete = new HashSet<String>();
			for(Map.Entry<String, String> attr : toProcess.entrySet())
			{
				if(geographicNames.contains(attr.getKey()))
				{
					toDelete.add(attr.getKey());
					continue;
				}
				
				Taxonomy t = taxonomyManager.findTaxonomyByName(attr.getKey(), false);
				if(t == null)
				{
					toDelete.add(attr.getKey());
					continue;
				}
				
				TaxonomyConfig tcfg = configurationManager.retrieveTaxonomyConfigById(t.getId().toString());
				if(tcfg == null)
				{
					toDelete.add(attr.getKey());
					continue;
				}
				
				List<AttributeMappingConfig> mcfgs = configurationManager.getAttributeMappingsForTermId(t.getId().toString());
				String layer = null;
				for(AttributeMappingConfig mcfg : mcfgs)
				{
					if(mcfg.getAttributeValue() == null)
					{
						layer = mcfg.getLayerTermId();
						//Attribute ai = new Attribute(mcfg.getAttributeName(), mcfg.getAttributeType(), t.getId().toString(), attr.getValue());
						break;
					}
				}
				if(layer == null)
				{
					toDelete.add(attr.getKey());
					continue;
				}
				
				Map<String, Attribute> layerAttrs = new HashMap<String, Attribute>();
				mcfgs = configurationManager.getMappingConfigsForLayer(layer);
				for(AttributeMappingConfig mcfg : mcfgs)
				{
					if(mcfg.getTermId() == null)
						continue;
					
					if(mcfg.getAttributeValue() == null)
					{
						Taxonomy mT = taxonomyManager.findTaxonomyById(mcfg.getTermId(), false);
						if(mT == null)
							continue;
						if(!mT.getName().equals(attr.getKey()))
							continue;
						toDelete.add(mT.getName());
						if(!geographicNames.contains(mT.getName()))
						{
							if(!layerAttrs.containsKey(mcfg.getAttributeName()))
								layerAttrs.put(mcfg.getAttributeName(), new Attribute(mcfg.getAttributeName(), mcfg.getAttributeType(), mcfg.getTermId(), attr.getValue()));					
						}
					}
				}
				
				if(!layerAttrs.isEmpty())
				{
					if(!partition.containsKey(layer))
						partition.put(layer, new HashMap<String, Attribute>());
					partition.get(layer).putAll(layerAttrs);
				}
				
			}
			for(String td : toDelete)
				toProcess.remove(td);
		}
		
		return partition;
	}
	
	@Transactional(readOnly=true)
	public List<GeoLocation> attributeLocate(SearchType searchType, Map<String, String> attributes, Principal principal) throws Exception {
		GeographyHierarchy hier = getDefaultGeographyHierarchy();
		Taxonomy mostSpecificGeog = null;
		String mostSpecificGeogTerm = null;
		Iterator<Taxonomy> hierIt = hier.getMainHierarchy().iterator();
		int i=0;
		int tIndex = -1;
		while(hierIt.hasNext())
		{
			Taxonomy t = hierIt.next();
			if(attributes.containsKey(t.getName()))
			{
				mostSpecificGeog = t;
				mostSpecificGeogTerm = attributes.get(t.getName());
				attributes.remove(t.getName());
				tIndex =  i;
			}
			i++;
		}
		
		int maxIndex = tIndex;
		for(List<Taxonomy> alt : hier.getAlternativeHierarchies())
		{
			Iterator<Taxonomy> altIt = alt.iterator();
			i=0;
			int altIndex = 0;
			while(altIt.hasNext())
			{
				Taxonomy t = altIt.next();
				if(attributes.containsKey(t.getName()))
				{
					altIndex = i;
					if(altIndex > maxIndex)
					{
						maxIndex = altIndex;
						mostSpecificGeog = t;
						mostSpecificGeogTerm = attributes.get(t.getName());
						altIndex = i;
					}
					attributes.remove(t.getName());
				}
				i++;
			}
		}
		
		TaxonomyTerm tt = taxonomyManager.findTermByName(mostSpecificGeogTerm, false);
		Shape shapeTerm = taxonomyManager.getShapeOfTerm(tt);
		
		Map<String, Project> projectShapeMappings = new HashMap<String, Project>();
		List<Shape> foundShapes = new ArrayList<Shape>();
		Map<String, Map<String, Attribute>> partition = partitionAttributes(attributes, hier);
		/*if(searchType == SearchType.MAP && partition.isEmpty())
			throw new Exception("No attributes were specified");*/
		
		if(!partition.isEmpty())
		{
			Map.Entry<String, Map<String, Attribute>> first = partition.entrySet().iterator().next();
			foundShapes = shapeDao.searchShapesWithinByAttributes(first.getValue(), shapeTerm);
			foundShapes = filterBySearchType(searchType, foundShapes, principal, projectShapeMappings);
			partition.remove(first.getKey());
		}else
		{
			foundShapes = shapeDao.searchShapesWithinByAttributes(new HashMap<String, Attribute>(), shapeTerm);
			foundShapes = filterBySearchType(searchType, foundShapes, principal, projectShapeMappings);
		}
		
		for(Map.Entry<String, Map<String, Attribute>> layerAttrs : partition.entrySet())
		{
			List<Shape> res = shapeDao.searchShapesWithinByAttributes(layerAttrs.getValue(), shapeTerm);
			Map<String, Shape> toAdd = new HashMap<String, Shape>();
			for(Shape rs : res)
			{
				for(Shape fs : foundShapes)
				{
					if(shapeDao.within(fs, rs))
					{
						if(!toAdd.containsKey(fs.getId().toString()))
							toAdd.put(fs.getId().toString(), fs);
					}
					else if(shapeDao.within(rs, fs))
					{
						if(!toAdd.containsKey(rs.getId().toString()))
							toAdd.put(rs.getId().toString(), rs);
					}
				}
			}
			foundShapes = new ArrayList<Shape>(toAdd.values());
			foundShapes = filterBySearchType(searchType, foundShapes, principal, projectShapeMappings);
		}
		
		List<GeoLocation> res = new ArrayList<GeoLocation>();
		
		for(Shape s : foundShapes)
		{
			/*boolean nonGeographic = false;
			List<TaxonomyTermShape> ttss = taxonomyTermShapeDao.findByShape(s);
			for(TaxonomyTermShape tts : ttss)
			{
				Taxonomy tax = tts.getTerm().getTaxonomy();
				if(tax.getExtraData() == null || tax.getExtraData().isEmpty() || !tax.getExtraData().contains("geographic=\"true\""))
				{
					nonGeographic = true;
					break;
				}
			}
			if(nonGeographic == false && ttss.size() > 0)
				continue;*/
			Point centroid = s.getGeography().getCentroid();
			List<TaxonomyTerm> terms = geoLocate(centroid.getX(), centroid.getY());
			if(terms == null || terms.isEmpty()) continue;
			
			List<GeoLocationTag> tags = new ArrayList<GeoLocationTag>();
			for(TaxonomyTerm t : terms)
			{
				Shape tts = taxonomyTermShapeDao.findUniqueByTerm(t).getShape();
				Point ttsCentroid = tts.getGeography().getCentroid();
				AttributeInfo tagInfo = retrieveShapeAttributeByTaxonomy(taxonomyTermShapeDao.findUniqueByTerm(t).getShape(), t.getTaxonomy().getId().toString());
				Taxonomy tax = taxonomyManager.findTaxonomyById(tagInfo.getTaxonomy(), false);
				Geometry b = tts.getGeography().getEnvelope();
				Bounds bounds = new Bounds(b.getCoordinates()[0].x, b.getCoordinates()[0].y, b.getCoordinates()[2].x, b.getCoordinates()[2].y, null);
				tags.add(new GeoLocationTag(t.getId().toString(), tagInfo.getValue(), tax.getId().toString(), tax.getName(), ttsCentroid.getX(), ttsCentroid.getY(), bounds));
			}
			Geometry b = s.getGeography().getEnvelope();
			Bounds bounds = new Bounds(b.getCoordinates()[0].x, b.getCoordinates()[0].y, b.getCoordinates()[2].x, b.getCoordinates()[2].y, null);
			if(searchType == SearchType.MAP)
				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds));
			else if(searchType == SearchType.PROJECTS)
			{
				Project shapeProject = projectShapeMappings.get(s.getId().toString());
				res.add(new GeoLocation(tags, centroid.getX(), centroid.getY(), bounds, shapeProject.getName(), shapeProject.getId().toString()));
			}
		}
		return res;
	}
	
	@Override
	@Transactional
	public void createShapeAssociationsWithLayerTerm(TaxonomyTerm taxonomyTerm, List<Shape> shapes){
		shapes.stream().forEach(shape -> {
			ShapeTerm shapeTerm = new ShapeTerm();
			shapeTerm.setCreator(this.principalDao.systemPrincipal());
			shapeTerm.setShape(shape);
			shapeTerm.setTerm(taxonomyTerm);
			this.shapeTermDao.create(shapeTerm);
		});
	}
}
