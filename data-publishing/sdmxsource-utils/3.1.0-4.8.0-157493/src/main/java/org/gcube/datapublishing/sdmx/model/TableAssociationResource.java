package org.gcube.datapublishing.sdmx.model;

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
	private GenericResource tableAssociation;
	private Map<String, TableIdentificators> associationsTable;
	private Logger logger;
	private boolean associationsModified;

	private final String 	RECORD_ELEMENT = "record",
							DATAFLOW_ELEMENT = "dataflow", 
							TABLE_ELEMENT = "table",
							TABLULAR_RESOURCE_ELEMENT = "tablularresource",
							ASSOCIATIONS_ELEMENT = "associations";

	public TableAssociationResource() {
		this.logger = LoggerFactory.getLogger(TableAssociationResource.class);
		this.tableAssociation = Resources.unmarshal(GenericResource.class,
				this.getClass().getResourceAsStream("/sdmxresourcemodel.xml"));
		this.associationsTable = new HashMap<>();
		this.associationsModified = false;
	}
	
	public TableAssociationResource(Map<String, TableIdentificators> associationsTable) {
		this.logger = LoggerFactory.getLogger(TableAssociationResource.class);
		this.tableAssociation = Resources.unmarshal(GenericResource.class,
				this.getClass().getResourceAsStream("/sdmxresourcemodel.xml"));
		this.associationsTable = associationsTable;
		this.associationsModified = true;

	}

	public TableAssociationResource(GenericResource tableAssociation) {
		this.logger = LoggerFactory.getLogger(TableAssociationResource.class);
		this.tableAssociation = tableAssociation;
		this.associationsTable = getAssociatons(tableAssociation.profile().body());
		this.associationsModified = false;

	}



	public GenericResource getGenericResurce() {
		if (associationsModified)
			updateResource(this.associationsTable);

		return this.tableAssociation;
	}

	public void addAssociation(String flowId, String tabularResourceId, String tableId) {
		this.associationsTable.put(flowId,new TableIdentificators(tabularResourceId, tableId));
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
		this.logger.debug("Original body "+this.tableAssociation.profile().bodyAsString());
		try {
			Element bodyElement = this.tableAssociation.profile().newBody();
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
				Element dataFlowElement = document.createElement(DATAFLOW_ELEMENT);
				Text dataFlowTextNode = document.createTextNode(dataFlow);
				dataFlowElement.appendChild(dataFlowTextNode);
				recordElement.appendChild(dataFlowElement);
				Element tableIDElement = document.createElement(TABLE_ELEMENT);
				Text tableIDTextNode = document.createTextNode(tableIdentificators.getTableIDString());
				tableIDElement.appendChild(tableIDTextNode);
				recordElement.appendChild(tableIDElement);
				Element tablularResourceIDElement = document.createElement(TABLULAR_RESOURCE_ELEMENT);
				Text tablularResourceIDTextNode = document.createTextNode(tableIdentificators.getTabularResourceIDString());
				tablularResourceIDElement.appendChild(tablularResourceIDTextNode);
				recordElement.appendChild(tablularResourceIDElement);
				this.logger.debug("Record generated, new body "+this.tableAssociation.profile().bodyAsString());
			}

			this.associationsModified = false;

		} catch (Exception e) {
			this.logger.error("Unable to update Generic Resource");
		}

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

			if (dataFlowElements.getLength() == 0 || tableElements.getLength() == 0 || tabularResourceElements.getLength() == 0)
				this.logger.warn("Invalid element");
			else {
				Node flowNode = dataFlowElements.item(0);
				String flowId = flowNode.getFirstChild().getNodeValue();
				this.logger.debug("Flow ID " + flowId);
				Node tableNode = tableElements.item(0);
				String tableId = tableNode.getFirstChild().getNodeValue();
				this.logger.debug("Table ID " + tableId);
				Node tablularResourceNode = tabularResourceElements.item(0);
				String tablularResourceId = tablularResourceNode.getFirstChild().getNodeValue();
				this.logger.debug("Tablular Resource ID " + tablularResourceId);
				response.put(flowId, new TableIdentificators(tablularResourceId, tableId));

			}

		}

		return response;

	}
}
