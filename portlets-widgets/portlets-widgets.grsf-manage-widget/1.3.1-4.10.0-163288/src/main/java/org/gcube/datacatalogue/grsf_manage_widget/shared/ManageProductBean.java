package org.gcube.datacatalogue.grsf_manage_widget.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.datacatalogue.common.enums.Status;

/**
 * The bean to be managed by GRSF Editors and Reviewers.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ManageProductBean extends GenericRecord{

	private static final long serialVersionUID = -4882608487467259326L;
	private String catalogueIdentifier; // Catalogue id
	private String grsfType; // Fishery or Stock type (e.g., Assessment_Unit, Marine Resource and so on)
	private String shortNameUpdated; // the updated one, if any
	private boolean traceabilityFlag; //from false to true etc
	private Status currentStatus;
	private Status newStatus;
	private String annotation; // added by the administrator
	private List<SourceRecord> sources; // sources for this record
	private List<SimilarGRSFRecord> similarGrsfRecords;
	private List<ConnectedBean> suggestedByKnowledgeBaseConnections;
	private List<ConnectedBean> suggestdByAdministratorConnections = new ArrayList<ConnectedBean>(0);
	private List<ConnectedBean> currentConnections;
	private List<ConnectedBean> connections; // the one to used eventually
	private boolean mergesInvolved; // important: in this case an email must be sent to the editors/reviewers
	private String report; // the report that keeps track of the changes
	private Set<String> hashtags;

	public ManageProductBean() {
		super();
	}

	public ManageProductBean(
			String semanticIdentifier,
			String catalogueIdentifier, 
			String knowledgeBaseIdentifier,
			String grsfType, 
			String grsfDomain,
			String shortName, 
			String description,
			String title,
			boolean traceabilityFlag, 
			Status currentStatus,
			String recordUrl, 
			List<SourceRecord> sources,
			List<SimilarGRSFRecord> similarGrsfRecords, 
			List<ConnectedBean> currentConnections, 
			List<ConnectedBean> suggestedByKnowledgeBaseConnections
			) {
		super(knowledgeBaseIdentifier, description, shortName, title, recordUrl, semanticIdentifier, grsfDomain);
		this.catalogueIdentifier = catalogueIdentifier;
		this.grsfType = grsfType;
		this.shortNameUpdated = shortName;
		this.traceabilityFlag = traceabilityFlag;
		this.currentStatus = currentStatus;
		this.sources = sources;
		this.similarGrsfRecords = similarGrsfRecords;
		this.currentConnections = currentConnections;
		this.suggestedByKnowledgeBaseConnections = suggestedByKnowledgeBaseConnections;
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

	public String getGrsfType() {
		return grsfType;
	}

	public void setGrsfType(String grsfType) {
		this.grsfType = grsfType;
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

	public String getShortNameUpdated() {
		return shortNameUpdated;
	}

	public void setShortNameUpdated(String shortNameUpdated) {
		this.shortNameUpdated = shortNameUpdated;
	}

	public List<ConnectedBean> getSuggestedByKnowledgeBaseConnections() {
		return suggestedByKnowledgeBaseConnections;
	}

	public void setSuggestedByKnowledgeBaseConnections(
			List<ConnectedBean> suggestedByKnowledgeBaseConnections) {
		this.suggestedByKnowledgeBaseConnections = suggestedByKnowledgeBaseConnections;
	}

	public List<ConnectedBean> getSuggestdByAdministratorConnections() {
		return suggestdByAdministratorConnections;
	}

	public void setSuggestdByAdministratorConnections(
			List<ConnectedBean> suggestdByAdministratorConnections) {
		this.suggestdByAdministratorConnections = suggestdByAdministratorConnections;
	}

	public List<ConnectedBean> getCurrentConnections() {
		return currentConnections;
	}

	public void setCurrentConnections(List<ConnectedBean> currentConnections) {
		this.currentConnections = currentConnections;
	}

	public boolean isMergesInvolved() {
		return mergesInvolved;
	}

	public void setMergesInvolved(boolean mergesInvolved) {
		this.mergesInvolved = mergesInvolved;
	}

	public List<ConnectedBean> getConnections() {
		return connections;
	}

	public void setConnections(List<ConnectedBean> connections) {
		this.connections = connections;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public Set<String> getHashtags() {
		return hashtags;
	}

	public void setHashtags(Set<String> hashtags) {
		this.hashtags = hashtags;
	}

	@Override
	public String toString() {
		return "ManageProductBean [catalogueIdentifier=" + catalogueIdentifier
				+ ", grsfType=" + grsfType + ", shortNameUpdated="
				+ shortNameUpdated + ", traceabilityFlag=" + traceabilityFlag
				+ ", currentStatus=" + currentStatus + ", newStatus="
				+ newStatus + ", annotation=" + annotation + ", sources="
				+ sources + ", similarGrsfRecords=" + similarGrsfRecords
				+ ", suggestedByKnowledgeBaseConnections="
				+ suggestedByKnowledgeBaseConnections
				+ ", suggestdByAdministratorConnections="
				+ suggestdByAdministratorConnections + ", currentConnections="
				+ currentConnections + ", connections=" + connections
				+ ", mergesInvolved=" + mergesInvolved + ", report=" + report
				+ ", hashtags=" + hashtags + ", GenericRecord=" + super.toString()
				+ "]";
	}

}
