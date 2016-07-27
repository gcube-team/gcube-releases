//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import org.apache.axis.message.addressing.AttributedURI;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
//import org.gcube.common.core.scope.GCUBEScope;
//import org.gcube.common.searchservice.searchlibrary.resultset.elements.ResultElementGeneric;
//import org.gcube.common.searchservice.searchlibrary.rsclient.elements.RSLocator;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLIterator;
//import org.gcube.common.searchservice.searchlibrary.rsreader.RSXMLReader;
//import org.gcube.datatransformation.datatransformationservice.stubs.ContentType;
//import org.gcube.datatransformation.datatransformationservice.stubs.DataTransformationServicePortType;
//import org.gcube.datatransformation.datatransformationservice.stubs.Input;
//import org.gcube.datatransformation.datatransformationservice.stubs.Output;
//import org.gcube.datatransformation.datatransformationservice.stubs.Parameter;
//import org.gcube.datatransformation.datatransformationservice.stubs.TransformData;
//import org.gcube.datatransformation.datatransformationservice.stubs.service.DataTransformationServiceAddressingLocator;
//
//public class DTSClient_CreateFTRowsetFromContent {
//
//	public static void main(String[] args) throws Exception {
//		String dtsEndpoint = args[0];
//		String scope = args[1];
//		String inputCollectionID = args[2];
//		
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new AttributedURI(dtsEndpoint));
//		
//		DataTransformationServicePortType dts = new DataTransformationServiceAddressingLocator().getDataTransformationServicePortTypePort(endpoint);
//		dts = GCUBERemotePortTypeContext.getProxy(dts, GCUBEScope.getScope(scope));
//
//		/* INPUT */
//		TransformData request = new TransformData();
//		Input input = new Input();
//		input.setInputType("Collection");
//		input.setInputValue(inputCollectionID);
//		request.setInput(input);
//		
//		/* OUTPUT */
//		Output output = new Output();
//		output.setOutputType("RSXML");
////		Parameter outputParam = new Parameter();
////		outputParam.setName("wrapWithMMEnvelope");
////		outputParam.setValue("true/false");//Default is false
////		Parameter [] outputParams = {outputParam};
////		output.setOutputparameters(outputParams);
//		request.setOutput(output);
//		
//		/* TARGET CONTENT TYPE */
//		ContentType targetContentType = new ContentType();
//		targetContentType.setMimeType("text/xml");
//		Parameter contentTypeParameter = new Parameter();
//		contentTypeParameter.setName("schemaURI");
//		contentTypeParameter.setValue("http://ftrowset.xsd");
//		Parameter [] contentTypeParameters = {contentTypeParameter};
//		targetContentType.setParameters(contentTypeParameters);
//		
//		request.setTargetContentType(targetContentType);
//
//		String rs = dts.transformData(request).getOutput();
//		persistRS(rs);
//		
//	}
//	public static void persistRS(String resultRS){
//		RSXMLIterator iterator;
//		
//		try{
//			iterator = RSXMLReader.getRSXMLReader(new RSLocator(resultRS)).getRSIterator();
//			int cnt=0;
//			while(iterator.hasNext()){
//				ResultElementGeneric element = (ResultElementGeneric)iterator.next(ResultElementGeneric.class);
//				if(element!=null){
//					System.out.println("Payload: "+element.getPayload());
//				}
//				cnt++;
//			}
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//}
