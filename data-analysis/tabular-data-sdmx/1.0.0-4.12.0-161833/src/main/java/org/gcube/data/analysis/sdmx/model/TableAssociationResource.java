package org.gcube.data.analysis.sdmx.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class TableAssociationResource {
	private GenericResource tableAssociationResource;
	private Map<String, TableIdentificators> associationsTable;
	private Logger logger;
	private boolean associationsModified;

	private final String 	RECORD_ELEMENT = "record",
							DATAFLOW_ELEMENT = "dataflow", 
							TABLE_ELEMENT = "table",
							TABLULAR_RESOURCE_ELEMENT = "tablularresource",
							TIMEDIMENSION_ELEMENT = "timedimension",
							PRIMARYMEASURE_ELEMENT = "primaryMeasure",
							ASSOCIATIONS_ELEMENT = "associations";

	public TableAssociationResource(String name) {
		this.logger = LoggerFactory.getLogger(TableAssociationResource.class);
		this.tableAssociationResource = Resources.unmarshal(GenericResource.class,
				this.getClass().getResourceAsStream("/sdmxresourcemodel.xml"));
		this.tableAssociationResource.profile().name(name);
		this.associationsTable = new HashMap<>();
		this.associationsModified = false;
	}
	
	public TableAssociationResource(String name,Map<String, TableIdentificators> associationsTable) {
		this.logger = LoggerFactory.getLogger(TableAssociationResource.class);
		this.tableAssociationResource = Resources.unmarshal(GenericResource.class,
				this.getClass().getResourceAsStream("/sdmxresourcemodel.xml"));
		this.tableAssociationResource.profile().name(name);
		this.associationsTable = associationsTable;
		this.associationsModified = true;

	}

	public TableAssociationResource(GenericResource tableAssociation) {
		this.logger = LoggerFactory.getLogger(TableAssociationResource.class);
		this.tableAssociationResource = tableAssociation;
		this.associationsTable = getAssociatons(tableAssociation.profile().body());
		this.associationsModified = false;

	}



	public GenericResource getGenericResurce() {
		if (associationsModified)
			updateResource(this.associationsTable);

		return this.tableAssociationResource;
	}

	public void addAssociation(String flowId, String tabularResourceId, String tableId, String timeDimension, String primaryMeasure) {
		this.associationsTable.put(flowId,new TableIdentificators(tabularResourceId, tableId,timeDimension,primaryMeasure));
		this.associationsModified = true;
	}

	public void removeAssociation(String flowId) {
		this.associationsTable.remove(flowId);
		this.associationsModified = true;
	}

	public Map<String, TableIdentificators> getAssociationsTable() {
		return this.associationsTable;
	}

	private void updateResource(Map<String, TableIdentificators> associations) {
		this.logger.debug("Updating generic element");
		this.logger.debug("Original body "+this.tableAssociationResource.profile().bodyAsString());
		try {
			Element bodyElement = this.tableAssociationResource.profile().newBody();
			Document document = bodyElement.getOwnerDocument();
			Element associationsElement = document.createElement(ASSOCIATIONS_ELEMENT);
			bodyElement.appendChild(associationsElement);
			Set<String> dataFlowSet = associations.keySet();

			for (String dataFlow : dataFlowSet) {
				this.logger.debug("Generating new record");
				Element recordElement = document.createElement(RECORD_ELEMENT);
				associationsElement.appendChild(recordElement);
				TableIdentificators tableIdentificators = associations.get(dataFlow);
				this.logger.debug("Adding data flow " + dataFlow + " table " + tableIdentificators);
				appendStringElement(document, recordElement, DATAFLOW_ELEMENT, dataFlow);
				String tableId = tableIdentificators.getTableIDString();
				this.logger.debug("Adding table element "+tableId);
				appendStringElement(document, recordElement, TABLE_ELEMENT, tableId);
				String tabularResourceID = tableIdentificators.getTabularResourceIDString();
				this.logger.debug("Adding tabular resource id  element "+tabularResourceID);
				appendStringElement(document, recordElement, TABLULAR_RESOURCE_ELEMENT, tabularResourceID);
				String timeDimension = tableIdentificators.getTimeDimension();
				this.logger.debug("Adding tabular time dimension  element "+tabularResourceID);
				appendStringElement(document, recordElement, TIMEDIMENSION_ELEMENT, timeDimension);
				String primaryMeasure = tableIdentificators.getPrimaryMeasure();
				this.logger.debug("Adding tabular primary measure  element "+primaryMeasure);
				appendStringElement(document, recordElement, PRIMARYMEASURE_ELEMENT, primaryMeasure);
				this.logger.debug("Record generated, new body "+this.tableAssociationResource.profile().bodyAsString());
			}

			this.associationsModified = false;

		} catch (Exception e) {
			this.logger.error("Unable to update Generic Resource");
		}

	}
	
	
	private void appendStringElement (Document originalDocument, Element rootElement,String elementName, String elementValue)
	{
		Element newElement = originalDocument.createElement(elementName);
		Text newElementTextNode = originalDocument.createTextNode(elementValue);
		newElement.appendChild(newElementTextNode);
		rootElement.appendChild(newElement);
	}

	private HashMap<String, TableIdentificators> getAssociatons(Element body) {
		this.logger.debug("Parsing result " + body.getLocalName());
		NodeList nodes = body.getElementsByTagName(RECORD_ELEMENT);
		HashMap<String, TableIdentificators> response = new HashMap<>();

		for (int i = 0; i < nodes.getLength(); i++) {
			Element recordElement = (Element) nodes.item(i);
			this.logger.debug("Parsing record element " + (i + 1));

			NodeList dataFlowElements = recordElement.getElementsByTagName(DATAFLOW_ELEMENT);
			NodeList tableElements = recordElement.getElementsByTagName(TABLE_ELEMENT);
			NodeList tabularResourceElements = recordElement.getElementsByTagName(TABLULAR_RESOURCE_ELEMENT);
			NodeList timeDimensionElements = recordElement.getElementsByTagName(TIMEDIMENSION_ELEMENT);
			NodeList primaryMeasureElements = recordElement.getElementsByTagName(PRIMARYMEASURE_ELEMENT);

			if (dataFlowElements.getLength() == 0 || tableElements.getLength() == 0 || tabularResourceElements.getLength() == 0)
				this.logger.warn("Invalid element");
			else {
				String flowId = getSingleValue(dataFlowElements);
				this.logger.debug("Flow ID " + flowId);
				String tableId = getSingleValue(tableElements);
				this.logger.debug("Table ID " + tableId);
				String tablularResourceId = getSingleValue(tabularResourceElements);
				this.logger.debug("Tablular Resource ID " + tablularResourceId);
				String timeDimension = getSingleValue(timeDimensionElements);
				this.logger.debug("Time dimension " + timeDimension);
				String primaryMeasure = getSingleValue(primaryMeasureElements);
				this.logger.debug("Primary Measure " + primaryMeasure);
				response.put(flowId, new TableIdentificators(tablularResourceId, tableId,timeDimension,primaryMeasure));

			}

		}

		return response;

	}
	
	private String getSingleValue (NodeList source)
	{
		Node singleNodeNode = source.item(0);
		String nodeValue = singleNodeNode.getFirstChild().getNodeValue();
		this.logger.debug("Single Value " + nodeValue);
		return nodeValue;
	}
}
