package org.gcube.portlets.user.statisticalmanager.server;



public class ClientService {

	//private static final String COMPUTATION_NAME = "BIOCLIMATE_HSPEC"; //"AQUAMAPS_SUITABLE";
	//private static final String COMPUTATION_CATEGORY = "TRANSDUCERER"; //"DISTRIBUTIONS";

	//   static GCUBEClientLog logger = new GCUBEClientLog(GenerateComputation.class);

	public static void main(String[] args) throws Exception {
//		System.out.println("scope set");
//		ScopeProvider.instance.set("/gcube");
//		String user = "gianpaolo.coro";
//		String hostName = "http://dbtest.next.research-infrastructures.eu:8888";		
//		URI host = URI.create(hostName);
//		
//		// get service entry point
////		StatisticalManagerDSL.createStateful();
//		StatisticalManagerFactory factory = StatisticalManagerDSL.createStateful().at(host).build(); // global
//
//		/////////////////////////
//		
//		// get and print algorithms
//		Features features = factory.getFeatures();
//		
//		for(Feature feature: features.getList()) {
//			System.out.println(feature.getCategory());
//			for(String string : feature.getAlgorithms()) {
//				System.out.println(" - " + string + " ");
//			}
//			System.out.println();
//		}
//		
//		/////////////////////////
//		
//		// connect to end point reference, get the ws-resource 
//		W3CEndpointReference eprWsResource = factory.serviceConnect(user); // this must be instantiate for session
//		StatisticalManagerService service = StatisticalManagerDSL.stateful().at(eprWsResource).build();
//
//		// computation creation
//		SMComputation computation = new SMComputation(COMPUTATION_NAME,ComputationalAgentClass.fromString(COMPUTATION_CATEGORY),"My computation");
//
//		// get parameters info
//		ParametersList parameters = service.getParameters(computation);
//
//		System.out.println("\n---PARAMETERS ALGORITHM AQUAMAPS_SUITABLE");
//		for(SMParameter parameter : parameters.getList()) {
// 			System.out.println();
// 			System.out.println("  Name: "+ parameter.getName() + "\n" +
// 					"  Description : " + parameter.getDescription() + "\n" +
// 					"  Type : " +parameter.getType().getName());
// 			for(String value : parameter.getType().getValue().getValues())
// 				System.out.println("  Type value : " + value);
//
// 			// parameter.getValue(), default value
//			//System.out.println("Name: "+parameter.getName() + ";\tValue: " + parameter.getValue() + ";\tType: " +parameter.getType());
//		}
//
////		return;
//		
//		
//		// for tabellar inputs (gettablemetadata)
//		System.out.println("\n---TABLE METADATA");
//		SMTableMetadataList list = service.getTableMetadata();
//		System.out.println("SIZE: "+ list.getList().length);
//		for(SMTableMetadata table : list.getList())
//			System.out.println("Name: "+table.getName()+";\tDescr: "+table.getDescription()+";\tType: "+table.getType()); // table type
//
//		// create computation config
//		ComputationConfig config = new ComputationConfig();
//		config.setComputation(computation);
//		SMEntry[] generationParameters = {
//				new SMEntry("DistributionTable","hspec_suitable_test_2"),
//				new SMEntry("CsquarecodesTable","hcaf_d"),
//				new SMEntry("EnvelopeTable","hspen_mini"),
//				new SMEntry("PreprocessedTable", "maxminlat_hspen"),
//				new SMEntry("CreateTable","true"),
//		};
//		config.setParameters(new SMEntries(generationParameters));
//
//		String computationId = service.executeComputation(config);
//
////		String computationId = "122c9ef9-5b5c-487f-8b43-098728c51891";
//		ComputationInfos infos = service.getComputationInfos(computationId);
//		System.out.println(infos.getStatus());
//		
//		while (!(infos.getStatus().toString().equals("COMPLETE"))) {
//			infos = service.getComputationInfos(computationId);
//			System.out.println("-----------\nSTATUS: "+infos.getStatus());
//			System.out.println("RESOURCES: "+infos.getResources());
//			System.out.println("RESOURCE_LOAD: "+infos.getResourceLoad());
//			//float status = Float.parseFloat(infos.getPercentage());
//			System.out.println("PERCENTAGE: "+infos.getPercentage());
//			Thread.sleep(1000);
//		}
	}
}
