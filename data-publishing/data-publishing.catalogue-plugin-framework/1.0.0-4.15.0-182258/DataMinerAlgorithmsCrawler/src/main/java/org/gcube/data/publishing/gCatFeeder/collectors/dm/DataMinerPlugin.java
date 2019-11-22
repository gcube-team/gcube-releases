package org.gcube.data.publishing.gCatFeeder.collectors.dm;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.InternalAlgorithmDescriptor;
import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.ckan.GCatModel;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatFeeder.model.ControllerConfiguration;
import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;
import org.gcube.data.publishing.gCatfeeder.collectors.CatalogueRetriever;
import org.gcube.data.publishing.gCatfeeder.collectors.CollectorPlugin;
import org.gcube.data.publishing.gCatfeeder.collectors.DataCollector;
import org.gcube.data.publishing.gCatfeeder.collectors.DataTransformer;
import org.gcube.data.publishing.gCatfeeder.collectors.model.PluginDescriptor;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CatalogueNotSupportedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataMinerPlugin implements CollectorPlugin<InternalAlgorithmDescriptor> {

	
	private EnvironmentConfiguration env;
	
	
	@Override
	public PluginDescriptor getDescriptor() {
		return new PluginDescriptor(Constants.PLUGIN_ID);
	}

	@Override
	public CatalogueRetriever getRetrieverByCatalogueType(String catalogueType) throws CatalogueNotSupportedException {
		switch(catalogueType) {
		case Constants.GCAT_TYPE : return GCATRetriever.get();
		default : throw new CatalogueNotSupportedException("No support for "+catalogueType); 
		}
	}

	@Override
	public Set<String> getSupportedCatalogueTypes() {
		return Collections.singleton(Constants.GCAT_TYPE);
	}

	@Override
	public DataCollector<InternalAlgorithmDescriptor> getCollector() {
		DMAlgorithmsInfoCollector toReturn=new DMAlgorithmsInfoCollector();
		toReturn.setEnvironmentConfiguration(env);
		return toReturn;
	}

	@Override
	public DataTransformer<? extends CatalogueFormatData, InternalAlgorithmDescriptor> getTransformerByCatalogueType(
			String catalogueType) throws CatalogueNotSupportedException {
		switch(catalogueType) {
		case Constants.GCAT_TYPE : return new GCATTransformer();
		default : throw new CatalogueNotSupportedException("No support for "+catalogueType); 
		}
	}

	@Override
	public void init() throws Exception {
		log.debug("Initializing..");
		
		InputStream is = getClass().getResourceAsStream("profile.xml");
		java.util.Scanner scanner = new java.util.Scanner(is).useDelimiter("\\A");
		String json = scanner.hasNext() ? scanner.next() : "";
		GCatModel.setProfile(json);
		
		DataMinerCollectorProperties.init();
		
	}
	
	@Override
	public void initInScope() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ControllerConfiguration getPublisherControllerConfiguration(String catalogueType)
			throws CatalogueNotSupportedException {
		return new ControllerConfiguration();
	}
	
	
	@Override
	public void setEnvironmentConfiguration(EnvironmentConfiguration env) {
		this.env=env;
	}
}
