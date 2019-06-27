package org.gcube.data.publishing.gCatFeeder.tests;

import java.util.ServiceLoader;

import org.gcube.data.publishing.gCatFeeder.catalogues.CataloguePlugin;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.CataloguePluginDescriptor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseCataloguePluginTest extends InfrastructureTests{

	private static final Logger log= LoggerFactory.getLogger(BaseCataloguePluginTest.class);
	
	private static ServiceLoader<CataloguePlugin> cataloguePluginsLoader   = ServiceLoader.load(CataloguePlugin.class);
	
	
	
	@BeforeClass
	public static void checkPluginRegistration() {
		int pluginsCounter=0;
		for(CataloguePlugin pl:cataloguePluginsLoader) {
			pluginsCounter++;			
		}
		
		Assert.assertFalse(pluginsCounter==0);
	}
	
	@Test
	public void checkImplementations() throws Exception{
		for(CataloguePlugin plugin:cataloguePluginsLoader) {
			CataloguePluginDescriptor desc=plugin.getDescriptor();
			plugin.setEnvironmentConfiguration(getEnvironmentConfiguration());
			Assert.assertNotNull(plugin.getClass()+" No Descriptor exposed", desc);
			try{
				plugin.init();			
			}catch(Throwable t) {
				throw new Exception("Unable to init plugin "+desc.getId());
			}
		}
	}
	
	
}
