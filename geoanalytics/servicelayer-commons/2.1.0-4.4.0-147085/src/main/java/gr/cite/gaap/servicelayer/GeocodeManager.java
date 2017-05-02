package gr.cite.gaap.servicelayer;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import gr.cite.clustermanager.layers.DataMonitor;
import gr.cite.gaap.datatransferobjects.LayerInfo;
import gr.cite.gaap.datatransferobjects.TaxonomyTermLinkInfo;
import gr.cite.gaap.utilities.ExceptionUtils;
import gr.cite.geoanalytics.dataaccess.entities.geocode.Geocode;
import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;
import gr.cite.geoanalytics.dataaccess.entities.geocode.TaxonomyTermLink;
import gr.cite.geoanalytics.dataaccess.entities.geocode.TaxonomyTermLinkPK;
import gr.cite.geoanalytics.dataaccess.entities.geocode.TaxonomyTermLink.Verb;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeDao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.GeocodeSystemDao;
import gr.cite.geoanalytics.dataaccess.entities.geocode.dao.TaxonomyTermLinkDao;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.layer.dao.LayerDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
//import gr.cite.geoanalytics.dataaccess.entities.taxonomy.dao.GeocodeShapeDao;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.definition.TaxonomyData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GeocodeManager {
	private static final Logger log = LoggerFactory.getLogger(GeocodeManager.class);
	
	private GeospatialBackendClustered geospatialBackendClustered;
	
	private GeocodeSystemDao geocodeSystemDao;
	private GeocodeDao geocodeDao;
	private TaxonomyTermLinkDao taxonomyTermLinkDao;
	private LayerDao layerDao;
	
	private Object taxonomyDataCtxLock = new Object();
	private JAXBContext taxonomyDataCtx = null;
	
	
	@Inject
	public void setGeospatialBackendClustered(GeospatialBackendClustered geospatialBackendClustered) {
		this.geospatialBackendClustered = geospatialBackendClustered;
	}
	
	@Inject
	public void setGeocodeSystemDao(GeocodeSystemDao geocodeSystemDao) {
		this.geocodeSystemDao = geocodeSystemDao;
	}
	
	@Inject
	public void setLayerDao(LayerDao layerDao) {
		this.layerDao = layerDao;
	}
	
	@Inject
	public void setGeocodeDao(GeocodeDao geocodeDao) {
		this.geocodeDao = geocodeDao;
	}
	
	@Inject
	public void setTaxonomyTermLinkDao(TaxonomyTermLinkDao taxonomyTermLinkDao) {
		this.taxonomyTermLinkDao = taxonomyTermLinkDao;
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
	
	private void getGeocodeSystemDetails(GeocodeSystem t) {
		t.getCreator().getPrincipalData().getFullName();
		if(t.getTaxonomyClass() != null) t.getTaxonomyClass().getName();
	}
	
	private void getGeocodeSystemDetails(List<GeocodeSystem> ts) {
		for(GeocodeSystem t : ts)
			getGeocodeSystemDetails(t);
	}
	
	public void getLayerDetails(Layer layer) {
		layer.getCreator().getPrincipalData().getFullName();
		layer.getExtraData();
	}
	
	public void getLayerDetails(List<Layer> layers)
	{
		for(Layer layer : layers)
			getLayerDetails(layer);
	}
	
	public void getGeocodeDetails(Geocode term) {
		term.getCreator().getPrincipalData().getFullName();
		term.getGeocodeSystem();
		term.getExtraData();
	}
	
	public void getGeocodeDetails(List<Geocode> terms)
	{
		for(Geocode term : terms)
			getGeocodeDetails(term);
	}
	
	
	@Transactional(readOnly = true)
	public GeocodeSystem findGeocodeSystemById(String id, boolean loadDetails)
	{
		GeocodeSystem t = geocodeSystemDao.read(UUID.fromString(id));
		if(loadDetails) getGeocodeSystemDetails(Collections.singletonList(t));
		return t;
	}
	
	
	@Transactional(readOnly = true)
	public GeocodeSystem findGeocodeSystemByName(String name, boolean loadDetails) {
		List<GeocodeSystem> res = geocodeSystemDao.findByName(name);
		if (res != null && res.size() > 1) {
			throw new IllegalArgumentException("More than one Geocode Systems with name \"" + name + "\" were found");
		}
		if (res == null || res.isEmpty()) {
			return null;
		}
		if (loadDetails) {
			getGeocodeSystemDetails(res);
		}
		return res.get(0);
	}
	
	@Transactional(readOnly = true)
	public Layer findLayerById(String id){
		return layerDao.getLayerById(UUID.fromString(id));	
	}
	
	
	@Transactional(readOnly = true)
	public List<Geocode> findAutoCreatedWithParent(String parentTaxonomyName, boolean loadDetails) throws Exception
	{
		GeocodeSystem t = findGeocodeSystemByName(parentTaxonomyName, false);
		if(t == null) throw new Exception("Taxonomy " + parentTaxonomyName + " not found");
		List<Geocode> res = geocodeDao.findAutoCreatedWithParent(t);
		if(loadDetails) getGeocodeDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<GeocodeSystem> allGeocodeSystems(boolean loadDetails) throws Exception
	{
		List<GeocodeSystem> res = geocodeSystemDao.getAll();
		if(loadDetails) getGeocodeSystemDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<GeocodeSystem> activeGeocodeSystems(boolean loadDetails) throws Exception
	{
		List<GeocodeSystem> res = geocodeSystemDao.getActive();
		if(loadDetails) getGeocodeSystemDetails(res);
		return res;
	}
	
	
	@Transactional(readOnly = true)
	public List<String> listGeocodeSystems(boolean active) throws Exception
	{
		if(!active) return geocodeSystemDao.listNames();
		else return geocodeSystemDao.listNamesOfActive();
	}
	
	
	@Transactional(readOnly = true)
	public List<Geocode> getGeocodesOfGeocodeSystem(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<Geocode> res = null;
		GeocodeSystem t = geocodeSystemDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy " + id + " does not exist");
		if(!active) res = geocodeSystemDao.getGeocodes(t);
		else res = geocodeSystemDao.getActiveGeocodes(t);
		
		if(loadDetails) getGeocodeDetails(res);
		return res;
	}
	
	
	@Transactional(readOnly = true)
	public List<GeocodeSystem> getClassDescendantsOfGeocodeSystem(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<GeocodeSystem> res = null;
		GeocodeSystem t = geocodeSystemDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy " + id + " does not exist");
		res = geocodeSystemDao.getInstances(t);
		
		if(active) res = filterGeocodeSystemByActive(res);
		if(loadDetails) getGeocodeSystemDetails(res);
		return res;
	}
		
	private List<GeocodeSystem> filterGeocodeSystemByActive(List<GeocodeSystem> ts)
	{
		List<GeocodeSystem> res = new ArrayList<GeocodeSystem>();
		for(GeocodeSystem t : ts)
		{
			if(t.getIsActive()) res.add(t);
		}
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<Geocode> getChildrenOfGeocode(String id, boolean active, boolean loadDetails)
	{
		List<Geocode> res = null;
		Geocode t = geocodeDao.read(UUID.fromString(id));
		if(t == null) throw new RuntimeException("Geocode " + id + " does not exist");
		res = geocodeDao.getChildren(t);
		if(active) 
			res = filterTermByActive(res);
		if(loadDetails) 
			getGeocodeDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<Geocode> getSiblingsOfGeocode(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<Geocode> res = null;
		Geocode t = geocodeDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Geocode " + id + " does not exist");
		res = geocodeDao.getSiblings(t);
		
		if(active) res = filterTermByActive(res);
		if(loadDetails) getGeocodeDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<Geocode> getClassDescendantsOfGeocode(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<Geocode> res = null;
		Geocode t = geocodeDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Geocode " + id + " does not exist");
		res = geocodeDao.getClassDescendants(t);
		
		if(active) res = filterTermByActive(res);
		if(loadDetails) getGeocodeDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<Geocode> getClassSiblingsOfGeocode(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<Geocode> res = null;
		Geocode t = geocodeDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Geocode " + id + " does not exist");
		res = geocodeDao.getClassSiblings(t);
		
		if(active) res = filterTermByActive(res);
		if(loadDetails) getGeocodeDetails(res);
		return res;
	}

	@Transactional(readOnly = true)
	public List<String> listTermsOfGeocodeSystem(String id, boolean active) throws Exception
	{
		GeocodeSystem t = geocodeSystemDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy " + id + " does not exist");
		if(active) return geocodeSystemDao.listGeocodes(t);
		else return geocodeSystemDao.listActiveGeocodes(t);
	}
	
	@Transactional(readOnly = true)
	public List<TaxonomyTermLink> getTermLinksOfGeocodeSystem(String id, boolean active, boolean loadDetails) throws Exception
	{
		List<TaxonomyTermLink> res = new ArrayList<TaxonomyTermLink>();
		GeocodeSystem t = geocodeSystemDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy " + id + " does not exist");
		if(!active) res = geocodeSystemDao.getTermLinks(t);
		else res = geocodeSystemDao.getActiveTermLinks(t);
		
		if(loadDetails) getTermLinkDetails(res);
		return res;
	}
	
	
	@Transactional(readOnly = true)
	public Shape getShapeOfTerm(Geocode tt, boolean loadDetails) throws Exception {
		Shape s =  geocodeDao.getShape(tt);
//		if(loadDetails) {
//			if(s.getShapeImport() != null) s.getShapeImport();
//		}
		return s;
	}
	
	@Transactional(readOnly = true)
	public List<Shape> getShapesOfTerm(Geocode tt, boolean loadDetails) throws Exception
	{
		List<Shape> shapes =  geocodeDao.getShapes(tt);
//		if(loadDetails)
//		{
//			for(Shape s : shapes)
//			{
//				if(s.getShapeImport() != null) s.getShapeImport();
//			}
//		}
		return shapes;
	}
	
	@Transactional(readOnly = true)
	public Shape getShapeOfTerm(Geocode tt) throws Exception
	{
		return getShapeOfTerm(tt, false);
	}
	
	@Transactional(readOnly = true)
	public List<Shape> getShapesOfTerm(Geocode tt) throws Exception
	{
		return getShapesOfTerm(tt, false);
	}
	
	@Transactional
	public void updateTaxonomy(GeocodeSystem t, String originalName, boolean create)
	{
		if(create)
		{
			GeocodeSystem ex = null;
			if(t.getId() != null) ex = findGeocodeSystemById(t.getId().toString(), false);
			else ex = findGeocodeSystemByName(t.getName(), false);
			if(ex != null)
			{
				log.error("Taxonomy " + t.getName() + " already exists");
				throw new IllegalArgumentException("Taxonomy " + t.getName() + " already exists");
			}
			
			geocodeSystemDao.create(t);
		}
		else
		{
			GeocodeSystem ex = null;
			if(t.getId() != null) ex = findGeocodeSystemById(t.getId().toString(), false);
			else ex = findGeocodeSystemByName(originalName, false);
			
			if(ex == null)
			{
				log.error("Taxonomy " + t.getName() + " does not exist");
				throw new IllegalArgumentException("Taxonomy " + t.getName() + " does not exist");
			}
			t.setId(ex.getId());
			t.setCreationDate(ex.getCreationDate());
			
			geocodeSystemDao.update(t);
		}
	}
	
	
	@Transactional
	public void updateTerm(Geocode t, String originalName, String originalTaxonomyName, boolean create)
	{
		if(create)
		{
			Geocode ex = null;
			if(t.getId() != null) ex = findTermById(t.getId().toString(), false);
			else ex = findTermByNameAndTaxonomy(t.getName(), t.getGeocodeSystem().getName(), false);
			if(ex != null)
			{
				log.error("Geocode " + t.getName() + " already exists");
				throw new IllegalArgumentException("Geocode " + t.getName() + " already exists");
			}
			
			geocodeDao.create(t);
			
			if(t.getParent() != null)
			{
				List<Geocode> siblings = geocodeDao.getSiblings(t);
				int max = 0;
				for(Geocode s : siblings)
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
					for(Geocode s : siblings)
					{
						if(s.getOrder() >= t.getOrder())
						{
							s.setOrder(s.getOrder()+1);
							geocodeDao.update(s);
						}
					}
				}
			}else //order does not matter for terms with no parent
				t.setOrder(0);
			geocodeDao.update(t);
		}
		else
		{
			Geocode ex = null;
			if(t.getId() != null) ex = findTermById(t.getId().toString(), false);
			else ex = findTermByNameAndTaxonomy(originalName, originalTaxonomyName, false);
			
			if(ex == null)
			{
				log.error("Geocode" + t.getName() + " does not exist");
				throw new IllegalArgumentException("Geocode " + t.getName() + " does not exist");
			}
			t.setId(ex.getId());
			t.setCreationDate(ex.getCreationDate());
			t.setCreator(ex.getCreator());
			
			if(t.getOrder() <= 0) 
				t.setOrder(ex.getOrder()); //do not update order if it is not set
			else
			{
				List<Geocode> siblings = geocodeDao.getSiblings(t);
				int max = 0;
				for(Geocode s : siblings)
				{
					if(s.getOrder() > max)
						max = s.getOrder();
				}
				if(t.getOrder() > max)
					t.setOrder(max+1); //if order exceeds maximum, set to maximum + 1
				else
				{
					//reorder if necessary
					for(Geocode s : siblings)
					{
						if(s.getOrder() >= t.getOrder())
						{
							s.setOrder(s.getOrder()+1);
							geocodeDao.update(s);
						}
					}
				}
			}
			geocodeDao.update(t);
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
			Geocode stt = findTermByNameAndTaxonomy(sourceTerm, sourceTermTaxonomy, false);
			Geocode dtt = findTermByNameAndTaxonomy(destTerm, destTermTaxonomy, false);
			if(stt == null) throw new Exception("Geocode " + sourceTermTaxonomy + ":" + sourceTerm + " does not exist");
			if(dtt == null) throw new Exception("Geocode " + destTermTaxonomy + ":" + destTerm + " does not exist");
			
			ex = taxonomyTermLinkDao.read(new TaxonomyTermLinkPK(stt.getId(), dtt.getId()));

			if(verb == null) throw new Exception("Verb is mandatory for Geocode links");
						
			if(ex != null)
			{
				log.error("Geocode link " + sourceTermTaxonomy + ":" + sourceTerm + "->" + 
							destTermTaxonomy + ":" + destTerm + " already exists");
				throw new Exception("Geocode link " + sourceTermTaxonomy+ ":" + sourceTerm + "->" + 
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
			Geocode stt = findTermByNameAndTaxonomy(origSourceTerm, origSourceTermTaxonomy, false);
			Geocode dtt = findTermByNameAndTaxonomy(origDestTerm, origDestTermTaxonomy, false);
			if(stt == null) throw new Exception("Geocode " + origSourceTermTaxonomy + ":" + origSourceTerm + " does not exist");
			if(dtt == null) throw new Exception("Geocode " + origDestTermTaxonomy + ":" + origDestTerm + " does not exist");
			ex = taxonomyTermLinkDao.read(new TaxonomyTermLinkPK(stt.getId(), dtt.getId()));

			if(ex == null)
			{
				log.error("Geocode link " + origSourceTermTaxonomy + ":" + origSourceTerm + "->" + 
							origDestTermTaxonomy + ":" + origDestTerm + " does not exist");
				throw new Exception("Geocode link " + origSourceTermTaxonomy+ ":" + origSourceTerm + "->" + 
							origDestTermTaxonomy + ":" + origDestTerm + " does not exist");
			}
			
			stt = findTermByNameAndTaxonomy(sourceTerm, sourceTermTaxonomy, false);
			dtt = findTermByNameAndTaxonomy(destTerm, destTermTaxonomy, false);
			if(stt == null) throw new Exception("Geocode " + sourceTermTaxonomy + ":" + sourceTerm + " does not exist");
			if(dtt == null) throw new Exception("Geocode " + destTermTaxonomy + ":" + destTerm + " does not exist");

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
			GeocodeSystem tax = findGeocodeSystemByName(t, false);
			if(tax == null) throw new Exception("Taxonomy " + t + " not found");
			List<Geocode> tts =  getGeocodesOfGeocodeSystem(tax.getId().toString(), false, false);
			//Delete all terms of taxonomy
			for(Geocode tt : tts)
			{
				List<Geocode> desc =  geocodeDao.getClassDescendants(tt);
				for(Geocode d : desc)
				{
					d.setGeocodeClass(tt.getGeocodeClass());
					geocodeDao.update(d);
				}
				desc =  geocodeDao.getChildren(tt);
				for(Geocode d : desc)
				{
					d.setParent(tt.getParent());
					geocodeDao.update(d);
				}
				
				List<Geocode> linked = geocodeDao.getLinked(tt);
				for(Geocode l : linked)
				{
					TaxonomyTermLinkPK linkKey = new TaxonomyTermLinkPK(tt.getId(), l.getId());
					TaxonomyTermLink link = taxonomyTermLinkDao.read(linkKey);
					if(link != null) taxonomyTermLinkDao.delete(link);
					
					linkKey = new TaxonomyTermLinkPK(l.getId(), tt.getId());
					link = taxonomyTermLinkDao.read(linkKey);
					if(link != null) taxonomyTermLinkDao.delete(link);
				}
//				projectLayerDao.deleteByTerm(tt);
				geocodeDao.delete(tt);
			}
			
			List<GeocodeSystem> desc = geocodeSystemDao.getInstances(tax);
			for(GeocodeSystem d : desc)
			{
				d.setTaxonomyClass(tax.getTaxonomyClass());
				geocodeSystemDao.update(d);
			}
			
			geocodeSystemDao.delete(tax);
		}
	}
	
	@Transactional
	public void deleteGeocode(Geocode geocode) {
		
		//reorder if necessary
		
		if(geocode.getOrder() > 1) {
			List<Geocode> siblings = geocodeDao.getSiblings(geocode);
			for(Geocode s : siblings) {
				if(s.getOrder() > 0 && s.getOrder() >= geocode.getOrder())
					s.setOrder(s.getOrder()-1);
				geocodeDao.update(s);
			}
		}
		
		List<Geocode> desc =  geocodeDao.getClassDescendants(geocode);
		for(Geocode d : desc) {
			d.setGeocodeClass(geocode.getGeocodeClass());
			geocodeDao.update(d);
		}
		
		List<Geocode> children =  geocodeDao.getChildren(geocode);
		for(Geocode child : children) {
			child.setParent(geocode.getParent());
			geocodeDao.update(child);
		}
		
//		List<Geocode> linked = geocodeDao.getLinked(geocode);
//		for(Geocode l : linked)
//		{
//			TaxonomyTermLinkPK linkKey = new TaxonomyTermLinkPK(geocode.getId(), l.getId());
//			TaxonomyTermLink link = taxonomyTermLinkDao.read(linkKey);
//			if(link != null) taxonomyTermLinkDao.delete(link);
//			
//			linkKey = new TaxonomyTermLinkPK(l.getId(), geocode.getId());
//			link = taxonomyTermLinkDao.read(linkKey);
//			if(link != null) taxonomyTermLinkDao.delete(link);
//		}
		
		
//		projectTermDao.deleteByTerm(tt);
		geocodeDao.delete(geocode);
	}
	
	@Transactional
	public void deleteTerms(List<LayerInfo> terms) throws Exception
	{
		boolean error = false;
		for(LayerInfo t : terms)
		{
			Geocode tt = findTermByNameAndTaxonomy(t.getlayerName(), t.getGeocodeSystem(), false);
			if(tt != null)
			{
				deleteGeocode(tt);
			}
			else error = true;
		}
		if(error) throw new Exception("Could not delete all Geocodes");
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void deleteTermLinks(List<TaxonomyTermLinkInfo> links) throws Exception
	{
		for(TaxonomyTermLinkInfo l : links)
		{
			Geocode st = findTermByNameAndTaxonomy(l.getSourceTerm(), l.getSourceTermTaxonomy(), false);
			Geocode dt = findTermByNameAndTaxonomy(l.getDestTerm(), l.getDestTermTaxonomy(), false);
			TaxonomyTermLink ttl = taxonomyTermLinkDao.read(
									new TaxonomyTermLinkPK(st.getId(), dt.getId()));
			if(ttl == null)
			{
				log.error("Geocode link " + l.getSourceTermTaxonomy() + ":" + l.getSourceTerm() + "->" +
							l.getDestTermTaxonomy() + ":" + l.getDestTerm() + " was not found");
				throw new Exception("Geocode link " + l.getSourceTermTaxonomy() + ":" + l.getSourceTerm() + "->" +
							l.getDestTermTaxonomy() + ":" + l.getDestTerm() + " was not found");
			}
			taxonomyTermLinkDao.delete(ttl);
		}
	}
	
	
	@Transactional(readOnly = true)
	public Geocode findTermByNameAndTaxonomy(String name, String taxonomyName, boolean loadDetails)
	{
		GeocodeSystem t = findGeocodeSystemByName(taxonomyName, false);
		if(t == null) throw new IllegalArgumentException("Taxonomy " + taxonomyName + " was not found");
		List<Geocode> res =  geocodeDao.findByNameAndGeocodeSystem(name, t);
		if(res != null && res.size() > 1) throw new IllegalArgumentException("More than one Geocodes with name \"" + name + "\" were found");
		if(res == null || res.isEmpty()) return null;
		res.forEach(x -> x.getGeocodeSystem().getName());
		if(loadDetails) getGeocodeDetails(res);
		return res.get(0);
	}
	
	@Transactional(readOnly = true)
	public Geocode findTermById(String id, boolean loadDetails)
	{
		Geocode tt = geocodeDao.read(UUID.fromString(id));
		if(loadDetails) getGeocodeDetails(Collections.singletonList(tt));
		return tt;
	}
	
	public List<GeocodeSystem> getAllGeocodeSystems(){
		return geocodeSystemDao.getAll();
	}
	
	private List<Geocode> filterTermByActive(List<Geocode> tts)
	{
		List<Geocode> res = new ArrayList<Geocode>();
		for(Geocode tt : tts)
		{
			if(tt.getIsActive()) res.add(tt);
		}
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<Geocode> getTopmostTermsOfTaxonomy(String id, boolean loadDetails)
	{
		List<Geocode> res = null;
		GeocodeSystem t = geocodeSystemDao.read(UUID.fromString(id));
		if(t == null) throw new IllegalArgumentException("Taxonomy " + id + " does not exist");
		res = geocodeSystemDao.getTopmostGeocodes(t);
		
		if(loadDetails) getGeocodeDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public List<Geocode> getBottomTermsOfTaxonomy(String id, boolean loadDetails) throws Exception
	{
		List<Geocode> res = null;
		GeocodeSystem t = geocodeSystemDao.read(UUID.fromString(id));
		if(t == null) throw new Exception("Taxonomy " + id + " does not exist");
		res = geocodeSystemDao.getBottomGeocodes(t);
		
		if(loadDetails) getGeocodeDetails(res);
		return res;
	}
	
	@Transactional(readOnly = true)
	public Geocode findTermByName(String name, boolean loadDetails) throws Exception
	{
		List<Geocode> res =  geocodeDao.findByName(name);
		if(res != null && res.size() > 1) throw new Exception("More than one Geocodes with name \"" + name + "\" were found");
		if(res == null || res.isEmpty()) return null;
		if(loadDetails) getGeocodeDetails(res);
		return res.get(0);
	}
	
	private void getTermLinkDetails(List<TaxonomyTermLink> ttls){
		for(TaxonomyTermLink ttl : ttls)
		{
			getGeocodeDetails(ttl.getSourceTerm());
			getGeocodeDetails(ttl.getDestinationTerm());
		}
	}
	
	public void deleteGeocodesOfTemplateLayer(Layer templateLayer) throws Exception{
		try{
			List<Geocode> geocodes =  geocodeDao.findByGeocodeSystem(templateLayer.getGeocodeSystem());
			geocodes.forEach(geocode -> deleteGeocode(geocode));
		} catch (Exception e){
			throw new Exception ("Could not remove all Geocodes of Template Layer " + templateLayer.getName(), e);
		}
	}
	
	@Transactional
	public void createGeocodesOfTemplateLayer(Layer layer, List<Shape> shapes, String geocodeMapping) throws Exception {
		/*
		//TODO: decide whether we need to load (inject) geospatialBackendClustered and add also shapes on gos endpoints (code below)
		List<String> res = geospatialBackendClustered.getDataMonitor().getAllGosEndpoints().parallelStream().map(gosDef -> {
			try {
				geospatialBackendClustered.createShapesOfLayer(gosDef.getGosEndpoint(), shapes);
				return "";
			} catch (Exception e) {	return gosDef.getGosEndpoint(); }
		})
		.filter(str -> !str.isEmpty())
		.collect(Collectors.toList());
		if(!res.isEmpty())
			log.error("An error occured while inserting shapes on gos endpoints: "+res);
		*/
		
		shapes.stream().filter(shape -> shape.getExtraData().contains(geocodeMapping)).forEach(shape -> {
			String name = shape.getExtraData();
			int index = name.lastIndexOf("</" + geocodeMapping);

			name = shape.getExtraData().substring(0, index);
			index = name.substring(0, index).lastIndexOf(">");
			name = name.substring(++index, name.length());

			Geocode geocode = new Geocode();
			geocode.setShapeID(shape.getId());
			geocode.setName(name);
			geocode.setExtraData("<extraData><geocode>" + name + "</geocode></extraData");
			geocode.setGeocodeSystem(layer.getGeocodeSystem());
			geocode.setCreator(layer.getCreator());

			geocodeDao.create(geocode);
		});
	}
	
	@Transactional
	public GeocodeSystem createGeocodeSystem(Principal creator, String name) throws Exception {
		GeocodeSystem geocodeSystem  = new GeocodeSystem();
		geocodeSystem.setName(name);
		geocodeSystem.setExtraData("<extraData geographic=\"true\" />");
		geocodeSystem.setCreator(creator);
		
		geocodeSystemDao.create(geocodeSystem);
		
		return geocodeSystem;
	}	
}
