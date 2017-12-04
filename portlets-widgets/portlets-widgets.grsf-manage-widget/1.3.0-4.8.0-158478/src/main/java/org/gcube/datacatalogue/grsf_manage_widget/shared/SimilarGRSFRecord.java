package org.gcube.datacatalogue.grsf_manage_widget.shared;

import java.io.Serializable;


/**
 * A similar grsf record.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class SimilarGRSFRecord implements Serializable{

	private static final long serialVersionUID = 6501670015333073045L;
	private String description; 
	private String semanticIdentifier;
	private String shortName;
	private String url;
	private boolean suggestedMerge;
	private boolean isExtra;

	public SimilarGRSFRecord() {
		super();
	}
	
	public SimilarGRSFRecord(boolean isExtra) {
		this.isExtra = isExtra;
	}

	/**
	 * @param description
	 * @param semanticIdentifier
	 * @param shortName
	 * @param url
	 */
	public SimilarGRSFRecord(String description, String semanticIdentifier,
			String shortName, String url) {
		super();
		this.description = description;
		this.semanticIdentifier = semanticIdentifier;
		this.shortName = shortName;
		this.url = url;
	}
	public String getIdentifier() {
		return description;
	}
	public void setIdentifier(String description) {
		this.description = description;
	}
	public String getSemanticIdentifier() {
		return semanticIdentifier;
	}
	public void setSemanticIdentifier(String semanticIdentifier) {
		this.semanticIdentifier = semanticIdentifier;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSuggestedMerge() {
		return suggestedMerge;
	}

	public void setSuggestedMerge(boolean suggestedMerge) {
		this.suggestedMerge = suggestedMerge;
	}

	public boolean isExtra() {
		return isExtra;
	}

	public void setExtra(boolean isExtra) {
		this.isExtra = isExtra;
	}

	@Override
	public String toString() {
		return "SimilarGRSFRecord [description=" + description
				+ ", semanticIdentifier=" + semanticIdentifier + ", shortName="
				+ shortName + ", url=" + url + ", suggestedMerge="
				+ suggestedMerge + ", isExtra=" + isExtra + "]";
	}

}
