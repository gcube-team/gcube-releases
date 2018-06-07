/**
 * 
 */
package gr.cite.geoanalytics.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import gr.cite.clustermanager.actuators.functions.ExecutionMonitor;
import gr.cite.clustermanager.model.functions.ExecutionDetails;
import gr.cite.gaap.datatransferobjects.PluginInfo;
import gr.cite.gaap.datatransferobjects.plugin.ExecutionDetailsInfo;
import gr.cite.gaap.datatransferobjects.plugin.FunctionResponse;
import gr.cite.gaap.datatransferobjects.plugin.PluginLibraryMessenger;
import gr.cite.gaap.datatransferobjects.plugin.PluginUploadMessenger;
import gr.cite.geoanalytics.dataaccess.entities.layer.Layer;
import gr.cite.geoanalytics.dataaccess.entities.plugin.Plugin;
import gr.cite.geoanalytics.dataaccess.entities.plugin.PluginConfiguration;
import gr.cite.geoanalytics.dataaccess.entities.plugin.PluginLibrary;
import gr.cite.geoanalytics.dataaccess.entities.plugin.dao.PluginConfigurationDao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.dao.PluginDao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.dao.PluginInfoDaoUtil;
import gr.cite.geoanalytics.dataaccess.entities.plugin.dao.PluginLibraryDao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.metadata.PluginConfigurationMetadata;
import gr.cite.geoanalytics.dataaccess.entities.plugin.metadata.PluginMetadata;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDao;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectLayerDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;
import gr.cite.geoanalytics.execution.SparkConfig;
import gr.cite.geoanalytics.functions.common.ExecutionParameters;
import gr.cite.geoanalytics.functions.common.model.functions.FunctionLayerConfigI;
import gr.cite.geoanalytics.functions.common.model.functions.LayerConfig;
import gr.cite.geoanalytics.helper.FileSearch;
import gr.cite.geoanalytics.security.SecurityContextAccessor;

/**
 * @author vfloros
 *
 */
@Service
public class PluginManager extends BaseManager {
	
	public PluginManager(SecurityContextAccessor securityContextAccessor) {
		super(securityContextAccessor);
	}

	public static String PLUGINS_FOLDER_LOCATION;
	public static final String PLUGINS_FOLDER_NAME = "geoanalytics_plugins";
	
	private final static String SPARK_SYSTEMIC_PROPERTIES = "spark_system.properties";
	private final static String SPARK_ENV_PROPERTIES = "spark_env.properties";
	
	
	private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
	private static JAXBContext pluginContext = null;

	@Autowired private ServletContext context;
	
	@Autowired private ExecutionMonitor executionMonitor;
	@Autowired private LayerManager layerManager;
	@Autowired private PluginConfigurationDao pluginConfigurationDao;
	@Autowired private PluginDao pluginDao;
	@Autowired private PluginLibraryDao pluginLibraryDao;
	@Autowired private PrincipalManager principalManager;
	@Autowired private ProjectDao projectDao;
	@Autowired private ProjectLayerDao projectLayerDao;
	@Autowired private ProjectManager projectManager;
	@Autowired private SparkConfig sparkConfig;
	
	
	@Value("${gr.cite.geoanalytics.plugins.basepath}")
    public void setPLUGINS_FOLDER_LOCATION(String pluginsFolderLocation) {
		PLUGINS_FOLDER_LOCATION = pluginsFolderLocation;
    }
	
	@Transactional(rollbackFor=Exception.class)
	public void deleteAllPlugins() throws Exception{
		pluginDao.deleteAll();
	}
	
	@Transactional(readOnly=true)
	public Plugin getPluginByID(UUID id) {
		return pluginDao.read(id);
	}
	
