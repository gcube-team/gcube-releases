package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.io.File;
import java.io.IOException;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads.DeletionMonitor;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis.AnalysisManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.JobExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables.TableGenerationExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.monitor.StatusMonitorThread;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.FileSetUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesReader;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.ConfigurationManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.DBDescriptor;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.application.aquamaps.publisher.PublisherConfiguration;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.gis.GISInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ServiceContext extends GCUBEServiceContext {

	
	private static Logger logger = LoggerFactory.getLogger(ServiceContext.class);
	
	public enum FOLDERS{
		SERIALIZED,CLUSTERS,IMPORTS,TABLES,ANALYSIS
	}
	
	/** Single context instance, created eagerly */
	private static ServiceContext cache = new ServiceContext();
	
	/** Returns cached instance */
	
	public static ServiceContext getContext() {return cache;}
	
	/** Prevents accidental creation of more instances */
	
	private ServiceContext(){};
	
	
	
	/** {@inheritDoc} */
	@Override
	protected String getJNDIName() {return "gcube/application/aquamaps/aquamapsservice";}

	
		//********PUBLISHER
	private Publisher publisher;
	
	private String configurationScope;
	
	@Override
	protected void onReady() throws Exception{
		
		super.onReady();
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void onInitialisation()throws Exception{
		logger.trace("Initializing AquaMaps Service Context...");
		super.onInitialisation();
		try{
//			int interval=Integer.parseInt(getProperty(PropertiesConstants.ISCRAWLER_INTERVAL_MINUTES));
//			logger.debug("Interval time is "+interval);
			GCUBEScope infrastructureScope=GHNContext.getContext().getStartScopes()[0].getInfrastructure();
			configurationScope=ConfigurationManager.init(infrastructureScope);
			
			logger.trace("Configuration Scope will be "+configurationScope);
			ScopeProvider.instance.set(configurationScope);
			GISInterface.get().getGeoServerDescriptorSet(true);
		}catch (Exception e){
			logger.error("Unable to init configuration",e);
			throw e;
		}
		
		
		try{
			DBDescriptor publisherDB=ConfigurationManager.getVODescriptor().getPublisherDB();
			logger.debug("Publisher Database is "+publisherDB);
			publisher=Publisher.getPublisher();
			PublisherConfiguration config= new PublisherConfiguration(
					publisherDB.getEntryPoint(),
					publisherDB.getUser(),
					publisherDB.getPassword(),
					getPersistenceRoot(),
					(String) this.getProperty("httpServerBasePath", true),
					Integer.parseInt((String)this.getProperty("httpServerPort",true))
					);			
			publisher.initialize(config);
			
		}catch(Exception e){
			logger.error("Unable to initiate Publisher library ",e);
			throw e;
		}
		
		try{
			ServiceUtils.deleteFile(FileSetUtils.getTempMapsFolder());
		}catch(Exception e){
			logger.error("Unable to clean temp maps folder",e);
			throw e;
		}
		
		try{
//			//Monitoring
			StatusMonitorThread t=new StatusMonitorThread(getPropertyAsInteger(PropertiesConstants.MONITOR_INTERVAL),
					getPropertyAsInteger(PropertiesConstants.MONITOR_FREESPACE_THRESHOLD));
			logger.debug("Staring monitor thread: interval = "+getPropertyAsInteger(PropertiesConstants.MONITOR_INTERVAL)+
					"; freespaceThreshold="+getPropertyAsInteger(PropertiesConstants.MONITOR_FREESPACE_THRESHOLD));
			t.start();
		}catch(Exception e){
			logger.error("Unable to start disk monitoring",e);
			throw e;
		}
			
		
		try{
			JobExecutionManager.init(getPropertyAsBoolean(PropertiesConstants.PURGE_PENDING_OBJECTS));
			TableGenerationExecutionManager.init(getPropertyAsBoolean(PropertiesConstants.PURGE_PENDING_HSPEC_REQUESTS),getPropertyAsInteger(PropertiesConstants.PROGRESS_MONITOR_INTERVAL_SEC));
			AnalysisManager.init(true,getPropertyAsInteger(PropertiesConstants.PROGRESS_MONITOR_INTERVAL_SEC));
			SourceManager.checkTables();
		}catch(Exception e){
			logger.error("Unable to start managers",e);
		}
		
		try{
			DeletionMonitor t=new DeletionMonitor(5000);
			t.start();
			logger.info("Deletion Monitor started");
		}catch(Exception e){
			logger.error("Unable to start Deletion Monitor ",e);
		}
		
	}
	
	
	@Override
    protected void onShutdown() throws Exception {
        try{
        	publisher.shutdown();
        }catch(Exception e){
        	logger.error("Unable to shutdown publisher ",e);
        }
        super.onShutdown();
        
    }
    @Override
    protected void onFailure() throws Exception {
        // TODO Auto-generated method stub
        super.onFailure();
        
    }
    
    public String getProperty(String paramName)throws Exception{
    	return PropertiesReader.get(this.getFile("config.properties", false).getAbsolutePath()).getParam(paramName);
    }

    public Boolean getPropertyAsBoolean(String propertyName)throws Exception{
    	return Boolean.parseBoolean(getProperty(propertyName));
    }
    
    public Integer getPropertyAsInteger(String propertyName) throws Exception{
    	return Integer.parseInt(getProperty(propertyName));
    }
    public Double getPropertyAsDouble(String propertyName) throws Exception{
    	return Double.parseDouble(getProperty(propertyName));
    }
    
   public Publisher getPublisher() {
	return publisher;
   }
    
	
	public File getEcoligicalConfigDir(){
		return this.getFile("generator", false);
	}
	
	
	
	
	public String getFolderPath(FOLDERS folderName){
		String persistencePath = ServiceContext.getContext().getPersistenceRoot().getAbsolutePath()+File.separator+folderName;
		File f=new File(persistencePath);
		if(!f.exists()){
			logger.debug("Creating persistence folder "+persistencePath);
			f.mkdirs();
			try {
				Process proc=Runtime.getRuntime().exec("chmod -R 777 "+persistencePath);
				try{
					proc.waitFor();
				}catch(InterruptedException e){
					int exitValue=proc.exitValue();
					logger.debug("Permission execution exit value = "+exitValue);
				}
			} catch (IOException e) {
				logger.warn("Unexpected Exception", e);
			}
		}
		return persistencePath;
	}
	
	public String getConfigurationScope(){
		return configurationScope;
	}
}
