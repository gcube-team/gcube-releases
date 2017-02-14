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
//
//public class DTSClient_MCollection2MCollection {
//
//	public static void main(String[] args) throws Exception {
//		String dtsEndpoint = args[0];
//		String scope = args[1];
//		String sourceMCollectionID = args[2];
//		String xslt = args[3];
//		String targetSchemaURI = args[4];
//		
//		/* MCollection Creation Parameters */
//		String newCollectionName = "TheNameOfNewCollection";
//		String newCollectionDescription = "A description of the new collection";
//		String isIndexable = "true";
//		String isUser = "true";
//		String relatedContentCollectionID = args[5];
//		String schemaName = args[6];
//		String schemaURI = targetSchemaURI;
//		String language = args[7];
//		String generatedByMCollectionID = sourceMCollectionID;
//		String generatedBySourceSchemaURI = args[8];
//		
//		String transformationProgramID = "$XSLT_Transformer";
//		String transformationUnitID = "0";
//		
//		EndpointReferenceType endpoint = new EndpointReferenceType();
//		endpoint.setAddress(new AttributedURI(dtsEndpoint));
//		
//		DataTransformationServicePortType dts = new DataTransformationServiceAddressingLocator().getDataTransformationServicePortTypePort(endpoint);
//		dts = GCUBERemotePortTypeContext.getProxy(dts, GCUBEScope.getScope(scope));
//		
//		TransformDataWithTransformationUnit request = new TransformDataWithTransformationUnit();
//		
//		/* INPUT */
//		Input input = new Input();
//		input.setInputType("MCollection");
//		input.setInputValue(sourceMCollectionID); // 61c5cca6-8497-49ce-8168-975db8403cc7
//		Input [] inputs = {input};
//		request.setInputs(inputs);
//		
//		/* TARGET CONTENT TYPE */
//		ContentType targetContentType = new ContentType();
//		targetContentType.setMimeType("text/xml");
//		Parameter contentTypeParameter = new Parameter();
//		contentTypeParameter.setName("schemaURI");
//		contentTypeParameter.setValue(targetSchemaURI); // http://193.43.36.238:8282/fi/figis/devcon/schema/dc/qualifieddc.xsd
//		Parameter [] contentTypeParameters = {contentTypeParameter};
//		targetContentType.setParameters(contentTypeParameters);
//		
//		request.setTargetContentType(targetContentType);
//
//		/* PROGRAM PARAMETERS */
//		
//		request.setTPID(transformationProgramID);
//		request.setTransformationUnitID(transformationUnitID); 
//		
//		Parameter xsltParameter = new Parameter();
//		xsltParameter.setName("xslt");
//		xsltParameter.setValue(xslt); // $BrokerXSLT_aquamaps_anylanguage_to_FARM_dc_anylanguage
//		
//		Parameter[] tProgramUnboundParameters = {xsltParameter};
//		request.setTProgramUnboundParameters(tProgramUnboundParameters);
//		
//		/* OUTPUT */
//		
//		Output output = new Output();
//		output.setOutputType("MCollection");
//		
//		org.gcube.datatransformation.datatransformationservice.stubs.Parameter [] outputParams = new org.gcube.datatransformation.datatransformationservice.stubs.Parameter [10];
//		/* NAME */
//		outputParams[0] = new org.gcube.datatransformation.datatransformationservice.stubs.Parameter();
////		outputParams[0].setName(MetadataViewDescription.CreationParameters.COLLECTIONNAME.toString());
////		String newCollectionName = "newCollectioName";
//		outputParams[0].setValue(newCollectionName); // newCollectioName
//		
//		// DESCRIPTION 
//		outputParams[1] = new org.gcube.datatransformation.datatransformationservice.stubs.Parameter();
////		outputParams[1].setName(MetadataViewDescription.CreationParameters.DESCRIPTION.toString());
//		outputParams[1].setValue("newCollectionDescription");
//		
//		// IS INDEXABLE 
//		outputParams[2] = new org.gcube.datatransformation.datatransformationservice.stubs.Parameter();
////		outputParams[2].setName(MetadataViewDescription.CreationParameters.INDEXABLE.toString());
//		outputParams[2].setValue(isIndexable);
//		
//		// IS USER 
//		outputParams[3] = new org.gcube.datatransformation.datatransformationservice.stubs.Parameter();
////		outputParams[3].setName(MetadataViewDescription.CreationParameters.USER.toString());
//		outputParams[3].setValue(isUser);
//		
//		// RELATED CONTENT COLLECTION ID
//		outputParams[4] = new org.gcube.datatransformation.datatransformationservice.stubs.Parameter();
////		outputParams[4].setName(MetadataViewDescription.CreationParameters.RELATEDBY_COLLID.toString());
//		outputParams[4].setValue(relatedContentCollectionID);
//		
//		// NEW MCOLLECTION SCHEMA NAME 
//		outputParams[5] = new org.gcube.datatransformation.datatransformationservice.stubs.Parameter();
////		outputParams[5].setName(MetadataViewDescription.CreationParameters.METADATANAME.toString());
//		outputParams[5].setValue(schemaName); //FARM_dc
//		
//		// NEW MCOLLECTION SCHEMA URI 
//		outputParams[6] = new org.gcube.datatransformation.datatransformationservice.stubs.Parameter();
////		outputParams[6].setName(MetadataViewDescription.CreationParameters.METADATAURI.toString());
//		outputParams[6].setValue(targetSchemaURI);
//		
//		// NEW MCOLLECTION LANGUAGE 
//		outputParams[7] = new org.gcube.datatransformation.datatransformationservice.stubs.Parameter();
////		outputParams[7].setName(MetadataViewDescription.CreationParameters.METADATALANG.toString());
//		outputParams[7].setValue(language);
//		
//		output.setOutputparameters(outputParams);
//		request.setOutput(output);
//		
//		/* PERFORMING THE TRANSFORMATION */
//		System.out.println("Metadata collection created with id: "+dts.transformDataWithTransformationUnit(request).getOutput());
//	}
//
//}
