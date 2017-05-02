/**
 * 
 */
package gr.cite.geoanalytics.manager;

import java.io.File;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.internal.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mchange.io.FileUtils;

import gr.cite.gaap.datatransferobjects.PluginInfo;
import gr.cite.geoanalytics.dataaccess.entities.plugin.Plugin;
import gr.cite.geoanalytics.dataaccess.entities.plugin.PluginConfiguration;
import gr.cite.geoanalytics.dataaccess.entities.plugin.PluginLibrary;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.plugin.dao.PluginConfigurationDao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.dao.PluginDao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.dao.PluginInfoDaoUtil;
import gr.cite.geoanalytics.dataaccess.entities.plugin.dao.PluginLibraryDao;
import gr.cite.geoanalytics.dataaccess.entities.plugin.metadata.PluginConfigurationMetadata;
import gr.cite.geoanalytics.dataaccess.entities.plugin.metadata.PluginMetadata;
import gr.cite.geoanalytics.dataaccess.entities.project.dao.ProjectDao;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

/**
 * @author vfloros
 *
 */
@Service
public class PluginManager {
	private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
	private static JAXBContext pluginContext = null;
	
//	private PrincipalManager principalManager;
//	private GeocodeManager taxonomyManager;
//	private ConfigurationManager configurationManager;
//	private GeospatialBackend shapeManager;
//	private SecurityContextAccessor securityContextAccessor;
//	private DocumentManager documentManager;
	@Autowired private ProjectManager projectManager;

//	private ShapeDao shapeDao;
	@Autowired private ProjectDao projectDao;
//	private ProjectLayerDao projectLayerDao;
	@Autowired private PluginDao pluginDao;
	@Autowired private PluginLibraryDao pluginLibraryDao;
	@Autowired private PluginConfigurationDao pluginConfigurationDao;
	
