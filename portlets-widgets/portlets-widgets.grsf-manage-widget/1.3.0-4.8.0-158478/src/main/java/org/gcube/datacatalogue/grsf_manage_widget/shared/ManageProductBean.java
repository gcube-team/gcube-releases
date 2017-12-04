package org.gcube.datacatalogue.grsf_manage_widget.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.gcube.datacatalogue.common.enums.Status;

/**
 * The bean to be managed by some people (e.g., GRSF).
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ManageProductBean implements Serializable{

	private static final long serialVersionUID = -4882608487467259326L;
	private String semanticIdentifier; // Stock id or Fishery id
	private String catalogueIdentifier; // catalogue id
	private String knowledgeBaseIdentifier; // GRSF UUID
	private String grsfType; // Fishery or Stock type (e.g., Assessment_Unit, Marine Resource and so on)
	private String grsfDomain; // fishery/stock
	private String grsfName; // Fishery name or stock name
	private String shortName; // it is editable ...
	private String shortNameUpdated;
	private boolean traceabilityFlag; //from false to true etc
	private Status currentStatus;
	private Status newStatus;
	private String annotation; // added by the administrator
	private Map<String, String> extrasIfAvailable; // read from GRSFManageEntries resource
	private List<SourceRecord> sources; // sources for this record
	private List<SimilarGRSFRecord> similarGrsfRecords;

	public ManageProductBean() {
		super();
	}

	public ManageProductBean(String semanticIdentifier,
			String catalogueIdentifier, String knowledgeBaseIdentifier,
			String grsfType, String grsfDomain, String grsfName,
			String shortName, boolean traceabilityFlag, Status currentStatus,
			Status newStatus, String annotation,
			Map<String, String> extrasIfAvailable, List<SourceRecord> sources,
			List<SimilarGRSFRecord> similarGrsfRecords) {
		super();
		this.semanticIdentifier = semanticIdentifier;
		this.catalogueIdentifier = catalogueIdentifier;
		this.knowledgeBaseIdentifier = knowledgeBaseIdentifier;
		this.grsfType = grsfType;
		this.grsfDomain = grsfDomain;
		this.grsfName = grsfName;
		this.shortName = shortName;
		this.shortNameUpdated = shortName;
		this.traceabilityFlag = traceabilityFlag;
		this.currentStatus = currentStatus;
		this.newStatus = newStatus;
		this.annotation = annotation;
		this.extrasIfAvailable = extrasIfAvailable;
		this.sources = sources;
		this.similarGrsfRecords = similarGrsfRecords;
	}

	public String getSemanticIdentifier() {
		return semanticIdentifier;
	}

	public void setSemanticIdentifier(String semanticIdentifier) {
		this.semanticIdentifier = semanticIdentifier;
	}

	public List<SourceRecord> getSources() {
		return sources;
	}

	public void setSources(List<SourceRecord> sources) {
		this.sources = sources;
	}

	public List<SimilarGRSFRecord> getSimilarGrsfRecords() {
		return similarGrsfRecords;
	}

	public void setSimilarGrsfRecords(List<SimilarGRSFRecord> similarGrsfRecords) {
		this.similarGrsfRecords = similarGrsfRecords;
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

	public Status getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(Status currentStatus) {
		this.currentStatus = currentStatus;
	}

	public Status getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(Status newStatus) {
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

	public String getShortNameUpdated() {
		return shortNameUpdated;
	}

	public void setShortNameUpdated(String shortNameUpdated) {
		this.shortNameUpdated = shortNameUpdated;
	}

	@Override
	public String toString() {
		return "ManageProductBean [semanticIdentifier=" + semanticIdentifier
				+ ", catalogueIdentifier=" + catalogueIdentifier
				+ ", knowledgeBaseIdentifier=" + knowledgeBaseIdentifier
				+ ", grsfType=" + grsfType + ", grsfDomain=" + grsfDomain
				+ ", grsfName=" + grsfName + ", shortName=" + shortName
				+ ", shortNameUpdated=" + shortNameUpdated
				+ ", traceabilityFlag=" + traceabilityFlag + ", currentStatus="
				+ currentStatus + ", newStatus=" + newStatus + ", annotation="
				+ annotation + ", extrasIfAvailable=" + extrasIfAvailable
				+ ", sources=" + sources + ", similarGrsfRecords="
				+ similarGrsfRecords + "]";
	}

}
