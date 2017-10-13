package org.gcube.datacatalogue.grsf_manage_widget.shared;

import java.io.Serializable;
import java.util.Map;

/**
 * The bean to be managed by some people (e.g., GRSF).
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ManageProductBean implements Serializable{

	private static final long serialVersionUID = -4882608487467259326L;
	private String semanticId; // Stock id or Fishery id
	private String catalogueIdentifier; // catalogue id
	private String knowledgeBaseIdentifier; // GRSF UUID
	private String grsfType; // Fishery or Stock type (e.g., Assessment_Unit, Marine Resource and so on)
	private String grsfDomain; // fishery/stock
	private String sources; // sources for this record
	private String grsfName; // Fishery name or stock name
	private boolean traceabilityFlag; //from false to true etc
	private GRSFStatus currentStatus;
	private GRSFStatus newStatus;
	private String annotation; // added by the administrator
	private String shortName;
	private Map<String, String> extrasIfAvailable; // read from GRSFManageEntries resource

	public ManageProductBean() {
		super();
	}

	/**
	 * @param semanticId
	 * @param catalogueIdentifier
	 * @param knowledgeBaseIdentifier
	 * @param grsfType
	 * @param grsfDomain
	 * @param sources
	 * @param grsfName
	 * @param traceabilityFlag
	 * @param currentStatus
	 * @param newStatus
	 * @param annotation
	 * @param shortName
	 * @param extrasIfAvailable
	 */
	public ManageProductBean(String semanticId, String catalogueIdentifier,
			String knowledgeBaseIdentifier, String grsfType, String grsfDomain,
			String sources, String grsfName, boolean traceabilityFlag,
			GRSFStatus currentStatus, GRSFStatus newStatus, String annotation,
			String shortName, Map<String, String> extrasIfAvailable) {
		super();
		this.semanticId = semanticId;
		this.catalogueIdentifier = catalogueIdentifier;
		this.knowledgeBaseIdentifier = knowledgeBaseIdentifier;
		this.grsfType = grsfType;
		this.grsfDomain = grsfDomain;
		this.sources = sources;
		this.grsfName = grsfName;
		this.traceabilityFlag = traceabilityFlag;
		this.currentStatus = currentStatus;
		this.newStatus = newStatus;
		this.annotation = annotation;
		this.shortName = shortName;
		this.extrasIfAvailable = extrasIfAvailable;
	}

	public String getSemanticId() {
		return semanticId;
	}

	public void setSemanticId(String semanticId) {
		this.semanticId = semanticId;
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

	public String getGrsfType() {
		return grsfType;
	}

	public void setGrsfType(String grsfType) {
		this.grsfType = grsfType;
	}

	public String getGrsfDomain() {
		return grsfDomain;
	}

	public void setGrsfDomain(String grsfDomain) {
		this.grsfDomain = grsfDomain;
	}

	public String getSources() {
		return sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public String getGrsfName() {
		return grsfName;
	}

	public void setGrsfName(String grsfName) {
		this.grsfName = grsfName;
	}

	public Map<String, String> getExtrasIfAvailable() {
		return extrasIfAvailable;
	}

	public void setExtrasIfAvailable(Map<String, String> extrasIfAvailable) {
		this.extrasIfAvailable = extrasIfAvailable;
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
	
	public boolean isTraceabilityFlag() {
		return traceabilityFlag;
	}

	public void setTraceabilityFlag(boolean traceabilityFlag) {
		this.traceabilityFlag = traceabilityFlag;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Override
	public String toString() {
		return "ManageProductBean [semanticId=" + semanticId
				+ ", catalogueIdentifier=" + catalogueIdentifier
				+ ", knowledgeBaseIdentifier=" + knowledgeBaseIdentifier
				+ ", grsfType=" + grsfType + ", grsfDomain=" + grsfDomain
				+ ", sources=" + sources + ", grsfName=" + grsfName
				+ ", traceabilityFlag=" + traceabilityFlag + ", currentStatus="
				+ currentStatus + ", newStatus=" + newStatus + ", annotation="
				+ annotation + ", shortName=" + shortName
				+ ", extrasIfAvailable=" + extrasIfAvailable + "]";
	}

}
