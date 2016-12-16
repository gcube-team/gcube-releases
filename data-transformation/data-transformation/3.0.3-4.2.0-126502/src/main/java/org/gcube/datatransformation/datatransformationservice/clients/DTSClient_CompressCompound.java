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
//import org.gcube.datatransformation.datatransformationservice.stubs.TransformDataWithTransformationUnit;
//import org.gcube.datatransformation.datatransformationservice.stubs.service.DataTransformationServiceAddressingLocator;
//
//public class DTSClient_CompressCompound {
//	public static void main(String[] args) throws Exception {
//		String dtsEndpoint = args[0];
//		String scope = args[1];
//		String collectionID = args[2];
//		String transformationProgramID = args[3];
//		String transformationUnitID = args[4];
//		
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new AttributedURI(dtsEndpoint));
//		
//		DataTransformationServicePortType dts = new DataTransformationServiceAddressingLocator().getDataTransformationServicePortTypePort(endpoint);
//		dts = GCUBERemotePortTypeContext.getProxy(dts, GCUBEScope.getScope(scope));
//
//		/* INPUT */
//		TransformDataWithTransformationUnit request = new TransformDataWithTransformationUnit();
//		Input input = new Input();
//		input.setInputType("Collection");
//		input.setInputValue(collectionID);
//		Parameter inputParam = new Parameter();
//		inputParam.setName("handleParts");
//		inputParam.setValue("true");
//		Parameter[] inputParameters = {inputParam};
//		input.setInputparameters(inputParameters);
//		Input [] inputs = {input};
//		request.setInputs(inputs);
//		
//		/* OUTPUT */
//		Output output = new Output();
//		output.setOutputType("AlternativeRepresentation");
//		Parameter outputParam = new Parameter();
//		outputParam.setName("RepresentationRole");
//		outputParam.setValue("Zipped");
//		Parameter [] outputParams = {outputParam};
//		output.setOutputparameters(outputParams);
//		request.setOutput(output);
//		
//		/* TARGET CONTENT TYPE */
//		ContentType targetContentType = new ContentType();
//		targetContentType.setMimeType("application/zip");
//		request.setTargetContentType(targetContentType);
//
//		request.setTPID(transformationProgramID);
//		request.setTransformationUnitID(transformationUnitID);
//		
//		//request.setFilterSources(false);//Default is false
//		//request.setCreateReport(false);//Default is false
//		
//		System.out.println("The EPR of the result set is: "+dts.transformDataWithTransformationUnit(request).getOutput());
//		
//	}
//}
