//package org.gcube.datatransformation.datatransformationservice.clients;
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
//import org.gcube.datatransformation.datatransformationservice.stubs.TransformDataResponse;
//import org.gcube.datatransformation.datatransformationservice.stubs.service.DataTransformationServiceAddressingLocator;
//
//public class DTSTest_TransformCollection {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) throws Exception {
//		System.out.println("DTSTest TransformCollection is running...");
//		String dtsEndpoint = args[0];
//		String scope = args[1];
//		String inputCollectionID = args[2];
//		String newCollectionName = args[3];
//		String targetMimeType = args[4];
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new AttributedURI(dtsEndpoint));
//		
//		DataTransformationServicePortType dts = new DataTransformationServiceAddressingLocator().getDataTransformationServicePortTypePort(endpoint);
//		dts = GCUBERemotePortTypeContext.getProxy(dts, GCUBEScope.getScope(scope));
//
//		TransformData request = new TransformData();
//		Input input = new Input();
//		input.setInputType("Collection");
//		input.setInputValue(inputCollectionID);
//		request.setInput(input);
//		Output output = new Output();
//		output.setOutputType("Collection");
//		Parameter outputParam = new Parameter();
//		outputParam.setName("CollectionName");
//		outputParam.setValue(newCollectionName);
//		org.gcube.datatransformation.datatransformationservice.stubs.Parameter [] outputParams = {outputParam};
//		output.setOutputparameters(outputParams);
//
//		request.setOutput(output);
//		ContentType targetContentType = new ContentType();
//		targetContentType.setMimeType(targetMimeType);
//		request.setTargetContentType(targetContentType);
//		request.setCreateReport(false);
//		TransformDataResponse response = dts.transformData(request);
//		System.out.println("Collection containing the transformed content created. The id of the new collection is: "+response.getOutput());
//		
//	}
//
//}
