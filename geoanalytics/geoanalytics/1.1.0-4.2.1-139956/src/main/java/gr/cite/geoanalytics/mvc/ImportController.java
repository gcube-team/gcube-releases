package gr.cite.geoanalytics.mvc;

import gr.cite.gaap.datatransferobjects.AnalyzeResponse;
import gr.cite.gaap.datatransferobjects.AttributeInfo;
import gr.cite.gaap.datatransferobjects.GenericResponse;
import gr.cite.gaap.datatransferobjects.ImportRequest;
import gr.cite.gaap.datatransferobjects.ImportResponse;
import gr.cite.gaap.datatransferobjects.ServiceResponse;
import gr.cite.gaap.datatransferobjects.ShapeImportInfo;
import gr.cite.gaap.datatransferobjects.ImportMetadata;
import gr.cite.gaap.datatransferobjects.TaxonomyTransfer;
import gr.cite.gaap.datatransferobjects.TsvImportProperties;
import gr.cite.gaap.datatransferobjects.WfsRequestMessenger;
import gr.cite.gaap.geospatialbackend.GeospatialBackend;
import gr.cite.gaap.servicelayer.ConfigurationManager;
import gr.cite.gaap.servicelayer.ShapeManager.GeographyHierarchy;
import gr.cite.gaap.servicelayer.TaxonomyManager;
import gr.cite.gaap.utilities.HtmlUtils;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.sysconfig.xml.layer.LayerConfig;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.Taxonomy;
import gr.cite.geoanalytics.dataaccess.entities.taxonomy.TaxonomyTerm;
import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;
import gr.cite.geoanalytics.manager.DataManager;
import gr.cite.geoanalytics.manager.ImportManager;
import gr.cite.geoanalytics.manager.ProjectManager;
import gr.cite.geoanalytics.manager.UserManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;
import gr.cite.commons.util.datarepository.DataRepository;
import gr.cite.commons.util.datarepository.elements.RepositoryFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

@Controller
public class ImportController {
	private static final Logger logger = LoggerFactory.getLogger(ImportController.class);
	
	private ImportManager layerManager;
	private GeospatialBackend shapeManager;
	private TaxonomyManager taxonomyManager;
	private ProjectManager projectManager;
	private ConfigurationManager configurationManager;
	private DataRepository repository;
	private DataManager dataManager;
	
	private SecurityContextAccessor securityContextAccessor;
	private static ObjectMapper mapper = new ObjectMapper();	

	@Inject
	public ImportController(ImportManager layerManager, GeospatialBackend shapeManager, TaxonomyManager taxonomyManager, ProjectManager projectManager,
			UserManager userManager, ConfigurationManager configurationManager, DataRepository repository, SecurityContextAccessor securityContextAccessor)
	{
		this.layerManager = layerManager;
		this.shapeManager = shapeManager;
		this.taxonomyManager = taxonomyManager;
		this.projectManager = projectManager;
		this.configurationManager = configurationManager;
		this.repository = repository;
		this.securityContextAccessor = securityContextAccessor;
	}
	
	@Inject
	public void setDataManager(DataManager dataManager) {
		this.dataManager = dataManager;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/admin/import/analyze"})
	public @ResponseBody AnalyzeResponse analyze(@RequestParam(value = "dbfCharset") String dbfCharset,

			@RequestParam(value = "shpFile") MultipartFile shapeFile,
			@RequestParam(value = "shxFile") MultipartFile shxFile,
			@RequestParam(value = "dbfFile") MultipartFile dbfFile,

			@RequestParam(value = "prjFile", required = false) MultipartFile prjFile,
			@RequestParam(value = "sbnFile", required = false) MultipartFile sbnFile,
			@RequestParam(value = "sbxFile", required = false) MultipartFile sbxFile,
			@RequestParam(value = "spgFile", required = false) MultipartFile spgFile,
			@RequestParam(value = "xmlFile", required = false) MultipartFile xmlFile,

			HttpSession session, MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			Iterator<String> files = request.getFileNames();
			List<RepositoryFile> rfs = new ArrayList<RepositoryFile>();
			while (files.hasNext()) {
				MultipartFile f = request.getFile(files.next());
				RepositoryFile rf = new RepositoryFile();
				rf.setOriginalName(f.getOriginalFilename());
				rf.setDataType(f.getContentType());
				rf.setSize(f.getSize());
				rf.setPermanent(false);
				rf.setInputStream(f.getInputStream());
				rfs.add(rf);
			}

			String folderId = repository.persistToFolder(rfs);
			File shapeFileDir = repository.retrieveFolder(folderId);
			Map<String, String> attrs = layerManager.analyzeAttributes(shapeFileDir.getAbsolutePath(), dbfCharset);

			return new AnalyzeResponse(true, attrs, folderId);
		} catch (Exception e) {
			return new AnalyzeResponse(false, null, e.getMessage());
		}
	}
	
	
	
	
	
