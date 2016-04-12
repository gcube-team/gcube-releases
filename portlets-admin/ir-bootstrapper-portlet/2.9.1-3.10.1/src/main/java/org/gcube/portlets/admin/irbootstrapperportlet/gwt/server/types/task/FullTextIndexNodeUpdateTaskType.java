/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;

import gr.uoa.di.madgik.taskexecutionlogger.model.LogEntryLevel;
import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransformation.DataTransformationClient;
import org.gcube.datatransformation.client.library.beans.Types.ContentType;
import org.gcube.datatransformation.client.library.beans.Types.Input;
import org.gcube.datatransformation.client.library.beans.Types.Output;
import org.gcube.datatransformation.client.library.beans.Types.Parameter;
import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnit;
import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnitResponse;
import org.gcube.datatransformation.client.library.exceptions.DTSException;
import org.gcube.datatransformation.client.library.exceptions.EmptySourceException;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.*;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.TaskExecutionLogger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.FullTextIndexNodeDataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.TreeManagerCollectionDataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityParsingUtil;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.TaskExecutionData;
import org.gcube.rest.index.client.IndexClient;
import org.gcube.rest.index.client.exceptions.IndexException;
import org.w3c.dom.Document;

/**
 * @author Panagiota Koltsida, NKUA
 *
 */
public class FullTextIndexNodeUpdateTaskType extends CustomTaskType {

	/** Logger */
	private static Logger log = Logger.getLogger(FullTextIndexNodeUpdateTaskType.class);

