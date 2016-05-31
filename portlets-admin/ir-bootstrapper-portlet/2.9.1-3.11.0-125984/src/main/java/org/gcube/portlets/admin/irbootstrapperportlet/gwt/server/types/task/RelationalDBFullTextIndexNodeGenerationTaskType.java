/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;

import gr.uoa.di.madgik.taskexecutionlogger.model.LogEntryLevel;
import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.datatransformation.DataTransformationClient;
import org.gcube.datatransformation.adaptors.discoverer.AdaptorsDiscoverer;
import org.gcube.datatransformation.client.library.beans.Types.ContentType;
import org.gcube.datatransformation.client.library.beans.Types.Input;
import org.gcube.datatransformation.client.library.beans.Types.Output;
import org.gcube.datatransformation.client.library.beans.Types.Parameter;
import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnit;
import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnitResponse;
import org.gcube.datatransformation.client.library.exceptions.DTSException;
import org.gcube.datatransformation.client.library.exceptions.EmptySourceException;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.TaskExecutionLogger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.FullTextIndexNodeWSResource;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceExpression;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement.ResourceManager;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.FullTextIndexNodeDataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.RelationalDBDataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityParsingUtil;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.TaskExecutionData;
import org.gcube.rest.index.client.IndexClient;
import org.gcube.rest.index.client.exceptions.IndexException;
import org.gcube.rest.index.client.factory.IndexFactoryClient;
import org.w3c.dom.Document;

