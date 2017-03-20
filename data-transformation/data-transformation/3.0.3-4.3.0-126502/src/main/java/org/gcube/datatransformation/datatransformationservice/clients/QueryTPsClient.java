//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import org.apache.axis.message.addressing.AttributedURI;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.core.scope.GCUBEScopeManager;
//import org.gcube.common.core.scope.GCUBEScopeManagerImpl;
//import org.gcube.common.core.utils.logging.GCUBELog;
//import org.gcube.datatransformation.datatransformationservice.stubs.DataTransformationServicePortType;
//import org.gcube.datatransformation.datatransformationservice.stubs.service.DataTransformationServiceAddressingLocator;
//
//public class QueryTPsClient {
//
//	private static GCUBELog log = new GCUBELog(QueryTPsClient.class);
//	
//	public static void main(String[] args) throws Exception{
//		GCUBEScopeManager manager = new GCUBEScopeManagerImpl();
//		manager.setScope(GCUBEScope.getScope(args[1]));
//		
//		log.info("Stateless Client is running...");
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new AttributedURI(args[0]));
//		
//		DataTransformationServicePortType dts = new DataTransformationServiceAddressingLocator().getDataTransformationServicePortTypePort(endpoint);
//		manager.prepareCall(dts, "DataTransformation", "DataTransformationService");
//	
//		String query = "GET TRANSFORMATION " +
//			"WHERE SFORMAT.1.MIMETYPE=image " +
//			"AND SFORMAT.1.MIMESUBTYPE=png " +
//			"AND TFORMAT.1.MIMESUBTYPE=jpg ";
//		String result = dts.queryTransformationPrograms(query);
//		log.debug("DTS Respond:\n"+result);
//	}
//}
