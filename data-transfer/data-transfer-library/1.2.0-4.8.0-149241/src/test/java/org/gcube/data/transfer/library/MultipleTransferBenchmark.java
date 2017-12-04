package org.gcube.data.transfer.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.gcube.data.transfer.library.TransferReport.ReportType;
import org.gcube.data.transfer.library.faults.FailedTransferException;
import org.gcube.data.transfer.library.faults.InitializationException;
import org.gcube.data.transfer.library.faults.InvalidSourceException;
import org.gcube.data.transfer.library.faults.SourceNotSetException;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;

import ch.qos.logback.core.util.ExecutorServiceUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultipleTransferBenchmark {


	static List<String> endpoints;
	static List<String> nodeIds;


	static List<String> files;
	static List<String> uris;
	static List<String> storageIds;

	static ExecutorService executor=ExecutorServiceUtil.newExecutorService();


	static CountDownLatch doneSignal;

	static ArrayList<TransferReport> reports=new ArrayList<>();
	
	
	static String scope="/gcube/devsec/devVRE";
	
	static{
		endpoints=Arrays.asList(new String[]{
//				"http://node4-d-d4s.d4science.org:80/data-transfer-service/gcube/service",
				"http://node3-d-d4s.d4science.org:80/data-transfer-service/gcube/service"
		});
		doneSignal=new CountDownLatch(endpoints.size());



		nodeIds=new ArrayList<>();
		files=new ArrayList<>();
		files.add("/home/fabio/Documents/Pictures/web_trend_map.png");
		files.add("/home/fabio/Documents/Pictures/web_trend_map.png");
		files.add("/home/fabio/Documents/Pictures/web_trend_map.png");
		files.add("/home/fabio/Documents/Pictures/web_trend_map.png");
		uris=new ArrayList<>();
		uris.add("http://goo.gl/r5jFZ9");
		storageIds=new ArrayList<>();
	}




	public static void main(String[] args) throws InitializationException{
		TokenSetter.set(scope);
		Map<String,DataTransferClient> clients=new HashMap<>();
		for(String endpoint:endpoints)			
			clients.put(endpoint,DataTransferClient.getInstanceByEndpoint(endpoint));
		for(String id:nodeIds)			
			clients.put(id,DataTransferClient.getInstanceByEndpoint(id));




		HashSet<DataTransferClient> startedTests=new HashSet<>();
		for(final Entry<String,DataTransferClient> entry:clients.entrySet()){
			if(!startedTests.contains(entry.getValue())){
				startedTests.add(entry.getValue());

				executor.execute(new Runnable(){

					final DataTransferClient dt=entry.getValue();
					final String id=entry.getKey();
					@Override
					public void run() {
						try{
							TransferReport report=new TransferReport(dt.getDestinationCapabilities());
							TokenSetter.set(scope);

							log.debug("Sending files to {} ",dt.getDestinationCapabilities());
							
							Destination dest=new Destination("banchmarkoutputFile");
							dest.setOnExistingFileName(DestinationClashPolicy.REWRITE);
							
							for(String f:files){
								try {
									TransferResult res=dt.localFile(f,dest);
									report.addReport(ReportType.local,f, res.getTransferedBytes(), res.getElapsedTime());
								} catch (InvalidSourceException | SourceNotSetException
										| FailedTransferException
										| InitializationException e) {
									log.error("Unable to send file {} to {}, error message : {}",f,dt.getDestinationCapabilities().getHostName(),e.getMessage());
								}
							}

							log.debug("Sending uris to {}",dt.getDestinationCapabilities());
							for(String f:uris){
								try {
									TransferResult res=dt.httpSource(f,dest);
									report.addReport(ReportType.uri,f, res.getTransferedBytes(), res.getElapsedTime());
								} catch (InvalidSourceException | SourceNotSetException
										| FailedTransferException
										| InitializationException e) {
									log.error("Unable to send uri {} to {}, error message : {}",f,dt.getDestinationCapabilities().getHostName(),e.getMessage());
								}
							}

							log.debug("Sending storageIds to {}",dt.getDestinationCapabilities());
							for(String f:storageIds){
								try {
									TransferResult res=dt.storageId(f,dest);
									report.addReport(ReportType.storage,f, res.getTransferedBytes(), res.getElapsedTime());
								} catch (InvalidSourceException | SourceNotSetException
										| FailedTransferException
										| InitializationException e) {
									log.error("Unable to send storageId {} to {}, error message : {}",f,dt.getDestinationCapabilities().getHostName(),e.getMessage());
								}
							}
							reports.add(report);
						}catch(Exception e){
							reports.add(new ErrorReport(id));
						}finally{
						doneSignal.countDown();
						}

					}
				});
			}
		}

		try {
			doneSignal.await();
		} catch (InterruptedException e) {
			
		}
		System.out.println("*****************************");
		for(TransferReport r:reports)System.out.println(r.print());
		
		ExecutorServiceUtil.shutdown(executor);
	}





}
