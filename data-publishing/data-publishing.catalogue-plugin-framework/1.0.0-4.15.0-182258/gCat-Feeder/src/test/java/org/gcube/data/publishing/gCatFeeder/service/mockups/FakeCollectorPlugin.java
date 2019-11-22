package org.gcube.data.publishing.gCatFeeder.service.mockups;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueInstanceDescriptor;
import org.gcube.data.publishing.gCatFeeder.model.ControllerConfiguration;
import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;
import org.gcube.data.publishing.gCatFeeder.service.TestCommon;
import org.gcube.data.publishing.gCatfeeder.collectors.CatalogueRetriever;
import org.gcube.data.publishing.gCatfeeder.collectors.CollectorPlugin;
import org.gcube.data.publishing.gCatfeeder.collectors.DataCollector;
import org.gcube.data.publishing.gCatfeeder.collectors.DataTransformer;
import org.gcube.data.publishing.gCatfeeder.collectors.model.PluginDescriptor;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CatalogueInstanceNotFound;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CatalogueNotSupportedException;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CollectorFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeCollectorPlugin implements CollectorPlugin<FakeCustomData> {
	private static final Logger log= LoggerFactory.getLogger(FakeCollectorPlugin.class);

	private static final int NUM_ITEMS=10;
	
	@Override
	public PluginDescriptor getDescriptor() {
		return new PluginDescriptor(TestCommon.FAKE_COLLECTOR_NAME);
	}

	@Override
	public CatalogueRetriever getRetrieverByCatalogueType(String catalogueType) throws CatalogueNotSupportedException {
		switch(catalogueType) {
		case TestCommon.UNAVAILABLE_CATALOGUE : 
		case TestCommon.FAKE_CATALOGUE_ID : return new CatalogueRetriever() {
			@Override
			public CatalogueInstanceDescriptor getInstance() throws CatalogueInstanceNotFound {
				// TODO Auto-generated method stub
				return null;
			}
		};
		default :  throw new CatalogueNotSupportedException(catalogueType+" not supported by "+TestCommon.FAKE_COLLECTOR_NAME);
		}
	}

	@Override
	public Set<String> getSupportedCatalogueTypes() {
		return new HashSet<String> (Arrays.asList(new String[] {
				TestCommon.FAKE_CATALOGUE_ID,TestCommon.UNAVAILABLE_CATALOGUE}));
	}

	@Override
	public DataTransformer<? extends CatalogueFormatData, FakeCustomData> getTransformerByCatalogueType(
			String catalogueType) throws CatalogueNotSupportedException {
		switch(catalogueType) {
		case TestCommon.UNAVAILABLE_CATALOGUE : 
		case TestCommon.FAKE_CATALOGUE_ID : return new DataTransformer<FakeCustomData,FakeCustomData>() {

			@Override
			public Set<FakeCustomData> transform(Collection<FakeCustomData> collectedData) {
				return new HashSet<>(collectedData); 
			}
			
						
		};
		default :  throw new CatalogueNotSupportedException(catalogueType+" not supported by "+TestCommon.FAKE_COLLECTOR_NAME);
		}
	}

	@Override
	public DataCollector<FakeCustomData> getCollector() {
		return new DataCollector<FakeCustomData>() {
			@Override
			public Set<FakeCustomData> collect() throws CollectorFault {
				HashSet<FakeCustomData> toReturn=new HashSet<>();
				for(int i=0;i<NUM_ITEMS;i++)
					toReturn.add(new FakeCustomData());
				return toReturn;
			}
		};
	}

	@Override
	public ControllerConfiguration getPublisherControllerConfiguration(String catalogueType)
			throws CatalogueNotSupportedException {
		switch(catalogueType) {
		case TestCommon.UNAVAILABLE_CATALOGUE : 
		case TestCommon.FAKE_CATALOGUE_ID : return new ControllerConfiguration();
		default :  throw new CatalogueNotSupportedException(catalogueType+" not supported by "+TestCommon.FAKE_COLLECTOR_NAME);
		}
		
	}

	@Override
	public void init() throws Exception {
		log.debug("Simulating init...");
		try{
			Thread.sleep(400);
		}catch(InterruptedException e) {}
	}

	@Override
	public void initInScope() throws Exception {
		log.debug("Simulating init in scope...");
		try{
			Thread.sleep(400);
		}catch(InterruptedException e) {}
	}

	
	@Override
	public void setEnvironmentConfiguration(EnvironmentConfiguration env) {
		// TODO Auto-generated method stub
		
	}
}
