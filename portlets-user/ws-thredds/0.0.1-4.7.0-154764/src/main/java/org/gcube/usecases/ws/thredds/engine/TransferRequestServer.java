package org.gcube.usecases.ws.thredds.engine;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;
import org.gcube.usecases.ws.thredds.FolderConfiguration;

import lombok.Data;
import lombok.Synchronized;

public class TransferRequestServer {

	@Data
	public static class Report{
		private AtomicLong requestCount=new AtomicLong(0l);
		private AtomicLong requestServed=new AtomicLong(0l);
		private ConcurrentHashMap<String,PublishReport> reports=new ConcurrentHashMap<>();
		
		
		public File toFile(FolderConfiguration configuration) {
			return toFile(configuration,this);
		}
		
		
		private static final File toFile(FolderConfiguration config,Report report) {
			PrintWriter writer =null;
			try {
				File toReturn=File.createTempFile("tempFile", ".tmp");
				writer=new PrintWriter(toReturn);

				writer.println("REPORT FOR WS-SYNCH");
				writer.println("Configuratiion was : "+config);
				writer.println("Submitted runs : "+report.getRequestCount());
				writer.println("Item reports : ");
				for(Entry<String,PublishReport> entry: report.getReports().entrySet()) {
					PublishReport rep=entry.getValue();
					writer.println("*********************************************************");
					if(rep.isError()) writer.println("OPERATION IS FAILED");
					writer.println("ITEM ID : "+rep.getSourceId());
					writer.println("ITEM NAME : "+rep.getSourceName());
					if(rep.getTransferResult()!=null)writer.println("Transfer report : "+rep.getTransferResult());
					if(rep.getMetadataReport()!=null)writer.println("Metadata report : "+rep.getMetadataReport());
				}
				return toReturn;
			}catch(Throwable t) {
				throw new RuntimeException(t);
			}finally {
				if(writer!=null) {
					IOUtils.closeQuietly(writer);
				}
			}
		}
	}
	
	private Report report=new Report();
	private ExecutorService service=null;
	
	public TransferRequestServer() {
		BlockingQueue<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<Runnable>(
				100);
		service= new ThreadPoolExecutor(1, 10, 30,
				TimeUnit.SECONDS, linkedBlockingDeque,
				new ThreadPoolExecutor.CallerRunsPolicy());
	}


	public void put(PublishRequest request){
		System.out.println("Submitting transfer "+getReport().requestCount.incrementAndGet());
		service.execute(new PublishThread(request, getReport().getReports()));		
		
//		service.execute(new RequestThread(baseUrl,filename,this,publishScope,toPublishMeta));		
	}
	@Synchronized
	public Report getReport(){
		return report;
	}

	
	public void waitCompletion() {
		boolean running=true;
		service.shutdown();
		while(running){
			System.out.println("******************* WAITING FOR TERMINATION ***************** ");
			try{
				running=!service.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
			}catch(InterruptedException e){
				running=!service.isTerminated();
			}			
		}		
		System.out.println("Service is completed : "+service.isTerminated());
	}
	
}
