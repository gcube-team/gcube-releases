package org.gcube.data.harmonization.occurrence;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.URI;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerDSL;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;

import com.thoughtworks.xstream.XStream;


public class DirectCalls {
	
	
	
	
	public static void main(String[] args) throws Exception {
		
		ScopeProvider.instance.set("/gcube/devsec");
		
		String user = "fabio.sinibaldi";
		
		URI host = URI.create("http://dbtest.next.research-infrastructures.eu:8888");
		
		
		StatisticalManagerFactory factory = StatisticalManagerDSL.createStateful().at(host).build();
		
	
		
//		StatisticalManagerService service= StatisticalManagerDSL.stateful().build();
//
//		System.out.println("History for "+user);		
//		for(ItemHistory item: service.getUserHistory().getList()){
//			StringBuilder out= new StringBuilder("History Item [ ");
//			out.append("Computation ID : "+item.getComputationId()+",");
//			out.append("Computation : "+item.getComputation()+",");
//			out.append("Start Date : "+item.getStartDate()+",");
//			out.append("End Date : "+item.getEndDate()+",");
//			out.append("Output : "+item.getOutput()+",");
//			out.append("Parameters : "+item.getParameters()+"]");
//			System.out.println(out.toString());
//		}
		
//		System.out.println("Available features");
////		SMTypeParameter type=new SMTypeParameter(StatisticalServiceType.TABULAR_LIST, new StringValues(values));
//		
//	
//		
//		for(Feature f:factory.getFeatures().getList()){
//			StringBuilder out=new StringBuilder("Feature [ ");
//			out.append("Algorithms : "+Arrays.asList(f.getAlgorithms())+",");
//			
//			out.append("Category : "+f.getCategory()+"]");
//			System.out.println(out.toString());
//		}
//		
//		
//		System.out.println("Filtered features");
//		List<String> defaultAlgorithms=(List<String>) deSerialize("/algorithms.xml");
////		factory.
//		
//		Map<String,ComputationalAgentClass> availableAlgorithms=new HashMap<String, ComputationalAgentClass>();
//		
//		for(Feature f:factory.getFeatures().getList()){
//			for(String algorithm:f.getAlgorithms())
//				if(defaultAlgorithms.contains(algorithm)) 
//					availableAlgorithms.put(algorithm, f.getCategory());
//		}
//		
//		for(Entry<String,ComputationalAgentClass> algorithm:availableAlgorithms.entrySet()){
//			System.out.println(algorithm.getKey()+" "+algorithm.getValue());
//			for(SMParameter param : factory.getAlgorithmParameters(new SMComputation(algorithm.getKey(), algorithm.getValue(), "")).getList()){
////				param.get
//			}
//		}
//		new SMCom
//		factory.getAlgorithmParameters(arg0)
//		service.getParameters(computation);
//		System.out.println("Available computations");
//		for(service.getParameters(computation)){
//			
//		}
		
		
		
		
		//****************** IMPORT OCCURRENCE Stream
//		final OccurrenceReader reader=new DarwinCoreReader();
//		final ResultWrapper<OccurrencePoint> wrapper=new ResultWrapper<OccurrencePoint>(scope);
//		final String filePath="/home/fabio/Downloads/Occurrences_239.xml";
//		
//		Thread t=new Thread(){
//			@Override
//			public void run() {
//				InputStream is=null;
//				try {
//					is=new FileInputStream(filePath);
//					reader.streamFile(is,wrapper);
//				} catch (InternalErrorException e) {
//					e.printStackTrace();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}finally{
//					if(is!=null)IOUtils.closeQuietly(is);
//					try{
//						wrapper.close();
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//				}
//			}
//		};
//		t.start();
//		
//		Stream<OccurrencePoint> stream=pipe(convert(new URI(wrapper.getLocator())).of(GenericRecord.class).withDefaults()).through(new ResultGenerator<OccurrencePoint>());
//		
		
//		System.out.println("Streaming to statistical");
//		StatisticalManagerDataSpace dataSpace=StatisticalManagerDSL.dataSpace().at(host).build();
//		W3CEndpointReference epr =dataSpace.createTableFromDataStream(stream, "ImportOKCSVOpen", "import test", user);
//		System.out.println("WS RES epr : "+epr);
//		
//		scope=GCUBEScope.getScope("/gcube/devsec");
//		StatisticalManagerDataSpaceImporter importer = StatisticalManagerDSL.dataSpaceImporter().at(epr).build();
//		
//		OperationInfo info = importer.getOperationStatus();
//    	
//    	while(info.getStatus() != ComputationStatus.COMPLETED) {
//		
//    		System.out.println("STATUS  "+info.getStatus());
//    		System.out.println("PERC "+info.getPercentage());
//    		
//    		Thread.sleep(3000);
//    		info = importer.getOperationStatus();
//
//    	}
    	
    	
    	
    	
	}

	
	public static Object deSerialize(String path)throws Exception{
		ObjectInputStream is=null;
		Object toReturn=null;
		try{
			is=(new XStream()).createObjectInputStream(DirectCalls.class.getResourceAsStream(path));
			while(true){
				toReturn=is.readObject();
			}
		}catch(EOFException e){
			if(is!=null)is.close();
		}
		if(toReturn==null) throw new Exception("Unable to load object from path "+path+", no objects found");
		else return toReturn;
	}
}
