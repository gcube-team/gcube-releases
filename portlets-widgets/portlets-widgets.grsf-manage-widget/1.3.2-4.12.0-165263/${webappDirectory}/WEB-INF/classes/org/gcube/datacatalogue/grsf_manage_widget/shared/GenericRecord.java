package org.gcube.datacatalogue.grsf_manage_widget.shared;

import java.io.Serializable;


/**
 * A generic record object.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GenericRecord implements Serializable{

	private static final long serialVersionUID = -7103588381218132232L;
	private String knowledgeBaseId;
	private String description; 
	private String shortName;
	private String title;
	private String url;
	private String semanticIdentifier;
	private String domain;

	public GenericRecord() {
		super();
	}

	public GenericRecord(String knowledgeBaseId, String description,
			String shortName, String title, String url,
			String semanticIdentifier, String domain) {
		super();
		this.knowledgeBaseId = knowledgeBaseId;
		this.description = description;
		this.shortName = shortName;
		this.title = title;
		this.url = url;
		this.semanticIdentifier = semanticIdentifier;
		this.domain = domain;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKnowledgeBaseId() {
		return knowledgeBaseId;
	}

	public void setKnowledgeBaseId(String knowledgeBaseId) {
		this.knowledgeBaseId = knowledgeBaseId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSemanticIdentifier() {
		return semanticIdentifier;
	}

	public void setSemanticIdentifier(String semanticIdentifier) {
		this.semanticIdentifier = semanticIdentifier;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "GenericRecord [knowledgeBaseId=" + knowledgeBaseId
				+ ", description=" + description + ", shortName=" + shortName
				+ ", title=" + title + ", url=" + url + ", semanticIdentifier="
				+ semanticIdentifier + ", domain=" + domain + "]";
	}

}
