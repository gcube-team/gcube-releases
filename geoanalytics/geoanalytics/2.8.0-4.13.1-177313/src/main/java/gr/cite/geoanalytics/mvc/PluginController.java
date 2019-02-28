/**
 * 
 */
package gr.cite.geoanalytics.mvc;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import gr.cite.clustermanager.actuators.functions.ExecutionMonitor;
import gr.cite.clustermanager.exceptions.NoExecutionDetailsFound;
import gr.cite.clustermanager.model.functions.ExecutionDetails;
import gr.cite.gaap.datatransferobjects.GenericResponse;
import gr.cite.gaap.datatransferobjects.PluginExecutionMessenger;
import gr.cite.gaap.datatransferobjects.PluginInfo;
import gr.cite.gaap.datatransferobjects.UserinfoObject;
import gr.cite.gaap.datatransferobjects.plugin.ExecutionDetailsInfo;
import gr.cite.gaap.datatransferobjects.plugin.FunctionResponse;
import gr.cite.gaap.datatransferobjects.plugin.PluginLibraryMessenger;
import gr.cite.gaap.datatransferobjects.GenericResponse.Status;
import gr.cite.geoanalytics.dataaccess.entities.plugin.metadata.PluginMetadata;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.functions.common.model.functions.LayerConfig;
import gr.cite.geoanalytics.manager.PluginManager;

/**
 * @author vfloros
 *
 */
@Controller
@Produces("application/json")
public class PluginController extends BaseController {

	public static String PLUGINS_FOLDER_LOCATION;
	
	public static final String FS = File.separator;
	public static final String PLUGINS_FOLDER_NAME = "geoanalytics_plugins";
	public static final String PLUGIN_AVAILABLE_TO_ALL_TENANTS = "shared";
	private static final Logger logger = LoggerFactory.getLogger(PluginController.class);
	
	@Autowired private PluginManager pluginManager;
	@Autowired private ExecutionMonitor executionMonitor;
	
	@Value("${gr.cite.geoanalytics.plugins.basepath}")
    public void setPLUGINS_FOLDER_LOCATION(String pluginsFolderLocation) {
		PLUGINS_FOLDER_LOCATION = pluginsFolderLocation;
    }

	@RequestMapping(value={"/plugin/listProjectPlugins"}, method=RequestMethod.POST, consumes={"application/json"}, produces = {"application/json"})
	public @ResponseBody GenericResponse listProjectPlugins(@RequestBody UserinfoObject uio) {
		logger.debug("Retrieving project plugins...");
		try{
			Tenant tenant = this.getSecurityContextAccessor().getTenant();//securityContextAccessor.getTenant();
			
			List<PluginInfo> res = pluginManager.listPluginsByTenantOrNullTenant(tenant);

			logger.debug("Retrieving project plugins has succeeded");
			return new GenericResponse(Status.Success, res, "Authorized");
		}catch(Exception e){
			logger.error("Error while retrieving available plugins for project " + uio.getProjectName(), e);
			return new GenericResponse(Status.Failure, false, e.getMessage());
		}
	}

