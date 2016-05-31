///**
// * 
// */
//package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;
//
//import static org.gcube.resources.discovery.icclient.ICFactory.client;
//import gr.uoa.di.madgik.taskexecutionlogger.model.LogEntryLevel;
//import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import javax.xml.ws.EndpointReference;
//import javax.xml.ws.wsaddressing.W3CEndpointReference;
//
//import org.apache.log4j.Logger;
//import org.gcube.common.clients.fw.queries.StatefulQuery;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.datatransformation.DataTransformationClient;
//import org.gcube.datatransformation.client.library.beans.Types.ContentType;
//import org.gcube.datatransformation.client.library.beans.Types.Input;
//import org.gcube.datatransformation.client.library.beans.Types.Output;
//import org.gcube.datatransformation.client.library.beans.Types.Parameter;
//import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnit;
//import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnitResponse;
//import org.gcube.datatransformation.client.library.exceptions.DTSException;
//import org.gcube.datatransformation.client.library.proxies.DTSCLProxyI;
//import org.gcube.datatransformation.client.library.proxies.DataTransformationDSL;
//import org.gcube.forwardindexnode.client.library.beans.Types.CreateResource;
//import org.gcube.forwardindexnode.client.library.beans.Types.CreateResourceResponse;
//import org.gcube.forwardindexnode.client.library.beans.Types.KeyDescriptionArray;
//import org.gcube.forwardindexnode.client.library.beans.Types.KeyDescriptionType;
//import org.gcube.forwardindexnode.client.library.proxies.ForwardIndexNodeCLProxyI;
//import org.gcube.forwardindexnode.client.library.proxies.ForwardIndexNodeDSL;
//import org.gcube.forwardindexnode.client.library.proxies.ForwardIndexNodeFactoryCLProxyI;
//import org.gcube.forwardindexnode.client.library.proxies.ForwardIndexNodeFactoryDSL;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.*;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.TaskExecutionLogger;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ForwardIndexNodeWSResource;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceExpression;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceManager;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.ForwardIndexNodeDataType;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.TreeManagerCollectionDataType;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityParsingUtil;
//import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.TaskExecutionData;
//import org.gcube.resources.discovery.client.api.DiscoveryClient;
//import org.gcube.resources.discovery.client.queries.api.Query;
//import org.gcube.resources.discovery.client.queries.impl.QueryBox;
//import org.w3c.dom.Document;
//
///**
// * @author Panagiota Koltsida, NKUA
// *
// */
//public class ForwardIndexNodeUpdateTaskType extends CustomTaskType {
//
//	/** Logger */
//	private static Logger log = Logger.getLogger(ForwardIndexNodeUpdateTaskType.class);
//
//	private static final int MANAGER_POLLING_INTERVAL = 30000;	// 30 seconds
//
//	/**
//	 * Class constructor
//	 */
//	public ForwardIndexNodeUpdateTaskType() { 
//		super(TreeManagerCollectionDataType.class, 
//				ForwardIndexNodeDataType.class,
//				ForwardIndexNodeUpdateTaskType.class);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#getUIDescription()
//	 */
//	@Override
//	public String getUIDescription() {
//		try {
//			String sourceName = this.getInput().getAttributeValue(TreeManagerCollectionDataType.ATTR_COLNAME);
//			return "Recreates the forward index for the collection  '" + sourceName + "'.";
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
//		/* Get the list of indexed key names. If it has not been set, halt the execution */
//		List<String> indexedKeyNames = null;
//		String sIndexedKeyNames = null;
//		try {
//			sIndexedKeyNames = getAttributeValue(ForwardIndexNodeUpdateTaskType.this.getName() + ".ForwardIndexNodeUpdateTask.IndexedKeyNames");
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
//			sIndexedKeyTypes = getAttributeValue(ForwardIndexNodeUpdateTaskType.this.getName() + ".ForwardIndexNodeUpdateTask.IndexedKeyTypes");
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
//			xsltID = getAttributeValue(ForwardIndexNodeUpdateTaskType.this.getName() + ".ForwardIndexNodeUpdateTask.TransformationXSLTID");
//			log.debug("xsltID to be passed -> " + xsltID);
//			if (xsltID == null) 
//				throw new Exception();
//		} catch (Exception e) {
//			logger.error("A transformation xslt ID was not supplied!");
//			wfLogger.addEntry(LogEntryLevel.ERROR, "Required transformation XSLT ID was not supplied. Execution will be terminated");
//			return;
//		}
//
//		List<String> xsltIDs = null;
//		String sXsltIDs = null;
//		try {
//			sXsltIDs = getAttributeValue(ForwardIndexNodeUpdateTaskType.this.getName() + ".ForwardIndexNodeUpdateTask.XsltsIDs");
//			if (sXsltIDs == null) 
//				throw new Exception();
//			xsltIDs = EntityParsingUtil.attrValueToArrayOfValues(sXsltIDs);
//		} catch (Exception e) {
//			logger.error("A list of view IDs was not supplied!");
//			wfLogger.addEntry(LogEntryLevel.ERROR, "Required list of XSLTs were not supplied. Execution will be terminated");
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
//			wfLogger.addEntry(LogEntryLevel.ERROR, "A valid collection ID was not supplied. Execution will be terminated");
//			return;
//		}
//		wfLogger.addEntry(LogEntryLevel.INFORMATION, "The forward index resource for the collection with ID: " + inColID + " is going to be recreated");
//
//		String rowsetRSEPR;
//		try {
//			rowsetRSEPR = generateRowsets(execData, xsltID, inColID, xsltIDs);
//		} catch (Exception e2) {
//			logger.error("Error while transforming the collection to rowsets", e2);
//			wfLogger.addEntry(LogEntryLevel.ERROR, "Error while transforming the collection to rowsets");
//			return;
//		}
//
//		String indexID;
//		try {
//			indexID = recreateIndex(execData, indexedKeyNames, indexedKeyTypes, inColID, rowsetRSEPR);
//		} catch (Exception e1) {
//			logger.error("Error while updating the index resource.", e1);
//			wfLogger.addEntry(LogEntryLevel.ERROR, "Error while creating the index resource");
//			return;
//		}
//
//		/* Set the task output's attributes */
//		ForwardIndexNodeDataType output = (ForwardIndexNodeDataType) this.getOutput();
//		try {
//			//output.setClusterID(input.getC());
//			//output.setCollectionName(input.getCollectionName());
//			output.setIndexID(indexID);
//			log.debug("Output is set for that task type.....");
//		} catch (Exception e) {
//			logger.error("Error while trying to set the generated index attributes on the output object", e);
//			wfLogger.addEntry(LogEntryLevel.WARNING, "Error while trying to set the generated index attributes on the output object");
//			return;
//		}
//		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Forward index update task has been completed");
//	}
//
//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#getXMLTaskDefinitionDocument()
//	 */
//	@Override
//	public Document getXMLTaskDefinitionDocument() throws Exception {
//		return Util.parseXMLString(
//				"<ForwardIndexNodeUpdateTask>" +
//						"<IndexedKeyNames/>" +
//						"<IndexedKeyTypes/>" + 
//						"<TransformationXSLTID/>" +
//						"<XsltsIDs/>" +
//				"</ForwardIndexNodeUpdateTask>"
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
//	private String findIndexIdFromCollectionId(String collectionID) {
//		String query = "declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';" +
//				" declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider';" +
//				" for $result in collection(\"/db/Properties\")//Document" +
//				" where ($result/Data/child::*[local-name()='ServiceName']/string() eq 'ForwardIndexNode')" +
//				" and ($result/Data/child::*[local-name()='CollectionID']/string() = '" + collectionID + "')" +
//				" return $result/Data/child::*[local-name()='IndexID']/text()";
//
//		DiscoveryClient<String> client = client();
//		Query q = new QueryBox(query); 
//		List<String> result = client.submit(q);
//		return result.get(0);
//	}
//
//	private String recreateIndex(TaskExecutionData execData,  List<String> keyNames, List<String> keyTypes, String collectionID, String rowsetRSEPR) throws Exception {
//		String indexID = null;
//		TaskExecutionLogger logger = execData.getExecutionLogger();
//		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
//		ScopeProvider.instance.set(execData.getSession().getScope());
//		CreateResourceResponse output;
//
//		String previousIndexID = findIndexIdFromCollectionId(collectionID);
//
//		System.out.println("Creating a temporary Index");
//
//		ForwardIndexNodeFactoryCLProxyI proxyRandomf = null;
//		try {
//			proxyRandomf = ForwardIndexNodeFactoryDSL.getForwardIndexNodeFactoryProxyBuilder().build();
//			//Create a resource
//			CreateResource createResource = new CreateResource();
//			KeyDescriptionArray keyArray = new KeyDescriptionArray();
//			List<KeyDescriptionType> desc = new ArrayList<KeyDescriptionType>();
//			for(int i=0; i<keyNames.size(); i++)
//			{
//				desc.add(i, new KeyDescriptionType());
//				desc.get(i).KeyName = keyNames.get(i);
//				desc.get(i).IndexTypeID = keyTypes.get(i);
//			}
//			keyArray.array = desc;
//			createResource.KeyDescription = keyArray;
//			output = proxyRandomf.createResource(createResource);
//			System.out.println("node created with index id : " + output.IndexID);
//
//			indexID = output.IndexID;
//		} catch (Exception e) {
//			ForwardIndexNodeCLProxyI newIndex = getProxyByIndexID(scope, indexID);
//			newIndex.destroyNode();
//			throw new Exception("Error creating the temporary index", e);
//		} 	
//
//
//		ForwardIndexNodeCLProxyI newIndex = getProxyByIndexID(scope, indexID);
//
//		//feed the new index
//		System.out.println("Feeding the temporary Index");
//
//		try {
//			Boolean result = newIndex.feedLocator(rowsetRSEPR);
//		} catch (Exception e) {
//			newIndex.destroyNode();
//			throw new Exception("Error while feeding the new index from locator : " , e);
//		}
//
//		System.out.println("Sleeping for 1 minute");
//		Thread.sleep(1000 * 60);
//
//		// flush the index
//		System.out.println("Flushin the temporary Index");
//		try {
//			if (!newIndex.flush()){
//				newIndex.destroyNode();
//				throw new Exception("Error while flushing the new index");
//			}
//		} catch (Exception e) {
//			newIndex.destroyNode();
//			throw new Exception("Error while flushing the new index", e);
//		}
//
//		//destroy the old index
//		System.out.println("Destroying the old index");
//		ForwardIndexNodeCLProxyI oldIndex = getProxyByIndexID(scope, previousIndexID);
//		oldIndex.destroyNode();
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
//
//	public static ForwardIndexNodeCLProxyI getProxyByIndexID(String scope, String indexID){
//		ForwardIndexNodeCLProxyI proxyRandom;
//
//		ScopeProvider.instance.set(scope);
//
//		StatefulQuery q = ForwardIndexNodeDSL.getSource().withIndexID(indexID).build();
//		List<EndpointReference> refs = q.fire();
//
//
//		proxyRandom = ForwardIndexNodeDSL.getForwardIndexNodeProxyBuilder()
//				.at((W3CEndpointReference) refs.get(0)).build();
//
//		System.out.println(refs.get(0));
//
//		return proxyRandom; 
//	}
//}
