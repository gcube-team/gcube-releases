package gr.cite.gaap.servicelayer;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import gr.cite.gaap.datatransferobjects.TaxonomyTermInfo;
import gr.cite.gaap.datatransferobjects.TaxonomyTermLinkInfo;
import gr.cite.gaap.utilities.ExceptionUtils;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectTermDao;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.shape.dao.ShapeDocumentDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLink.Verb;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyTermDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyTermLinkDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.TaxonomyTermShapeDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.definition.TaxonomyData;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermLinkPK;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTermShape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaxonomyManager {
	private static final Logger log = LoggerFactory.getLogger(TaxonomyManager.class);
	
	private TaxonomyDao taxonomyDao;
	private TaxonomyTermDao taxonomyTermDao;
	private TaxonomyTermLinkDao taxonomyTermLinkDao;
	private TaxonomyTermShapeDao taxonomyTermShapeDao;
	private ShapeDocumentDao shapeDocumentDao;
	private ProjectTermDao projectTermDao;
	
	private Object taxonomyDataCtxLock = new Object();
	private JAXBContext taxonomyDataCtx = null;
	
	
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
	public void setProjectTermDao(ProjectTermDao projectTermDao) {
		this.projectTermDao = projectTermDao;
	}
	
	@Inject
	public void setShapeDocumentDao(ShapeDocumentDao shapeDocumentDao) {
		this.shapeDocumentDao = shapeDocumentDao;
	}
	
	private Unmarshaller getTaxonomyDataUnmarshaller() throws JAXBException {
		synchronized(taxonomyDataCtxLock)
		{
			if(taxonomyDataCtx == null) taxonomyDataCtx = JAXBContext.newInstance(TaxonomyData.class);
			return taxonomyDataCtx.createUnmarshaller();
		}
	}
	
	private Marshaller getTaxonomyDataMarshaller() throws JAXBException {
		synchronized(taxonomyDataCtxLock)
		{
			if(taxonomyDataCtx == null) taxonomyDataCtx = JAXBContext.newInstance(TaxonomyData.class);
			Marshaller marshaller = taxonomyDataCtx.createMarshaller();
			marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
			return marshaller;
		}
	}
	
	public TaxonomyData unmarshalTaxonomyData(String data) {
		return ExceptionUtils.wrap(() -> (TaxonomyData)getTaxonomyDataUnmarshaller().unmarshal(new StringReader(data))).get();
	}
	
	public String marshalTaxonomyData(TaxonomyData data) {
		return ExceptionUtils.wrap(() -> {
			StringWriter sw = new StringWriter();
			Marshaller marshaller = getTaxonomyDataMarshaller();
			marshaller.marshal(data, sw);
			return sw.toString().replace("\r\n", "\n").replace("\n", "&#10;");
		}).get();
	}
	
	private void getTaxonomyDetails(Taxonomy t) {
		t.getCreator().getPrincipalData().getFullName();
		if(t.getTaxonomyClass() != null) t.getTaxonomyClass().getName();
	}
	
	private void getTaxonomyDetails(List<Taxonomy> ts) {
		for(Taxonomy t : ts)
			getTaxonomyDetails(t);
	}
	
	public void getTermDetails(TaxonomyTerm t) {
		t.getCreator().getPrincipalData().getFullName();
		t.getTaxonomy().getName();
		if(t.getTaxonomyTermClass() != null)
		{
			t.getTaxonomyTermClass().getName();
			t.getTaxonomyTermClass().getTaxonomy().getName();
		}
		if(t.getParent() != null)
		{
			t.getParent().getName();
			t.getParent().getTaxonomy().getName();
		}
		t.getExtraData();
	}
	
	private void getTermDetails(List<TaxonomyTerm> ts)
	{
		for(TaxonomyTerm t : ts)
			getTermDetails(t);
	}
	
	private void getTermLinkDetails(List<TaxonomyTermLink> ttls)
	{
		for(TaxonomyTermLink ttl : ttls)
		{
			getTermDetails(ttl.getSourceTerm());
			getTermDetails(ttl.getDestinationTerm());
		}
	}
	
	@Transactional(readOnly = true)
	public Taxonomy findTaxonomyById(String id, boolean loadDetails)
	{
		Taxonomy t = taxonomyDao.read(UUID.fromString(id));
		if(loadDetails) getTaxonomyDetails(Collections.singletonList(t));
		return t;
	}
	
	@Transactional(readOnly = true)
	public TaxonomyTerm findTermById(String id, boolean loadDetails)
	{
		TaxonomyTerm tt = taxonomyTermDao.read(UUID.fromString(id));
		if(loadDetails) getTermDetails(Collections.singletonList(tt));
		return tt;
	}
	
	@Transactional(readOnly = true)
	public Taxonomy findTaxonomyByName(String name, boolean loadDetails)
	{
		List<Taxonomy> res =  taxonomyDao.findByName(name);
		if(res != null && res.size() > 1) throw new IllegalArgumentException("More than one taxonomies with name \"" + name + "\" were found");
		if(res == null || res.isEmpty()) return null;
		if(loadDetails) getTaxonomyDetails(res);
		return res.get(0);
	}
	
	@Transactional(readOnly = true)
	public TaxonomyTerm findTermByName(String name, boolean loadDetails) throws Exception
	{
		List<TaxonomyTerm> res =  taxonomyTermDao.findByName(name);
		if(res != null && res.size() > 1) throw new Exception("More than one taxonomy terms with name \"" + name + "\" were found");
		if(res == null || res.isEmpty()) return null;
		if(loadDetails) getTermDetails(res);
		return res.get(0);
	}
	
	@Transactional(readOnly = true)
	public TaxonomyTerm findTermByNameAndTaxonomy(String name, String taxonomyName, boolean loadDetails)
	{
		Taxonomy t = findTaxonomyByName(taxonomyName, false);
		if(t == null) throw new IllegalArgumentException("Taxonomy " + taxonomyName + " was not found");
		List<TaxonomyTerm> res =  taxonomyTermDao.findByNameAndTaxonomy(name, t);
		if(res != null && res.size() > 1) throw new IllegalArgumentException("More than one taxonomy terms with name \"" + name + "\" were found");
		if(res == null || res.isEmpty()) return null;
		res.forEach(x -> x.getTaxonomy().getName());
		if(loadDetails) getTermDetails(res);
		return res.get(0);
	}
	
	@Transactional(readOnly = true)
	public TaxonomyTerm findTermByNameAndTaxonomies(String taxonomyTermName, List<String> taxonomyNames, boolean loadDetails) throws Exception
	{
		List<Taxonomy> taxonomies = new ArrayList<Taxonomy>();
		for (String taxonomyName : taxonomyNames){
			Taxonomy taxonomy = this.findTaxonomyByName(taxonomyName, loadDetails);
			if (taxonomy == null){
				throw new Exception("Taxonomy " + taxonomyName + " was not found.");
			}
			taxonomies.add(taxonomy);
		}
		List<TaxonomyTerm> taxonomyTerms =  taxonomyTermDao.findByNameAndTaxonomies(taxonomyTermName, taxonomies);
		if (taxonomyTerms != null && taxonomyTerms.size() > 1) {
			throw new Exception("More than one taxonomy terms with name \"" + taxonomyTermName + "\" were found");
		}
		if (taxonomyTerms == null || taxonomyTerms.isEmpty()){
			return null;
		}
		if (loadDetails){ 
			getTermDetails(taxonomyTerms);
		} else {
			//taxonomyTerms.forEach(x -> x.getTaxonomy().getName());
		}
		return taxonomyTerms.get(0);
	}
	
	@Transactional(readOnly = true)
	public TaxonomyTerm findTermByNameAndTaxonomies(String taxonomyTermName, List<Taxonomy> taxonomies) throws Exception
	{		
		List<TaxonomyTerm> taxonomyTerms =  taxonomyTermDao.findByNameAndTaxonomies(taxonomyTermName, taxonomies);
		if (taxonomyTerms != null && taxonomyTerms.size() > 1) {
			throw new Exception("More than one taxonomy terms with name \"" + taxonomyTermName + "\" were found");
		}
		if (taxonomyTerms == null || taxonomyTerms.isEmpty()){
			return null;
		}
		return taxonomyTerms.get(0);
	}
	
	@Transactional(readOnly = true)
	public Map<String, TaxonomyTerm> findTermByNameAndTaxonomies(List<Taxonomy> taxonomies) throws Exception {		
		List<TaxonomyTerm> taxonomyTerms =  taxonomyTermDao.findAllTermsByTaxonomies(taxonomies);

		if (taxonomyTerms == null || taxonomyTerms.isEmpty()){
			throw new Exception("No Taxonomy Terms were found");
		}
		
		Map<String, TaxonomyTerm> results = new HashMap<>();
		for(TaxonomyTerm taxonomyTerm : taxonomyTerms){
			results.put(taxonomyTerm.getName(), taxonomyTerm);
		}
		
		return results;
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTerm> findAutoCreatedWithParent(String parentTaxonomyName, boolean loadDetails) throws Exception
	{
		Taxonomy t = findTaxonomyByName(parentTaxonomyName, false);
		if(t == null) throw new Exception("Taxonomy " + parentTaxonomyName + " not found");
		List<TaxonomyTerm> res = taxonomyTermDao.findAutoCreatedWithParent(t);
		if(loadDetails) getTermDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<Taxonomy> allTaxonomies(boolean loadDetails) throws Exception
	{
		List<Taxonomy> res = taxonomyDao.getAll();
		if(loadDetails) getTaxonomyDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<Taxonomy> activeTaxonomies(boolean loadDetails) throws Exception
	{
		List<Taxonomy> res = taxonomyDao.getActive();
		if(loadDetails) getTaxonomyDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTerm> allTerms(boolean loadDetails) throws Exception
	{
		List<TaxonomyTerm> res = taxonomyTermDao.getAll();
		if(loadDetails) getTermDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<String> listTaxonomies(boolean active) throws Exception
	{
		if(!active) return taxonomyDao.listNames();
		else return taxonomyDao.listNamesOfActive();
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTerm> getTermsOfTaxonomy(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<TaxonomyTerm> res = null;
		Taxonomy t = taxonomyDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy " + id + " does not exist");
		if(!active) res = taxonomyDao.getTerms(t);
		else res = taxonomyDao.getActiveTerms(t);
		
		if(loadDetails) getTermDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<Taxonomy> getClassDescendantsOfTaxonomy(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<Taxonomy> res = null;
		Taxonomy t = taxonomyDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy " + id + " does not exist");
		res = taxonomyDao.getInstances(t);
		
		if(active) res = filterTaxonomyByActive(res);
		if(loadDetails) getTaxonomyDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTerm> getTopmostTermsOfTaxonomy(String id, boolean loadDetails)
	{
		List<TaxonomyTerm> res = null;
		Taxonomy t = taxonomyDao.read(UUID.fromString(id));
		if(t == null) throw new IllegalArgumentException("Taxonomy " + id + " does not exist");
		res = taxonomyDao.getTopmostTerms(t);
		
		if(loadDetails) getTermDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTerm> getBottomTermsOfTaxonomy(String id, boolean loadDetails) throws Exception
	{
		List<TaxonomyTerm> res = null;
		Taxonomy t = taxonomyDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy " + id + " does not exist");
		res = taxonomyDao.getBottomTerms(t);
		
		if(loadDetails) getTermDetails(res);
		return res;
	}
	
	private List<TaxonomyTerm> filterTermByActive(List<TaxonomyTerm> tts)
	{
		List<TaxonomyTerm> res = new ArrayList<TaxonomyTerm>();
		for(TaxonomyTerm tt : tts)
		{
			if(tt.getIsActive()) res.add(tt);
		}
		return res;
	}
	
	private List<Taxonomy> filterTaxonomyByActive(List<Taxonomy> ts)
	{
		List<Taxonomy> res = new ArrayList<Taxonomy>();
		for(Taxonomy t : ts)
		{
			if(t.getIsActive()) res.add(t);
		}
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTerm> getChildrenOfTerm(String id, boolean active, boolean loadDetails)
	{
		List<TaxonomyTerm> res = null;
		TaxonomyTerm t = taxonomyTermDao.read(UUID.fromString(id));
		if(t == null) throw new RuntimeException("Taxonomy term " + id + " does not exist");
		res = taxonomyTermDao.getChildren(t);
		
		if(active) res = filterTermByActive(res);
		if(loadDetails) getTermDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTerm> getSiblingsOfTerm(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<TaxonomyTerm> res = null;
		TaxonomyTerm t = taxonomyTermDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy term " + id + " does not exist");
		res = taxonomyTermDao.getSiblings(t);
		
		if(active) res = filterTermByActive(res);
		if(loadDetails) getTermDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTerm> getClassDescendantsOfTerm(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<TaxonomyTerm> res = null;
		TaxonomyTerm t = taxonomyTermDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy term " + id + " does not exist");
		res = taxonomyTermDao.getClassDescendants(t);
		
		if(active) res = filterTermByActive(res);
		if(loadDetails) getTermDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTerm> getClassSiblingsOfTerm(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<TaxonomyTerm> res = null;
		TaxonomyTerm t = taxonomyTermDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy term " + id + " does not exist");
		res = taxonomyTermDao.getClassSiblings(t);
		
		if(active) res = filterTermByActive(res);
		if(loadDetails) getTermDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<String> listTermsOfTaxonomy(String id, boolean active) throws Exception
	{
		Taxonomy t = taxonomyDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy " + id + " does not exist");
		if(active) return taxonomyDao.listTerms(t);
		else return taxonomyDao.listActiveTerms(t);
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTermLink> getTermLinksOfTaxonomy(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<TaxonomyTermLink> res = new ArrayList<TaxonomyTermLink>();
		Taxonomy t = taxonomyDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy " + id + " does not exist");
		if(!active) res = taxonomyDao.getTermLinks(t);
		else res = taxonomyDao.getActiveTermLinks(t);
		
		if(loadDetails) getTermLinkDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public Shape getShapeOfTerm(TaxonomyTerm tt, boolean loadDetails) throws Exception {
		Shape s =  taxonomyTermDao.getShape(tt);
		if(loadDetails) {
			s.getCreator().getName();
			if(s.getCreator().getTenant() != null) s.getCreator().getTenant().getName();
			if(s.getShapeImport() != null) s.getShapeImport().getShapeImport();
		}
		return s;
	}
	
	@Transactional(readOnly = true)
	public List<Shape> getShapesOfTerm(TaxonomyTerm tt, boolean loadDetails) throws Exception
	{
		List<Shape> shapes =  taxonomyTermDao.getShapes(tt);
		if(loadDetails)
		{
			for(Shape s : shapes)
			{
				s.getCreator().getName();
				if(s.getCreator().getTenant() != null) s.getCreator().getTenant().getName();
				if(s.getShapeImport() != null) s.getShapeImport().getShapeImport();
			}
		}
		return shapes;
	}
	
	@Transactional(readOnly = true)
	public Shape getShapeOfTerm(TaxonomyTerm tt) throws Exception
	{
		return getShapeOfTerm(tt, false);
	}

	@Transactional(readOnly = true)
	public List<Shape> getShapesOfTerm(TaxonomyTerm tt) throws Exception
	{
		return getShapesOfTerm(tt, false);
	}
	
	@Transactional
	public void updateTaxonomy(Taxonomy t, String originalName, boolean create)
	{
		if(create)
		{
			Taxonomy ex = null;
			if(t.getId() != null) ex = findTaxonomyById(t.getId().toString(), false);
			else ex = findTaxonomyByName(t.getName(), false);
			if(ex != null)
			{
				log.error("Taxonomy " + t.getName() + " already exists");
				throw new IllegalArgumentException("Taxonomy " + t.getName() + " already exists");
			}
			
			taxonomyDao.create(t);
		}
		else
		{
			Taxonomy ex = null;
			if(t.getId() != null) ex = findTaxonomyById(t.getId().toString(), false);
			else ex = findTaxonomyByName(originalName, false);
			
			if(ex == null)
			{
				log.error("Taxonomy " + t.getName() + " does not exist");
				throw new IllegalArgumentException("Taxonomy " + t.getName() + " does not exist");
			}
			t.setId(ex.getId());
			t.setCreationDate(ex.getCreationDate());
			
			taxonomyDao.update(t);
		}
	}
	
	@Transactional
	public void updateTerm(TaxonomyTerm t, String originalName, String originalTaxonomyName, boolean create)
	{
		if(create)
		{
			TaxonomyTerm ex = null;
			if(t.getId() != null) ex = findTermById(t.getId().toString(), false);
			else ex = findTermByNameAndTaxonomy(t.getName(), t.getTaxonomy().getName(), false);
			if(ex != null)
			{
				log.error("Taxonomy term " + t.getName() + " already exists");
				throw new IllegalArgumentException("Taxonomy term " + t.getName() + " already exists");
			}
			
			taxonomyTermDao.create(t);
			
			if(t.getParent() != null)
			{
				List<TaxonomyTerm> siblings = taxonomyTermDao.getSiblings(t);
				int max = 0;
				for(TaxonomyTerm s : siblings)
				{
					if(s.getOrder() > max)
						max = s.getOrder();
				}
				//if order has not been set or exceeds maximum, set to maximum + 1
				if(t.getOrder() <= 0 || t.getOrder() > max)
					t.setOrder(max+1);
				else
				{
					//reorder if necessary
					for(TaxonomyTerm s : siblings)
					{
						if(s.getOrder() >= t.getOrder())
						{
							s.setOrder(s.getOrder()+1);
							taxonomyTermDao.update(s);
						}
					}
				}
			}else //order does not matter for terms with no parent
				t.setOrder(0);
			taxonomyTermDao.update(t);
		}
		else
		{
			TaxonomyTerm ex = null;
			if(t.getId() != null) ex = findTermById(t.getId().toString(), false);
			else ex = findTermByNameAndTaxonomy(originalName, originalTaxonomyName, false);
			
			if(ex == null)
			{
				log.error("Taxonomy term" + t.getName() + " does not exist");
				throw new IllegalArgumentException("Taxonomy term " + t.getName() + " does not exist");
			}
			t.setId(ex.getId());
			t.setCreationDate(ex.getCreationDate());
			t.setCreator(ex.getCreator());
			
			if(t.getOrder() <= 0) 
				t.setOrder(ex.getOrder()); //do not update order if it is not set
			else
			{
				List<TaxonomyTerm> siblings = taxonomyTermDao.getSiblings(t);
				int max = 0;
				for(TaxonomyTerm s : siblings)
				{
					if(s.getOrder() > max)
						max = s.getOrder();
				}
				if(t.getOrder() > max)
					t.setOrder(max+1); //if order exceeds maximum, set to maximum + 1
				else
				{
					//reorder if necessary
					for(TaxonomyTerm s : siblings)
					{
						if(s.getOrder() >= t.getOrder())
						{
							s.setOrder(s.getOrder()+1);
							taxonomyTermDao.update(s);
						}
					}
				}
			}
			taxonomyTermDao.update(t);
		}
	}
	
	@Transactional
	public void updateTermLink(String sourceTermTaxonomy, String sourceTerm, String destTermTaxonomy, String destTerm, 
				String origSourceTermTaxonomy, String origSourceTerm, String origDestTermTaxonomy, String origDestTerm,
				Verb verb, Principal creator, boolean create) throws Exception
	{
		TaxonomyTermLink ex = null;
		
		if(create)
		{
			TaxonomyTerm stt = findTermByNameAndTaxonomy(sourceTerm, sourceTermTaxonomy, false);
			TaxonomyTerm dtt = findTermByNameAndTaxonomy(destTerm, destTermTaxonomy, false);
			if(stt == null) throw new Exception("Taxonomy term " + sourceTermTaxonomy + ":" + sourceTerm + " does not exist");
			if(dtt == null) throw new Exception("Taxonomy term " + destTermTaxonomy + ":" + destTerm + " does not exist");
			
			ex = taxonomyTermLinkDao.read(new TaxonomyTermLinkPK(stt.getId(), dtt.getId()));

			if(verb == null) throw new Exception("Verb is mandatory for taxonomy term links");
						
			if(ex != null)
			{
				log.error("Taxonomy term link " + sourceTermTaxonomy + ":" + sourceTerm + "->" + 
							destTermTaxonomy + ":" + destTerm + " already exists");
				throw new Exception("Taxonomy term link " + sourceTermTaxonomy+ ":" + sourceTerm + "->" + 
							destTermTaxonomy + ":" + destTerm + " already exists");
			}
			
			TaxonomyTermLink ttl = new TaxonomyTermLink();
			ttl.setSourceTerm(stt);
			ttl.setDestinationTerm(dtt);
			ttl.setVerb(verb);
			ttl.setCreator(creator);
			taxonomyTermLinkDao.create(ttl);
			
		}
		else
		{
			TaxonomyTerm stt = findTermByNameAndTaxonomy(origSourceTerm, origSourceTermTaxonomy, false);
			TaxonomyTerm dtt = findTermByNameAndTaxonomy(origDestTerm, origDestTermTaxonomy, false);
			if(stt == null) throw new Exception("Taxonomy term " + origSourceTermTaxonomy + ":" + origSourceTerm + " does not exist");
			if(dtt == null) throw new Exception("Taxonomy term " + origDestTermTaxonomy + ":" + origDestTerm + " does not exist");
			ex = taxonomyTermLinkDao.read(new TaxonomyTermLinkPK(stt.getId(), dtt.getId()));

			if(ex == null)
			{
				log.error("Taxonomy term link " + origSourceTermTaxonomy + ":" + origSourceTerm + "->" + 
							origDestTermTaxonomy + ":" + origDestTerm + " does not exist");
				throw new Exception("Taxonomy term link " + origSourceTermTaxonomy+ ":" + origSourceTerm + "->" + 
							origDestTermTaxonomy + ":" + origDestTerm + " does not exist");
			}
			
			stt = findTermByNameAndTaxonomy(sourceTerm, sourceTermTaxonomy, false);
			dtt = findTermByNameAndTaxonomy(destTerm, destTermTaxonomy, false);
			if(stt == null) throw new Exception("Taxonomy term " + sourceTermTaxonomy + ":" + sourceTerm + " does not exist");
			if(dtt == null) throw new Exception("Taxonomy term " + destTermTaxonomy + ":" + destTerm + " does not exist");

			if(verb != null) ex.setVerb(verb);
			ex.setSourceTerm(stt);
			ex.setDestinationTerm(dtt);
			
			taxonomyTermLinkDao.update(ex);
		}
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void deleteTaxonomies(List<String> taxonomies) throws Exception
	{
		for(String t : taxonomies)
		{
			Taxonomy tax = findTaxonomyByName(t, false);
			if(tax == null) throw new Exception("Taxonomy " + t + " not found");
			List<TaxonomyTerm> tts =  getTermsOfTaxonomy(tax.getId().toString(), false, false);
			//Delete all terms of taxonomy
			for(TaxonomyTerm tt : tts)
			{
				List<TaxonomyTerm> desc =  taxonomyTermDao.getClassDescendants(tt);
				for(TaxonomyTerm d : desc)
				{
					d.setTaxonomyTermClass(tt.getTaxonomyTermClass());
					taxonomyTermDao.update(d);
				}
				desc =  taxonomyTermDao.getChildren(tt);
				for(TaxonomyTerm d : desc)
				{
					d.setParent(tt.getParent());
					taxonomyTermDao.update(d);
				}
				
				List<TaxonomyTerm> linked = taxonomyTermDao.getLinked(tt);
				for(TaxonomyTerm l : linked)
				{
					TaxonomyTermLinkPK linkKey = new TaxonomyTermLinkPK(tt.getId(), l.getId());
					TaxonomyTermLink link = taxonomyTermLinkDao.read(linkKey);
					if(link != null) taxonomyTermLinkDao.delete(link);
					
					linkKey = new TaxonomyTermLinkPK(l.getId(), tt.getId());
					link = taxonomyTermLinkDao.read(linkKey);
					if(link != null) taxonomyTermLinkDao.delete(link);
				}
				projectTermDao.deleteByTerm(tt);
				taxonomyTermDao.delete(tt);
			}
			
			List<Taxonomy> desc = taxonomyDao.getInstances(tax);
			for(Taxonomy d : desc)
			{
				d.setTaxonomyClass(tax.getTaxonomyClass());
				taxonomyDao.update(d);
			}
			
			taxonomyDao.delete(tax);
		}
	}
	
	@Transactional
	public void deleteTerm(TaxonomyTerm tt) throws Exception
	{
		//reorder if necessary
		if(tt.getOrder() > 1)
		{
			List<TaxonomyTerm> siblings = taxonomyTermDao.getSiblings(tt);
			for(TaxonomyTerm s : siblings)
			{
				if(s.getOrder() > 0 && s.getOrder() >= tt.getOrder())
					s.setOrder(s.getOrder()-1);
				taxonomyTermDao.update(s);
			}
		}
		
		List<TaxonomyTerm> desc =  taxonomyTermDao.getClassDescendants(tt);
		for(TaxonomyTerm d : desc)
		{
			d.setTaxonomyTermClass(tt.getTaxonomyTermClass());
			taxonomyTermDao.update(d);
		}
		desc =  taxonomyTermDao.getChildren(tt);
		for(TaxonomyTerm d : desc)
		{
			d.setParent(tt.getParent());
			taxonomyTermDao.update(d);
		}
		
		List<TaxonomyTerm> linked = taxonomyTermDao.getLinked(tt);
		for(TaxonomyTerm l : linked)
		{
			TaxonomyTermLinkPK linkKey = new TaxonomyTermLinkPK(tt.getId(), l.getId());
			TaxonomyTermLink link = taxonomyTermLinkDao.read(linkKey);
			if(link != null) taxonomyTermLinkDao.delete(link);
			
			linkKey = new TaxonomyTermLinkPK(l.getId(), tt.getId());
			link = taxonomyTermLinkDao.read(linkKey);
			if(link != null) taxonomyTermLinkDao.delete(link);
		}
		
		taxonomyTermDao.update(tt); //save transient object so that TaxonomyTermShapes referencing to it can be deleted
		List<TaxonomyTermShape> ttss =  taxonomyTermShapeDao.findByTerm(tt);
		if(ttss != null) 
		{
			for(TaxonomyTermShape tts : ttss)
			{
				shapeDocumentDao.deleteByTaxonomyTermShape(tts);
				taxonomyTermShapeDao.delete(tts);
			}
		}
		
		projectTermDao.deleteByTerm(tt);
		taxonomyTermDao.delete(tt);
	}
	
	@Transactional
	public void deleteTerms(List<TaxonomyTermInfo> terms) throws Exception
	{
		boolean error = false;
		for(TaxonomyTermInfo t : terms)
		{
			TaxonomyTerm tt = findTermByNameAndTaxonomy(t.getTerm(), t.getTaxonomy(), false);
			if(tt != null)
			{
				deleteTerm(tt);
			}
			else error = true;
		}
		if(error) throw new Exception("Could not delete all taxonomy terms");
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void deleteTermLinks(List<TaxonomyTermLinkInfo> links) throws Exception
	{
		for(TaxonomyTermLinkInfo l : links)
		{
			TaxonomyTerm st = findTermByNameAndTaxonomy(l.getSourceTerm(), l.getSourceTermTaxonomy(), false);
			TaxonomyTerm dt = findTermByNameAndTaxonomy(l.getDestTerm(), l.getDestTermTaxonomy(), false);
			TaxonomyTermLink ttl = taxonomyTermLinkDao.read(
									new TaxonomyTermLinkPK(st.getId(), dt.getId()));
			if(ttl == null)
			{
				log.error("Taxonomy term link " + l.getSourceTermTaxonomy() + ":" + l.getSourceTerm() + "->" +
							l.getDestTermTaxonomy() + ":" + l.getDestTerm() + " was not found");
				throw new Exception("Taxonomy term link " + l.getSourceTermTaxonomy() + ":" + l.getSourceTerm() + "->" +
							l.getDestTermTaxonomy() + ":" + l.getDestTerm() + " was not found");
			}
			taxonomyTermLinkDao.delete(ttl);
		}
	}
}
