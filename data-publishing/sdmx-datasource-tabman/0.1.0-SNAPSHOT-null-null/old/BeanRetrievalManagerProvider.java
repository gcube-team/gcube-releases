package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager;

import org.gcube.datapublishing.sdmx.RegistryInformationProvider;
import org.gcube.datapublishing.sdmx.model.Registry;
import org.sdmxsource.sdmx.structureretrieval.manager.DefaultServiceRetrievalManager;
import org.sdmxsource.sdmx.structureretrieval.manager.RESTSdmxBeanRetrievalManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BeanRetrievalManagerProvider {

	private RESTSdmxBeanRetrievalManager beanRetrievalManager= null;
	
	private static Logger logger =LoggerFactory.getLogger(BeanRetrievalManagerProvider.class);
	private static BeanRetrievalManagerProvider instance;
	
	private BeanRetrievalManagerProvider () throws Exception
	{
		Registry registry = RegistryInformationProvider.getRegistry();
		
		if (registry == null) throw new Exception("Unable to generate retrieval manager");
		
		logger.debug("Using registry "+registry.getEndpoint() );
		this.beanRetrievalManager = new RESTSdmxBeanRetrievalManager(registry.getEndpoint());
		DefaultServiceRetrievalManager serviceRetrievalManager = new DefaultServiceRetrievalManager();
		serviceRetrievalManager.setBaseUrl(registry.getEndpoint());
		this.beanRetrievalManager.setServiceRetrievalManager(serviceRetrievalManager);
		logger.debug("Configuration completed");
	}
	
	
	public static BeanRetrievalManagerProvider getInstance ()
	{
		if (instance == null)
		{

			try {
				instance = new BeanRetrievalManagerProvider();
			} catch (Exception e) {
				logger.error("Unable to create the beanretrieval provider",e);
			}
		}
		
		return instance;
	}
	
	public RESTSdmxBeanRetrievalManager getRESTSdmxBeanRetrievalManager ()
	{
		return this.beanRetrievalManager;
	}
}
