package org.gcube.portlets.user.geoexplorer.shared.metadata.identification;

import java.io.Serializable;
import java.util.Collection;

import org.gcube.portlets.user.geoexplorer.shared.metadata.ResponsiblePartyItem;
import org.gcube.portlets.user.geoexplorer.shared.metadata.citation.CitationItem;

public class IdentificationItem implements Serializable {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -7962126129532829771L;

	/**
     * Citation data for the resource(s).
     */
    private CitationItem citation;

    /**
     * Brief narrative summary of the content of the resource(s).
     */
    private String abstracts;

    /**
     * Summary of the intentions with which the resource(s) was developed.
     */
    private String purpose;

    /**
     * Recognition of those who contributed to the resource(s).
     */
    private Collection<String> credits;

    /**
     * Status of the resource(s).
     */
    private Collection<String> status;

    /**
     * Identification of, and means of communication with, person(s) and organizations(s)
     * associated with the resource(s).
     */
    private Collection<ResponsiblePartyItem> pointOfContacts;
//
//    /**
//     * Provides information about the frequency of resource updates, and the scope of those updates.
//     */
//    private Collection<MaintenanceInformation> resourceMaintenances;

//    /**
//     * Provides a graphic that illustrates the resource(s) (should include a legend for the graphic).
//     */
//    private Collection<BrowseGraphic> graphicOverviews;

//    /**
//     * Provides a description of the format of the resource(s).
//     */
//    private Collection<Format> resourceFormats;

    /**
     * Provides category keywords, their type, and reference source.
     */
    private Collection<KeywordsItem> descriptiveKeywords;

//    /**
//     * Provides basic information about specific application(s) for which the resource(s)
//     * has/have been or is being used by different users.
//     */
//    private Collection<Usage> resourceSpecificUsages;

//    /**
//     * Provides information about constraints which apply to the resource(s).
//     */
//    private Collection<Constraints> resourceConstraints;

//    /**
//     * Provides aggregate dataset information.
//     */
//    private Collection<AggregateInformation> aggregationInfo;

    /**
     * Constructs an initially empty identification.
     */
    public IdentificationItem() {
    }


	public String getAbstracts() {
		return abstracts;
	}

	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Collection<String> getCredits() {
		return credits;
	}

	public void setCredits(Collection<String> credits) {
		this.credits = credits;
	}

	public Collection<String> getStatus() {
		return status;
	}

	public void setStatus(Collection<String> status) {
		this.status = status;
	}

	public Collection<ResponsiblePartyItem> getPointOfContacts() {
		return pointOfContacts;
	}

	public void setPointOfContacts(Collection<ResponsiblePartyItem> pointOfContacts) {
		this.pointOfContacts = pointOfContacts;
	}

	public CitationItem getCitation() {
		return citation;
	}

	public void setCitation(CitationItem citation) {
		this.citation = citation;
	}

	public Collection<KeywordsItem> getDescriptiveKeywords() {
		return descriptiveKeywords;
	}

	public void setDescriptiveKeywords(Collection<KeywordsItem> descriptiveKeywords) {
		this.descriptiveKeywords = descriptiveKeywords;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IdentificationItem [citation=");
		builder.append(citation);
		builder.append(", abstracts=");
		builder.append(abstracts);
		builder.append(", purpose=");
		builder.append(purpose);
		builder.append(", credits=");
		builder.append(credits);
		builder.append(", status=");
		builder.append(status);
		builder.append(", pointOfContacts=");
		builder.append(pointOfContacts);
		builder.append(", descriptiveKeywords=");
		builder.append(descriptiveKeywords);
		builder.append("]");
		return builder.toString();
	}

}
