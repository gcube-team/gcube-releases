package gr.cite.geoanalytics.mvc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import gr.cite.commons.util.datarepository.DataRepository;
import gr.cite.gaap.datatransferobjects.Coords;
import gr.cite.gaap.datatransferobjects.ServiceResponse;
import gr.cite.gaap.datatransferobjects.ShapeImportInstance;
import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.gaap.datatransferobjects.ShapeSearchSelection;
import gr.cite.gaap.datatransferobjects.TaxonomyTermInfo;
import gr.cite.gaap.datatransferobjects.TaxonomyTermMessenger;
import gr.cite.gaap.datatransferobjects.UpdateResponse;
import gr.cite.gaap.datatransferobjects.ShapeSearchSelection.GeoSearchType;
import gr.cite.gaap.geospatialbackend.GeospatialBackend;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.ShapeImportManager;
import gr.cite.gaap.servicelayer.ShapeInfo;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.gaap.utilities.HtmlUtils;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;

import org.gcube.common.resources.gcore.common.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vividsolutions.jts.io.WKTReader;

@Controller
public class ShapeController {
	private static final Logger log = LoggerFactory.getLogger(ShapeController.class);
	
	private GeospatialBackend shapeManager;
	private ShapeImportManager shapeImportManager;
	private ImportManager importManager;
	private TaxonomyManager taxonomyManager;
	private ConfigurationManager configurationManager;
	
