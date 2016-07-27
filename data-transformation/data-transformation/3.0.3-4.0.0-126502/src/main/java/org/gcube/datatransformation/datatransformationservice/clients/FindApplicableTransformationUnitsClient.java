//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//import org.apache.axis.message.addressing.AttributedURI;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.datatransformation.datatransformationservice.stubs.*;
//import org.gcube.datatransformation.datatransformationservice.stubs.service.DataTransformationServiceAddressingLocator;
//
//public class FindApplicableTransformationUnitsClient {
//	
//	public static void main(String[] args) throws Exception {
//		String dtsEndpoint = null;
//		String scope = null;
//		
//		if (args.length == 0){
//			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//			System.out.print("Scope: ");
//			scope = in.readLine();
//			System.out.print("DTS host: ");
//			String dtshost = in.readLine();
//			System.out.print("DTS port: ");
//			String dtsport = in.readLine();			
//			dtsEndpoint ="http://"+dtshost+":"+dtsport+"/wsrf/services/gcube/datatransformation/DataTransformationService";
//
//		}else{
//			dtsEndpoint = args[0];
//			scope = args[1];
//		}
//
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new AttributedURI(dtsEndpoint));
//		
//		DataTransformationServicePortType dts = new DataTransformationServiceAddressingLocator().getDataTransformationServicePortTypePort(endpoint);
//		dts = GCUBERemotePortTypeContext.getProxy(dts, GCUBEScope.getScope(scope));
//
//		FindApplicableTransformationUnits params = new FindApplicableTransformationUnits();
//
////		ContentType sourceContentType = new ContentType();
////		sourceContentType.setMimeType("text/xml");
////		Parameter sparam1 = new Parameter();
////		sparam1.setName("schema");
////		sparam1.setValue("dc");
////		
////		Parameter sparam2 = new Parameter();
////		sparam2.setName("schemaURI");
////		sparam2.setValue("http://dublincore.org/documents/dces/");
////
////		Parameter sparam3 = new Parameter();
////		sparam3.setName("language");
////		sparam3.setValue("en");
////
////		Parameter [] sParameters = {sparam1, sparam2, sparam3};
////		sourceContentType.setParameters(sParameters);
////		
////		params.setSourceContentType(sourceContentType);
////		
////		ContentType targetContentType = new ContentType();
////		targetContentType.setMimeType("text/xml");
////		
////		Parameter tparam1 = new Parameter();
////		tparam1.setName("schema");
////		tparam1.setValue("es");
////		
////		Parameter tparam2 = new Parameter();
////		tparam2.setName("schemaURI");
////		tparam2.setValue("http://es.xsd");
////		Parameter [] tParameters = {tparam1, tparam2};
////		targetContentType.setParameters(tParameters);
////		
////		params.setTargetContentType(targetContentType);
//		
//		ContentType sourceContentType = new ContentType();
//		sourceContentType.setMimeType("image/tiff");
////		Parameter sparam1 = new Parameter();
////		sparam1.setName("width");
////		sparam1.setValue("1000");
////		
////		Parameter sparam2 = new Parameter();
////		sparam2.setName("height");
////		sparam2.setValue("1000");
//
////		Parameter [] sParameters = {sparam1, sparam2};
////		sourceContentType.setParameters(sParameters);
//		
//		params.setSourceContentType(sourceContentType);
//		
//		ContentType targetContentType = new ContentType();
//		targetContentType.setMimeType("image/png");
//		
//		Parameter tparam1 = new Parameter();
//		tparam1.setName("width");
//		tparam1.setValue("*");
//		
//		Parameter tparam2 = new Parameter();
//		tparam2.setName("height");
//		tparam2.setValue("*");
//		Parameter [] tParameters = {tparam1, tparam2};
//		targetContentType.setParameters(tParameters);
//		
//		params.setTargetContentType(targetContentType);
//		
//		FindApplicableTransformationUnitsResponse resp = dts.findApplicableTransformationUnits(params);
//		if(resp!=null){
//			for(TPAndTransformationUnit tpandtr: resp.getTPAndTransformationUnitIDs()){
//				System.out.println("TP: "+tpandtr.getTransformationProgramID()+", TR: "+tpandtr.getTransformationUnitID());
//			}
//		}else{
//			System.out.println("No applicable transformation unit found...");
//		}
//	}
//}
