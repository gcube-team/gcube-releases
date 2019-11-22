package org.gcube.data.publishing.gCatFeeder.tests;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.Set;

import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatFeeder.model.InternalConversionException;
import org.gcube.data.publishing.gCatfeeder.collectors.CollectorPlugin;
import org.gcube.data.publishing.gCatfeeder.collectors.model.CustomData;
import org.gcube.data.publishing.gCatfeeder.collectors.model.PluginDescriptor;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CatalogueNotSupportedException;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CollectorFault;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseCollectorTest extends InfrastructureTests{

	
	
	private static final Logger log= LoggerFactory.getLogger(BaseCollectorTest.class);

	private static ServiceLoader<CollectorPlugin> collectorPluginsLoader   = ServiceLoader.load(CollectorPlugin.class);



	@BeforeClass
	public static void checkPluginRegistration() {
		int pluginsCounter=0;
		for(CollectorPlugin pl:collectorPluginsLoader) {
			pluginsCounter++;			
		}

		Assert.assertFalse("No plugin is actually registered ",pluginsCounter==0);

		for(CollectorPlugin<? extends CustomData> plugin:collectorPluginsLoader) {
			PluginDescriptor desc=plugin.getDescriptor();
			plugin.setEnvironmentConfiguration(getEnvironmentConfiguration());
			Assert.assertNotNull(plugin.getClass()+" No Descriptor exposed", desc);
			try{
				plugin.init();			
			}catch(Throwable t) {
				log.error("",t);
				Assert.fail("Unable to init plugin "+desc.getName());
			}		

			Assert.assertTrue(desc.getName()+": No catalogues supported",plugin.getSupportedCatalogueTypes().size()>0);

			for(String supportedCatalogue:plugin.getSupportedCatalogueTypes()) {
				try{
					Assert.assertNotNull(desc.getName()+": Null configuration for exposed "+supportedCatalogue,plugin.getPublisherControllerConfiguration(supportedCatalogue));
					Assert.assertNotNull(desc.getName()+": Null retriever for exposed "+supportedCatalogue,plugin.getRetrieverByCatalogueType(supportedCatalogue));
					Assert.assertNotNull(desc.getName()+": Null transformer for exposed "+supportedCatalogue,plugin.getTransformerByCatalogueType(supportedCatalogue));
				}catch(CatalogueNotSupportedException e) {
					log.error("",e);
					Assert.fail("Exposed supported catalogue actually not covered."+e.getMessage());
				}
			}

			Assert.assertNotNull(desc.getName()+": No actual collector ",plugin.getCollector());
		}
		
		
		// Check if instrastructure is enabled
		
		
		
		if(isTestInfrastructureEnabled()) {
			
			
			
			for(CollectorPlugin<? extends CustomData> plugin:collectorPluginsLoader) {
				try {
					plugin.setEnvironmentConfiguration(getEnvironmentConfiguration());
					Collection collected=plugin.getCollector().collect();
					for(String catalogue:plugin.getSupportedCatalogueTypes()) {
						log.debug("Simulating publication towards {} ",catalogue);
						Set<CatalogueFormatData> transformed=plugin.getTransformerByCatalogueType(catalogue).transform(collected);
						log.debug("Transformed size = {} ");
						for(CatalogueFormatData f:transformed)
							log.debug(f.toCatalogueFormat());
					}
							
				} catch (CollectorFault e) {
					log.error("",e);
					Assert.fail("Infrastructure Environment not suitable for testing"+e.getMessage());
				} catch (CatalogueNotSupportedException e) {
					log.error("",e);
					Assert.fail("Exception while getting transformer in scope : "+e.getMessage());
				} catch (InternalConversionException e) {
					log.error("",e);
					Assert.fail("Failed internatl conversion : "+e.getMessage());
				}
			}
			
			
		}
	}


	
	



}
