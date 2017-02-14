package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.AquaMapsObjectExecutionRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Generator;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.application.aquamaps.publisher.StoreConfiguration;
import org.gcube.application.aquamaps.publisher.StoreConfiguration.StoreMode;
import org.gcube.application.aquamaps.publisher.UpdateConfiguration;
import org.gcube.application.aquamaps.publisher.impl.model.CoverageDescriptor;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AquaMapsObjectWorker extends Thread {


	final static Logger logger= LoggerFactory.getLogger(AquaMapsObjectWorker.class);
	
	
	private AquaMapsObjectExecutionRequest request;

	public AquaMapsObjectWorker(AquaMapsObjectExecutionRequest requestSettings) {		
		request=requestSettings;
	}

	@Override
	public void run() {
		try{
			logger.debug("Started OBJECT "+request.getObject().getSearchId()+"Execution");
			SubmittedManager.setStartTime(request.getObject().getSearchId());
			request.setObject(SubmittedManager.getSubmittedById(request.getObject().getSearchId()));			
			Publisher publisher=ServiceContext.getContext().getPublisher();
			CoverageDescriptor descriptor=new CoverageDescriptor(request.getObject().getSourceHSPEC()+"", request.getObject().getSpeciesCoverage());
			String fileSetID=null;
			String layerID=null;
			Generator<FileSet> fileSetGenerator=new Generator<FileSet>(request, FileSet.class);
			Generator<Layer> layerGenrator=new Generator<Layer>(request,Layer.class);
			
			
			if(request.getObject().getIsCustomized()||request.getObject().isForceRegeneration()){
				descriptor.setCustomized(request.getObject().getIsCustomized());
					fileSetID=publisher.store(FileSet.class, fileSetGenerator,new StoreConfiguration(StoreMode.UPDATE_EXISTING, new UpdateConfiguration(true, true, true)) ,descriptor).getStoredId().getId();
					request.getObject().setFileSetId(fileSetID); //needed to generate gismetadata
					if(request.getObject().getGisEnabled())layerID=publisher.store(Layer.class, layerGenrator,new StoreConfiguration(StoreMode.UPDATE_EXISTING, new UpdateConfiguration(true, true, true)) ,descriptor).getStoredId().getId();
			}else {
				fileSetID=publisher.get(FileSet.class, fileSetGenerator, descriptor).get().getId();
				request.getObject().setFileSetId(fileSetID); //needed to generate gismetadata
				if(request.getObject().getGisEnabled())layerID=publisher.get(Layer.class, layerGenrator, descriptor).get().getId();
			}
			request.getObject().setGisPublishedId(layerID);
			SubmittedManager.update(request.getObject());
			SubmittedManager.updateStatus(request.getObject().getSearchId(), SubmittedStatus.Completed);
		}catch(Exception e){
			logger.error("Failed Object execution "+request.getObject().getSearchId(),e);
			try {
				SubmittedManager.updateStatus(request.getObject().getSearchId(), SubmittedStatus.Error);
			} catch (Exception e1) {
				logger.error("Unexpected Error ",e1);
			}
		}
		finally{
			JobExecutionManager.alertJob(request.getObject().getSearchId(),request.getObject().getJobId());
			JobExecutionManager.cleanReferences(request.getObject());
		}
	}





	


	
	
	
	
	
	
}