	@Transactional(rollbackFor=Exception.class)
	public void deleteAllPlugins() throws Exception{
		pluginDao.deleteAll();
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void deleteAllPluginLibraries() throws Exception{
		pluginLibraryDao.deleteAll();
	}
	
	@Transactional(rollbackFor=Exception.class)
	public Plugin createPlugin(Plugin p) throws Exception{
		return pluginDao.create(p);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public PluginLibrary createPluginLibrary(PluginLibrary pl) throws Exception{
		return pluginLibraryDao.create(pl);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void updatePlugin(Plugin p) throws Exception{
		pluginDao.update(p);
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
	public List<PluginInfo> retrieveAllAvailablePlugins(Tenant tenant){
		List<PluginInfoDaoUtil> plugins = pluginDao.listPlugins(tenant);
		
		return this.pluginDaoUtilToPluginfo(plugins);
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
			
			PluginMetadata pm = new PluginMetadata();
			try {
				pm = getPluginMetadataFromXMLField(this.getPluginMetadataByID(pl.getPluginId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			pluginInfo.setWidgetName(pm.getWidgetName());
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
	
	public boolean loadPluginJarToDirectory(Plugin plugin, String directoryName, String fileName){
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
					fos.write(plugin.getPluginLibrary().getData());
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
			java.util.Enumeration enumEntries = jar.entries();
			while (enumEntries.hasMoreElements()) {
			    java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
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
	
	public PluginMetadata getPluginMetaDataByPluginId(UUID pluginId) throws JAXBException {
		Plugin plugin = pluginDao.read(pluginId);
		
		String metaData = plugin.getMetadata();
		
		return this.getPluginMetadataFromXMLField(metaData);
	}
	
	public void addCompiledFileToClasspath(String jarPath, PluginMetadata pluginMetadata) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException{
		File file = new File(jarPath);
		URL url = file.toURI().toURL();

		URL[] classLoaderUrls = new URL[]{url};//new URL[]{new URL("file://"+ jarPath)};
		
		URLClassLoader child = new URLClassLoader (classLoaderUrls, this.getClass().getClassLoader());
		URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
		
		Class<?> beanClass = urlClassLoader.loadClass(pluginMetadata.getQualifiedNameOfClass());
        
        // Create a new instance from the loaded class
        Constructor<?> constructor = beanClass.getConstructor();
        Object beanObj = constructor.newInstance();
        
        // Getting a method from the loaded class and invoke it
        Method method = beanClass.getMethod(pluginMetadata.getMethodName());
        method.invoke(beanObj);

//		Class classToLoad = Class.forName (className, true, child);
//		Method method = classToLoad.getDeclaredMethod ("greetings");
//		Object instance = classToLoad.newInstance ();
//		Object result = method.invoke (instance);
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
		URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
		
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
			public boolean accept(File dir, String filename)
			{ return filename.endsWith(".js"); }
		} );

	}
	
	@Transactional(rollbackFor=Exception.class)
	public PluginConfiguration createConfigurationForPluginInNotExists(
			UUID pluginId, UUID projectId,
			String tenantName) throws JAXBException{
		
		Plugin plugin = pluginDao.read(pluginId);
//				this.getPluginByNameAndTenantName(pluginName, tenantName);
		Project project = null;
		try {
			project =  projectManager.findByID(projectId);
//					projectManager.findByNameAndTenant(projectName, tenantName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PluginConfiguration plc = pluginConfigurationDao.findByPluginAndProject(plugin, project);
		
		if(plc == null){
			Date date = Calendar.getInstance().getTime();
			
			plc = new PluginConfiguration();
			plc.setPlugin(plugin);
			plc.setProject(project);
			plc.setConfiguration(this.marshaldPluginConfigurationMetadata());
			plc.setCreationDate(date);
			plc.setLastUpdate(date);
			
			pluginConfigurationDao.create(plc);
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
	
	public Object addCompiledFileToClasspathAndInvokeMethod(
			String jarPath, PluginMetadata pluginMetadata,
			Map<String,Object> parameters)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {
		
		File file = new File(jarPath);
		URL url = file.toURI().toURL();

		URL[] classLoaderUrls = new URL[]{url};//new URL[]{new URL("file://"+ jarPath)};
		
//		URLClassLoader child = new URLClassLoader (classLoaderUrls, this.getClass().getClassLoader());
		URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls, Thread.currentThread().getContextClassLoader());
		
		Class<?> beanClass = null;
		try {
//			beanClass = Class.forName(pluginMetadata.getQualifiedNameOfClass());
			beanClass = urlClassLoader.loadClass(pluginMetadata.getQualifiedNameOfClass());
		} catch(Exception e) {
			e.printStackTrace();
		}
        
        // Create a new instance from the loaded class
        Constructor<?> constructor = beanClass.getConstructor();
        Object beanObj = constructor.newInstance();
        
        // Getting a method from the loaded class and invoke it
        Method method = beanClass.getMethod(pluginMetadata.getMethodName(), Map.class);
        
        Object ret = method.invoke(beanObj, parameters);
        
        urlClassLoader.close();
        
        return (String)ret;
	}
	
	public String returnJarPath(String pluginsFolderLocation, String pluginsFolderName, UUID tenantId, UUID pluginId) {
		String dirPath = pluginsFolderLocation;
		String directoryName = pluginsFolderName;
		String tenantID = tenantId.toString();
		String plugiFolderName = pluginId.toString();
		String jarPlugin = pluginId.toString() + ".jar";
		
		String jarPath=null;

		String directoryFullPath = dirPath + File.separator + directoryName;
		File dir = new File(directoryFullPath);
		
		if(dir.exists() && dir.isDirectory()){
			try (Stream<Path> stream = Files.find(dir.toPath(), 5,
		            (path, attr) -> path.getFileName().toString().equals(jarPlugin))) {
//		        System.out.println(stream.findAny().isPresent());
				jarPath = stream.findAny().get().toString();
				log.info("Plugin found: " + jarPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
//		String directoryFullPath = dirPath + File.separator + directoryName
//				+ File.separator + tenantID + File.separator + plugiFolderName;
//		String jarPath = directoryFullPath + File.separator + plugiFolderName + ".jar";
		
		return jarPath;
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
}