	@Inject
	public ShapeController(ImportManager layerManager, GeospatialBackend shapeManager, ShapeImportManager shapeImportManager, 
			ImportManager importManager, TaxonomyManager taxonomyManager, 
			ConfigurationManager configurationManager, DataRepository repository)
	{
		//this.layerManager = layerManager;
		this.shapeManager = shapeManager;
		this.shapeImportManager = shapeImportManager;
		this.importManager = importManager;
		this.taxonomyManager = taxonomyManager;
		this.configurationManager = configurationManager;
		//this.repository = repository;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/getBounds"}, consumes="application/json")
	public @ResponseBody Bounds getShapeBounds(String id) throws Exception
	{
		return shapeManager.getShapeBounds(UUID.fromString(id));
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/retrieveShape"}, consumes="application/json")
	public @ResponseBody ShapeMessenger getSingleShape(
			@RequestBody String id) throws Exception
	{
		ShapeInfo si = shapeManager.getShape(UUID.fromString(id));
		if(si == null) return null;
		
		ShapeMessenger sm = new ShapeMessenger();
		sm.setId(si.getShape().getId().toString());
		sm.setCode(si.getShape().getCode());
		sm.setExtraData(si.getShape().getExtraData());
		sm.setGeometry(si.getShape().getGeography().toText());
		sm.setImportId(si.getShape().getShapeImport().getShapeImport().toString());
		sm.setName(si.getShape().getName());
		sm.setShapeClass(si.getShape().getShapeClass());
		if(si.getTerm() != null)
		{
			sm.setTermName(si.getTerm().getName());
			sm.setTermTaxonomy(si.getTerm().getTaxonomy().getName());
		}
		return sm;
	
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/shapes/retrieveByTerm"}, consumes="application/json")
	public @ResponseBody ShapeMessenger getShapeByTerm(
			@RequestBody TaxonomyTermInfo term) throws Exception
	{
		Taxonomy t = taxonomyManager.findTaxonomyByName(term.getTaxonomy(), false);
		if(t == null) throw new Exception("Taxonomy " + term.getTaxonomy() + " not found"); //this will commonly be the geography taxonomy, but no check is enforced
		
		TaxonomyTerm tt = taxonomyManager.findTermByNameAndTaxonomy(term.getTerm(), term.getTaxonomy(), false);
		if(tt == null) throw new Exception("Taxonomy term " + term.getTaxonomy() + ":" + term.getTerm() + " not found");
		
		Shape s = taxonomyManager.getShapeOfTerm(tt, true);
		if(s == null) return null;
		
		ShapeMessenger sm = new ShapeMessenger();
		sm.setId(s.getId().toString());
		sm.setCode(s.getCode());
		sm.setExtraData(s.getExtraData());
		sm.setGeometry(s.getGeography().toText());
		if(s.getShapeImport() != null) sm.setImportId(s.getShapeImport().getShapeImport().toString());
		sm.setName(s.getName());
		sm.setShapeClass(s.getShapeClass());
		/*if(si.getTerm() != null)
		{
			sm.setTermName(si.getTerm().getName());
			sm.setTermTaxonomy(si.getTerm().getTaxonomy().getName());
		}*/
		return sm;
	
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/search"}, consumes="application/json")
	public @ResponseBody List<ShapeMessenger> searchShapes(@RequestBody ShapeSearchSelection selection) throws Exception
	{
		List<ShapeInfo> shapes = new ArrayList<ShapeInfo>();
		List<ShapeInfo> filtered = new ArrayList<ShapeInfo>();
		if(selection.getGeoSearchType() == GeoSearchType.None)
		{
			if(selection.getTerms() != null && !selection.getTerms().isEmpty())
			{
				for(String term : selection.getTerms())
				{
					String[] parts = term.split(":");
					if(parts.length != 2) throw new Exception("Malformed taxonomy term");
					shapes.addAll(shapeManager.getShapeInfoForTerm(parts[1], parts[0]));
					
				}
				if(selection.getImportInstances() != null && !selection.getImportInstances().isEmpty())
				{
					for(ShapeInfo s : shapes)
					{
						for(String importId : selection.getImportInstances())
						{
							if(s.getShape().getShapeImport().getShapeImport().toString().equals(importId))
								filtered.add(s);
						}
					}
				}else
					filtered = shapes;
			}else if(selection.getImportInstances() != null)
			{
				if(selection.getImportInstances() != null) //nothing to search for if null
				{
					for(String importId : selection.getImportInstances())
						filtered.addAll(shapeManager.findShapesOfImport(UUID.fromString(importId)));
				}
			}else if(selection.getId() != null) {
				ShapeInfo foundShape = shapeManager.findShapeByIdInfo(selection.getId());
				if(foundShape != null)
					filtered.add(foundShape);
			}
		}else
		{
			switch(selection.getGeoSearchType())
			{
			case BoundingBox:
				shapes = shapeManager.findShapeWithinBounds(selection.getGeometry());
				break;
			case Proximity:
				//TODO
				break;
			case Overlap:
				//TODO
				break;
			}
			
			for(ShapeInfo s : shapes)
			{
				if(selection.getTerms() != null && !selection.getTerms().isEmpty())
				{
					for(String term : selection.getTerms())
					{
						String[] parts = term.split(":");
						if(parts.length != 2) throw new Exception("Malformed taxonomy term");
						if(s.getTerm() != null)
						{
							if(s.getTerm().getTaxonomy().getName().equals(parts[0]) && s.getTerm().getName().equals(parts[1]))
								filtered.add(s);
						}
					}
				}
				if(selection.getImportInstances() != null && !selection.getImportInstances().isEmpty())
				{
					for(String importId : selection.getImportInstances())
					{
						if(s.getShape().getShapeImport().getShapeImport().toString().equals(importId))
							filtered.add(s);
					}
				}
			}
		}
		
		List<ShapeMessenger> res = new ArrayList<ShapeMessenger>();
		for(ShapeInfo s : filtered)
		{
			ShapeMessenger sm = new ShapeMessenger();
			sm.setId(s.getShape().getId().toString());
			sm.setCode(s.getShape().getCode());
			sm.setExtraData(s.getShape().getExtraData());
			sm.setGeometry(s.getShape().getGeography().toText());
			if(s.getShape().getShapeImport() != null)
				sm.setImportId(s.getShape().getShapeImport().getShapeImport().toString());
			sm.setName(s.getShape().getName());
			sm.setShapeClass(s.getShape().getShapeClass());
			if(s.getTerm() != null)
			{
				sm.setTermName(s.getTerm().getName());
				sm.setTermTaxonomy(s.getTerm().getTaxonomy().getName());
			}
			res.add(sm);
		}
		return res;
	}

	@RequestMapping(method = RequestMethod.GET, value = {"/admin/shapes/layerTerms"}, produces="application/json")
	public @ResponseBody List<TaxonomyTermInfo> getLayerTerms() throws Exception
	{
		List<TaxonomyTermInfo> res = new ArrayList<TaxonomyTermInfo>();
		List<LayerConfig> layers = configurationManager.getLayerConfig();
		
		for(LayerConfig l : layers)
		{
			TaxonomyTerm tt = taxonomyManager.findTermById(l.getTermId(), true);
			TaxonomyTermInfo ti = new TaxonomyTermInfo();
			ti.setTaxonomy(tt.getTaxonomy().getName());
			ti.setTerm(tt.getName());
			
			res.add(ti);
		}
		return res;	
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/importInstances"}, consumes="application/json")
	public @ResponseBody ServiceResponse getImportInstances() throws Exception {
		try {
			List<ShapeImportInstance> shapeImportInstances = shapeImportManager.getImportInstances(true);
			return new ServiceResponse(true, shapeImportInstances, "shape import instances returned");
		} catch(Exception e) {
			return new ServiceResponse(false, null, e.getMessage());
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/update"}, consumes="application/json")
	public @ResponseBody UpdateResponse update(@RequestBody ShapeMessenger sm)
	{
		try
		{
			Shape s = new Shape();
			s.setId(UUID.fromString(sm.getId()));
			s.setExtraData(HtmlUtils.htmlEscape(sm.getExtraData().trim()));
			if(sm.getGeometry() != null) s.setGeography(new WKTReader().read(sm.getGeometry()));
			s.setName(HtmlUtils.htmlEscape(sm.getName().trim()));
			s.setShapeClass(sm.getShapeClass());
			s.setCode(HtmlUtils.htmlEscape(sm.getCode().trim()));
			shapeManager.update(s);
			
			return new UpdateResponse(true, "OK");
		}catch(Exception e)
		{
			log.error("An error has occurred while updating shape " + sm.getId(), e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/delete"}, consumes="application/json")
	public @ResponseBody UpdateResponse delete(@RequestBody List<String> shapes)
	{
		try
		{
			shapeManager.delete(shapes);
			return new UpdateResponse(true, "Ok");
		}catch(Exception e)
		{
			log.error("An error has occurred while deleting shapes", e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/shapes/deleteLayer"}, consumes="application/json")
	public @ResponseBody UpdateResponse deleteLayer(@RequestBody String term)
	{
		try
		{
			String[] parts = term.split(":");
			if(parts.length != 2) throw new Exception("Malformed taxonomy term");
			
			TaxonomyTerm tt = taxonomyManager.findTermByNameAndTaxonomy(parts[1], parts[0], false);
			if(tt == null) return new UpdateResponse(false, "Taxonomy term " + term + " not found");
			
			LayerConfig cfg = configurationManager.getLayerConfig(tt);
			if(cfg == null) return new UpdateResponse(false, "Layer " + tt.getName() + " not found");
			
			importManager.removeLayer(tt);
			
			return new UpdateResponse(true, "Ok");
		}catch(Exception e)
		{
			log.error("An error has occurred during layer deletion", e);
			return new UpdateResponse(false, e.getMessage());
		}
	}
	
	@RequestMapping(value= "/shapes/mostSpecificBreadcrumbsByCoordinates" , method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody List<TaxonomyTermMessenger> mostSpecificBreadcrumbsByCoordinates(@RequestBody Coords coords) throws Exception {
		return getBreadcrumbMessenger(
				shapeManager.getBreadcrumbs(coords).values().stream().
					max(Comparator.comparing(List::size)).
					orElse(null)
		);
	}
	
	@RequestMapping(value= "/shapes/breadcrumbsByCoordinates" , method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody Map<UUID, List<TaxonomyTermMessenger>> breadcrumbsByCoordinates(@RequestBody Coords coords) throws Exception {
		return shapeManager.getBreadcrumbs(coords).entrySet().stream().
				collect(Collectors.toMap(breadcrumbEntry -> breadcrumbEntry.getKey(), 
						breadcrumbEntry -> getBreadcrumbMessenger(breadcrumbEntry.getValue())));
	}
	
	private List<TaxonomyTermMessenger> getBreadcrumbMessenger(List<TaxonomyTerm> breadcrumb) {
		return breadcrumb == null ? null : 
			breadcrumb.stream().
				map(t -> {
					TaxonomyTermMessenger ttm = new TaxonomyTermMessenger();
					ttm.setId(t.getId().toString());
					ttm.setName(t.getName());
					return ttm;
				}).
				collect(Collectors.toList());
	}

}