	/**
	 * Class constructor
	 */
	public FullTextIndexNodeUpdateTaskType() { 
		super(TreeManagerCollectionDataType.class, 
				FullTextIndexNodeDataType.class,
				FullTextIndexNodeUpdateTaskType.class);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#getUIDescription()
	 */
	@Override
	public String getUIDescription() {
		try {
			String sourceName = this.getInput().getAttributeValue(TreeManagerCollectionDataType.ATTR_COLNAME);
			return "Recreates the full text index for the collection '" + sourceName + "'.";
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
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "The FullTextIndex resource is going to be recreated");
		log.debug("Starting the execution of the Fulltext index update task");

		/* Get the transformation xslt ID. If it has not been set, halt the execution */
		String xsltID = null;
		try {
			xsltID = getAttributeValue(FullTextIndexNodeUpdateTaskType.this.getName() + ".FullTextIndexNodeUpdateTask.TransformationXSLTID");
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
			indexTypeID = getAttributeValue(FullTextIndexNodeUpdateTaskType.this.getName() + ".FullTextIndexNodeUpdateTask.IndexTypeID");
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
			sXsltIDs = getAttributeValue(FullTextIndexNodeUpdateTaskType.this.getName() + ".FullTextIndexNodeUpdateTask.XsltsIDs");
			if (sXsltIDs == null) 
				throw new Exception();
			xsltIDs = EntityParsingUtil.attrValueToArrayOfValues(sXsltIDs);
		} catch (Exception e) {
			logger.error("A list of view IDs was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "Required XSLTs are missing. Cannot create rowsets. Execution will be terminated");
			return;
		}


		/* Get the input collection ID. If it has not been set, halt the execution */
		TreeManagerCollectionDataType input = (TreeManagerCollectionDataType) getInput();
		String inColID = input.getCollectionID();
		log.debug("INPUT collection ID -> " + inColID);
		if (inColID == null) {
			wfLogger.addEntry(LogEntryLevel.ERROR, "An input collection ID was not supplied. Execution will be terminated");
			logger.error("An input collection ID value was not supplied!");
			return;
		}
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "An index for the collection with ID: " + inColID + " will be updated");

		String rowsetRSEPR;
		try {
			rowsetRSEPR = generateRowsets(execData, xsltID, inColID, indexTypeID, xsltIDs);
		} catch (Exception e2) {
			wfLogger.addEntry(LogEntryLevel.ERROR, "Error while transforming the collection to rowsets");
			logger.error("Error while transforming the collection to rowsets", e2);
			return;
		}

		String indexID;
		try {
			indexID = updateIndex(execData, inColID, rowsetRSEPR);
		} catch (Exception e1) {
			wfLogger.addEntry(LogEntryLevel.ERROR, "Error while updating the index resource");
			logger.error("Error while updating the index resource.", e1);
			return;
		}

		/* Set the task output's attributes */
		FullTextIndexNodeDataType output = (FullTextIndexNodeDataType) this.getOutput();
		try {
			output.setCollectionID(input.getCollectionID());
			output.setCollectionName(input.getCollectionName());
			output.setIndexID(indexID);
			log.debug("Output is set for that task type.....");
		} catch (Exception e) {
			logger.error("Error while trying to set the generated index attributes on the output object", e);
			wfLogger.addEntry(LogEntryLevel.WARNING, "Error while trying to set the generated index attributes on the output object.");
			return;
		}
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Fulltext index update task has been completed");
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.admin.irbootstrapperportlet.servlet.types.task.CustomTaskType#getXMLTaskDefinitionDocument()
	 */
	@Override
	public Document getXMLTaskDefinitionDocument() throws Exception {
		return Util.parseXMLString(
				"<FullTextIndexNodeUpdateTask>" +
						"<IndexTypeID/>" + 
						"<TransformationXSLTID/>" +
						"<XsltsIDs/>" +
						"<LookupRunningInstancesToUse/>" +
				"</FullTextIndexNodeUpdateTask>"
				);
	}

	private String generateRowsets(TaskExecutionData execData, String xsltID, String inputColID, String indexTypeID, List<String> xsltIDs) throws Exception {
		TaskExecutionLogger logger = execData.getExecutionLogger();
		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Started generating rowsets by invoking DTS client library");
		ScopeProvider.instance.set(execData.getSession().getScope());
		
		DataTransformationClient dtscl = new DataTransformationClient();
		dtscl.setScope(execData.getSession().getScope());
		dtscl.randomClient();

		TransformDataWithTransformationUnit request = new TransformDataWithTransformationUnit();
		request.tpID = "$FtsRowset_Transformer";
		request.transformationUnitID = "6";

		/* INPUT */
		Input input = new Input();
		input.inputType = "TMDataSource";
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

	private String updateIndex(TaskExecutionData execData, String collectionID, final String rowsetRSEPR) throws Exception {
		TaskExecutionLogger logger = execData.getExecutionLogger();
		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Started updating the index resource by invoking the Index client library");
				
		final IndexClient iClient = new IndexClient.Builder().scope(execData.getSession().getScope())
				.collectionID(collectionID).build();
		Set<String> indicesOfCollection  = iClient.indicesOfCollection(collectionID);
		
		if (indicesOfCollection == null || indicesOfCollection.isEmpty()) {
			logger.error("Error while updating the index. Could not find the current index ID");
			wfLogger.addEntry(LogEntryLevel.ERROR, "Failed to update the index. Could not find the current index ID");
			throw new Exception("Error while updating the index. Could not find the current index ID");
		}
		//feed the new index
		log.debug("Starting feeding the temporary index");
		final String newIndexName = (collectionID+new Date()).toLowerCase().replaceAll(" ", "_");
		try {
			log.info("Calling feedLocatorUnit");
			
			
			
			
			 Runnable longRunningTask = new Runnable() {

					public void run() {
						log.info("Long running task started: "
								+ new Date().toString());
						try {
							iClient.feedLocatorSync(rowsetRSEPR, newIndexName, false, null);
						} catch (IndexException e) {
							e.printStackTrace();
						}
						log.info("Long running task ended: "
								+ new Date().toString());
						
					}
				};
				// Monitor every 10 seconds
				MonitoredThread mthread = new MonitoredThread(longRunningTask, 10000);
				mthread.execute();

		} catch (Exception e) {
			logger.error("Error while feeding the new index.", e);
			wfLogger.addEntry(LogEntryLevel.ERROR, "Failed to feed the new index");
			try {
				iClient.deleteIndex(newIndexName);
			} catch (Exception e2) {
				logger.error("Failed to delete the new index node, so as not to be empty", e2);
			}
			throw new Exception("Error while feeding the new index", e);
		}
		
		
		log.debug("Sleeping for a minute... before flushing the changes to the new index");
		Thread.sleep(10000);
		// flush the index
		log.debug("Flushing the temporary Index");
		try {
			if (iClient.flush() == false){
				iClient.deleteIndex(newIndexName);
				logger.error("Error while flushing the new index. Feeding failed");
				wfLogger.addEntry(LogEntryLevel.ERROR, "Error while flushing the new index. Feeding failed");
				throw new Exception("Error while flushing the new index. Feeding failed");
			}
		} catch (Exception e) {
			iClient.deleteIndex(newIndexName);
			throw new Exception("Error while flushing the new index", e);
		}
		log.debug("Activating the new index node");
		iClient.activateIndex(newIndexName);
		log.debug("Deleting the old index node");
		iClient.deleteIndex(indicesOfCollection.iterator().next());
	
		return newIndexName;
	}
	
	/**
	 * A class that monitors the execution of a thread
	 * 
	 * @author Panagiota Koltsida, NKUA
	 *
	 */
	private static class MonitoredThread {	
		final AtomicBoolean shouldMonitor = new AtomicBoolean(true);
		final Runnable task;
		final long monitorInterval;
		
		public MonitoredThread(Runnable task, long monitorInterval){
			this.task = task;
			this.monitorInterval = monitorInterval;
		}
		
		public void execute() throws InterruptedException{
			log.debug("Execution started.........");
			
			Runnable monitor = new Runnable() {
				public void run() {
					while (shouldMonitor.get()) {
						log.debug("Monitoring heartbeat on : " + new Date().toString());
						try {
							Thread.sleep(monitorInterval);
						} catch (InterruptedException e) {
							log.debug("problem while monitoring sleep", e);
						}
					}
				}
			};
			
			Thread monitorThread = new Thread(monitor);
			monitorThread.setName("thread monitor");
			monitorThread.setDaemon(true);
			
			Thread taskThread = new Thread(this.task);
			taskThread.setName("long running task");
			
			
			taskThread.start();
			monitorThread.start();
			
			taskThread.join();
			shouldMonitor.set(false);
			monitorThread.join();
			
			log.debug("Execution ended.........");
		}
	}
}
