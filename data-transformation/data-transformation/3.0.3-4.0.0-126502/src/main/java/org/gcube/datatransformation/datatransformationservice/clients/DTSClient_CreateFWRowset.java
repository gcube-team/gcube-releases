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
//import org.gcube.datatransformation.datatransformationservice.stubs.TransformDataWithTransformationUnit;
//import org.gcube.datatransformation.datatransformationservice.stubs.service.DataTransformationServiceAddressingLocator;
//
//public class DTSClient_CreateFWRowset {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) throws Exception {
//		String dtsEndpoint = args[0];
//		String scope = args[1];
//		String inputMCollectionID = args[2];
//		String xslt = args[3];
//		
//		String transformationProgramID = "$FwRowset_Transformer";
//		String transformationUnitID = "0";
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
//		input.setInputType("MCollection");
//		input.setInputValue(inputMCollectionID);
//		Input [] inputs = {input};
//		request.setInputs(inputs);
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
//		contentTypeParameter.setValue("http://fwrowset.xsd");
//		Parameter [] contentTypeParameters = {contentTypeParameter};
//		targetContentType.setParameters(contentTypeParameters);
//		
//		request.setTargetContentType(targetContentType);
//
//		request.setTPID(transformationProgramID);
//		request.setTransformationUnitID(transformationUnitID);
//		
//		/* If transformation unit is generic (= parameters xslt and idxtype are not defined '-'),
//		 they should be explicitly set. */
//		
//		Parameter xsltParameter = new Parameter();
//		xsltParameter.setName("xslt");
//		xsltParameter.setValue(xslt);
////		Parameter indexTypeParameter = new Parameter();
////		xsltParameter.setName("indexType");
////		xsltParameter.setValue(...the indexType..);
//		
//		Parameter[] tProgramUnboundParameters = {xsltParameter};
//		request.setTProgramUnboundParameters(tProgramUnboundParameters);
//		
//		//request.setFilterSources(false);//Default is false
//		//request.setCreateReport(false);//Default is false
//		String rs = dts.transformDataWithTransformationUnit(request).getOutput();
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
//					System.out.println("ID: "+(element.getRecordAttributes(ResultElementGeneric.RECORD_ID_NAME)[0]).getAttrValue());
//					System.out.println("MetadataOID: "+(element.getRecordAttributes("MetadataOID")[0]).getAttrValue());
//					System.out.println("ContentOID: "+(element.getRecordAttributes("ContentOID")[0]).getAttrValue());
//					System.out.println("Payload: "+element.getPayload());
//				}
//				cnt++;
//			}
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//}
