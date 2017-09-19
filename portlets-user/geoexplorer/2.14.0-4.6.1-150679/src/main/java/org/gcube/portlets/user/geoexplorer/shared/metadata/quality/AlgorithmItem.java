package org.gcube.portlets.user.geoexplorer.shared.metadata.quality;

import java.io.Serializable;

import org.gcube.portlets.user.geoexplorer.shared.metadata.citation.CitationItem;

public class AlgorithmItem implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -1742991831251671906L;

	/**
     * Information identifying the algorithm and version or date.
     *
     * @return Algorithm and version or date.
     */

    private CitationItem citation;

    /**
     * Information describing the algorithm used to generate the data.
     *
     * @return Algorithm used to generate the data.
     */
    private String description;



	public AlgorithmItem() {
	}

	public AlgorithmItem(CitationItem citation, String description) {
		super();
		this.citation = citation;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CitationItem getCitation() {
		return citation;
	}

	public void setCitation(CitationItem citation) {
		this.citation = citation;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AlgorithmItem [citation=");
		builder.append(citation);
		builder.append(", description=");
		builder.append(description);
		builder.append("]");
		return builder.toString();
	}

}