	@Transactional(readOnly=true)
	public String getPluginNameByID(UUID pluginID){
		return pluginDao.getPluginNameByID(pluginID);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void deleteAllPluginLibraries() throws Exception{
		pluginLibraryDao.deleteAll();
	}
	
//Attention this is a temporary method.
//Deleting plugins should be a different operation to deleting plugin libraries.
	@Transactional(rollbackFor=Exception.class)
	public void deletePluginAndParentPluginLibraryByPluginID(UUID pluginID) throws Exception {
		log.debug("Deleting PluginLibrary and Plugin with plugin id: "+ pluginID.toString());

		UUID plID = pluginLibraryDao.getPluginLibraryIDByPluginID(pluginID);
		pluginConfigurationDao.deletePluginConfigurationByPluginID(pluginID);
		pluginDao.deletePluginByPluginID(pluginID);
		pluginLibraryDao.deletePluginLibraryByPluginID(plID);
		
		log.debug("Deleted Plugin with id: {} and PluginLibrary with id: {}",pluginID.toString(), plID.toString());
		this.deletePluginJARAndFolders(pluginID);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Plugin createPlugin(Plugin p) throws Exception{
		return pluginDao.create(p);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public PluginLibrary createPluginLibrary(PluginLibrary pl) throws Exception{
		return pluginLibraryDao.create(pl);
	}
	
	@Transactional(rollbackFor = Exception.class )
	public boolean deletePluginJARAndFolders(UUID pluginID) throws IOException
	{
		UUID tenantID = this.getSecurityContextAccessor().getTenant().getId();
		
		String jarPath = returnJarPath(PLUGINS_FOLDER_LOCATION, PLUGINS_FOLDER_NAME, tenantID, pluginID);
		
		if(jarPath == null)
			return false;
		//Otherwise delete the folder containing the jar and all its contents
		
		Path path = Paths.get(jarPath);
		
		try {
			//Getting the path of a jar, from this I get the folder containing the jar
			//and thed deleting all the files the folder contains
			Arrays.asList(path.toFile().getParentFile().listFiles()).forEach(f -> {
				try {
					Files.delete(f.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			//Deleting the folder containing the jar
			Files.delete(path.toFile().getParentFile().toPath());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.debug("Deleted jar of Plugin with id: {} from filesystem", pluginID.toString());
		
		return true;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void updatePlugin(PluginUploadMessenger pum) throws Exception {
		log.debug("Updating plugin with ID: " + pum.getId());
		Date now = Calendar.getInstance().getTime();
		
		Plugin p = pluginDao.read(pum.getId());
		p.setDescrtiption(pum.getDescription());
		p.setLastUpdate(now);
		p.setName(pum.getName());
		p.setType((short)pum.getType());
		p.setMetadata(xmlMetaData(pum));
		
		pluginDao.update(p);
		
		log.debug("Updated plugin with ID: " + pum.getId());
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void updatePluginLibrary(PluginLibrary pl) throws Exception{
		pluginLibraryDao.update(pl);
	}
	
	@PreAuthorize("@projectPermissionEvaluator.hasPermissionDeleteProject(#project.id)")
	@Transactional(readOnly = true)
	public Map<String,String> retrieveAvailablePluginsForProject(Project project){
		Map<String, String> pluginNameDescriptionMap = new HashMap<String, String>();
		Project prj = this.projectDao.read(project.getId());
		for(PluginConfiguration plgnc : prj.getPluginConfiguration()){
			pluginNameDescriptionMap.put(plgnc.getPlugin().getName(), plgnc.getPlugin().getDescrtiption());
		}
		
		return pluginNameDescriptionMap;
	}
	
	@Transactional(readOnly = true)
	public List<PluginInfo> retrieveAllAvailablePluginsByTenant(Tenant tenant){
		List<Plugin> plugins = pluginDao.listPluginsByTenant(tenant);
		
		return this.pluginToPluginfo(plugins);
	}
	
	@Transactional(readOnly = true)
	public List<PluginInfo> listPluginsByTenantOrNullTenant(Tenant tenant){
		List<PluginInfoDaoUtil> plugins = pluginDao.listPluginsByTenantOrNullTenant(tenant);
		
		List<PluginInfo> result = this.pluginDaoUtilToPluginfo(plugins);
		
		result.sort((o1, o2) -> o1.getPluginName().compareTo(o2.getPluginName()));
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public List<PluginInfo> retrievePluginInfoByPluginLibraryId(UUID pluginLibraryId) throws Exception {
		PluginLibrary pl = pluginLibraryDao.read(pluginLibraryId);
		
		if(pl.getPlugins().size() > 0){
			return this.pluginToPluginfo(pl.getPlugins().stream().collect(Collectors.toList()));
		} else {
			throw new Exception("Plugin library contains no plugins.");
		}
	}
	
	public List<PluginInfo> pluginToPluginfo(Collection<Plugin> plugins) {
		List<PluginInfo> pluginsInfo = new ArrayList<PluginInfo>();
		
		plugins.forEach(pl -> {
			PluginInfo pluginInfo = new PluginInfo();
			pluginInfo.setPluginDescription(pl.getDescrtiption());
			pluginInfo.setPluginId(pl.getId());
			pluginInfo.setPluginName(pl.getName());
			
			PluginMetadata pm = new PluginMetadata();
			try {
				pm = getPluginMetadataFromXMLField(pl.getMetadata());
			} catch (Exception e) {
				e.printStackTrace();
			}
			pluginInfo.setWidgetName(pm.getWidgetName());
			pluginsInfo.add(pluginInfo);
		});
		
		return pluginsInfo;
	}
	
	public List<PluginInfo> pluginDaoUtilToPluginfo(Collection<PluginInfoDaoUtil> plugins) {
		List<PluginInfo> pluginsInfo = new ArrayList<PluginInfo>();
		
		plugins.forEach(pl -> {
			PluginInfo pluginInfo = new PluginInfo();
			pluginInfo.setPluginDescription(pl.getPluginDescription());
			pluginInfo.setPluginId(pl.getPluginId());
			pluginInfo.setPluginName(pl.getPluginName());
			pluginInfo.setPluginType(pl.getPluginType());
			DateFormat df = new SimpleDateFormat("dd-MMM-YYYY HH:mm");
			String dateStringCreate = df.format(pl.getCreationDate());
			pluginInfo.setCreationDate(dateStringCreate);
			String dateStringUpdate = df.format(pl.getUpdateDate());
			pluginInfo.setUpdateDate(dateStringUpdate);
			
			PluginMetadata pm = new PluginMetadata();
			try {
				pm = getPluginMetadataFromXMLField(this.getPluginMetadataByID(pl.getPluginId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			pluginInfo.setWidgetName(pm.getWidgetName());
			pluginInfo.setJsFileName(pm.getJsFileName());
			pluginInfo.setMethodName(pm.getMethodName());
			pluginInfo.setQualifiedNameOfClass(pm.getQualifiedNameOfClass());
			pluginInfo.setConfigurationClass(pm.getConfigurationClass());
			
			pluginsInfo.add(pluginInfo);
		});
		
		return pluginsInfo;
	}
	
	@Transactional(readOnly = true)
	public String getPluginMetadataByID(UUID pluginID) {
		return pluginDao.getPluginMetadataByID(pluginID);
	}
	
	@Transactional(readOnly = true)
	public Plugin getPluginByNameAndTenantName(String pluginName, String tenantName) {
		Plugin plugin = pluginDao.getPluginByNameAndTenantName(pluginName, tenantName);
		
		return plugin;
	}
	
	public Map<String, ExecutionDetails> getAllExecutionDetails(){
		return executionMonitor.getAllLatestExecutionDetails();
	}
	
	
	public Set<LayerConfig> getPluginLayerConfigs(UUID pluginID) throws InstantiationException, IllegalAccessException, ClassNotFoundException, JAXBException, IOException{
		//get from geoanalytics database the pluginMetadata
		PluginMetadata pluginMetadata = getPluginMetaDataByPluginId(pluginID);
		UUID tenantId = this.getSecurityContextAccessor().getTenant().getId();
		String jarPath = returnJarPath(PLUGINS_FOLDER_LOCATION, PLUGINS_FOLDER_NAME, tenantId, pluginID);
		//load the plugin jar to the classpath, so it is feasible to call the class.
		URLClassLoader urlClassLoader = appendJarToClassLoader(jarPath);
		FunctionLayerConfigI functionExecConfig = (FunctionLayerConfigI)urlClassLoader.loadClass(pluginMetadata.getConfigurationClass()).newInstance();
		
		urlClassLoader.close();
		
		return functionExecConfig.getLayerConfigs();
	}
	
	
	
	@Transactional
	public boolean loadPluginJarToDirectory(String pluginId, String directoryName, String fileName){
//		boolean directoryCreated = createDirectoryIfNotExists(directoryName);
		
//		if(!directoryCreated){
//			return false;
//		}
		
		File directory = new File(directoryName);
		if(!directory.exists()){
			if(directory.mkdirs()){
				log.info("Plugins directory created successfully!!!");
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(fileName);
					fos.write(this.pluginDao.read(UUID.fromString(pluginId)).getPluginLibrary().getData());
					fos.flush();
					fos.close();
					
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					
					return false;
				}
			}else {
				log.info("Failed to create plugins directory!!!");
				return false;
			}
		} else {
			log.info("Plugins directory already exists");
			return true;
		}
	}

	public boolean createDirectoryIfNotExists(String dirPath){
		File directory = new File(dirPath);
		if(!directory.exists()){
			if(directory.mkdirs()){
				log.info("Plugins directory created successfully!!!");
				return true;
			}else {
				log.info("Failed to create plugins directory!!!");
			}
		} else {
			log.info("Plugins directory already exists");
			return true;
		}
		
		return false;
	}
	
	public boolean unzipJarFile(String destinationDirectory, String jarPath){
		try{
			JarFile jar = new JarFile(jarPath);
			java.util.Enumeration<JarEntry> enumEntries = jar.entries();
			while (enumEntries.hasMoreElements()) {
			    java.util.jar.JarEntry file = enumEntries.nextElement();
			    java.io.File f = new java.io.File(destinationDirectory + java.io.File.separator + file.getName());
			    if (file.isDirectory()) { // if its a directory, create it
			        f.mkdir();
			        continue;
			    }
			    java.io.InputStream is = jar.getInputStream(file); // get the input stream
			    java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
			    while (is.available() > 0) {  // write contents of 'is' to 'fos'
			        fos.write(is.read());
			    }
			    fos.close();
			    is.close();
			}
			
			jar.close();
			
			return true;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}	
	}
	
	public PluginMetadata getPluginMetadataFromXMLField(String metaData) throws JAXBException{
		pluginContext = JAXBContext.newInstance(PluginMetadata.class);

		Unmarshaller um = pluginContext.createUnmarshaller();
		PluginMetadata pluginMetadata = (PluginMetadata)um.unmarshal(new StringReader(metaData));
		return pluginMetadata;
	}
	
	@Transactional
	public PluginMetadata getPluginMetaDataByPluginId(UUID pluginId) throws JAXBException {
		String metaData = this.getPluginMetadataByID(pluginId);
		
		return this.getPluginMetadataFromXMLField(metaData);
	}
	
	public File loadWidgetFile(String directoryFullPath, String jarPath, String fileName) throws ClassNotFoundException,
		NoSuchMethodException, SecurityException, InstantiationException,
		IllegalAccessException, IllegalArgumentException, InvocationTargetException,
		IOException {
		
		String theFileName = directoryFullPath + File.separator + fileName;
		File file = new File(theFileName);
		if(file.exists()){
			return file;
		}
		
		File jarFile = new File(jarPath);
		URL url = jarFile.toURI().toURL();
		URL[] classLoaderUrls = new URL[]{url};
		URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls, Thread.currentThread().getContextClassLoader());
		
//		File file = new File(urlClassLoader.getResource(fileName).getFile());
		InputStream is = urlClassLoader.getResourceAsStream(fileName);
		
		byte[] buffer = null;
		try {
			buffer = new byte[is.available()];
			is.read(buffer);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			OutputStream outS = new FileOutputStream(file);
			outS.write(buffer);
			outS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        urlClassLoader.close();
        
        
		return file;
	}
	
	public File[] findFileInDirectory( String dirName){
		log.info("Directory location: "+ dirName);
		File dir = new File(dirName);

		return dir.listFiles(new FilenameFilter() { 
			@Override
			public boolean accept(File dir, String filename)
			{ return filename.endsWith(".js"); }
		} );

	}
	
	@Transactional(rollbackFor=Exception.class)
	public synchronized PluginConfiguration createConfigurationForPluginInNotExists(
			UUID pluginId, UUID projectId,
			String tenantName) throws JAXBException{
		
		Project project = null;
		try {
			project =  projectManager.findByID(projectId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<PluginConfiguration> plcList = pluginConfigurationDao.findByPluginAndProject(pluginId, project);
		PluginConfiguration plc = new PluginConfiguration();
		
		if(plcList == null || plcList.size() == 0) {
			plc = new PluginConfiguration();
			
			Date date = Calendar.getInstance().getTime();
			
			plc.setPlugin(this.getPluginByID((pluginId)));
			plc.setProject(project);
			plc.setConfiguration(this.marshaldPluginConfigurationMetadata());
			plc.setCreationDate(date);
			plc.setLastUpdate(date);
			
			pluginConfigurationDao.create(plc);
		} else if(plcList.size() == 1) {
			plc = plcList.get(0);
			log.info("Retrieved exactly one");
		} else if(plcList.size() > 1 ) {
			log.info("Retrieved more than one");
		}
		
		return plc;
	}
	
	public String marshaldPluginConfigurationMetadata() throws JAXBException{
		pluginContext = JAXBContext.newInstance(PluginConfigurationMetadata.class);
		Marshaller m = pluginContext.createMarshaller();
		StringWriter sw = new StringWriter();
		
		PluginConfigurationMetadata pm = new PluginConfigurationMetadata();
		pm.setParam1("Par1");
		pm.setParam2("Par2");
		
		m.marshal(pm, sw);
		
		return sw.toString();
	}
	
	@SuppressWarnings("unchecked")
	public FunctionResponse addCompiledFileToClasspathAndInvokeMethod(
			UUID pluginID, UUID projectID,
			String jarPath, PluginMetadata pluginMetadata,
			Map<String,Object> parameters)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {
		
		URLClassLoader urlClassLoader = appendJarToClassLoader(jarPath);
		
		FunctionLayerConfigI functionExecConfig = (FunctionLayerConfigI)urlClassLoader.loadClass(pluginMetadata.getConfigurationClass()).newInstance();
		
		Gson gson = new Gson();
		
		String layerConfigsJSON = gson.toJson(parameters.get("layers"));
		
		Set <LayerConfig>  layerConfigs = gson.fromJson(layerConfigsJSON, new TypeToken<HashSet<LayerConfig>>(){}.getType());
		layerConfigs.stream().forEach(lc -> {
			functionExecConfig.getLayerConfigByObjectID(lc.getObjectID()).setLayerID(lc.getLayerID());
		});
		
		parameters.put("functionExecConfig", functionExecConfig);
		
		Class<?> beanClass = null;
		try {
//			beanClass = Class.forName(pluginMetadata.getQualifiedNameOfClass(), true, urlClassLoader);
			beanClass = urlClassLoader.loadClass(pluginMetadata.getQualifiedNameOfClass());
		} catch(Exception e) {
			e.printStackTrace();
		}
        
        // Create a new instance from the loaded class
        Constructor<?> constructor = beanClass.getConstructor();
        Object beanObj = constructor.newInstance();
        
//         Getting a method from the loaded class and invoke it
        Method method = beanClass.getMethod(pluginMetadata.getMethodName(), Map.class);
        method.setAccessible(true);
        
        parameters.put("loader", urlClassLoader);
        
        ExecutionParameters ep = new ExecutionParameters();
        UUID creatorID = null;
        UUID tenantID = null;
		try {
			creatorID = this.getSecurityContextAccessor().getPrincipal().getId();
			tenantID = this.getSecurityContextAccessor().getTenant().getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ep.setTenantID(tenantID.toString());
        ep.setCreatorID(creatorID.toString());
        ep.setPluginID(pluginID.toString());
        ep.setProjectID(projectID.toString());
        
        List<String> jars = new ArrayList<String>();
        jars.add(jarPath);
        ep.setJars(jars);
        
        
        ep.setZookeeperConnStr(sparkConfig.getZkConnStr());
        ep.setSparkEndpoint(sparkConfig.getSparkEndpoint());
        ep.setSparkExecutorMemory(sparkConfig.getSparkExecutorMemory());
        ep.setGeoanalyticsEndpoint(sparkConfig.getGeoanalyticsEndpoint());
        ep.setGeoanalyticsToken(sparkConfig.getGeoanalyticsToken());
        ep.setSparkGeoSplitsX(sparkConfig.getSparkGeoSplitsX());
        ep.setSparkGeoSplitsY(sparkConfig.getSparkGeoSplitsY());
        ep.setFunctionExecConfig((FunctionLayerConfigI)parameters.get("functionExecConfig"));

        
        if(parameters.containsKey("resultingLayerName")){
        	ep.setResultingLayerName((String)parameters.get("resultingLayerName"));
        }
        
        if(parameters.containsKey("resultingLayerStyleName")) {
        	ep.setResultingLayerStyleName((String)parameters.get("resultingLayerStyleName"));
        }

        if(parameters.containsKey("samplingMeters")){
        	ep.setSamplingMeters(Integer.parseInt((String)parameters.get("samplingMeters")));
        }

        if(parameters.containsKey("bbox")) {
        	List<Object> bbox = (List<Object>)parameters.get("bbox");
            ep.setMinX((Double)bbox.get(0));
            ep.setMinY((Double)bbox.get(1));
            ep.setMaxX((Double)bbox.get(2));
            ep.setMaxY((Double)bbox.get(3));
        }
        
        ep.setTenantName(this.getSecurityContextAccessor().getTenant().getName());
        
        //add also the systemic execution parameters of Spark (defined within SPARK_SYSTEMIC_PROPERTIES file)
        ep.setSystemicSparkParams(getPropertiesFromFile(SPARK_SYSTEMIC_PROPERTIES));
        ep.setEnvSparkParams(getPropertiesFromFile(SPARK_ENV_PROPERTIES));
        
        parameters.put("executionParameters", ep);
        
        FunctionResponse ret = (FunctionResponse)method.invoke(beanObj, parameters);
        urlClassLoader.close();
        
        return ret;
	}
	
	public Map<String, String> getPropertiesFromFile(String propertiesFilename) {
		log.info("Loading properties file " + propertiesFilename);
		Map<String, String> fetchedProperties = new HashMap<String, String>();

		try {
			Properties properties = new Properties();
			InputStream inputStream = context.getResourceAsStream("/WEB-INF/" + propertiesFilename);
			properties.load(inputStream);

			Enumeration e = properties.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				fetchedProperties.put(key, properties.getProperty(key));
				log.info("Setting property: " + key + " - " + properties.getProperty(key));
			}
		} catch (Exception e1) {
			log.error("Error loading the systemic properties for Spark within file", e1);
			e1.printStackTrace();
		}

		return fetchedProperties;
	}
	
	public URLClassLoader appendJarToClassLoader(String jarPath) throws MalformedURLException
	{
		File file = new File(jarPath);
		URL url = file.toURI().toURL();

		URL[] classLoaderUrls = new URL[]{url};//new URL[]{new URL("file://"+ jarPath)};
		
//		URLClassLoader child = new URLClassLoader (classLoaderUrls, this.getClass().getClassLoader());
		URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls, Thread.currentThread().getContextClassLoader());
//		URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls, null);
		
		return urlClassLoader;
	}
	
	public Object addJarAndAllContainingClassesToClasspathAndInvokeMethod(
			String jarPath, PluginMetadata pluginMetadata,
			Map<String,Object> parameters)
					throws ClassNotFoundException, NoSuchMethodException, SecurityException,
					InstantiationException, IllegalAccessException, IllegalArgumentException,
					InvocationTargetException, IOException {
		
		JarFile jarFile = new JarFile(jarPath);
		Enumeration<JarEntry> en = jarFile.entries();

		URL[] classLoaderUrls = { new URL("jar:file:" + jarPath+"!/") };
		
		URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls, null);
		
		Class<?> beanClass = null;
		try {

			while (en.hasMoreElements()) {
			    JarEntry je = en.nextElement();
			    if(je.isDirectory() || !je.getName().endsWith(".class") || je.getName().contains("SimpleApp")){
			    	System.out.println("Continueing for:" + je.getName());
			        continue;
			    }
			    // -6 because of .class
			    String className = je.getName().substring(0,je.getName().length()-6);
			    className = className.replace('/', '.');
			    if(className.equals(pluginMetadata.getQualifiedNameOfClass())){
			    	System.out.println("Found the class I was looking for");
			    	continue;
			    }
//			    try {
//			    	System.out.println("Loading class: " + className);
//			    	Class c = urlClassLoader.loadClass(className);
//			    }catch(Exception e){
//			    	System.out.println("Failed to load class: " + className);
//			    	e.printStackTrace();
//			    }
			
			}
			
			beanClass = urlClassLoader.loadClass(pluginMetadata.getQualifiedNameOfClass());
		} catch(Exception e) {
			e.printStackTrace();
		}
        
        // Create a new instance from the loaded class
        Constructor<?> constructor = beanClass.getConstructor();
        Object beanObj = constructor.newInstance();
        
        // Getting a method from the loaded class and invoke it
        Method method = beanClass.getMethod(pluginMetadata.getMethodName(), Map.class);
        UUID creatorID = null;
		try {
			creatorID = this.getSecurityContextAccessor().getPrincipal().getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
        parameters.put("creatorID", creatorID);
        parameters.put("tenant", this.getSecurityContextAccessor().getTenant().getName());
        parameters.put("loader", urlClassLoader);
        
        Object ret = method.invoke(beanObj, parameters);
        
        urlClassLoader.close();
        
        jarFile.close();
        
        return ret;
	}
	
	public String returnJarPath(String pluginsFolderLocation, String pluginsFolderName, UUID tenantId, UUID pluginId) {
		String dirPath = pluginsFolderLocation;
		String directoryName = pluginsFolderName;
		String jarPlugin = pluginId.toString() + ".jar";
		
		String jarPath=null;

		String directoryFullPath = dirPath + File.separator + directoryName;
		File dir = new File(directoryFullPath);
		
		if(dir.exists() && dir.isDirectory()){
			try (Stream<Path> stream = Files.find(dir.toPath(), 5,
		            (path, attr) -> path.getFileName().toString().equals(jarPlugin))) {
				jarPath = stream.findAny().get().toString();
				log.debug("Plugin location: " + jarPath);
			} catch (IOException | NoSuchElementException e) {
				log.debug("Plugin with id: " + pluginId + " was not found in the file system.");
			}
		}	
		
		return jarPath;
	}
	
	public String returnJarDirectory(String pluginsFolderLocation, String pluginsFolderName, UUID tenantId, UUID pluginId)
	{
		String jarDirectory = null;
		
		
		
		return jarDirectory;
	}
	
	public String returnDirectoryFullPath(String pluginsFolderLocation, String pluginsFolderName, UUID tenantId, UUID pluginId) {
		String dirPath = pluginsFolderLocation;
		String directoryName = pluginsFolderName;
		String tenantID = tenantId.toString();
		String plugiFolderName = pluginId.toString();
		String directoryFullPath = dirPath + File.separator + directoryName
				+ File.separator + tenantID + File.separator + plugiFolderName;
		
		return directoryFullPath;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public UUID storePluginLibraryJARToDB(PluginLibraryMessenger plm, MultipartHttpServletRequest request){
		PluginLibrary pluginLibrary = new PluginLibrary();
		Date now = Calendar.getInstance().getTime();
		pluginLibrary.setCreationDate(now);
		pluginLibrary.setLastUpdate(now);
		pluginLibrary.setName(plm.getPluginLibraryName());
		
		InputStream is;
		byte[] jarFile = null;
		try {
			String filename = request.getFiles(request.getFileNames().next()).get(0).getOriginalFilename();
			if(!filename.endsWith(".jar")){
				try {
					throw new Exception("The file you are uploading is not a jar file!!");
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			is = request.getFiles(request.getFileNames().next()).get(0).getInputStream();
			jarFile = IOUtils.toByteArray(is);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		 
		pluginLibrary.setData(jarFile);
		
		try {
			pluginLibrary.setChecksum(generateChecksumForPluginFile(is));
			is.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		
		try {
			this.createPluginLibrary(pluginLibrary);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		if(plm.getPluginMessengers() != null && plm.getPluginMessengers().size() > 0){
			for(PluginUploadMessenger pum : plm.getPluginMessengers()){
				Plugin plugin = new Plugin();
				plugin.setCreationDate(now);
				plugin.setLastUpdate(now);
				plugin.setDescrtiption(pum.getDescription());
				plugin.setName(pum.getName());
				plugin.setType((short)pum.getType());
				try {
					plugin.setMetadata(xmlMetaData(pum));
				} catch (JAXBException e) {
					e.printStackTrace();
					return null;
				}

				plugin.setPluginLibrary(pluginLibrary);
				try {
					this.createPlugin(plugin);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				
				plugin.setTenant(this.getSecurityContextAccessor().getTenant());
			}
		}
		
		return pluginLibrary.getId();
	}
	
	static String xmlMetaData(PluginUploadMessenger pum) throws JAXBException {
		pluginContext = JAXBContext.newInstance(PluginMetadata.class);
		Marshaller m = pluginContext.createMarshaller();
		StringWriter sw = new StringWriter();
		
		PluginMetadata pm = new PluginMetadata();
		pm.setWidgetName(pum.getWidgetName());
		pm.setQualifiedNameOfClass(pum.getClassName());
		pm.setJsFileName(pum.getJsFileName());
		pm.setMethodName(pum.getMethodName());
		pm.setConfigurationClass(pum.getConfigurationClass());
		
		m.marshal(pm, sw);
		
		return sw.toString();
	}
	
	public byte[] generateChecksumForPluginFile(InputStream is) throws IOException{
//		http://stackoverflow.com/questions/4317035/how-to-convert-inputstream-to-virtual-file
		
		final File file = File.createTempFile("inMemoryFile", ".tmp");
		file.deleteOnExit();
		
		FileInputStream fis = null;
		
		try (FileOutputStream fos = new FileOutputStream(file);) {
			IOUtils.copy(is, fos);
			
			MessageDigest md = MessageDigest.getInstance("SHA1");
			fis = new FileInputStream(file);
			
			byte[] dataBytes = new byte[1024];

		    int nread = 0;

		    while ((nread = fis.read(dataBytes)) != -1) {
		      md.update(dataBytes, 0, nread);
		    };
		    
		    log.debug("Generate checksum length: " + md.digest().length);
		    
		    return md.digest();
			
		} catch(IOException | NoSuchAlgorithmException e){
			e.printStackTrace();
		} finally{
			if(fis != null)
				fis.close();
		}
		
		return null;
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Map<String,UUID> relateFunctionExecutionResultedLayersToProject(FunctionResponse fr, UUID projectID) {
		Map<String, UUID> layerToIDmap = new HashMap<String, UUID>();
//		if(fr != null && fr.getLayerIDs() != null){
//			fr.getLayerIDs().forEach(l -> {
//				Layer layer = null;
//				try {
//					layer = layerManager.findLayerById(l);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
//				layerToIDmap.put(layer.getName(), layer.getId());
//				
//				Project project = projectManager.findByID(projectID);
//				if(layer != null){
//					ProjectLayer projectLayer = new ProjectLayer();
//					projectLayer.setProject(project);
//					projectLayer.setLayer(layer);
//					projectLayer.setCreator(project.getCreator());
//					projectLayerDao.create(projectLayer);
//				}
//			});
//		}
		
		return layerToIDmap;
	}
	
	public void validateFile(MultipartHttpServletRequest request) throws Exception {
		String filename = request.getFiles(request.getFileNames().next()).get(0).getOriginalFilename();
		
		String contentType = request.getFiles(request.getFileNames().next()).get(0).getContentType();
		
		if(!contentType.equals("application/x-java-archive")){
			throw new Exception("Content type is not application/x-java-archive");
		}
		
		if(!filename.endsWith(".jar")){
			throw new Exception("The file you are uploading is not a jar file!!");
		}
	}
	
	@Transactional(readOnly=true)
	public List<ExecutionDetailsInfo> buildResponseFromExecutionDetails(Map<String, ExecutionDetails> execDet, String tenantName, String principalID) {
		return execDet.entrySet().stream()
				.filter(execDetMap -> {
					try {
//						return filterExecutionDetailsByTenantAndUser(execDetMap.getValue(), execDetMap.getValue().getTenantName(), principalID);
//						boolean sameTenantClause = execDetMap.getValue().getTenantName().equals(tenantName);
						Project project = projectManager.getProjectById(UUID.fromString(execDetMap.getValue().getProjectID()));
						boolean userOrUserGroupOwnsProject = execDetMap.getValue().getUserID().equals(principalID) || projectManager.canViewProject(this.getSecurityContextAccessor().getPrincipal(), project);
						boolean userSubmittedTheExecution = execDetMap.getValue().getUserID().equals(this.getSecurityContextAccessor().getPrincipal().getId().toString());
//						return sameTenantClause && userOrUserGroupOwnsProject;
						return userOrUserGroupOwnsProject || userSubmittedTheExecution;
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return false;
				})
				.map(execDetailsMap -> {
					return new ExecutionDetailsInfo(
							execDetailsMap.getValue().getId(),
							execDetailsMap.getValue().getSubmissionOrigin(),
							execDetailsMap.getValue().getStartTimestamp(),
							execDetailsMap.getValue().getStopTimestamp(),
							execDetailsMap.getValue().getStatus().toString(),
							execDetailsMap.getValue().getProgress(),
							this.layerNameUtil(execDetailsMap.getValue().getLayerID()),
							execDetailsMap.getValue().getLayerID(),
							principalManager.getPrincipal(UUID.fromString(execDetailsMap.getValue().getUserID())).getName(),
							this.getPluginNameByID(UUID.fromString(execDetailsMap.getValue().getPluginID())),
							projectManager.getProjectById(UUID.fromString(execDetailsMap.getValue().getProjectID())).getName(),
							execDetailsMap.getValue().getTenantName());
				}).collect(Collectors.toList());
	}
	
	public boolean filterExecutionDetailsByTenantAndUser(ExecutionDetails ed, String tenantName, String principalID) throws Exception {
		boolean sameTenantClause = ed.getTenantName().equals(tenantName);
		Project project = projectManager.getProjectById(UUID.fromString(ed.getProjectID()));
		boolean userOrUserGroupOwnsProject = ed.getUserID().equals(principalID) || projectManager.canViewProject(this.getSecurityContextAccessor().getPrincipal(), project);
		
		return sameTenantClause && userOrUserGroupOwnsProject;
	}
	
	public ExecutionDetailsInfo executionDetailsToExecutionDetailsInfo(ExecutionDetails ed) {
		
		return new ExecutionDetailsInfo(
				ed.getId(), null, 0L, 0L, ed.getStatus().toString(), 0,
				this.layerNameUtil(ed.getLayerID()), ed.getLayerID(),
				null, null, null, null);
	}
	
	@Transactional(readOnly=true)
	public String layerNameUtil(String possibleLayerID) {
		if(possibleLayerID != null) {
			try {
				return layerManager.findLayerById(UUID.fromString(possibleLayerID)).getName();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return possibleLayerID;
	}
}