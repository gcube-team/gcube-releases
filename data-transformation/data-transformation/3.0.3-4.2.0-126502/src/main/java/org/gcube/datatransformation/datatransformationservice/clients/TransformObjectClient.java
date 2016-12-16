//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//
//import org.apache.axis.message.addressing.AttributedURI;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.datatransformation.datatransformationservice.stubs.ContentType;
//import org.gcube.datatransformation.datatransformationservice.stubs.DataTransformationServicePortType;
//import org.gcube.datatransformation.datatransformationservice.stubs.Input;
//import org.gcube.datatransformation.datatransformationservice.stubs.Output;
//import org.gcube.datatransformation.datatransformationservice.stubs.Parameter;
//import org.gcube.datatransformation.datatransformationservice.stubs.TransformData;
//import org.gcube.datatransformation.datatransformationservice.stubs.service.DataTransformationServiceAddressingLocator;
//
//public class TransformObjectClient {
//	public static void main(String[] args) throws Exception {
//		System.out.println("Stateless Client is running...");
//		
//		String dtsEndpoint = null;
//		String scope = null;
//		String objectUrl = null;
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
//			System.out.print("Object Url: ");
//			objectUrl = in.readLine();			
//
//		}else{
//			dtsEndpoint = args[0];
//			scope = args[1];
//			objectUrl = args[2];
//		}
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new AttributedURI(dtsEndpoint));
//		DataTransformationServicePortType dts = new DataTransformationServiceAddressingLocator().getDataTransformationServicePortTypePort(endpoint);
//		dts = GCUBERemotePortTypeContext.getProxy(dts, GCUBEScope.getScope(scope));
//		
//		/* Program */
//		TransformData request = new TransformData();
//		
//		/* Input */
//		Input input = new Input();
//		input.setInputType("CObject");
//		input.setInputValue(objectUrl);
//
//		request.setInput(input);
//		
//		/* Target Content Type */
//		ContentType targetFormat = new ContentType();
//		targetFormat.setMimeType("image/png");
//		Parameter fparam1 = new Parameter();
//		fparam1.setName("width");
//		fparam1.setValue("200");
//		Parameter fparam2 = new Parameter();
//		fparam2.setName("height");
//		fparam2.setValue("200");
//		Parameter[] fparams = {fparam1, fparam2};
//		targetFormat.setParameters(fparams);
//		request.setTargetContentType(targetFormat);
//		
//		/* Output */
//		Output output = new Output();
//		output.setOutputType("AlternativeRepresentation");
//		request.setOutput(output);
//		
////		Output output = new Output();
////		output.setOutputType("Local");
////		output.setOutputValue("/tmp");
////		request.setOutput(output);
//		
//		dts.transformData(request);
//	}
//}
