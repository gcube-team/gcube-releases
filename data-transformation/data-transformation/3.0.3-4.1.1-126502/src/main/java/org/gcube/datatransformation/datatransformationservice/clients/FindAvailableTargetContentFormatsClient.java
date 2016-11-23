//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//import org.apache.axis.message.addressing.AttributedURI;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.datatransformation.datatransformationservice.StubsToModelUtils;
//import org.gcube.datatransformation.datatransformationservice.stubs.ContentType;
//import org.gcube.datatransformation.datatransformationservice.stubs.DataTransformationServicePortType;
//import org.gcube.datatransformation.datatransformationservice.stubs.FindAvailableTargetContentTypes;
//import org.gcube.datatransformation.datatransformationservice.stubs.FindAvailableTargetContentTypesResponse;
//import org.gcube.datatransformation.datatransformationservice.stubs.service.DataTransformationServiceAddressingLocator;
//
//public class FindAvailableTargetContentFormatsClient {
//
//	public static void main(String[] args) throws Exception {
//		String dtsEndpoint = null;
//		String scope = null;
//		String sourceMimeType = null;
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
//			System.out.print("Mime type: ");
//			sourceMimeType = in.readLine();
//
//		}else{
//			dtsEndpoint = args[0];
//			scope = args[1];
//			sourceMimeType = args[2];
//		}
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new AttributedURI(dtsEndpoint));
//		
//		DataTransformationServicePortType dts = new DataTransformationServiceAddressingLocator().getDataTransformationServicePortTypePort(endpoint);
//		dts = GCUBERemotePortTypeContext.getProxy(dts, GCUBEScope.getScope(scope));
//		
//		ContentType sourceContentType = new ContentType();
//		sourceContentType.setMimeType(sourceMimeType);
//		FindAvailableTargetContentTypes request = new FindAvailableTargetContentTypes();
//		request.setSourceContentType(sourceContentType);
//		FindAvailableTargetContentTypesResponse response = dts.findAvailableTargetContentTypes(request);
//		if(response==null || response.getTargetContentTypes()==null || response.getTargetContentTypes().getContentTypesArray()==null ||
//				response.getTargetContentTypes().getContentTypesArray().length==0 ){
//			System.out.println("No available target content types found");
//		}else{
//			for(ContentType targetContentFormat: response.getTargetContentTypes().getContentTypesArray()){
//				//Works only with lib and service's jar ;-)
//				System.out.println("TargetContentTypeFound: "+StubsToModelUtils.contentTypeFromStub(targetContentFormat).toString());
//			}
//		}
//	}
//
//}