	@RequestMapping(value={"/plugin/fetchPluginsByPluginLibraryId"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse listProjectPlugins(@RequestBody UUID pluginLibraryId) {
		logger.debug("Retrieving plugin by ID:" + pluginLibraryId.toString());
		try{
			List<PluginInfo> res = pluginManager.retrievePluginInfoByPluginLibraryId(pluginLibraryId);

			logger.debug("Plugins were retrieved successfully");
			return new GenericResponse(Status.Success, res, "Authorized");
		}catch(Exception e){
			logger.error("Error while retrieving plugin with id " + pluginLibraryId, e);
			return new GenericResponse(Status.Failure, null, e.getMessage());
		}
	}
	
	
	@RequestMapping(value={"/plugin/fetchConfiguration"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse pluginConfiguration(@RequestBody UUID pluginId) {
		logger.info("Fetching plugin configuration...");
		try{
	        Set<LayerConfig> layerConfigs = pluginManager.getPluginLayerConfigs(pluginId);
			logger.info("Plugin config was successfully fetched");
			return new GenericResponse(Status.Success, layerConfigs, "Authorized");
		}catch(Exception e){
			logger.error("Error while executing plugin function", e);
			e.printStackTrace();
			return new GenericResponse(Status.Failure, false, e.getMessage());
		}
	}
	
	
	@RequestMapping(value={"/plugin/executionDetails"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse executionDetails(@RequestBody UUID executionID) {
		logger.info("Fetching function execution statuses...");
		Map<String,ExecutionDetails> allExecutionDetails = pluginManager.getAllExecutionDetails();
		if(executionID != null){
			ExecutionDetails executionDetails = allExecutionDetails.get(executionID.toString());
			allExecutionDetails.clear();
			allExecutionDetails.put(executionDetails.getId(), executionDetails);
			return new GenericResponse(Status.Success, allExecutionDetails, "Got execution details for executionID: "+executionID.toString());
		}
		return new GenericResponse(Status.Success, allExecutionDetails, "Got execution details for all executions");
	}
	
	
	
	@RequestMapping(value={"/plugin/executeFunction"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse pluginFunctionExectution(@RequestBody PluginExecutionMessenger pem) {
		logger.info("Executing plugin function...");
		try{
			UUID tenantId = this.getSecurityContextAccessor().getTenant().getId();
			String jarPath = pluginManager.returnJarPath(PLUGINS_FOLDER_LOCATION, PLUGINS_FOLDER_NAME, tenantId, pem.getPluginId());
			UUID pluginID = pem.getPluginId();
			UUID projectID = pem.getProjectId();
			
			PluginMetadata pluginMetadata = pluginManager.getPluginMetaDataByPluginId(pem.getPluginId());
			
//			FunctionLayerConfigI functionExecConfig = (FunctionLayerConfigI)Class.forName(pluginMetadata.getConfigurationClass()).newInstance();
			
//			String layerConfigsJSON = gson.toJson(pem.getParameters().get("layers"));
			
//			Set <LayerConfig>  layerConfigs = gson.fromJson(layerConfigsJSON, new TypeToken<HashSet<LayerConfig>>(){}.getType());
//			layerConfigs.stream().forEach(lc -> {
//				functionExecConfig.getLayerConfigByObjectID(lc.getObjectID()).setLayerID(lc.getLayerID());
//			});
			
			Map<String, Object> parameters = pem.getParameters();
//			parameters.put("functionExecConfig", functionExecConfig);
			
			FunctionResponse res = pluginManager.addCompiledFileToClasspathAndInvokeMethod(pluginID, projectID, jarPath, pluginMetadata, parameters);
//			String res = (String)pluginManager.addJarAndAllContainingClassesToClasspathAndInvokeMethod(jarPath, pluginMetadata, pem.getParameters());
			
//			Map<String, UUID> result = pluginManager.relateFunctionExecutionResultedLayersToProject(res, pem.getProjectId());
			
			logger.info("Plugin function wass successfully submitted");
//			return new GenericResponse(Status.Success, result, "Authorized");
			return new GenericResponse(Status.Success, res, "Authorized");
		}catch(Exception e){
			logger.error("Error while submitting plugin function", e);
			e.printStackTrace();
			return new GenericResponse(Status.Failure, false, e.getMessage());
		}
	}
	
	@RequestMapping(value = "/plugin/loadPluginByNameAndTenant", method = RequestMethod.GET)
	public void loadScriptToPage(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "principalName", required = true) String principalName,
			@RequestParam(value = "tenantName", required = true) String tenantName,
			@RequestParam(value = "projectId", required = true) UUID projectID,
			@RequestParam(value = "pluginId", required = true) String pluginId,
			@RequestParam(value = "pluginName", required = true) String pluginName) throws Exception {
		
		logger.debug("Loading plugin by name: "+pluginName+" and tenant: "+tenantName);
		
//		PluginConfiguration pc = pluginManager.createConfigurationForPluginInNotExists(
//													UUID.fromString(pluginId),
//													projectID,
//													tenantName);
		
//		Plugin plugin = pc.getPlugin();
		
		if(pluginId == null){
			logger.error("plugin ID cannot be null");
			logger.error("Plugin with name: " + pluginName + " not found");
			return;
		} else {
			
		}
		String dirPath = PLUGINS_FOLDER_LOCATION;
		String directoryName = PLUGINS_FOLDER_NAME;
		String tenantID = (this.getSecurityContextAccessor().getTenant() == null) ? PLUGIN_AVAILABLE_TO_ALL_TENANTS : this.getSecurityContextAccessor().getTenant().getId().toString();
		String plugiFolderName = pluginId;
		String directoryFullPath = dirPath + FS + directoryName + FS + tenantID + FS + plugiFolderName;
		String jarPath = directoryFullPath + FS + plugiFolderName + ".jar";
		
		logger.info("Directory: " + directoryFullPath);
		boolean loadedSuccessfully = pluginManager.loadPluginJarToDirectory(pluginId, directoryFullPath, jarPath);
//		boolean unzippedSuccessfully = true;//pluginManager.unzipJarFile(directoryFullPath,jarPath);
		
		String className = null;
		String jsFileName = "";
		String methodName = "";
		String widgetName = "";
		File widget = null;
		
		if(loadedSuccessfully){
			try {
				String pluginMetaDataString = pluginManager.getPluginMetadataByID(UUID.fromString(pluginId));
				PluginMetadata pluginMetadata = pluginManager.getPluginMetadataFromXMLField(pluginMetaDataString);
				className = pluginMetadata.getQualifiedNameOfClass();
				jsFileName = pluginMetadata.getJsFileName();
				widgetName = pluginMetadata.getWidgetName();
				methodName = pluginMetadata.getMethodName();
			} catch (JAXBException e) {
				e.printStackTrace();
			}

//			if(unzippedSuccessfully){
//				widget = pluginManager.findFileInDirectory(directoryFullPath)[0];
//			}
			
			widget = pluginManager.loadWidgetFile(directoryFullPath, jarPath, jsFileName);
		}
		 else {
			logger.error("Failed to retrieve the .jar file");
		}
		
		if(widget == null || !widget.exists()){
			String errorMessage = "Sorry. The file " + jsFileName + " does not exist";
            logger.info(errorMessage);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return;
        }
         
		
//		byte[] jarByteArray = plugin.getData();
		
		response.setContentType("application/javascript");
		response.setContentLength((int) widget.length());
		response.setHeader("Content-Disposition", "inline; filename=\"" + jsFileName + "\"");
		
		BufferedInputStream input = null;
		BufferedOutputStream output = null;
		
		//DELETE these lines
//		if(widget.isDirectory())
//			widget = new File("/tmp/geoanalytics_plugins/shared/7779d1cc-1201-4848-841b-d2e6d8cf9705/7779d1cc-1201-4848-841b-d2e6d8cf9705.jar");
			
			
		FileInputStream fis =  new FileInputStream(widget); 
		
		try {
			input = new BufferedInputStream(fis);
			output = new BufferedOutputStream(response.getOutputStream());
			byte[] buffer = new byte[1048576];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} catch (IOException e) {
			logger.error("There are errors in reading/writing image stream " + e.getMessage());
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException ignore) {
					ignore.printStackTrace();
				}
			if (input != null)
				try {
					input.close();
				} catch (IOException ignore) {
					ignore.printStackTrace();
				}
		}
	}

	@RequestMapping(value = { "/plugin/upload" }, method = RequestMethod.POST, consumes = { "multipart/form-data" })
	public @ResponseBody GenericResponse importPluginLibrary(@RequestPart(value = "pluginLibrary", required = true) PluginLibraryMessenger plm, MultipartHttpServletRequest request, HttpServletResponse response) {
		logger.debug("Plugin upload request...");
		
		try {
			plm.validate();
//			pluginManager.validateFile(request);
		} catch (Exception e) {
			e.printStackTrace();
			return new GenericResponse(Status.ValidationError, null,
					"Some of the fields you have entered are invalid");
		}
		
		UUID pluginLibID = pluginManager.storePluginLibraryJARToDB(plm, request);
		
		if(pluginLibID != null) {
			logger.info("Plugin librarry uploaded successfully!");
			
			return new GenericResponse(Status.Success, pluginLibID, "");
		} else {
			logger.info("Plugin librarry upload has failed!");
			
			return new GenericResponse(Status.Failure, null, "");
		}
	}

	@RequestMapping(value={"/plugin/listPluginsByTenantOrNullTenant"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody List<PluginInfo>/*GenericResponse*/ listPluginsByTenantOrNullTenant() throws Exception {
		logger.debug("Retrieving project plugins...");
		Tenant tenant = this.getSecurityContextAccessor().getTenant();
		try {
			List<PluginInfo> res = pluginManager.listPluginsByTenantOrNullTenant(tenant);

			logger.debug("Retrieving project plugins has succeeded");
			return res;
//			return new GenericResponse(Status.Success, res, "Authorized");
		} catch(Exception e){
			logger.error("Error while retrieving available plugins for tenant " + tenant.getName(), e);
			throw new Exception("Error while retrieving available plugins for tenant " + tenant.getName());
//			return new GenericResponse(Status.Failure, false, e.getMessage());
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = {"/plugin/deletePluginAndPluginLibrary"}, consumes="application/json")
	public @ResponseBody GenericResponse deletePluginAndParentPluginLibrary(@RequestBody UUID pluginID) throws Exception {
		logger.debug("PluginLibrary and Plugin to be deleted with plugin id: "+ pluginID.toString());
		GenericResponse gr = null;
		
		try {
			pluginManager.deletePluginAndParentPluginLibraryByPluginID(pluginID);
			logger.debug("Deleted PluginLibrary and Plugin with plugin id: "+ pluginID.toString());
			gr = new GenericResponse(Status.Success, null, "");
		} catch(Exception e) {
			logger.debug("Failed to delete PluginLibrary and Plugin with plugin id: "+ pluginID.toString());
			e.printStackTrace();
			gr = new GenericResponse(Status.Failure, null, "");
		}
		
		return gr;
	}
	
	@RequestMapping(value = { "/plugin/updatePlugin" }, method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody GenericResponse updatePlugin(@RequestBody PluginLibraryMessenger plm) {
		logger.debug("Plugin to be updated with plugin id: "+ plm.getPluginMessengers().get(0).getId());
		
		GenericResponse gr = null;
		try {
		plm.getPluginMessengers().forEach(p -> {
			try {
				pluginManager.updatePlugin(p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		gr = new GenericResponse(Status.Success, null, "");
		
		logger.debug("Updated Plugin with plugin id: "+ plm.getPluginMessengers().get(0).getId());
		} catch(Exception e) {
			e.printStackTrace();
			gr = new GenericResponse(Status.Failure, null, "");
			logger.debug("Updated Plugin with plugin id: "+ plm.getPluginMessengers().get(0).getId());
		}
		
		return gr;
	}
	
	
	@RequestMapping(value={"/plugin/executionStatus"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody List<ExecutionDetailsInfo> pluginFunctionExecutionStatus(@RequestBody String[] executionIDs) throws Exception {

		List<ExecutionDetailsInfo> response = new ArrayList<ExecutionDetailsInfo>();
		
		try{
			response = pluginManager.buildResponseFromExecutionDetails(
			executionMonitor.getAllLatestExecutionDetails(),
			this.getSecurityContextAccessor().getTenant().getName(),
			this.getSecurityContextAccessor().getPrincipal().getId().toString());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	@RequestMapping(value = {"/plugin/getLatestExecutionDetailsOf"}, method = RequestMethod.GET)
	public @ResponseBody ExecutionDetailsInfo getLatestExecutionDetailsOf(@RequestParam String executionID) throws NoExecutionDetailsFound {
		
		return pluginManager.executionDetailsToExecutionDetailsInfo(executionMonitor.getLatestExecutionDetailsOf(executionID));
	}
	
	@RequestMapping( value = "/plugin/deleteExecutionDetailsInfo", method = RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse deleteExecutionDetailsByID(@RequestBody String[] ExecutionInfoIDs)
	{
		logger.debug("Removing execution details for ids: "+ ExecutionInfoIDs);
		
		GenericResponse gr = null;
		String returnValue = null;
		
		try {
			Arrays.asList(ExecutionInfoIDs).forEach(executionID -> {
				executionMonitor.getAllLatestExecutionDetails().remove(executionID);
			});
			
			returnValue = ExecutionInfoIDs.toString();
			
			gr = new GenericResponse(Status.Success, returnValue, "Successfully removed");
			
			
			logger.debug("Succeeded at removing execution details for id: "+ ExecutionInfoIDs);
		} catch(Exception e) {
			gr = new GenericResponse(Status.Failure, returnValue, "Removal failed");
			logger.debug("Failed at removing execution details for ids: "+ ExecutionInfoIDs);
			e.printStackTrace();
		}
		
		return gr;
	}
}