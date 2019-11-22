package org.gcube.data.publishing.gCatFeeder.service.engine.impl;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

import org.gcube.data.publishing.gCatFeeder.catalogues.CatalogueController;
import org.gcube.data.publishing.gCatFeeder.catalogues.CataloguePlugin;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.PublishReport;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.CatalogueInteractionException;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.ControllerInstantiationFault;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.PublicationException;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.WrongObjectFormatException;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueInstanceDescriptor;
import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;
import org.gcube.data.publishing.gCatFeeder.model.InternalConversionException;
import org.gcube.data.publishing.gCatFeeder.service.engine.CatalogueControllersManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.CollectorsManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;
import org.gcube.data.publishing.gCatFeeder.service.engine.PersistenceManager;
import org.gcube.data.publishing.gCatFeeder.service.engine.Storage;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionStatus;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.CataloguePluginNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.CollectorNotFound;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InvalidRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.PersistenceError;
import org.gcube.data.publishing.gCatFeeder.service.model.reports.CatalogueReport;
import org.gcube.data.publishing.gCatFeeder.service.model.reports.CollectorReport;
import org.gcube.data.publishing.gCatFeeder.service.model.reports.ExecutionReport;
import org.gcube.data.publishing.gCatfeeder.collectors.CatalogueRetriever;
import org.gcube.data.publishing.gCatfeeder.collectors.CollectorPlugin;
import org.gcube.data.publishing.gCatfeeder.collectors.DataCollector;
import org.gcube.data.publishing.gCatfeeder.collectors.DataTransformer;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CatalogueInstanceNotFound;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CatalogueNotSupportedException;
import org.gcube.data.publishing.gCatfeeder.collectors.model.faults.CollectorFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionTask implements Runnable {

	private static final Logger log= LoggerFactory.getLogger(ExecutionTask.class);

	private ExecutionDescriptor request;
	private PersistenceManager persistence;
	private CollectorsManager collectors;
	private CatalogueControllersManager catalogues;

	private Infrastructure infrastructure;
	
	private Storage storage;
	
	
	private EnvironmentConfiguration environmentConfiguration;
	
	public ExecutionTask(ExecutionDescriptor desc) {
		super();
		this.request=desc;		
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((request == null) ? 0 : request.hashCode());
		return result;
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExecutionTask other = (ExecutionTask) obj;
		if (request == null) {
			if (other.request != null)
				return false;
		} else if (!request.equals(other.request))
			return false;
		return true;
	}


	public void setPersistence(PersistenceManager p) {
		this.persistence=p;
	}

	public void setCollectorPluginManager(CollectorsManager c) {
		this.collectors=c;
	}

	public void setCataloguesPluginManager(CatalogueControllersManager c) {
		this.catalogues=c;
	}

	
	public void setInfastructureInterface(Infrastructure infra) {
		this.infrastructure=infra;
	}
	
	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	
	
	public void setEnvironmentConfiguration(EnvironmentConfiguration environmentConfiguration) {
		this.environmentConfiguration = environmentConfiguration;
	}
	
	@Override
	public void run() {
		try {
			log.debug("Starting to handle {} ",request);

			log.debug("Setting caller token..");
			String actualToken=infrastructure.decrypt(request.getCallerEncryptedToken());
			infrastructure.setToken(actualToken);

			// try to lock request (FINISH ON FAIL)
			if(persistence.acquire(request.getId())) {
				try {


					log.info("Acquired : {} ",request);

					ExecutionReport report=new ExecutionReport();				
					report.getGenericInformations().setStartTime(Instant.now());
					report.setStartingScope(infrastructure.getCurrentContextName());

					// -- ON SUCCESS reload request
					request =persistence.getById(request.getId());

					
						
					// For ALL COLLECTORS IN REQUEST				
					for(String collectorId : request.getCollectors()) { 
						CollectorReport collectorReport=handleCollector(collectorId);
						report.getCollectorReports().add(collectorReport);				

					}

					// FINALIZE

					report.getGenericInformations().setEndTime(Instant.now());

					String reportUrl=storage.storeReport(report);
					log.info("Stored report at {} ",reportUrl);
					request.setReportUrl(reportUrl);
					request.setStatus(ExecutionStatus.SUCCESS);
					persistence.update(request);
				}catch(PersistenceError | InvalidRequest e) {
					log.error("Unexpected exception while dealing with persistence ",e);				
				}catch(Throwable t) {
					log.error("Unexpected generic exception.",t);
					request.setStatus(ExecutionStatus.FAILED);
					persistence.update(request);
				}



			}else {
				log.debug("Request [{}] is already being managed.",request);
			}
		}catch(Throwable t) {
			log.error("THREAD CANNOT HANDLE REQUESTS!!!. ",t);
			request.setStatus(ExecutionStatus.FAILED);			
			try {
				persistence.update(request);
			} catch (Throwable e) {
				log.error("Unable to update execution Descriptor {} ",request,e);
			} 
		}
	}



	private CatalogueReport handleCatalogueController(String cataloguePluginId,CollectorPlugin collectorPlugin,Set<Serializable> collectedData) {
		CatalogueReport catalogueReport=new CatalogueReport();
		catalogueReport.getGenericInformations().setStartTime(Instant.now());
		try {
			log.debug("Checking catalogue {} support ",cataloguePluginId);
			if(collectorPlugin.getSupportedCatalogueTypes().contains(cataloguePluginId)) {
				CataloguePlugin cataloguePlugin=catalogues.getPluginById(cataloguePluginId);
				// ** INSTANTIATE CATALOGUE CONTROLLER
				log.debug("Looking for catalogue instance ..");
				CatalogueRetriever retriever=collectorPlugin.getRetrieverByCatalogueType(cataloguePluginId);
				CatalogueInstanceDescriptor instanceDescriptor=retriever.getInstance();
				cataloguePlugin.setEnvironmentConfiguration(environmentConfiguration);
				CatalogueController controller=cataloguePlugin.instantiateController(instanceDescriptor);
				
				controller.configure(collectorPlugin.getPublisherControllerConfiguration(cataloguePluginId));
				// ** TRANSFORM
				log.debug("Transforming Collected Data");
				DataTransformer transformer=collectorPlugin.getTransformerByCatalogueType(cataloguePluginId);
				Set<CatalogueFormatData> transformed=transformer.transform(collectedData);
				log.trace("Going to publish {} items to {} ",transformed.size(),instanceDescriptor);

				// ** PUBLISH VIA CONTROLLER							
				for(CatalogueFormatData item : transformed) {
					try {
						catalogueReport.getPublishedRecords().add(controller.publishItem(item));
					}catch(WrongObjectFormatException e) {
						catalogueReport.getPublishedRecords().add(new PublishReport(false,"Wrong format : "+e.getMessage()));
					} catch (CatalogueInteractionException e) {
						catalogueReport.getPublishedRecords().add(new PublishReport(false,"Error while communicating with catalogue : "+e.getMessage()));
					} catch (PublicationException e) {
						catalogueReport.getPublishedRecords().add(new PublishReport(false,"Publication error : "+e.getMessage()));
					} catch (InternalConversionException e) {
						catalogueReport.getPublishedRecords().add(new PublishReport(false,"Conversion error : "+e.getMessage()));
					}
				}

				catalogueReport.getGenericInformations().setSuccess(true);
				catalogueReport.getGenericInformations().setGenericMessage("Data published. See detailed log.");

			}else {
				catalogueReport.getGenericInformations().setSuccess(false);
				catalogueReport.getGenericInformations().setGenericMessage("Catalogue not supported by Collector Plugin.");
			}
		}catch(CataloguePluginNotFound e) {
			String msg="Supported catalogue implementation not found. Catalogue id : "+cataloguePluginId;
			log.warn(msg,e);
			catalogueReport.getGenericInformations().setSuccess(false);
			catalogueReport.getGenericInformations().setGenericMessage(msg);
		} catch (CatalogueNotSupportedException e) {
			String msg="Catalogue not supported by Collector Plugin.";
			log.warn(msg,e);
			catalogueReport.getGenericInformations().setSuccess(false);
			catalogueReport.getGenericInformations().setGenericMessage(msg);
		} catch (ControllerInstantiationFault e) {
			String msg="Unable to contact Catalogue instance.";
			log.warn(msg,e);
			catalogueReport.getGenericInformations().setSuccess(false);
			catalogueReport.getGenericInformations().setGenericMessage(msg);
		} catch (CatalogueInstanceNotFound e) {
			String msg="Unable to find Catalogue instance.";
			log.warn(msg,e);
			catalogueReport.getGenericInformations().setSuccess(false);
			catalogueReport.getGenericInformations().setGenericMessage(msg);
		}
		catalogueReport.getGenericInformations().setEndTime(Instant.now());
		return catalogueReport;
	}


	private CollectorReport handleCollector(String collectorId) {
		CollectorReport collectorReport=new CollectorReport();
		collectorReport.getGenericInformations().setStartTime(Instant.now());
		collectorReport.setSource(collectorId);

		try {
			// * COLLECT DATA
			log.info("Starting collector {} ",collectorId);
			CollectorPlugin collectorPlugin=collectors.getPluginById(collectorId);
			collectorPlugin.setEnvironmentConfiguration(environmentConfiguration);
			DataCollector collector=collectorPlugin.getCollector();
			log.info("Collecting data..");
			Set collectedData=collector.collect();
			log.debug("Collected {} items. Going to transform..",collectedData.size());
			collectorReport.setCollectedItems(collectedData.size());

			// * FOR ALL CATALOGUES IN REQUEST
			for(String cataloguePluginId:request.getCatalogues()) {
				CatalogueReport catalogueReport=handleCatalogueController(cataloguePluginId,collectorPlugin,collectedData);

				collectorReport.getPublisherReports().add(catalogueReport);
			}

		} catch (CollectorNotFound e) {
			String msg="Requested collector implementation not found.";
			log.warn(msg,e);
			collectorReport.getGenericInformations().setSuccess(false);
			collectorReport.getGenericInformations().setGenericMessage(msg);
		} catch (CollectorFault e) {
			String msg="Collector Failed. "+e.getMessage();
			log.warn(msg,e);
			collectorReport.getGenericInformations().setSuccess(false);
			collectorReport.getGenericInformations().setGenericMessage(msg);
		}

		collectorReport.getGenericInformations().setEndTime(Instant.now());		
		return collectorReport;
	}


	

}
