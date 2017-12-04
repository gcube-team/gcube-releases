package org.gcube.spatial.data.sdi.rest;

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.gcube.smartgears.annotations.ManagedBy;
import org.gcube.spatial.data.sdi.SDIServiceManager;
import org.gcube.spatial.data.sdi.ScopeUtils;
import org.gcube.spatial.data.sdi.engine.TemplateManager;
import org.gcube.spatial.data.sdi.engine.ThreddsManager;
import org.gcube.spatial.data.sdi.engine.impl.metadata.GenericTemplates;
import org.gcube.spatial.data.sdi.model.CatalogDescriptor;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Path("Thredds")
@Api(value="Thredds")
@Slf4j
@ManagedBy(SDIServiceManager.class)
public class Thredds {

	private static class Constants{
		public static final String AUTHORITY_PARAMETER="authority";
		public static final String PATH_PARAMETER="path";
		public static final String FOLDER_PARAMETER="folder";		
		public static final String BASE_NAME_PARAMETER="name";
	}
	
	
	
	@Inject 
	TemplateManager templateManager;
	
	@Inject 
	ThreddsManager threddsManager;
	
	
	@PUT	
	@Produces(MediaType.APPLICATION_JSON)
	public CatalogDescriptor registerCatalog(@QueryParam(Constants.AUTHORITY_PARAMETER) @DefaultValue("www.d4science.org") String authority,
			@QueryParam(Constants.BASE_NAME_PARAMETER) String baseName,
			@QueryParam(Constants.PATH_PARAMETER) String path,
			@QueryParam(Constants.FOLDER_PARAMETER) String folder) {
		try {
		String scopeName=ScopeUtils.getCurrentScopeName();
		
		log.info("Received register catalog request under scope {} ",scopeName);
		
		if(baseName==null) {
			log.debug("Base name not provided, using VRE {} ",scopeName);
			baseName=scopeName+"_VRE";
		}
		
		if(folder==null) {
			log.debug("Folder not provided, using base name {} ",baseName);
			folder=baseName+"_folder";
		}
		
		if(path==null) {
			log.debug("Path not provided, using baseName {} ",baseName);
			path=baseName;
		}
		
		
		String datasetScanName=(baseName+" Catalog").replace("_", " ");
		String datasetScanId=baseName+"_in_"+folder;
		String catalogReference=(baseName+" Catalog").replaceAll("_", " ");
		
		return createCatalog(authority,path,datasetScanId,datasetScanName,folder,catalogReference);
		}catch(Throwable t) {
			log.debug("Unable to create catalog",t);
			throw new WebApplicationException("Unable to serve request", t);		
		}
	}
	
	
	
	private CatalogDescriptor createCatalog(String authorityUrl,String catalogPath,
			String datasetScanId,String datasetScanName, String subFolder, String catalogReference) throws Exception {

		log.info("Going to create catalog for authorityURL {}, path {}, subFolder {} ",authorityUrl,catalogPath,subFolder);
		
		HashMap<String,String> parameters=new HashMap<String,String>();
		parameters.put(GenericTemplates.ThreddsCatalogTemplate.AUTHORITY_URL, authorityUrl);
		parameters.put(GenericTemplates.ThreddsCatalogTemplate.CATALOG_PATH, catalogPath);
		parameters.put(GenericTemplates.ThreddsCatalogTemplate.DATASET_SCAN_ID, datasetScanId);
		parameters.put(GenericTemplates.ThreddsCatalogTemplate.DATASET_SCAN_NAME, datasetScanName);
		parameters.put(GenericTemplates.ThreddsCatalogTemplate.LOCATION, "content/"+subFolder);
		
		File catalog=
				templateManager.generateFromTemplate(parameters, GenericTemplates.ThreddsCatalogTemplate.FILENAME);
		
		return threddsManager.createCatalog(catalog, catalogReference);
	}
	
}
