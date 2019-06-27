package org.gcube.usecases.ws.thredds.engine;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.data.transfer.library.DataTransferClient;
import org.gcube.data.transfer.library.TransferResult;
import org.gcube.data.transfer.library.faults.DestinationNotSetException;
import org.gcube.data.transfer.library.faults.FailedTransferException;
import org.gcube.data.transfer.library.faults.InitializationException;
import org.gcube.data.transfer.library.faults.InvalidDestinationException;
import org.gcube.data.transfer.library.faults.InvalidSourceException;
import org.gcube.data.transfer.library.faults.ServiceNotFoundException;
import org.gcube.data.transfer.library.faults.SourceNotSetException;
import org.gcube.data.transfer.library.faults.UnreachableNodeException;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.spatial.data.sdi.interfaces.Metadata;
import org.gcube.spatial.data.sdi.model.metadata.MetadataPublishOptions;
import org.gcube.spatial.data.sdi.model.metadata.MetadataReport;
import org.gcube.spatial.data.sdi.model.metadata.TemplateInvocationBuilder;
import org.gcube.spatial.data.sdi.plugins.SDIAbstractPlugin;
import org.gcube.spatial.data.sdi.utils.ScopeUtils;
import org.gcube.usecases.ws.thredds.Commons;
import org.gcube.usecases.ws.thredds.NetUtils;
import org.gcube.usecases.ws.thredds.TokenSetter;
import org.gcube.usecases.ws.thredds.engine.PublishRequest.Mode;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class PublishThread implements Runnable {





	@NonNull
	private PublishRequest request;
	@NonNull
	private ConcurrentHashMap<String,PublishReport> reports;


	private PublishReport publishReport;
	//	private Map<String,Report> reports;
	//	

	@Override
	public void run() {
		log.info("Request is {}",request);
		log.debug("Switching from {} to {}",SecurityTokenProvider.instance.get(),request.getPublishToken());
		TokenSetter.setToken(request.getPublishToken());
		log.debug("Current scope is :{}, token is {} ",ScopeUtils.getCurrentScope(),SecurityTokenProvider.instance.get());
		
		Destination dest=new Destination();
		dest.setPersistenceId("thredds");
		dest.setSubFolder("public/netcdf/"+request.getCatalog());
		dest.setOnExistingFileName(DestinationClashPolicy.REWRITE);	
		dest.setCreateSubfolders(true);
		dest.setOnExistingSubFolder(DestinationClashPolicy.APPEND);		
		String threddsHostName;


		try {
			threddsHostName = Commons.getThreddsHost();

			DataTransferClient client=Commons.getDTClient(threddsHostName);


			File toPublishSource=null;

			if(request.getMode().equals(Mode.NCML)) {
				if(request.isQueue()){
					log.debug("Waiting for queue {}, expected Count {} ",request.getQueueId(),request.getQueueCount());
					waitFor(request.getQueueId(), request.getQueueCount());

				log.debug("Loading netcdfFile ..");
				File ncmlFile=NetUtils.download(request.getSource().getUrl());
				String toUpdateSource=new String(Files.readAllBytes(ncmlFile.toPath()));			

				for(String reportId:request.getToGatherReportsId()) {
					PublishReport report=getReport(reportId);
					//file://home/gcube/julien.barde/Workspace/DataMiner/Output_Data_Sets/Ichthyop2013.nc

					String toSetUrl="file:/"+report.getTransferResult().getRemotePath();
					toUpdateSource=toUpdateSource.replaceAll(reportId, toSetUrl);				
				}			

				toPublishSource=File.createTempFile("nc_", ".ncml");
				PrintWriter out = new PrintWriter(toPublishSource);
				out.write(toUpdateSource);
				out.flush();

				}
			}



			TransferResult result;
			
			
			// TODO NB Test run without metadata publication
//			result=client.httpSource(request.getSource().getUrl(), dest);
//			publishReport=new PublishReport(false,request.getSource().getId(),result,null);
			
			
			if(!request.isGenerateMeta()) {
				log.debug("Transfering before publishing meta..");

				result = toPublishSource==null?client.httpSource(request.getSource().getUrl(), dest):
					client.localFile(toPublishSource, dest);


				Metadata meta=SDIAbstractPlugin.metadata().build();

				log.debug("Publishing metadata.. ");

				MetadataPublishOptions opts=new MetadataPublishOptions(new TemplateInvocationBuilder().threddsOnlineResources(threddsHostName, request.getSource().getName(), request.getCatalog()).get());
				opts.setGeonetworkCategory("Datasets");
				MetadataReport report=meta.pushMetadata(request.getMetadata(), opts);

				publishReport=new PublishReport(false,request.getSource().getId(),request.getSource().getName(),result,report);
			}else {
				log.debug("Metadata not provided.. ");
				if(request.isQueue()&&request.getMode().equals(Mode.NC)) {
					log.debug("Dataset file is linked in ncml, skipping metadata generation");
					result=client.httpSource(request.getSource().getUrl(), dest);
				}else 
					result=client.httpSource(request.getSource().getUrl(), dest,new PluginInvocation("SIS/GEOTK"));


				publishReport=new PublishReport(false,request.getSource().getId(),request.getSource().getName(),result,null);
			}




		} catch (UnreachableNodeException | ServiceNotFoundException e) {
			log.error("Unable to find Thredds. Publish scope is {} ",ScopeUtils.getCurrentScope(),e);
		} catch (InvalidSourceException | SourceNotSetException | FailedTransferException | InitializationException
				| InvalidDestinationException | DestinationNotSetException e) {
			log.error("Unable to transfer file, ",e);
		} catch (IOException e) {
			log.error("Unable to read/ write file. ",e);
		}

		onCompletion();
	}


	private void onCompletion() {
		if(publishReport==null) publishReport=new PublishReport(true, request.getSource().getId(),request.getSource().getName(), null, null); 
		publishReport(publishReport);

		if(request.getMode().equals(Mode.NC)&&(request.isQueue())) {			
			alert(request.getQueueId(),request.getQueueCount());		
		}

	}


	private PublishReport getReport(String reportId) {
		return reports.get(reportId);
	}


	private void publishReport(PublishReport report) {
		reports.put(report.getSourceId(), report);
	}

	

	
	// static 


	private static ConcurrentHashMap<String,Semaphore> semaphores=new ConcurrentHashMap<>();



	private static void waitFor(String queueId,Integer expected) {
		try {
			log.debug("Waiting for queue {}. Expected Count is {} ",queueId,expected);
			semaphores.getOrDefault(queueId, new Semaphore(expected*-1)).acquire();						
		} catch (InterruptedException e) {
			log.debug("Queue {} is completed.");
		}
	}

	private static void alert(String queueId, Integer expected) {
		log.debug("Alerting queue {}. Expected count is {} ",queueId,expected);
		Semaphore sem=semaphores.getOrDefault(queueId, new Semaphore(expected*-1));
		sem.release();
		log.debug("Queue {} alerted. Remaining : {} out of {} ",queueId,sem.availablePermits(),expected);
	}



}
