package org.gcube.data.harmonization.occurrence;

import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.data.streams.dsl.Streams.pipe;
import gr.uoa.di.madgik.grs.record.GenericRecord;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.harmonization.occurrence.impl.ReconciliationImpl;
import org.gcube.data.harmonization.occurrence.impl.readers.CSVParserConfiguration;
import org.gcube.data.harmonization.occurrence.impl.readers.StreamProgress;
import org.gcube.data.spd.client.ResultGenerator;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.streams.Stream;

public class ReconciliationTests {

	/**
	 * @param args
	 */
	
	private static final String scope="/gcube/devsec/devVRE";
	private static final String user="fabio.sinibaldi";
	private static boolean completedImport=false;
	
	public static void main(String[] args) throws Exception {
		System.out.println("Instantiating module..");
		 URI host = URI.create("http://dbtest.next.research-infrastructures.eu:8888");
		 ScopeProvider.instance.set(scope);
		Reconciliation reconciliation=new ReconciliationImpl(user);
		System.out.println("Available computations "+reconciliation.getCapabilities());
		
		//************************ DATASETS
//		List<Resource> data=reconciliation.getDataSets();
//		for(Resource resource:data){
//			System.out.println(resource);
//			try{
//				List<String> fields=reconciliation.openTableInspection(resource.getId());			
//				System.out.println("Fields : "+fields);
//				System.out.println("First row JSON : "+reconciliation.getJSONImported(new PagedRequestSettings(fields.get(0), Order.ASC, 0, 1)));
//			}catch(Exception e){
//				System.err.println("Skipped inspection, exception was  "+e.getMessage());
//			}
//		}
//		reconciliation.closeTableConnection();
//		
//		for(Resource comp:data)
//			if(comp.getOperation().getStatus().equals(Status.COMPLETED)){
//				System.out.println("Trying to export computation result "+comp);
//				System.out.println(reconciliation.getResourceAsFile(comp.getOperation().getOperationId()+"",comp.getOperation().getOperationType()));
//			}
//		
//		//************************* COMUPUTATIONS
//		
//		System.out.println("User History : ");
//		List<Computation> comps=reconciliation.getSubmittedOperationList();
//		for(Computation comp:comps)
//			System.out.println(comp);
//		
//		//************************ EXPORT ******************
//		
//		for(Computation comp:comps)
//			if(comp.getStatus().equals(Status.COMPLETED)){
//				System.out.println("Trying to export computation result "+comp);
//				System.out.println(reconciliation.getResourceAsFile(comp.getOperationId()+"",comp.getOperationType()));
//			}
//		
//		
		// ********************** IMPORT ****************
		
		
		
		
		boolean[] fieldMap=new boolean[21];
		for(int i=0;i<21;i++)fieldMap[i]=true;
		
		
		final OccurrenceStreamer streamer=reconciliation.getStreamer(new File("/home/fabio/Occurrences49OM.csv"), 
				new CSVParserConfiguration(Charset.defaultCharset(), ',', '$', true, fieldMap),"Import Test","Import test");
		
		streamer.streamData();
		
		Thread t=new Thread(){
			@Override
			public void run() {
				try{							
					do{
						StreamProgress progress=streamer.getProgress();
						completedImport=progress.getState().equals(StreamProgress.OperationState.COMPLETED)||progress.getState().equals(StreamProgress.OperationState.FAILED);
						System.out.println(progress);
						try{
							sleep(1000);
						}catch(InterruptedException e){}
					}while(!completedImport);
				}catch(Throwable t){
					t.printStackTrace();
				}
			}
		};
		t.start();
		
		Thread t2=new Thread(){
			@Override
			public void run() {
				try{					
					Stream<OccurrencePoint> stream=pipe(convert(new URI(streamer.getLocator())).of(GenericRecord.class).withDefaults()).through(new ResultGenerator<OccurrencePoint>());
					long count=0;
					while(stream.hasNext()){
						count++;
						System.out.println(count+" point read "+stream.next().getId());
					}
					System.out.println("Total read points  "+count);
				}catch(Exception e){
					System.err.println(e);
				}
			}
		};
		t2.start();
		
		do{
			try{
			Thread.sleep(5000);
			}catch(InterruptedException e){}
		}while(!completedImport);
		
		
		
		
		
	}

}
