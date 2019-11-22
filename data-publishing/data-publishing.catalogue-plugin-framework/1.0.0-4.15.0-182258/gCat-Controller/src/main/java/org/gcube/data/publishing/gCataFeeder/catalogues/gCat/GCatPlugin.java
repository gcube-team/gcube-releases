package org.gcube.data.publishing.gCataFeeder.catalogues.gCat;

import org.gcube.data.publishing.gCatFeeder.catalogues.CatalogueController;
import org.gcube.data.publishing.gCatFeeder.catalogues.CataloguePlugin;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.CataloguePluginDescriptor;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.ControllerInstantiationFault;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueInstanceDescriptor;
import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;

public class GCatPlugin implements CataloguePlugin {

	@Override
	public CataloguePluginDescriptor getDescriptor() {
		return new CataloguePluginDescriptor("GCAT");
	}

	@Override
	public CatalogueController instantiateController(CatalogueInstanceDescriptor desc)
			throws ControllerInstantiationFault {
		return new GCatController(desc);
	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initInScope() throws Exception {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void setEnvironmentConfiguration(EnvironmentConfiguration env) {
		// TODO Auto-generated method stub
		
	}
}
