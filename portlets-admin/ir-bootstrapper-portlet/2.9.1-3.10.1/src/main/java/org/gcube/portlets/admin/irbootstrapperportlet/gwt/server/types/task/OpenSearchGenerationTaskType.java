package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;

import gr.uoa.di.madgik.taskexecutionlogger.model.LogEntryLevel;
import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.TaskExecutionLogger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.GCUBECollectionDataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.OpenSearchDataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityParsingUtil;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.TaskExecutionData;
import org.gcube.rest.opensearch.client.exception.OpenSearchClientException;
import org.gcube.rest.opensearch.client.factory.OpenSearchFactoryClient;
import org.gcube.rest.opensearch.common.entities.Provider;
import org.w3c.dom.Document;

import com.google.common.collect.Lists;

/**
 * Task for generating the OpenSearch DataSource ws resource for a given collection
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class OpenSearchGenerationTaskType extends CustomTaskType {

	private static Logger log = Logger.getLogger(OpenSearchGenerationTaskType.class);

	/**
	 * Class Constructor
	 */
	public OpenSearchGenerationTaskType() {
		super(GCUBECollectionDataType.class, 
				OpenSearchDataType.class, 
				OpenSearchGenerationTaskType.class);
	}

	@Override
	public void executeTask(TaskExecutionData execData) {
		/* Get a reference to the active logger */
		log.debug("Starting the execution....");
		TaskExecutionLogger logger = execData.getExecutionLogger();
		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Starting the execution of the OpenSearch task");
		
		/* Get the Open search resource ID. If it has not been set, halt the execution */
		String openSearchResourceID = null;
		try {
			openSearchResourceID = getAttributeValue(OpenSearchGenerationTaskType.this.getName() + ".OpenSearchGenerationTask.OpenSearchResourceID");
			if (openSearchResourceID == null) 
				throw new Exception();
			log.debug("OpenSearchResourceID -> " + openSearchResourceID);
		} catch (Exception e) {
			logger.error("A open search resource ID was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "The required OpenSearch resource ID was not supplied. Execution will be terminated");
			return;
		}

		/* Retrieve the required values from the supplied input and output objects */
		GCUBECollectionDataType input = (GCUBECollectionDataType) getInput();
		String inColID = input.getCollectionID();
		if (inColID == null) {
			logger.error("An input collection ID value was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "The required Collection ID was not supplied. Execution will be terminated");
			return;
		}
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "The OpenSearch resource will be created for the collection with ID: " + inColID);
		log.debug("Collection ID -> " + inColID);

		/*Get the field parameters if they are available. */
		List<String> fieldParameters = null;
		String sfieldParameters = null;
		try {
			sfieldParameters = getAttributeValue(OpenSearchGenerationTaskType.this.getName() + ".OpenSearchGenerationTask.FieldParameters");
			if (sfieldParameters == null)
				throw new Exception();
		} catch (Exception e1) {
			logger.error("At least one field is required to create the open search resource!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "The required Field Parameters were not supplied. Execution will be terminated");
			return;
		}
		fieldParameters = EntityParsingUtil.attrValueToArrayOfValues(sfieldParameters);

		/*Get the fixed parameters if they are available. Fixed parameters are optional */
		List<String> fixedParameters = null;
		String sfixedParameters = null;
		try {
		sfixedParameters = getAttributeValue(OpenSearchGenerationTaskType.this.getName() + ".OpenSearchGenerationTask.FixedParameters");
		if (sfixedParameters != null)  {
			fixedParameters = EntityParsingUtil.attrValueToArrayOfValues(sfixedParameters);
		}
		}catch (Exception e1) {
			logger.error("At least one field is required to create the open search resource!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "The required Fixed Parameters were not supplied. Execution will be terminated");
			return;
		}
		
		
		try {
			createOpenSearchResource(execData, fieldParameters, fixedParameters, inColID, openSearchResourceID);
		} catch (Exception e) {
			logger.error("Error while creating the open search resource for collection.", e);
			wfLogger.addEntry(LogEntryLevel.ERROR, "An error occurred during the creation for the OpenSearch resource. Job failed to be completed");
			return;
		}
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "OpenSearch resource generation task has been completed");
	}

	@Override
	public Document getXMLTaskDefinitionDocument() throws Exception {
		return Util.parseXMLString(
				"<OpenSearchGenerationTask>" +
				"<FieldParameters/>" +
				"<OpenSearchResourceID/>" +
				"<FixedParameters/>" +
				"</OpenSearchGenerationTask>"
		);
	}

	@Override
	public String getUIDescription() {
		try {
			String sourceName = this.getInput().getAttributeValue(GCUBECollectionDataType.ATTR_COLLECTIONNAME);
			String sourceID = this.getInput().getAttributeValue(GCUBECollectionDataType.ATTR_COLID);
			return "Create openSearch resource for the collection '" + sourceName + "' (ID: " + sourceID + ")" + ".";
		} catch (Exception e) {
			return null;
		}
	}
	
	private void createOpenSearchResource(TaskExecutionData execData, List<String> fieldParameters, List<String> fixedParameters, String collectionID, String openSearchResourceID) throws Exception {
		TaskExecutionLogger logger = execData.getExecutionLogger();
		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
		ScopeProvider.instance.set(execData.getSession().getScope());
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Contacting the OpenSearch client for the resource creation");
		OpenSearchFactoryClient factory = new OpenSearchFactoryClient.Builder()
											.scope(execData.getSession().getScope())
											.build();
		
		try {
			List<String> fieldParamsArray = new ArrayList<String>();
			for (int i=0; i<fieldParameters.size(); i++) {
				fieldParamsArray.add(collectionID + ":" + fieldParameters.get(i));
				log.debug("Field parameter: " + (i+1) + " " + fieldParamsArray.get(i));
			}
			
            Provider p = new Provider();
            p.setCollectionID(collectionID);
            p.setOpenSearchResourceID(openSearchResourceID);
            p.setFixedParameters(fixedParameters);
           
            List<Provider> providers = Lists.newArrayList();
            providers.add(p);
           
            factory.createResource(fieldParamsArray, providers, execData.getSession().getScope());
            wfLogger.addEntry(LogEntryLevel.INFORMATION, "Resource has been created successfully");
		} catch (OpenSearchClientException e1) {
			logger.error("Failed to create the open search resource", e1);
			wfLogger.addEntry(LogEntryLevel.ERROR, "An error occurred during the creation for the OpenSearch resource. Job failed to be completed");
			throw new Exception("Failed to create the open search resource", e1);
		}

	}

}
