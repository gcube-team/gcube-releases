package org.gcube.data.publishing.gCatfeeder.collectors;

import java.util.Set;

import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatFeeder.model.ControllerConfiguration;
import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;
import org.gcube.data.publishing.gCatfeeder.collectors.model.CustomData;
import org.gcube.data.publishing.gCatfeeder.collectors.model.PluginDescriptor;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CatalogueNotSupportedException;

public interface CollectorPlugin<E extends CustomData>{

	public PluginDescriptor getDescriptor();
	
	public CatalogueRetriever getRetrieverByCatalogueType(String catalogueType) throws CatalogueNotSupportedException;
	
	public Set<String> getSupportedCatalogueTypes();
	
	public DataTransformer<? extends CatalogueFormatData,E> getTransformerByCatalogueType(String catalogueType)throws CatalogueNotSupportedException;
	
	public DataCollector<E> getCollector();
	
	public ControllerConfiguration getPublisherControllerConfiguration(String catalogueType)throws CatalogueNotSupportedException;;
	
	
	public void init() throws Exception;
	
	public void initInScope() throws Exception;
	
	
	public void setEnvironmentConfiguration(EnvironmentConfiguration env);
}