	@RequestMapping(value={"/admin/import/attributeValues"}, method = RequestMethod.POST)
	public @ResponseBody Set<String> attributeValues(@RequestParam("token") String token,
													 @RequestParam("charset") String charset,
													 @RequestParam("attribute") String attribute) throws Exception {
		File shapeFileDir = repository.retrieveFolder(token);
		if(shapeFileDir == null) 
			throw new Exception("Invalid token");
		
		Set<String> values = layerManager.getAttributeValues(shapeFileDir.getAbsolutePath(), charset, attribute);
		if(values == null)
			throw new Exception("Invalid attribute: " + attribute);
		return values;
	}
	
	@RequestMapping(consumes="application/json", method = RequestMethod.POST, value = {"/admin/import/importData"})
	public @ResponseBody ImportResponse importData(@RequestBody ImportRequest importReq,
			HttpSession session,
			HttpServletResponse response) throws Exception {
		
		Principal creator = securityContextAccessor.getPrincipal();
		
		String srid = importReq.getCrs();
		String folderId = importReq.getToken();
		boolean forceLonLat = importReq.getForceLonLat();
		String dbfCharset = importReq.getDbfCharset();
		String taxonomyTermTaxonomy = importReq.getTaxonomyTermTaxonomy();
		String taxonomyTerm = importReq.getTaxonomyTerm();
		List<AttributeInfo> attrInfo = importReq.getAttributeConfig();
		for(AttributeInfo ai : attrInfo) {
			
			if(ai.getName() != null) ai.setName(HtmlUtils.htmlEscape(ai.getName().trim()));
			if(ai.getValue() != null) ai.setValue(HtmlUtils.htmlEscape(ai.getValue().trim()));
		}
		int sridVal = -1;
		if(srid != null && !srid.isEmpty()) {
			
			try { sridVal = Integer.parseInt(srid);}
			catch(NumberFormatException e) { return new ImportResponse(ImportResponse.Status.Failure, null, "Invalid srid"); }
		}else sridVal = -1;
		
		if(importReq.isReplace() && importReq.isMerge()) return new ImportResponse(ImportResponse.Status.Failure, null, "Replace and merge cannot be both enabled");
		
		TaxonomyTerm tt = taxonomyManager.findTermByNameAndTaxonomy(taxonomyTerm, taxonomyTermTaxonomy, false);
		if(tt == null) return new ImportResponse(ImportResponse.Status.Failure, null, taxonomyTerm + " is not a valid taxonomy term");
		
		TaxonomyTerm boundaryTerm = null;
		if(importReq.getBoundaryTerm() != null) {
			
			boundaryTerm = taxonomyManager.findTermByNameAndTaxonomy(importReq.getBoundaryTerm(), importReq.getBoundaryTermTaxonomy(), false);
			if(boundaryTerm == null) return new ImportResponse(ImportResponse.Status.Failure, null, boundaryTerm + " is not a valid taxonomy term");
		}
		
		GeographyHierarchy hierarchy = importReq.getGeographyTaxonomy() == null ?
				shapeManager.getDefaultGeographyHierarchy() :
				shapeManager.getGeographyHierarchy(taxonomyManager.findTaxonomyByName(importReq.getGeographyTaxonomy(), false));
		LayerConfig layerConfig = configurationManager.getLayerConfig(tt);
		if(layerConfig != null) {
			
			if(!importReq.isReplace() && !importReq.isMerge()) {
				
				Bounds b = new Bounds(layerConfig.getBoundingBox().getMinX(), layerConfig.getBoundingBox().getMinY(),
									  layerConfig.getBoundingBox().getMaxX(), layerConfig.getBoundingBox().getMaxY(), null);
				return new ImportResponse(ImportResponse.Status.Existing, b, layerConfig.getName());
			}
		}
		
		File shapeFileDir = repository.retrieveFolder(folderId);
		if(shapeFileDir == null) 
			return new ImportResponse(ImportResponse.Status.Failure, null, "Invalid token");
		
		ShapeImportInfo info = layerManager.importLayerFromShapeFile(shapeFileDir.getAbsolutePath(), tt, boundaryTerm,
				sridVal, dbfCharset, forceLonLat, !importReq.isMerge(), attrInfo, creator, true, null, hierarchy); //TODO expose overwriteMappings
		
		return new ImportResponse(ImportResponse.Status.Success, info.getBoundingBox(), info.getLayerName());
	}

	
	@RequestMapping(value = "/admin/import/getCapabilities", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody ServiceResponse getCapabilities(@RequestBody WfsRequestMessenger reqM, HttpServletRequest request)
			throws Exception {
		String scope = request.getHeader("scope");
		//TODO: check if something gone bad with request, info user
		if (reqM!=null && (reqM.getUrl().isEmpty() || reqM.getVersion().isEmpty()))
			return new ServiceResponse(false, null, "invalid data input");
		
		List<String> featureTypes=null;
		try {
			featureTypes = (List<String>)layerManager.doCapabilities(reqM, scope, false);
		}catch (Exception e){
			return new ServiceResponse(false, null, e.getMessage());
		}
		if (featureTypes==null) return new ServiceResponse(true, null, "An error occured with wfs data publisher");
		return new ServiceResponse(true, featureTypes, "feature types returned");
	}
	
	@RequestMapping(value = "/admin/import/storeShapeFilesForFeatureType", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody ServiceResponse storeShapeFilesForFeatureType(@RequestBody WfsRequestMessenger reqM, HttpServletRequest request)
			throws Exception {
		
		try {
			String scope = request.getHeader("scope");
			Principal creator = securityContextAccessor.getPrincipal();
			
			if (reqM.getFeatureTypes()==null || reqM.getFeatureTypes().isEmpty())
				return new ServiceResponse(false, null, "no feature types to insert");
			
			layerManager.doCapabilities(reqM, scope, true);
			
			for (String featureType: reqM.getFeatureTypes()) {
				Map<String, InputStream> map = layerManager.doWfsCall(reqM, featureType);
				if (map==null)
					return new ServiceResponse(false, null, "No valid files from geoserver. Corrupted files maybe?");
				
				File tf = Files.createTempDir();
				for (Map.Entry<String, InputStream> entry: map.entrySet()) {
					File file = new File(tf.getAbsolutePath()+"/"+entry.getKey());
					file.createNewFile();
					
					FileOutputStream fos = new FileOutputStream(file);
					byte[] bytes = new byte[1024];
					int length;
					while ((length = entry.getValue().read(bytes)) >= 0) {
						fos.write(bytes, 0, length);
					}
		//			entry.getValue().close();
		//			fos.close();
				}
	
				Map<String, String> attrs = layerManager.analyzeAttributes(tf.getAbsolutePath(), "UTF-8");
				
				List<AttributeInfo> attrInfo = new ArrayList<AttributeInfo>();
				for (Map.Entry<String, String> attrEntry: attrs.entrySet()) {
					AttributeInfo aI = new AttributeInfo(attrEntry.getKey(), attrEntry.getValue(), null, null, true, true);
					attrInfo.add(aI);
				}
					
				if (reqM.getTaxonomyName()==null || reqM.getTaxonomyName().isEmpty() ||
					reqM.getTermName()==null || reqM.getTermName().isEmpty())
					return new ServiceResponse(false, null, "Not valid args!");
				Taxonomy taxonomy = taxonomyManager.findTaxonomyByName(reqM.getTaxonomyName(), true);
				if (taxonomy==null) return new ServiceResponse(false, null, "Taxonomy ("+ reqM.getTaxonomyName() +") not exist");
				TaxonomyTerm tt;
				featureType = featureType.split(":")[1];
				
				if ((tt = taxonomyManager.findTermByNameAndTaxonomy(reqM.getTermName(), taxonomy.getName(), true))==null) {
					tt = new TaxonomyTerm();
					tt.setName(reqM.getTermName());
					tt.setTaxonomy(taxonomy);
					tt.setCreator(creator);
					taxonomyManager.updateTerm(tt, null, taxonomy.getName(), true);
				
					tt = taxonomyManager.findTermByNameAndTaxonomy(tt.getName(), taxonomy.getName(), true);
				}
		//		LayerConfig layerConfig = configurationManager.getLayerConfig(tt);
		//		if(layerConfig != null) {
		//			
		//			if(!true && !false) {
		//				Bounds b = new Bounds(layerConfig.getBoundingBox().getMinX(), layerConfig.getBoundingBox().getMinY(),
		//									  layerConfig.getBoundingBox().getMaxX(), layerConfig.getBoundingBox().getMaxY(), null);
		//			}
		//		}
				
					ShapeImportInfo info = layerManager.importLayerFromShapeFile(tf.getAbsolutePath(), tt, null,
							4326, "UTF-8", true, !false, attrInfo, creator, true, null, null); //TODO expose overwriteMappings
				
			}
			return new ServiceResponse(true, null, "Layers was stored");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ServiceResponse(true, null, e.getMessage());			
		}
	}
	
	
	/**
	 * This method is for testing purposes only. It does not need to be invoked explicitly, as it is automatically invoked during the import procedure
	 * @return
	 */
	@RequestMapping(consumes="application/json", method = RequestMethod.POST, value = {"/admin/import/updateProjects"})
	public @ResponseBody ImportResponse updateProjectAttributes()
	{
		try
		{
			projectManager.updateAllProjectAttributes();
			return new ImportResponse(ImportResponse.Status.Success, null, "Ok");
		}catch(Exception e)
		{
			e.printStackTrace();
			return new ImportResponse(ImportResponse.Status.Failure, null, "Error while updating project attributes");
		}
	}
	

	@RequestMapping(value = {"/importTsv"}, method = RequestMethod.POST, consumes = {"multipart/form-data"})
	public @ResponseBody ServiceResponse importTsv(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception{			
		String importMetadataString =  request.getParameter("tsvImportMetadata");	
		ImportMetadata importMetadata = mapper.readValue( importMetadataString, ImportMetadata.class);	
		
		String tsvImportPropertiesString =  request.getParameter("tsvImportProperties");
		TsvImportProperties tsvImportProperties = mapper.readValue( tsvImportPropertiesString, TsvImportProperties.class);		
		
		String scope = request.getHeader("scope");	
		
		try{
			InputStream tsvInputStream = request.getFiles(request.getFileNames().next()).get(0).getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(tsvInputStream, writer, Charsets.UTF_8);
			String tsvInString = writer.toString();
			
			TaxonomyTerm templateTaxonomyTerm = this.taxonomyManager.findTermByName(tsvImportProperties.getTemplateLayerName(), true);
			
			this.dataManager.importDataToShapesOfLayerUsingTsvAndUpdate(templateTaxonomyTerm.getId(), tsvInString, tsvImportProperties.getNewLayerName(), templateTaxonomyTerm.getTaxonomy().getId());
			this.layerManager.publishLayerMetadataToGeonetwork(scope, tsvImportProperties.getNewLayerName(), importMetadata);
			
			return new ServiceResponse(true, null, tsvImportProperties.getNewLayerName());			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("Tsv import failed", e);
			return new ServiceResponse(false, null, e.getMessage());
		}
	}
	
	/*public List<String> createSeparatedTsvStrings(String tsvInString){
	String headerLine = "";
	String newTsvString = "";
	String currentAttribute = "";
	boolean header = true;
	List<String> separatedTsvStrings = new ArrayList<>();

	try (BufferedReader br = new BufferedReader(new FileReader("nama_10r_2coe.tsv"))) {
	    String line;
	    while ((line = br.readLine()) != null) {	
			if (header) {
				headerLine = line + "\n";			
				header = false;
				continue;
			} 
			
			String[] lineAsArray = line.split("\\W+");				
			String attribute = lineAsArray[1];

			if (!attribute.equals(currentAttribute)) {
				
				if(newTsvString.length() > 0){
					separatedTsvStrings.add(newTsvString);		
				}
				newTsvString = headerLine;
				currentAttribute = lineAsArray[1];						
			}			
			
			newTsvString += line + "\n";	
	    }
	    
		if(newTsvString.length() > 0){
			separatedTsvStrings.add(newTsvString);		
		}		    
	} catch (IOException e) {
		e.printStackTrace();
	}
	return separatedTsvStrings;
}*/
	
}
