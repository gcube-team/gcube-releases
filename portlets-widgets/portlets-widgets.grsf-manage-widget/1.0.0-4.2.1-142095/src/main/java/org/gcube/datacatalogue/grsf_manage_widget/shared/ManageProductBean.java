package org.gcube.datacatalogue.grsf_manage_widget.shared;

import java.io.Serializable;

/**
 * The bean to be managed by some people (e.g., GRSF).
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ManageProductBean implements Serializable{

	private static final long serialVersionUID = -4882608487467259326L;
	private String productName;
	private String catalogueIdentifier;
	private String knowledgeBaseIdentifier;
	private GRSFStatus currentStatus;
	private GRSFStatus newStatus;
	private String annotation;
	private String productType; // fishery/stock

	public ManageProductBean() {
		super();
	}

	/**
	 * @param productName
	 * @param catalogueIdentifier
	 * @param knowledgeBaseIdentifier
	 * @param statusOld
	 * @param newStatus
	 * @param annotation
	 */
	public ManageProductBean(String productName, String catalogueIdentifier,
			String knowledgeBaseIdentifier, GRSFStatus currentStatus, GRSFStatus newStatus,
			String annotation, String productType) {
		super();
		this.productName = productName;
		this.catalogueIdentifier = catalogueIdentifier;
		this.knowledgeBaseIdentifier = knowledgeBaseIdentifier;
		this.currentStatus = currentStatus;
		this.newStatus = newStatus;
		this.annotation = annotation;
		this.productType = productType;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	@Override
	public String toString() {
		return "ManageProductBean [productName=" + productName
				+ ", catalogueIdentifier=" + catalogueIdentifier
				+ ", knowledgeBaseIdentifier=" + knowledgeBaseIdentifier
				+ ", currentStatus=" + currentStatus + ", newStatus="
				+ newStatus + ", annotation=" + annotation + ", productType="
				+ productType + "]";
	}

}