/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class RelationalDBFullTextIndexNodeGenerationTaskType extends CustomTaskType {

	/** Logger */
	private static Logger log = Logger.getLogger(RelationalDBFullTextIndexNodeGenerationTaskType.class);

	private static final int MANAGER_POLLING_INTERVAL = 30000;	// 30 seconds

	/**
	 * Class constructor
	 */
	public RelationalDBFullTextIndexNodeGenerationTaskType() { 
		super(RelationalDBDataType.class, 
				FullTextIndexNodeDataType.class,
				RelationalDBFullTextIndexNodeGenerationTaskType.class);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#getUIDescription()
	 */
	@Override
	public String getUIDescription() {
		try {
			String sourceName = this.getInput().getAttributeValue(RelationalDBDataType.ATTR_DATASOURCE_NAME);
			return "Create full text index for the Relational DB Data Source '" + sourceName + "'.";
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#executeTask(org.gcube.portlets.admin.irbootstrapperportlet.servlet.util.TaskExecutionData)
	 */
	public void executeTask(TaskExecutionData execData) {
		TaskExecutionLogger logger = execData.getExecutionLogger();
		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "A new FullTextIndex resource is going to be created");
		log.debug("Starting the execution of the Fulltext index generation task");
		String appendManagerID = null;
		try {
			appendManagerID = getAttributeValue(RelationalDBFullTextIndexNodeGenerationTaskType.this.getName() + ".RelationalDBFullTextIndexNodeGenerationTask.IdOfIndexManagerToAppend");
			if (appendManagerID == null || appendManagerID.length()==0) 
				throw new Exception();
			wfLogger.addEntry(LogEntryLevel.INFORMATION, "Index will be added to the existing resource with ID: " + appendManagerID);
		} catch (Exception e) {
			logger.info("An index manager ID was not supplied, creating new index...");
			wfLogger.addEntry(LogEntryLevel.INFORMATION, "An index node ID was not supplied. Going to create a new index resource");
			appendManagerID = null;
		}
		log.debug("Index Node ID to append -> " + appendManagerID);


		/* Get the transformation xslt ID. If it has not been set, halt the execution */
		String xsltID = null;
		try {
			xsltID = getAttributeValue(RelationalDBFullTextIndexNodeGenerationTaskType.this.getName() + ".RelationalDBFullTextIndexNodeGenerationTask.TransformationXSLTID");
			log.debug("xsltID to be passed -> " + xsltID);
			if (xsltID == null) 
				throw new Exception();
		} catch (Exception e) {
			wfLogger.addEntry(LogEntryLevel.ERROR, "A transformation XSLT was not supplied. Cannot create rowsets. Execution will be terminated");
			logger.error("A transformation xslt ID was not supplied!");
			return;
		}

		/* Get the index type ID. If it has not been set, halt the execution */
		String indexTypeID = null;
		try {
			indexTypeID = getAttributeValue(RelationalDBFullTextIndexNodeGenerationTaskType.this.getName() + ".RelationalDBFullTextIndexNodeGenerationTask.IndexTypeID");
			log.debug("indexType ID to be passed -> " + indexTypeID);
			if (indexTypeID == null) 
				throw new Exception();
		} catch (Exception e) {
			wfLogger.addEntry(LogEntryLevel.ERROR, "An index type ID was not supplied. Cannot create rowsets. Execution will be terminated");
			logger.error("An indexTypeID was not supplied!");
			return;
		}

		List<String> xsltIDs = null;
		String sXsltIDs = null;
		try {
			sXsltIDs = getAttributeValue(RelationalDBFullTextIndexNodeGenerationTaskType.this.getName() + ".RelationalDBFullTextIndexNodeGenerationTask.XsltsIDs");
			if (sXsltIDs == null) 
				throw new Exception();
			xsltIDs = EntityParsingUtil.attrValueToArrayOfValues(sXsltIDs);
		} catch (Exception e) {
			logger.error("A list of view IDs was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "Required XSLTs are missing. Cannot create rowsets. Execution will be terminated");
			return;
		}


		/* Get the input URL generated from the appropriate client. If it has not been set, halt the execution */
		RelationalDBDataType input = (RelationalDBDataType) getInput();
		String inputURL = null;
		String inputSourceName = input.getDataSourceSourceName();
		String inputPropertiesName = input.getDataSourcePropertiesName();
		log.debug("Going to retrieve the URL for DB with source name -> " + inputSourceName + " and properties name -> " + inputPropertiesName);
		AdaptorsDiscoverer d = new AdaptorsDiscoverer(execData.getSession().getScope());
		try {
			List<String> DBurls = d.discoverAllDBEps(inputSourceName, inputPropertiesName);
			if (!DBurls.isEmpty())
				inputURL = DBurls.get(0);
		} catch (IOException e3) {
			wfLogger.addEntry(LogEntryLevel.ERROR, "Error while trying to retrieve the DB Data Source URL");
			logger.error("Error while trying to retrieve the DB Data Source URL", e3);
			return;
		}

		log.debug("INPUT URL -> " + inputURL);
		if (inputURL == null) {
			wfLogger.addEntry(LogEntryLevel.ERROR, "An input URL for the DB Data source was not supplied. Execution will be terminated");
			logger.error("An input URL for the DB Data source was not supplied!");
			return;
		}
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "An index for the DB Data source with URL: " + inputURL + " will be created");

		String rowsetRSEPR;
		try {
			rowsetRSEPR = generateRowsets(execData, xsltID, inputURL, indexTypeID, xsltIDs);
		} catch (Exception e2) {
			wfLogger.addEntry(LogEntryLevel.ERROR, "Error while transforming the collection to rowsets");
			logger.error("Error while transforming the collection to rowsets", e2);
			return;
		}

		String indexID;
		try {
			indexID = createAndFeedIndex(execData, appendManagerID, inputSourceName+"_"+inputPropertiesName, rowsetRSEPR);
		} catch (Exception e1) {
			wfLogger.addEntry(LogEntryLevel.ERROR, "Error while creating the index resource");
			logger.error("Error while creating the index resource.", e1);
			return;
		}

		/* Set the task output's attributes */
		FullTextIndexNodeDataType output = (FullTextIndexNodeDataType) this.getOutput();
		try {
			output.setCollectionID(inputURL);
			output.setCollectionName(input.getDataSourceName());
			output.setIndexID(indexID);
			log.debug("Output is set for that task type.....");
		} catch (Exception e) {
			logger.error("Error while trying to set the generated index attributes on the output object", e);
			wfLogger.addEntry(LogEntryLevel.WARNING, "Error while trying to set the generated index attributes on the output object");
			return;
		}
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Fulltext index generation task has been completed");
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#getXMLTaskDefinitionDocument()
	 */
	@Override
	public Document getXMLTaskDefinitionDocument() throws Exception {
		return Util.parseXMLString(
				"<RelationalDBFullTextIndexNodeGenerationTask>" +
						"<IndexTypeID/>" + 
						"<TransformationXSLTID/>" +
						"<XsltsIDs/>" +
						"<IdOfIndexManagerToAppend/>" +
				"</RelationalDBFullTextIndexNodeGenerationTask>"
				);
	}

	private String generateRowsets(TaskExecutionData execData, String xsltID, String inputColID, String indexTypeID, List<String> xsltIDs) throws Exception {
		TaskExecutionLogger logger = execData.getExecutionLogger();
		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Started generating rowsets by invoking DTS client library");

		DataTransformationClient dtscl = new DataTransformationClient();
		dtscl.setScope(execData.getSession().getScope());
		dtscl.randomClient();
		
		TransformDataWithTransformationUnit request = new TransformDataWithTransformationUnit();
		request.tpID = "$FtsRowset_Transformer";
		request.transformationUnitID = "6";

		/* INPUT */
		Input input = new Input();
		input.inputType = "HTTPDataSource";
		input.inputValue = inputColID;
		request.inputs = Arrays.asList(input);

		/* OUTPUT */
		request.output = new Output();
		request.output.outputType = "RS2";

		/* TARGET CONTENT TYPE */
		request.targetContentType = new ContentType();
		request.targetContentType.mimeType = "text/xml";
		Parameter contentTypeParam = new Parameter("schemaURI", "http://ftrowset.xsd");
		request.targetContentType.contentTypeParameters = Arrays.asList(contentTypeParam);

		// construct the 'xslt' parameter
		List<Parameter> parameterList = new ArrayList<Parameter>();
		Parameter xsltParameter = new Parameter("finalftsxslt", xsltID);
		Parameter indexTypeParameter = new Parameter("indexType", indexTypeID);
		parameterList.add(xsltParameter);
		parameterList.add(indexTypeParameter);

		//view IDs and XSLTS must have the same size 1-1
		for (int i=0; i<xsltIDs.size(); i++) {
			log.debug("XSLT -> " + xsltIDs.get(i));
			Parameter viewxslt = new Parameter("xslt:" + (i+1), xsltIDs.get(i));
			parameterList.add(viewxslt);
		}

		request.tProgramUnboundParameters = parameterList;
		request.filterSources = false;
		request.createReport = false;

		try {
			TransformDataWithTransformationUnitResponse response = dtscl.transformDataWithTransformationUnit(request);
			return response.output;
		} catch (DTSException e) {
			logger.error("Transformation Failed", e);
			wfLogger.addEntry(LogEntryLevel.ERROR, "Transformation failed. An error on DTS side occurred");
			throw new Exception("Transformation Failed", e);
		} catch (EmptySourceException e1) {
			logger.error("Empty data source, cannot transform it", e1);
			wfLogger.addEntry(LogEntryLevel.ERROR, "Empty data source, cannot transform it");
			throw new Exception("Empty data source, cannot transform it", e1);
		}
	}

	private String createAndFeedIndex(TaskExecutionData execData, String indexNodeID, String collectionID, String rowsetRSEPR) throws Exception {
		String indexID = null;
		TaskExecutionLogger logger = execData.getExecutionLogger();
		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Started creating the index resource by invoking the Index client library");

		IndexClient.Builder rClientBuilder = new IndexClient.Builder().scope(execData.getSession().getScope());

		String resourceID = null;
		if (indexNodeID == null) {
			try {
				IndexFactoryClient fclient = new IndexFactoryClient.Builder().scope(execData.getSession().getScope()).build();
				resourceID = fclient.createResource(null, execData.getSession().getScope());
				indexID = resourceID;
				wfLogger.addEntry(LogEntryLevel.INFORMATION, "Index resource has been created with ID: " + indexID);
				log.debug("Index resource has been created");
				rClientBuilder.resourceID(resourceID);
				log.debug("Initialized index client with resource ID -> " + resourceID);
			} catch (IndexException e2) {
				logger.error("Failed to create a new index node", e2);
				wfLogger.addEntry(LogEntryLevel.ERROR, "An error occurred at the index side. Resource creation failed.");
				throw new Exception("Failed to create a new index node", e2);
			}
		}
		else {
			rClientBuilder.indexID(indexNodeID);
			indexID = indexNodeID;

		}
		IndexClient rClient;
		try {
			rClient = rClientBuilder.build();
		} catch (IndexException e2) {
			logger.error("Failed to retrieve the index node", e2);
			wfLogger.addEntry(LogEntryLevel.ERROR, "An error occurred at the index side. Indexing will terminate.");
			throw new Exception("Failed to retrieve the index node", e2);
		}

		try {
			log.debug("Going to feed the locator...");
			log.debug("RSEPR -> " + rowsetRSEPR);

			rClient.feedLocator(rowsetRSEPR, (collectionID+new Date()).toLowerCase().replaceAll(" ", "_"), true, null);
			log.debug("Feed finished!!");
		} catch (IndexException e1) {
			logger.error("Failed to feed the index with indexID -> " + indexID, e1);
			wfLogger.addEntry(LogEntryLevel.ERROR, "An error occurred while trying to feed the index resource");
			throw new Exception("Failed to feed the index with indexID -> " + indexID, e1);
		} 
		log.debug("Going to check if the resource is available on IS");

		/* Wait until the index management resource has been published to the IS */
		FullTextIndexNodeWSResource ftResTemplate = new FullTextIndexNodeWSResource(getScope());
		ftResTemplate.setAttributeValue(FullTextIndexNodeWSResource.ATTR_INDEXID, indexID);
		ftResTemplate.setAttributeValue(FullTextIndexNodeWSResource.ATTR_COLLECTIONID, collectionID);
		ResourceExpression<FullTextIndexNodeWSResource> ftExpr = null;
		try {
			ftExpr = ResourceManager.generateExpressionForResourceTempate(ftResTemplate, false);
		} catch (Exception e) {
			wfLogger.addEntry(LogEntryLevel.WARNING, "The resource has been created but could not located on IS");
			throw e;
		}

		while (true) {
			try {
				Thread.sleep(MANAGER_POLLING_INTERVAL);
			} catch (InterruptedException e) { /* In case of interruption, just return, because the task has been cancelled by the user */
				return "";
			}

			/* Retrieve the generated full text manager resource from the IS */
			List<FullTextIndexNodeWSResource> r = null;
			try {
				r = ResourceManager.retrieveResourcesFromIS(ftExpr);				
			} catch (Exception e) {
				wfLogger.addEntry(LogEntryLevel.WARNING, "The resource has been created but could not be located on IS");
				throw new Exception("Failed to locate the WS resource on IS", e);
			}

			if (r!=null && r.size()>0) {
				log.debug("Resource found on IS as Generic resource. Task is going to be completed");
				break;
			}
		}
		return indexID;
	}
}
