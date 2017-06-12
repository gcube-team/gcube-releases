package org.gcube.portlets.user.geoexplorer.shared.metadata.citation;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.gcube.portlets.user.geoexplorer.shared.metadata.ResponsiblePartyItem;

public class CitationItem implements Serializable{
	
	   /**
	 * 
	 */
	private static final long serialVersionUID = 5583009000629399262L;

	/**
     * Name by which the cited resource is known.
     */
    private String title;
    
    /**
     * Name and position information for an individual or organization that is responsible
     * for the resource. Returns an empty string if there is none.
     *
     */
    private List<ResponsiblePartyItem> citedResponsibleParty;

    /**
     * Short name or other language name by which the cited information is known.
     * Example: "DCW" as an alternative title for "Digital Chart of the World.
     */
    private Collection<String> alternateTitles;

    /**
     * Reference date for the cited resource.
     */
    private Collection<String> date;

    /**
     * Version of the cited resource.
     */
    private String edition;

    /**
     * Date of the edition in milliseconds elapsed sine January 1st, 1970,
     * or {@link Long#MIN_VALUE} if none.
     */
    private long editionDate;
    
    
    public CitationItem(){
    	
    }


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public Collection<String> getAlternateTitles() {
		return alternateTitles;
	}


	public void setAlternateTitles(Collection<String> alternateTitles) {
		this.alternateTitles = alternateTitles;
	}


	public Collection<String> getDates() {
		return date;
	}


	public void setDates(Collection<String> dates) {
		this.date = dates;
	}


	public String getEdition() {
		return edition;
	}


	public void setEdition(String edition) {
		this.edition = edition;
	}


	public long getEditionDate() {
		return editionDate;
	}


	public void setEditionDate(long editionDate) {
		this.editionDate = editionDate;
	}

	public List<ResponsiblePartyItem> getCitedResponsibleParty() {
		return citedResponsibleParty;
	}


	public void setCitedResponsibleParty(
			List<ResponsiblePartyItem> citedResponsibleParty) {
		this.citedResponsibleParty = citedResponsibleParty;
	}


	public Collection<String> getDate() {
		return date;
	}


	public void setDate(Collection<String> date) {
		this.date = date;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CitationItem [title=");
		builder.append(title);
		builder.append(", citedResponsibleParty=");
		builder.append(citedResponsibleParty);
		builder.append(", alternateTitles=");
		builder.append(alternateTitles);
		builder.append(", date=");
		builder.append(date);
		builder.append(", edition=");
		builder.append(edition);
		builder.append(", editionDate=");
		builder.append(editionDate);
		builder.append("]");
		return builder.toString();
	}

}
