package org.gcube.data.publishing.gCatFeeder.service.mockups;

import org.gcube.data.publishing.gCatFeeder.catalogues.CatalogueController;
import org.gcube.data.publishing.gCatFeeder.catalogues.CataloguePlugin;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.CataloguePluginDescriptor;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.PublishReport;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.CatalogueInteractionException;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.ControllerInstantiationFault;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.PublicationException;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.WrongObjectFormatException;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueInstanceDescriptor;
import org.gcube.data.publishing.gCatFeeder.model.ControllerConfiguration;
import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;
import org.gcube.data.publishing.gCatFeeder.service.TestCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FakeCataloguePlugin implements CataloguePlugin {

	private static final Logger log= LoggerFactory.getLogger(FakeCataloguePlugin.class);

	
	
	private class FakeController implements  CatalogueController{
		@Override
		public PublishReport publishItem(CatalogueFormatData toPublish)
				throws WrongObjectFormatException, CatalogueInteractionException, PublicationException {
			try {
				log.debug("Simulating publish wait..");
				Thread.sleep(400);
			} catch (InterruptedException e) {				
			}
			return new PublishReport(true, "FAKE CONTROLLER DOES NOT PUBLISH DATA");
		}
		
		@Override
		public void configure(ControllerConfiguration config) {
			
		}
	}
	
	
	
	@Override
	public CataloguePluginDescriptor getDescriptor() {
		return new CataloguePluginDescriptor(TestCommon.FAKE_CATALOGUE_ID);
	}

	@Override
	public CatalogueController instantiateController(CatalogueInstanceDescriptor desc)
			throws ControllerInstantiationFault {
		return new FakeController();
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
