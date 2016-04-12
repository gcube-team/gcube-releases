///**
// * 
// */
//package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;
//
//import gr.uoa.di.madgik.taskexecutionlogger.model.LogEntryLevel;
//import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import javax.xml.ws.wsaddressing.W3CEndpointReference;
//
//import org.apache.log4j.Logger;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.datatransformation.DataTransformationClient;
//import org.gcube.datatransformation.client.library.beans.Types.ContentType;
//import org.gcube.datatransformation.client.library.beans.Types.Output;
//import org.gcube.datatransformation.client.library.beans.Types.Input;
//import org.gcube.datatransformation.client.library.beans.Types.Parameter;
//import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnit;
//import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnitResponse;
//import org.gcube.datatransformation.client.library.exceptions.DTSException;
//import org.gcube.opensearch.client.library.beans.Types.CreateResourceResponse;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.TaskExecutionLogger;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ForwardIndexNodeWSResource;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceExpression;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceManager;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.ForwardIndexNodeDataType;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.TreeManagerCollectionDataType;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityParsingUtil;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.TaskExecutionData;
//import org.w3c.dom.Document;
//
///**
// * @author Panagiota Koltsida, NKUA
// *
// */
//public class ForwardIndexNodeGenerationTaskType extends CustomTaskType {
//
//	/** Logger */
//	private static Logger log = Logger.getLogger(ForwardIndexNodeGenerationTaskType.class);
//	
//	private static final int MANAGER_POLLING_INTERVAL = 30000;	// 30 seconds
//
//	/**
//	 * Class constructor
//	 */
//	public ForwardIndexNodeGenerationTaskType() { 
//		super(TreeManagerCollectionDataType.class, 
//				ForwardIndexNodeDataType.class,
//				ForwardIndexNodeGenerationTaskType.class);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#getUIDescription()
//	 */
//	@Override
//	public String getUIDescription() {
//		try {
//			String sourceName = this.getInput().getAttributeValue(TreeManagerCollectionDataType.ATTR_COLNAME);
//			return "Create forward index for the collection '" + sourceName + "'.";
//		} catch (Exception e) {
//			return null;
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#executeTask(org.gcube.portlets.admin.irbootstrapperportlet.servlet.util.TaskExecutionData)
//	 */
//	public void executeTask(TaskExecutionData execData) {
//		TaskExecutionLogger logger = execData.getExecutionLogger();
//		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
//		wfLogger.addEntry(LogEntryLevel.INFORMATION, "A new Forward Index resource is going to be created");
//		String appendManagerID = null;
//		try {
//			appendManagerID = getAttributeValue(ForwardIndexNodeGenerationTaskType.this.getName() + ".ForwardIndexNodeGenerationTask.IdOfIndexManagerToAppend");
//			if (appendManagerID == null || appendManagerID.length()==0) 
//				throw new Exception();
//			wfLogger.addEntry(LogEntryLevel.INFORMATION, "Index will be added to the existing resource with ID: " + appendManagerID);
//		} catch (Exception e) {
//			logger.info("An index manager ID was not supplied, creating new index...");
//			wfLogger.addEntry(LogEntryLevel.INFORMATION, "An index node ID was not supplied. Going to create a new index resource");
//			appendManagerID = null;
//		}
//		log.debug("append manager id ->> " + appendManagerID);
//
//
//		/* Get the list of indexed key names. If it has not been set, halt the execution */
//		List<String> indexedKeyNames = null;
//		String sIndexedKeyNames = null;
//		try {
//			sIndexedKeyNames = getAttributeValue(ForwardIndexNodeGenerationTaskType.this.getName() + ".ForwardIndexNodeGenerationTask.IndexedKeyNames");
//			if (sIndexedKeyNames == null) 
//				throw new Exception();
//			indexedKeyNames = EntityParsingUtil.attrValueToArrayOfValues(sIndexedKeyNames);
//		} catch (Exception e) {
//			logger.error("A list of key names was not supplied!");
//			wfLogger.addEntry(LogEntryLevel.ERROR, "Required key-names were not supplied. Execution will be terminated");
//			return;
//		}
//
//		/* Get the list of indexed key types. If it has not been set, halt the execution */
//		List<String> indexedKeyTypes = null;
//		String sIndexedKeyTypes = null;
//		try {
//			sIndexedKeyTypes = getAttributeValue(ForwardIndexNodeGenerationTaskType.this.getName() + ".ForwardIndexNodeGenerationTask.IndexedKeyTypes");
//			if (sIndexedKeyTypes == null) 
//				throw new Exception();
//			indexedKeyTypes = EntityParsingUtil.attrValueToArrayOfValues(sIndexedKeyTypes);
//		} catch (Exception e) {
//			logger.error("A list of key names was not supplied!");
//			wfLogger.addEntry(LogEntryLevel.ERROR, "Required key-types were not supplied. Execution will be terminated");
//			return;
//		}
//		
//		/* Get the transformation xslt ID. If it has not been set, halt the execution */
//		String xsltID = null;
//		try {
//			xsltID = getAttributeValue(ForwardIndexNodeGenerationTaskType.this.getName() + ".ForwardIndexNodeGenerationTask.TransformationXSLTID");
//			log.debug("xsltID to be passed -> " + xsltID);
//			if (xsltID == null) 
//				throw new Exception();
//		} catch (Exception e) {
//			logger.error("A transformation xslt ID was not supplied!");
//			wfLogger.addEntry(LogEntryLevel.ERROR, "A transformation XSLT was not supplied. Cannot create rowsets. Execution will be terminated");
//			return;
//		}
//
//		List<String> xsltIDs = null;
//		String sXsltIDs = null;
//		try {
//			sXsltIDs = getAttributeValue(ForwardIndexNodeGenerationTaskType.this.getName() + ".ForwardIndexNodeGenerationTask.XsltsIDs");
//			if (sXsltIDs == null) 
//				throw new Exception();
//			xsltIDs = EntityParsingUtil.attrValueToArrayOfValues(sXsltIDs);
//		} catch (Exception e) {
//			logger.error("A list of XSLT IDs was not supplied!");
//			wfLogger.addEntry(LogEntryLevel.ERROR, "Required XSLTs are missing. Cannot create rowsets. Execution will be terminated");
//			return;
//		}
//
//
//		/* Get the input collection ID. If it has not been set, halt the execution */
//		TreeManagerCollectionDataType input = (TreeManagerCollectionDataType) getInput();
//		String inColID = input.getCollectionID();
//		log.debug("INPUT collection ID -> " + inColID);
//		if (inColID == null) {
//			logger.error("An input collection ID value was not supplied!");
//			wfLogger.addEntry(LogEntryLevel.ERROR, "An input collection ID value was not supplied. Execution will be terminated");
//			return;
//		}
//		wfLogger.addEntry(LogEntryLevel.INFORMATION, "An index for the collection with ID: " + inColID + " will be created");
//		
//		String rowsetRSEPR;
//		try {
//			rowsetRSEPR = generateRowsets(execData, xsltID, inColID, xsltIDs);
//		} catch (Exception e1) {
//			wfLogger.addEntry(LogEntryLevel.ERROR, "Error while transforming the collection to rowsets");
//			logger.error("Error while transforming the collection to rowsets", e1);
//			return;
//		}
//
//		String indexID;
//		try {
//			indexID = createAndFeedIndex(execData, indexedKeyNames, indexedKeyTypes, appendManagerID, inColID, rowsetRSEPR);
//		} catch (Exception e1) {
//			wfLogger.addEntry(LogEntryLevel.ERROR, "Error while creating the index resource");
//			logger.error("Error while creating the index resource.", e1);
//			return;
//		}
//
//		/* Set the task output's attributes */
//		ForwardIndexNodeDataType output = (ForwardIndexNodeDataType) this.getOutput();
//		try {
//			output.setCollectionID(input.getCollectionID());
//			output.setCollectionName(input.getCollectionName());
//			output.setIndexID(indexID);
//			log.debug("Output is set for that task type.....");
//		} catch (Exception e) {
//			logger.error("Error while trying to set the generated index attributes on the output object", e);
//			wfLogger.addEntry(LogEntryLevel.WARNING, "Error while trying to set the generated index attributes on the output object");
//			return;
//		}
//		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Forward index generation task has been completed");
//	}
//
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#getXMLTaskDefinitionDocument()
//	 */
//	@Override
//	public Document getXMLTaskDefinitionDocument() throws Exception {
//		return Util.parseXMLString(
//				"<ForwardIndexNodeGenerationTask>" +
//						"<IndexedKeyNames/>" +
//						"<IndexedKeyTypes/>" +
//						"<XsltsIDs/>" +
//						"<TransformationXSLTID/>" +
//						"<LookupRunningInstancesToUse/>" +
//						"<IdOfIndexManagerToAppend/>" +
//				"</ForwardIndexNodeGenerationTask>"
//				);
//	}
//	
//	private String generateRowsets(TaskExecutionData execData, String xsltID, String inputColID, List<String> xsltIDs) throws Exception {
//		TaskExecutionLogger logger = execData.getExecutionLogger();
//		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
//		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Started generating rowsets by invoking DTS client library");
//	//	ScopeProvider.instance.set(execData.getSession().getScope());
//		
//		DataTransformationClient dtscl = new DataTransformationClient();
//		dtscl.setScope(execData.getSession().getScope());
//		dtscl.randomClient();
//
//		TransformDataWithTransformationUnit request = new TransformDataWithTransformationUnit();
//		request.tpID = "$FwRowset_Transformer";
//		request.transformationUnitID = "1";
//
//		/* INPUT */
//		Input input = new Input();
//		input.inputType = "TMDataSource";
//		input.inputValue = inputColID;
//		request.inputs = Arrays.asList(input);
//
//		/* OUTPUT */
//		request.output = new Output();
//		request.output.outputType = "RS2";
//
//		/* TARGET CONTENT TYPE */
//		request.targetContentType = new ContentType();
//		request.targetContentType.mimeType = "text/xml";
//		Parameter contentTypeParam = new Parameter("schemaURI", "http://fwrowset.xsd");
//		request.targetContentType.contentTypeParameters = Arrays.asList(contentTypeParam);
//
//		// construct the 'xslt' parameter
//		List<Parameter> parameterList = new ArrayList<Parameter>();
//		Parameter xsltParameter = new Parameter("finalfwdxslt", xsltID);
//		parameterList.add(xsltParameter);
//
//		//view IDs and XSLTS must have the same size 1-1
//		for (int i=0; i<xsltIDs.size(); i++) {
//			log.debug("XSLT -> " + xsltIDs.get(i));
//			Parameter viewxslt = new Parameter("xslt:" + (i+1), xsltIDs.get(i));
//			parameterList.add(viewxslt);
//		}
//
//		request.tProgramUnboundParameters = parameterList;
//
//		request.filterSources = false;
//		request.createReport = false;
//
//		try {
//			TransformDataWithTransformationUnitResponse response = dtscl.transformDataWithTransformationUnit(request);
//			return response.output;
//		} catch (DTSException e) {
//			logger.error("Transformation Failed", e);
//			wfLogger.addEntry(LogEntryLevel.ERROR, "Transformation failed. An error on DTS side occurred");
//			throw new Exception("Transformation Failed", e);
//		}
//	}
//
//	private String createAndFeedIndex(TaskExecutionData execData, List<String> keyNames, List<String> keyTypes, String indexNodeID, String collectionID, String rowsetRSEPR) throws Exception {
//		String indexID = null;
//		TaskExecutionLogger logger = execData.getExecutionLogger();
//		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
//		ScopeProvider.instance.set(execData.getSession().getScope());
//		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Started creating the index resource by invoking the Index client library");
//		CreateResourceResponse output;
//		KeyDescriptionArray keyArray = new KeyDescriptionArray();
//		try {
//			ForwardIndexNodeFactoryCLProxyI proxyRandomf = ForwardIndexNodeFactoryDSL.getForwardIndexNodeFactoryProxyBuilder().build();
//			//Create a resource
//			CreateResource createResource = new CreateResource();
//			createResource.IndexID = indexNodeID;
//			
//			String collectionIDs[] = new String[1];
//			collectionIDs[0] = collectionID;
//			createResource.CollectionID = collectionIDs;
//			
//			List<KeyDescriptionType> desc = new ArrayList<KeyDescriptionType>();
//			for(int i=0; i<keyNames.size(); i++)
//			{
//				desc.add(i, new KeyDescriptionType());
//				desc.get(i).KeyName = keyNames.get(i);
//				desc.get(i).IndexTypeID = keyTypes.get(i);
//			}
//			keyArray.array = desc;
//			createResource.KeyDescription = keyArray;
//			
//			output = proxyRandomf.createResource(createResource);	
//			indexID = output.IndexID;
//			wfLogger.addEntry(LogEntryLevel.INFORMATION, "Index resource has been created with ID: " + indexID);
//		} catch (ForwardIndexNodeException e1) {
//			logger.error("Failed to create a new index node", e1);
//			wfLogger.addEntry(LogEntryLevel.ERROR, "An error occurred at the index side. Resource creation failed.");
//			throw new Exception("Failed to create a new index node", e1);
//		}
//
//		try {
//			ForwardIndexNodeCLProxyI proxyRandom = ForwardIndexNodeDSL.getForwardIndexNodeProxyBuilder().at((W3CEndpointReference)output.EndpointReference).build();
//			if (indexNodeID != null) {
//				proxyRandom.addKeyDescription(keyArray);
//			}
//			proxyRandom.feedLocator(rowsetRSEPR);
//		} catch (ForwardIndexNodeException e1) {
//			logger.error("Failed to feed the index with indexID -> " + indexID, e1);
//			wfLogger.addEntry(LogEntryLevel.ERROR, "An error occurred while trying to feed the index resource");
//			throw new Exception("Failed to feed the index with indexID -> " + indexID, e1);
//		}
//		
//		/* Wait until the index management resource has been published to the IS */
//		ForwardIndexNodeWSResource ftResTemplate = new ForwardIndexNodeWSResource(getScope());
//		ftResTemplate.setAttributeValue(ForwardIndexNodeWSResource.ATTR_INDEXID, indexID);
//		ftResTemplate.setAttributeValue(ForwardIndexNodeWSResource.ATTR_COLLECTIONID, collectionID);
//		ResourceExpression<ForwardIndexNodeWSResource> ftExpr = null;
//		try {
//			ftExpr = ResourceManager.generateExpressionForResourceTempate(ftResTemplate, false);
//		} catch (Exception e) {
//			wfLogger.addEntry(LogEntryLevel.WARNING, "The resource has been created but could not located on IS");
//			throw e;
//		}
//
//		while (true) {
//			try {
//				Thread.sleep(MANAGER_POLLING_INTERVAL);
//			} catch (InterruptedException e) { /* In case of interruption, just return, because the task has been cancelled by the user */
//				return "";
//			}
//
//			/* Retrieve the generated full text manager resource from the IS */
//			List<ForwardIndexNodeWSResource> r = null;
//			try {
//				r = ResourceManager.retrieveResourcesFromIS(ftExpr);				
//			} catch (Exception e) {
//				throw new Exception("Failed to retrieve the WS resource from IS", e);
//			}
//
//			if (r!=null && r.size()>0)
//				break;
//		}
//		return indexID;
//	}
//}
