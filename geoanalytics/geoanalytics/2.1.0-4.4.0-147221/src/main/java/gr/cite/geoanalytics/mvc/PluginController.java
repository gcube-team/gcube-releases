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
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import gr.cite.gaap.datatransferobjects.GenericResponse;
import gr.cite.gaap.datatransferobjects.PluginExecutionMessenger;
import gr.cite.gaap.datatransferobjects.PluginInfo;
import gr.cite.gaap.datatransferobjects.UserinfoObject;
import gr.cite.gaap.datatransferobjects.GenericResponse.Status;
import gr.cite.geoanalytics.dataaccess.entities.plugin.Plugin;
import gr.cite.geoanalytics.dataaccess.entities.plugin.PluginConfiguration;
import gr.cite.geoanalytics.dataaccess.entities.plugin.metadata.PluginMetadata;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.manager.PluginManager;
import gr.cite.geoanalytics.security.SecurityContextAccessor;

/**
 * @author vfloros
 *
 */
@Controller
public class PluginController {
	public static final String FS = File.separator;
	public static final String PLUGINS_FOLDER_LOCATION = FS + "home" + FS + "vfloros" + FS + "Desktop";
	public static final String PLUGINS_FOLDER_NAME = "Geoanalytics Portlet Plugins Folder";
	public static final String PLUGIN_AVAILABLE_TO_ALL_TENANTS = "Available to all tenants";
	private static final Logger logger = LoggerFactory.getLogger(PluginController.class);
	
	@Autowired private SecurityContextAccessor securityContextAccessor;
//	@Autowired private TenantManager tenantManager;
//	@Autowired private PrincipalManager principalManager;
//	@Autowired private ProjectManager projectManager;
	@Autowired private PluginManager pluginManager;

	@RequestMapping(value={"/plugin/listProjectPlugins"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse listProjectPlugins(@RequestBody UserinfoObject uio) {
		logger.debug("Retrieving project plugins...");
		try{
			Tenant tenant = securityContextAccessor.getTenant();
			
			List<PluginInfo> res = pluginManager.retrieveAllAvailablePlugins(tenant);

			logger.debug("Retrieving project plugins has been succeeded");
			return new GenericResponse(Status.Success, res, "Authorized");
		}catch(Exception e){
			logger.error("Error while retrieving available plugins for project " + uio.getProjectName(), e);
			return new GenericResponse(Status.Failure, false, e.getMessage());
		}
	}
	
	@RequestMapping(value={"/plugin/executeFunction"}, method=RequestMethod.POST, consumes={"application/json"})
	public @ResponseBody GenericResponse pluginFunctionExectution(@RequestBody PluginExecutionMessenger pem) {
		logger.info("Executing plugin function...");
		try{
			UUID tenantId = securityContextAccessor.getTenant().getId();
			
			String jarPath = pluginManager.returnJarPath(PLUGINS_FOLDER_LOCATION, PLUGINS_FOLDER_NAME, tenantId, pem.getPluginId());
			
			PluginMetadata pluginMetadata = pluginManager.getPluginMetaDataByPluginId(pem.getPluginId());
			
			String res = (String)pluginManager.addCompiledFileToClasspathAndInvokeMethod(jarPath, pluginMetadata, pem.getParameters());

			logger.info("Executing plugin function has been succeeded");
			return new GenericResponse(Status.Success, res, "Authorized");
		}catch(Exception e){
			logger.error("Error while executing plugin function", e);
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
		
		PluginConfiguration pc = pluginManager.createConfigurationForPluginInNotExists(
													UUID.fromString(pluginId),
													projectID,
													tenantName);
		
		Plugin plugin = pc.getPlugin();
		
		if(plugin == null){
			logger.error("Plugin with name: " + pluginName + " not found");
			return;
		} else {
			
		}
		String dirPath = PLUGINS_FOLDER_LOCATION;
		String directoryName = PLUGINS_FOLDER_NAME;
		String tenantID = (plugin.getTenant() == null) ? PLUGIN_AVAILABLE_TO_ALL_TENANTS : plugin.getTenant().getId().toString();
		String plugiFolderName = plugin.getId().toString();
		String directoryFullPath = dirPath + FS + directoryName + FS + tenantID + FS + plugiFolderName;
		String jarPath = directoryFullPath + FS + plugiFolderName + ".jar";
		
		logger.info("Directory: " + directoryFullPath);
		boolean loadedSuccessfully = pluginManager.loadPluginJarToDirectory(plugin, directoryFullPath, jarPath);
		boolean unzippedSuccessfully = true;//pluginManager.unzipJarFile(directoryFullPath,jarPath);
		
		String className = null;
		String jsFileName = "";
		String methodName = "";
		String widgetName = "";
		File widget = null;
		
		if(loadedSuccessfully){
			try {
				PluginMetadata pluginMetadata = pluginManager.getPluginMetadataFromXMLField(plugin.getMetadata());
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
}
