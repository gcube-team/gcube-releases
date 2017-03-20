package gr.uoa.di.madgik.rr;

import gr.uoa.di.madgik.rr.access.InMemoryStore;
import gr.uoa.di.madgik.rr.plugins.Plugin;
import gr.uoa.di.madgik.rr.plugins.PluginManager;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RRContext
{
	private static final Logger logger = LoggerFactory
			.getLogger(RRContext.class);
	
	public enum DatastoreType
	{
		REMOTE,
		LOCAL,
		LOCALBUFFER
	}
	
	public enum ReadPolicy
	{
		READ_THROUGH,
		READ_LOCAL,
		REFRESH_AHEAD
	}
	public enum WritePolicy
	{
		WRITE_THROUGH,
		WRITE_LOCAL,
		WRITE_BEHIND
	}
	
	private PersistenceManagerFactory pmfLocal;
//	private String connectionURL = null;
//	private String connectionURLBuffer = null;
	private PersistenceManagerFactory pmfRemoteRead;
	private PersistenceManagerFactory pmfRemoteWrite;
	private PersistenceManagerFactory pmfBufferLocal;
	private Set<String> readOnlyTargets=new HashSet<String>();
	private Set<String> editableTargets=new HashSet<String>();
	private Set<String> updateTargets=new HashSet<String>();
	private Set<String> inMemoryTargets=new HashSet<String>();
	
	private Set<String> nonUpdateVOScopes=new HashSet<String>();
	
	private String repositoryProvider=null;
	private Properties repositoryProviderConfig=null;
	private Long bridgingPeriod=null;
	private Long shortBridgingPeriod=null;
	private Boolean clearDataStoreOnStartup=null;
	private String localNodeHostname=null;
	private String localNodePort=null;
	
//	private String connectionDriverName=null;
//	private String connectionDriverNameBuffer=null;
	
	/**
	 * The lock used both by all readers and the updater. Used to prevent the readers from seeing
	 * an empty or half-updated local database when its contents have been cleared and before replicating the buffer database
	 * to the local database. Since we are interested in protecting only the local image, only load operations need to acquire
	 * a shared lock prior to reading from the data store. Protecting store operations as well with a -shared- lock would do
	 * no harm, but is not actually needed currently.
	 */
	private ReadWriteLock lock=null;
	
	protected RRContext() throws ResourceRegistryException
	{
		try
		{
			this.lock = new ReentrantReadWriteLock(true);
			
			Properties propsRegistry = new Properties();
			propsRegistry.load(Thread.currentThread().getContextClassLoader().getResource("resourceregistry.properties").openStream());
			
			
			try {
				Properties propsVOScopesRegistry = new Properties();
				logger.info( "nonUpdateVOScopes reading");
				propsVOScopesRegistry.load(Thread.currentThread().getContextClassLoader().getResource("nonupdatescopes.properties").openStream());
				this.nonUpdateVOScopes = parseNonUpdateVOScopes(propsVOScopesRegistry);
			} catch (Exception e) {
				logger.warn( "nonupdatescopes.properties could not be read");
			}
			
			logger.info( "nonUpdateVOScopes : " + nonUpdateVOScopes);
			//System.out.println("nonUpdateVOScopes : " + nonUpdateVOScopes);
			
			String localDatastoreConfig = parseLocalDatastoreConfig(propsRegistry);
			
			logger.info( "localDatastoreConfig : " + localDatastoreConfig);
			
			if(localDatastoreConfig == null) throw new ResourceRegistryException("Could not find configuration for local data store");
			Properties propsLocal = new Properties();
			URL propsLocalResource = Thread.currentThread().getContextClassLoader().getResource(localDatastoreConfig);
			if(propsLocalResource == null) throw new ResourceRegistryException("Could not find specified configuration for local data store");
			propsLocal.load(propsLocalResource.openStream());
			this.pmfLocal = JDOHelper.getPersistenceManagerFactory(propsLocal);
			
			
			//this.connectionDriverName = propsLocal.getProperty("datanucleus.ConnectionDriverName");
			/* Create and store the connectionURL in order for the reset operation
			 * to be able to contact the derby database. */
//			this.connectionURL = propsLocal.getProperty("datanucleus.ConnectionURL") + ";user=" + propsLocal.getProperty("datanucleus.ConnectionUserName") + ";password=" + propsLocal.getProperty("datanucleus.ConnectionPassword");
//			
//			logger.info( "connectionURL : " + this.connectionURL);
			logger.info( "connectionURL : " + propsLocal.getProperty("datanucleus.ConnectionURL"));
			logger.info( "connectionDriverName : " + propsLocal.getProperty("datanucleus.ConnectionDriverName\""));
			
			String localBufferDatastoreConfig = parseLocalBufferDatastoreConfig(propsRegistry);
			if(localBufferDatastoreConfig == null) throw new ResourceRegistryException("Could not find configuration for local buffer data store");
			Properties propsBufferLocal = new Properties();
			URL propsLocalBufferConfigResource = Thread.currentThread().getContextClassLoader().getResource(localBufferDatastoreConfig);
			if(propsLocalBufferConfigResource == null) throw new ResourceRegistryException("Could not find specified configuration for local buffer data store");
			propsBufferLocal.load(propsLocalBufferConfigResource.openStream());
			this.pmfBufferLocal = JDOHelper.getPersistenceManagerFactory(propsBufferLocal);
			
//			this.connectionDriverNameBuffer = propsBufferLocal.getProperty("datanucleus.ConnectionDriverName");
			/* Create and store the connectionURL in order for the reset operation
			 * to be able to contact the derby database. */
//			this.connectionURLBuffer = propsBufferLocal.getProperty("datanucleus.ConnectionURL") + ";user=" + propsBufferLocal.getProperty("datanucleus.ConnectionUserName") + ";password=" + propsBufferLocal.getProperty("datanucleus.ConnectionPassword");
			
//			logger.info( "connectionURLBuffer : " + this.connectionURLBuffer);
			logger.info( "buffer connectionURL : " + propsBufferLocal.getProperty("datanucleus.ConnectionURL"));
			logger.info( "buffer connectionDriverName : " + propsBufferLocal.getProperty("datanucleus.ConnectionDriverName\""));
			
			
			String remoteReadDatastoreConfig = parseRemoteReadDatastoreConfig(propsRegistry);
			if(remoteReadDatastoreConfig != null)
			{
				Properties propsRemote = new Properties();
				URL propsRemoteReadDatastoreConfigResource = Thread.currentThread().getContextClassLoader().getResource(remoteReadDatastoreConfig);
				if(propsRemoteReadDatastoreConfigResource == null) throw new ResourceRegistryException("Could not find specified configuration for remote data store - read mode");
				propsRemote.load(propsRemoteReadDatastoreConfigResource.openStream());
				this.pmfRemoteRead = JDOHelper.getPersistenceManagerFactory(propsRemote);
			}
			
			String remoteWriteDatastoreConfig = parseRemoteWriteDatastoreConfig(propsRegistry);
			if(remoteWriteDatastoreConfig != null)
			{
				if(remoteReadDatastoreConfig.equals(remoteWriteDatastoreConfig)) this.pmfRemoteWrite = this.pmfRemoteRead;
				else
				{
					Properties propsRemote = new Properties();
					URL propsRemoteWriteDatastoreConfig = Thread.currentThread().getContextClassLoader().getResource(remoteWriteDatastoreConfig);
					if(propsRemoteWriteDatastoreConfig == null) throw new ResourceRegistryException("Could not find specified configutation for remote data store - write mode");
					propsRemote.load(propsRemoteWriteDatastoreConfig.openStream());
					this.pmfRemoteWrite = JDOHelper.getPersistenceManagerFactory(propsRemote);	
				}
			}
			
			String targetsModelConfig = parseTargetsModelConfig(propsRegistry);
			if(targetsModelConfig == null) throw new ResourceRegistryException("Could not find model targets configuration");
			Properties propsTargets = new Properties();
			URL propsTargetsConfigResource = Thread.currentThread().getContextClassLoader().getResource(targetsModelConfig);
			if(propsTargetsConfigResource == null) throw new ResourceRegistryException("Could not find specified model targets configuration");
			propsTargets.load(propsTargetsConfigResource.openStream());
			
			this.bridgingPeriod = this.parseBridgingPeriod(propsRegistry);
			this.shortBridgingPeriod = this.parseShortBridgingPeriod(propsRegistry);
			this.clearDataStoreOnStartup = this.parseClearDataStoreOnStartup(propsRegistry);
			this.repositoryProvider=this.parseRepositoryProvider(propsRegistry);
			this.repositoryProviderConfig=this.parseRepositoryProviderConfig(propsRegistry);
			
			this.readOnlyTargets = this.parseReadOnlyTargets(propsTargets);
			this.editableTargets = this.parseEditableTargets(propsTargets);
			this.updateTargets = this.parseUpdateTargets(propsTargets);
			this.inMemoryTargets = this.parseInMemoryTargets(propsTargets);
			
			
			logger.info( "bridgingPeriod : " + bridgingPeriod);
			logger.info( "shortBridgingPeriod : " + shortBridgingPeriod);
			logger.info( "clearDataStoreOnStartup : " + clearDataStoreOnStartup);
			logger.info( "repositoryProvider : " + repositoryProvider);
			logger.info( "repositoryProviderConfig : " + repositoryProviderConfig);
			
			
			logger.info( "readOnlyTargets : " + readOnlyTargets);
			logger.info( "editableTargets : " + editableTargets);
			logger.info( "updateTargets : " + updateTargets);
			logger.info( "inMemoryTargets : " + inMemoryTargets);
			
						
			registerPlugins(propsRegistry);

		}catch(Exception ex)
		{
			throw new ResourceRegistryException("Could not load persistency factories", ex);
		}
	}
	
	private void registerPlugins(Properties props) throws ResourceRegistryException
	{
		try
		{
			int maxOrder = -1;
			int pluginOrder;
			int count=Integer.parseInt(props.getProperty("pluginCount","0"));
			for(int i=0;i<count;i+=1)
			{
				String pluginClass = props.getProperty("plugin."+i+".class");
				if(pluginClass == null) throw new ResourceRegistryException("Missing class for plugin #"+i);
				String propPluginOrder = props.getProperty("plugin."+i+".order");
				if(propPluginOrder != null)
				{
					pluginOrder = Integer.parseInt(propPluginOrder.trim());
					if(pluginOrder > maxOrder) maxOrder = pluginOrder; 
				}
				else
				{
					if(maxOrder != -1) pluginOrder = maxOrder;
					else
					{
						maxOrder = 0;
						pluginOrder = 0;
					}
				}
				Plugin p = (Plugin)Class.forName(pluginClass.trim()).newInstance();
				p.setup();
				p.readConfiguration("plugin."+i, props);
				PluginManager.registerPlugin(p, pluginOrder);
			}
		}catch(Exception e)
		{
			throw new ResourceRegistryException("Could not register plugins", e);
		}
	}
	
	private String parseRepositoryProvider(Properties props)
	{
		return props.getProperty("repositoryProvider","");
	}
	
	private String parseLocalDatastoreConfig(Properties props) throws Exception
	{
		return props.getProperty("localDatastoreConfig");
	}
	
	private String parseLocalBufferDatastoreConfig(Properties props) throws Exception
	{
		return props.getProperty("localBufferDatastoreConfig");
	}
	
	private String parseRemoteReadDatastoreConfig(Properties props) throws Exception
	{
		String prop = props.getProperty("remoteDatastoreConfig");
		if(prop != null) return prop;
		return props.getProperty("remoteReadDatastoreConfig");
	}
	
	private String parseRemoteWriteDatastoreConfig(Properties props) throws Exception
	{
		String prop = props.getProperty("remoteDatastoreConfig");
		if(prop != null) return prop;
		return props.getProperty("remoteWriteDatastoreConfig");
	}
	
	private Properties parseRepositoryProviderConfig(Properties props) throws Exception
	{
		Properties repositoryConfig = new Properties();
		String config = props.getProperty("repositoryProviderConfig");
		if(config==null) return repositoryConfig;
		URL configResource = Thread.currentThread().getContextClassLoader().getResource(config);
		if(configResource==null) return repositoryConfig;
		repositoryConfig.load(configResource.openStream());	
		return repositoryConfig;
	}
	
	private String parseTargetsModelConfig(Properties props) throws Exception
	{
		return props.getProperty("targetsModelConfig", "targets.model.properties");
	}
	
	private Set<String> parseReadOnlyTargets(Properties props)
	{
		int count=Integer.parseInt(props.getProperty("readonlyTargetsCount","0"));
		Set<String> res=new HashSet<String>(count);
		for(int i=0;i<count;i+=1) res.add(props.getProperty("readonlyTargets."+i,"undefined"));
		return res;
	}
	
	private Set<String> parseEditableTargets(Properties props)
	{
		int count=Integer.parseInt(props.getProperty("editableTargetsCount","0"));
		Set<String> res=new HashSet<String>(count);
		for(int i=0;i<count;i+=1) res.add(props.getProperty("editableTargets."+i,"undefined"));
		return res;
	}
	
	private Set<String> parseUpdateTargets(Properties props)
	{
		int count=Integer.parseInt(props.getProperty("updateTargetsCount","0"));
		Set<String> res=new HashSet<String>(count);
		for(int i=0;i<count;i+=1) res.add(props.getProperty("updateTargets."+i,"undefined"));
		return res;
	}
	
	private Set<String> parseInMemoryTargets(Properties props)
	{
		int count=Integer.parseInt(props.getProperty("inmemoryTargetsCount", "0"));
		Set<String> res=new HashSet<String>(count);
		for(int i=0;i<count;i+=1) res.add(props.getProperty("inmemoryTargets."+i,"undefined"));
		return res;
	}
	
	private Set<String> parseNonUpdateVOScopes(Properties props)
	{
		int count=Integer.parseInt(props.getProperty("nonUpdateVOScopesCount","0"));
		logger.info( "nonupdatescopes count : " + count);
		Set<String> res=new HashSet<String>(count);
		for(int i=0;i<count;i+=1) res.add(props.getProperty("nonUpdateVOScopes."+i,"undefined"));
		
		logger.info( "nonupdatescopes : " + res);
		
		return res;
	}
	
	private Long parseBridgingPeriod(Properties props)
	{
		Long value = null;
		String period, periodUnit;
		if((period = props.getProperty("bridgingPeriod")) == null) return null;
		if((periodUnit = props.getProperty("bridgingPeriodUnit")) == null) return null;
		value = TimeUnit.MILLISECONDS.convert(Long.parseLong(period.trim()), TimeUnit.valueOf(periodUnit.trim()));
		return value;
	}
	
	private Long parseShortBridgingPeriod(Properties props)
	{
		Long value = null;
		String period, periodUnit;
		if((period = props.getProperty("shortBridgingPeriod")) == null) return null;
		if((periodUnit = props.getProperty("shortBridgingPeriodUnit")) == null) return null;
		value = TimeUnit.MILLISECONDS.convert(Long.parseLong(period.trim()), TimeUnit.valueOf(periodUnit.trim()));
		return value;
	}
	
	private Boolean parseClearDataStoreOnStartup(Properties props)
	{
		Boolean propValue = null;
		String prop = null;
		if((prop = props.getProperty("clearDataStoreOnStartup")) == null) return null;
		propValue = Boolean.parseBoolean(prop.trim());
		return propValue;
	}
	
	public String getRepositoryProvider()
	{
		return this.repositoryProvider;
	}
	
	public Properties getRepositoryProviderConfig()
	{
		return this.repositoryProviderConfig;
	}
	
	public Set<String> getReadOnlyTargets()
	{
		return this.readOnlyTargets;
	}
	
	public Set<String> getEditableTargets()
	{
		return this.editableTargets;
	}
	
	public Set<String> getUpdateTargets()
	{
		return this.updateTargets;
	}
	
	public Set<String> getInMemoryTargets()
	{
		return this.inMemoryTargets;
	}
	
	public Set<String> getNonUpdateVOScopes()
	{
		return this.nonUpdateVOScopes;
	}
	
	public void setLocalNodeHostname(String localNodeHostname)
	{
		this.localNodeHostname = localNodeHostname;
	}
	
	public String getLocalNodeHostname() throws ResourceRegistryException
	{
		if(this.localNodeHostname == null) throw new ResourceRegistryException("Missing local node hostname information.");
		return this.localNodeHostname;
	}
	
	public void setLocalNodePort(String localNodePort)
	{
		this.localNodePort = localNodePort;
	}
	
	public String getLocalNodePort() throws ResourceRegistryException
	{
		if(this.localNodePort == null) throw new ResourceRegistryException("Missing local node port information.");
		return this.localNodePort;
	}
	
	public boolean isTargetInMemory(String target)
	{
		if(this.inMemoryTargets.contains(target) == false)
			return InMemoryStore.containsItemType(target);
		return true;
	}

	public Long getBridgingPeriod()
	{
		return this.bridgingPeriod;
	}
	
	public Long getShortBridgingPeriod()
	{
		return this.shortBridgingPeriod;
	}
	
	public Boolean getClearDataStoreOnStartup()
	{
		return this.clearDataStoreOnStartup;
	}
	
	public PersistenceManagerFactory getFactoryForRead(DatastoreType persistencyType) throws ResourceRegistryException
	{
		switch(persistencyType)
		{
			case REMOTE:
			{
				return this.pmfRemoteRead;
			}
			case LOCAL:
			{
				return this.pmfLocal;
			}
			case LOCALBUFFER:
			{
				return this.pmfBufferLocal;
			}
			default:
			{
				throw new ResourceRegistryException("undefined context type "+persistencyType);
			}
		}
	}

	public PersistenceManagerFactory getFactoryForWrite(DatastoreType persistencyType) throws ResourceRegistryException
	{
		switch(persistencyType)
		{
			case REMOTE:
			{
				return this.pmfRemoteWrite;
			}
			case LOCAL:
			{
				return this.pmfLocal;
			}
			case LOCALBUFFER:
			{
				return this.pmfBufferLocal;
			}
			default:
			{
				throw new ResourceRegistryException("undefined context type "+persistencyType);
			}
		}
	}
	
	public PersistenceManager getManagerForRead(DatastoreType persistencyType) throws ResourceRegistryException
	{
		switch(persistencyType)
		{
			case REMOTE:
			{
				return this.pmfRemoteRead.getPersistenceManager();
			}
			case LOCAL:
			{
				return this.pmfLocal.getPersistenceManager();
			}
			case LOCALBUFFER:
			{
				return this.pmfBufferLocal.getPersistenceManager();
			}
			default:
			{
				throw new ResourceRegistryException("undefined context type "+persistencyType);
			}
		}
	}
	
	public PersistenceManager getManagerForWrite(DatastoreType persistencyType) throws ResourceRegistryException
	{
		switch(persistencyType)
		{
			case REMOTE:
			{
				return this.pmfRemoteWrite.getPersistenceManager();
			}
			case LOCAL:
			{
				return this.pmfLocal.getPersistenceManager();
			}
			case LOCALBUFFER:
			{
				return this.pmfBufferLocal.getPersistenceManager();
			}
			default:
			{
				throw new ResourceRegistryException("undefined context type "+persistencyType);
			}
		}
	}
	
	public boolean isDatastoreSupportedForRead(DatastoreType datastore)
	{
		switch(datastore)
		{
			case LOCAL:
			{
				return this.pmfLocal != null;
			}
			case LOCALBUFFER:
			{
				return this.pmfBufferLocal != null;
			}
			case REMOTE:
			{
				return this.pmfRemoteRead != null;
			}
			default:
				return false;
		}
	}
	
	public boolean isDatastoreSupportedForWrite(DatastoreType datastore)
	{
		switch(datastore)
		{
			case LOCAL:
			{
				return this.pmfLocal != null;
			}
			case LOCALBUFFER:
			{
				return this.pmfBufferLocal != null;
			}
			case REMOTE:
			{
				return this.pmfRemoteRead != null;
			}
			default:
				return false;
		}
	}
	
	public boolean isDatastoreSupported(DatastoreType datastore)
	{
		return isDatastoreSupportedForRead(datastore) && isDatastoreSupportedForWrite(datastore);
	}
	
	public Lock getSharedLock()
	{
		return this.lock.readLock();
	}
	
	public Lock getExclusiveLock()
	{
		return this.lock.writeLock();
	}
	
	public void reset() throws ResourceRegistryException {
		try {
			this.resetDT(DatastoreType.LOCAL);
		} catch (Exception e) {
			logger.warn("Error resetting", e);
		}
		try {
			this.resetDT(DatastoreType.LOCALBUFFER);
		} catch (Exception e) {
			logger.warn("Error resetting", e);
		} 
		
		/*try {
			this.resetDT(DatastoreType.REMOTE);
		} catch (Exception e) {
			logger.warn("Error resetting", e);
		}*/
	}
	
	
	public void resetDT(DatastoreType datastoretype) throws ResourceRegistryException {
		PersistenceManager pm = null;
		Lock lock = null;
		Connection conn = null;
	    Statement stmt = null;
	    
	    /*String databaseConnectionUrl = null;
	    String databaseDriverName = null;
	    if (datastoretype == DatastoreType.LOCAL){
	    	databaseConnectionUrl = connectionURL;
	    	databaseDriverName = connectionDriverName;
	    } else if (datastoretype == DatastoreType.LOCALBUFFER) {
	    	databaseConnectionUrl = connectionURLBuffer;
	    	databaseDriverName = connectionDriverNameBuffer;
	    }*/
	    
		
		try {
			lock = this.getExclusiveLock();
			lock.lock();
			
			pm = this.getManagerForWrite(datastoretype);
			pm.currentTransaction().begin();
			
			Set<String> classes = new HashSet<String>();
			classes.addAll(readOnlyTargets);
			classes.addAll(editableTargets);
			classes.addAll(updateTargets);
			classes.addAll(pm.getManagedObjects());

			classes.remove("gr.uoa.di.madgik.rr.element.config.StaticConfigurationDao");
			
			//System.out.println("will delete " + classes + " from " + datastoretype);
			
			for (String cl : classes) {
				try {
					pm.newQuery(Class.forName(cl)).deletePersistentAll();
				} catch (Exception e) {
					logger.warn("problem while deleting : " + cl);
					logger.debug("problem while deleting : " + cl, e);
				}
			} 
			
//			try {
//				pm.deletePersistentAll(classes);
//			} catch (Exception e) {
//				logger.warn("problem while deleting : " + classes);
//				logger.warn("problem while deleting : " + classes, e);
//			}
			
			try {
				pm.deletePersistentAll(pm.getManagedObjects());
			} catch (Exception e) {
				logger.warn("problem while deleting : " + pm.getManagedObjects());
				logger.debug("problem while deleting : " + pm.getManagedObjects(), e);
			}
			
			/*try {
				Class.forName(databaseDriverName).newInstance();
				conn = DriverManager.getConnection(databaseConnectionUrl
						databaseUsername,
						databasePassword,
						); 
	            stmt = conn.createStatement();
	            int val = stmt.executeUpdate("DELETE from NUCLEUS_TABLES");
	            
	            logger.info("deleted : " + val);
			}
			catch (Exception ex) {
				logger.warn("problem while deleting NUCLEUS_TABLES");
				
				throw new ResourceRegistryException(ex);
			}*/
			
			pm.currentTransaction().commit();
		}
		finally
		{
			if (pm.currentTransaction().isActive()) {
				pm.currentTransaction().rollback();
			}
			pm.close();
			
			/*try {
				if (stmt != null)
					stmt.close();

				if (conn != null)
					conn.close();
			}
			catch(Exception ex) {
				throw new ResourceRegistryException(ex);
			}*/
			
			lock.unlock();
		}
	}
}
