package org.gcube.data.publishing.gCatFeeder.catalogues;

import org.gcube.data.publishing.gCatFeeder.catalogues.model.CataloguePluginDescriptor;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.ControllerInstantiationFault;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueInstanceDescriptor;
import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;

public interface CataloguePlugin {

	public CataloguePluginDescriptor getDescriptor();
	
	public CatalogueController instantiateController(CatalogueInstanceDescriptor desc) throws ControllerInstantiationFault;
	
	
	public void init() throws Exception;
	
	public void initInScope() throws Exception;
	
	public void setEnvironmentConfiguration(EnvironmentConfiguration env);
	
}
