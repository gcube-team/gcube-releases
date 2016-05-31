package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.task;

import gr.uoa.di.madgik.taskexecutionlogger.model.LogEntryLevel;
import gr.uoa.di.madgik.taskexecutionlogger.model.WorkflowLogEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.Util;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.logging.TaskExecutionLogger;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.GCUBECollectionDataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.SRUCollectionDataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.types.data.SRUDataType;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.EntityParsingUtil;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util.TaskExecutionData;
import org.gcube.search.sru.consumer.client.factory.SruConsumerFactoryClient;
import org.gcube.search.sru.consumer.common.resources.SruConsumerResource;
import org.w3c.dom.Document;

/**
 * Task for generating the SRU DataSource resource for a given collection
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class SRUGenerationTaskType extends CustomTaskType {

	private static Logger log = Logger.getLogger(SRUGenerationTaskType.class);

	/**
	 * Class Constructor
	 */
	public SRUGenerationTaskType() {
		super(SRUCollectionDataType.class, 
				SRUDataType.class, 
				SRUGenerationTaskType.class);
	}

	@Override
	public void executeTask(TaskExecutionData execData) {
		/* Get a reference to the active logger */
		log.debug("Starting the execution....");
		TaskExecutionLogger logger = execData.getExecutionLogger();
		WorkflowLogEntry wfLogger = this.getWorkflowLogger();
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "Starting the execution of the SRU generation task");
		
		/* Get the schema. If it has not been set, halt the execution */
		String schema = null;
		try {
			schema = getAttributeValue(SRUGenerationTaskType.this.getName() + ".SRUGenerationTask.Schema");
			if (schema == null) 
				throw new Exception();
			log.debug("Schema -> " + schema);
		} catch (Exception e) {
			logger.error("A schema was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "The required Schema was not supplied. Execution will be terminated");
			return;
		}
		/* Get the Open search resource ID. If it has not been set, halt the execution */
		String host = null;
		try {
			host = getAttributeValue(SRUGenerationTaskType.this.getName() + ".SRUGenerationTask.Host");
			if (host == null) 
				throw new Exception();
			log.debug("Host -> " + host);
		} catch (Exception e) {
			logger.error("A host was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "The required host was not supplied. Execution will be terminated");
			return;
		}
		
		/* Get the Open search resource ID. If it has not been set, halt the execution */
		String port = null;
		try {
			port = getAttributeValue(SRUGenerationTaskType.this.getName() + ".SRUGenerationTask.Port");
			if (port == null) 
				throw new Exception();
			log.debug("Port -> " + port);
		} catch (Exception e) {
			logger.error("Port was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "The required port was not supplied. Execution will be terminated");
			return;
		}
		
		/* Get the Open search resource ID. If it has not been set, halt the execution */
		String servlet = null;
		try {
			servlet = getAttributeValue(SRUGenerationTaskType.this.getName() + ".SRUGenerationTask.Servlet");
			if (servlet == null) 
				throw new Exception();
			log.debug("Servlet -> " + servlet);
		} catch (Exception e) {
			logger.error("Servlet was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "The required servlet was not supplied. Execution will be terminated");
			return;
		}
		
		/* Get the Open search resource ID. If it has not been set, halt the execution */
		String maxRecords = null;
		try {
			maxRecords = getAttributeValue(SRUGenerationTaskType.this.getName() + ".SRUGenerationTask.MaxRecords");
			if (maxRecords == null) 
				throw new Exception();
			log.debug("MaxRecords -> " + maxRecords);
		} catch (Exception e) {
			logger.error("Max records was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "The required max records was not supplied. Execution will be terminated");
			return;
		}
		
		/* Get the Open search resource ID. If it has not been set, halt the execution */
		String version = null;
		try {
			version = getAttributeValue(SRUGenerationTaskType.this.getName() + ".SRUGenerationTask.Version");
			if (version == null) 
				throw new Exception();
			log.debug("Version -> " + version);
		} catch (Exception e) {
			logger.error("Version was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "The required version was not supplied. Execution will be terminated");
			return;
		}
		
		/* Get the Open search resource ID. If it has not been set, halt the execution */
		String id = null;
		try {
			id = getAttributeValue(SRUGenerationTaskType.this.getName() + ".SRUGenerationTask.ID");
			if (id == null) 
				throw new Exception();
			log.debug("(Record) ID -> " + id);
		} catch (Exception e) {
			logger.error("(Record) ID was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "Record ID was not supplied. Execution will be terminated");
			return;
		}
		
		/* Get the Open search resource ID. If it has not been set, halt the execution */
		String defaultRecordSchema = null;
		try {
			defaultRecordSchema = getAttributeValue(SRUGenerationTaskType.this.getName() + ".SRUGenerationTask.DefaultRecordSchema");
			if (defaultRecordSchema == null) 
				throw new Exception();
			log.debug("Default Record Schema -> " + defaultRecordSchema);
		} catch (Exception e) {
			logger.error("Default Record Schema was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "Default Record Schema was not supplied. Execution will be terminated");
			return;
		}

		/* Retrieve the required values from the supplied input and output objects */
		SRUCollectionDataType input = (SRUCollectionDataType) getInput();
		String inColID = input.getCollectionID();
		if (inColID == null) {
			logger.error("An input collection ID value was not supplied!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "The required Collection ID was not supplied. Execution will be terminated");
			return;
		}
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "The SRU resource will be created for the collection with ID: " + inColID);
		log.debug("Collection ID -> " + inColID);

		/*Get the field parameters if they are available. */
		List<String> fields = null;
		String fieldsParameters = null;
		try {
			fieldsParameters = getAttributeValue(SRUGenerationTaskType.this.getName() + ".SRUGenerationTask.Fields");
			if (fieldsParameters == null)
				throw new Exception();
		} catch (Exception e1) {
			logger.error("At least one field is required to create the SRU resource!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "None field was provided for the SRU resource. Execution will be terminated");
			return;
		}
		fields = EntityParsingUtil.attrValueToArrayOfValues(fieldsParameters);

		/*Get the fixed parameters if they are available. Fixed parameters are optional */
		List<String> fieldsPaths = null;
		String fieldsPathsParameters = null;
		try {
		fieldsPathsParameters = getAttributeValue(SRUGenerationTaskType.this.getName() + ".SRUGenerationTask.FieldsPath");
		if (fieldsPathsParameters != null)  {
			fieldsPaths = EntityParsingUtil.attrValueToArrayOfValues(fieldsPathsParameters);
		}
		}catch (Exception e1) {
			logger.error("At least one field is required to create the SRU resource!");
			wfLogger.addEntry(LogEntryLevel.ERROR, "None field path was provided for the SRU resource. Execution will be terminated");
			return;
		}
		
		
		try {
			// fields and fieldsPath MUST have the same size and a 1-1 mapping
			int i=0;
			Map<String,String> fieldsMap = new HashMap<String,String>();
			for (String field : fields) {
				fieldsMap.put(field, fieldsPaths.get(i));
				i++;
			}
			//create the SRU resource here
			SruConsumerResource resource = new SruConsumerFactoryClient.ResourceBuilder()
			.schema(schema)
			.host(host)
			.port(new Integer(port))
			.servlet(servlet)
			.collectionID(inColID)
			.maxRecords(new Long(maxRecords))
			.version(version)
			.defaultRecordSchema("info:srw/schema/1/dc-v1.1")
			.presentables(fields)
			.searchables(fields)
			.mapping(fieldsMap)
			.recordIDField(id)
			.build();
			
			 SruConsumerFactoryClient factory = new SruConsumerFactoryClient.Builder()
				.scope(execData.getSession().getScope())
				.build();
			 String resourceID = factory.createResource(resource, scope);
			
		} catch (Exception e) {
			logger.error("Error while creating the SRU resource for the selected collection.", e);
			wfLogger.addEntry(LogEntryLevel.ERROR, "An error occurred during the creation of the SRU resource. Job failed to be completed");
			return;
		}
		wfLogger.addEntry(LogEntryLevel.INFORMATION, "SRU resource generation task has been completed");
	}

	@Override
	public Document getXMLTaskDefinitionDocument() throws Exception {
		return Util.parseXMLString(
				"<SRUGenerationTask>" +
				"<Schema/>" +
				"<Host/>" +
				"<Port/>" +
				"<Servlet/>" +
				"<MaxRecords/>" +
				"<Version/>" +
				"<DefaultRecordSchema/>" +
				"<ID/>" +
				"<Fields/>" +
				"<FieldsPath/>" +
				"</SRUGenerationTask>"
		);
	}

	@Override
	public String getUIDescription() {
		try {
			String sourceName = this.getInput().getAttributeValue(GCUBECollectionDataType.ATTR_COLLECTIONNAME);
			String sourceID = this.getInput().getAttributeValue(GCUBECollectionDataType.ATTR_COLID);
			return "Create SRU resource for the collection '" + sourceName + "' (ID: " + sourceID + ")" + ".";
		} catch (Exception e) {
			return null;
		}
	}

}
