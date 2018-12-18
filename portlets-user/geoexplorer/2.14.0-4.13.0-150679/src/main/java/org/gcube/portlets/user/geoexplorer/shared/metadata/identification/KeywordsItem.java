package org.gcube.portlets.user.geoexplorer.shared.metadata.identification;

import java.io.Serializable;
import java.util.Collection;

import org.gcube.portlets.user.geoexplorer.shared.metadata.citation.CitationItem;

public class KeywordsItem implements Serializable{
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 4245039925196904630L;

	/**
     * Commonly used word(s) or formalised word(s) or phrase(s) used to describe the subject.
     */
    private Collection<String> keywords;

    /**
     * Subject matter used to group similar keywords.
     */
    private String type;

    /**
     * Name of the formally registered thesaurus or a similar authoritative source of keywords.
     */
    private CitationItem thesaurusName;

    /**
     * Constructs an initially empty keywords.
     */
    public KeywordsItem() {
    }

	public Collection<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Collection<String> keywords) {
		this.keywords = keywords;
	}

	public String getType() {
		return type;
	}

	public KeywordsItem(Collection<String> keywords, String type, CitationItem thesaurusName) {
		super();
		this.keywords = keywords;
		this.type = type;
		this.thesaurusName = thesaurusName;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CitationItem getThesaurusName() {
		return thesaurusName;
	}

	public void setThesaurusName(CitationItem thesaurusName) {
		this.thesaurusName = thesaurusName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KeywordsItem [keywords=");
		builder.append(keywords);
		builder.append(", type=");
		builder.append(type);
		builder.append(", thesaurusName=");
		builder.append(thesaurusName);
		builder.append("]");
		return builder.toString();
	}
}
