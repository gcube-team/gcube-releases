package org.gcube.datacatalogue.grsf_manage_widget.shared;

import java.io.Serializable;
import java.util.Map;

/**
 * The bean to be managed by some people (e.g., GRSF).
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ManageProductBean implements Serializable{

	private static final long serialVersionUID = -4882608487467259326L;
	private String itemTitle; 
	//	private String semanticId; // Stock id or Fishery id
	//	private String shortTitle;
	private String catalogueIdentifier;
	private String knowledgeBaseIdentifier;
	//	private String type; // Fishery or Stock type (e.g., Assessment_Unit, Marine Resource and so on)
	private String grsfType; // fishery/stock
	//	private String source; // the current source
	private Map<String, String> extrasIfAvailable;
	private String description;

	// info that could change
	private GRSFStatus currentStatus;
	private GRSFStatus newStatus;
	private String annotation; // added by the administrator

	public ManageProductBean() {
		super();
	}

	/**
	 * @param itemTitle
	 * @param catalogueIdentifier
	 * @param knowledgeBaseIdentifier
	 * @param grsfType
	 * @param extrasIfAvailable
	 * @param description
	 * @param currentStatus
	 * @param newStatus
	 * @param annotation
	 */
	public ManageProductBean(String itemTitle, String catalogueIdentifier,
			String knowledgeBaseIdentifier, String grsfType,
			Map<String, String> extrasIfAvailable, String description,
			GRSFStatus currentStatus, GRSFStatus newStatus, String annotation) {
		super();
		this.itemTitle = itemTitle;
		this.catalogueIdentifier = catalogueIdentifier;
		this.knowledgeBaseIdentifier = knowledgeBaseIdentifier;
		this.grsfType = grsfType;
		this.extrasIfAvailable = extrasIfAvailable;
		this.description = description;
		this.currentStatus = currentStatus;
		this.newStatus = newStatus;
		this.annotation = annotation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCatalogueIdentifier() {
		return catalogueIdentifier;
	}

	public void setCatalogueIdentifier(String catalogueIdentifier) {
		this.catalogueIdentifier = catalogueIdentifier;
	}

	public String getKnowledgeBaseIdentifier() {
		return knowledgeBaseIdentifier;
	}

	public void setKnowledgeBaseIdentifier(String knowledgeBaseIdentifier) {
		this.knowledgeBaseIdentifier = knowledgeBaseIdentifier;
	}

	public GRSFStatus getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(GRSFStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

	public GRSFStatus getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(GRSFStatus newStatus) {
		this.newStatus = newStatus;
	}

	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public String getGrsfType() {
		return grsfType;
	}

	public void setGrsfType(String grsfType) {
		this.grsfType = grsfType;
	}

	public Map<String, String> getExtrasIfAvailable() {
		return extrasIfAvailable;
	}

	public void setExtrasIfAvailable(Map<String, String> extrasIfAvailable) {
		this.extrasIfAvailable = extrasIfAvailable;
	}

	@Override
	public String toString() {
		return "ManageProductBean [itemTitle=" + itemTitle
				+ ", catalogueIdentifier=" + catalogueIdentifier
				+ ", knowledgeBaseIdentifier=" + knowledgeBaseIdentifier
				+ ", grsfType=" + grsfType + ", extrasIfAvailable="
				+ extrasIfAvailable + ", description=" + description
				+ ", currentStatus=" + currentStatus + ", newStatus="
				+ newStatus + ", annotation=" + annotation + "]";
	}
}
