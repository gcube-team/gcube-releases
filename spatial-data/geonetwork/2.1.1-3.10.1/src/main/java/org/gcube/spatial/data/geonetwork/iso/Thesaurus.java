package org.gcube.spatial.data.geonetwork.iso;

import java.util.Date;

import org.opengis.metadata.identification.KeywordType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Thesaurus")
public class Thesaurus {

	
	
	
	private KeywordType type;
	private String title;
	private Date citationDate;
	private String citationDescription;
	private String citationUri;
	private String citationOrganization;	
	private boolean isAuthored=false;
	
	
	
	public Thesaurus(KeywordType type, String title, Date citationDate) {
		super();
		this.type = type;
		this.title = title;
		this.citationDate = citationDate;		
	}
	
	
	public Thesaurus(KeywordType type, String title, Date citationDate,
			String citationDescription, String citationUri,
			String citationOrganization) {
		super();
		this.type = type;
		this.title = title;
		this.citationDate = citationDate;
		this.citationDescription = citationDescription;
		this.citationUri = citationUri;
		this.citationOrganization = citationOrganization;
		this.isAuthored=true;
	}
	
	
	/**
	 * @return the type
	 */
	public KeywordType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(KeywordType type) {
		this.type = type;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the citationDate
	 */
	public Date getCitationDate() {
		return citationDate;
	}
	/**
	 * @param citationDate the citationDate to set
	 */
	public void setCitationDate(Date citationDate) {
		this.citationDate = citationDate;
	}
	/**
	 * @return the citationDescription
	 */
	public String getCitationDescription() {
		return citationDescription;
	}
	/**
	 * @param citationDescription the citationDescription to set
	 */
	public void setCitationDescription(String citationDescription) {
		this.citationDescription = citationDescription;
	}
	/**
	 * @return the citationUri
	 */
	public String getCitationUri() {
		return citationUri;
	}
	/**
	 * @param citationUri the citationUri to set
	 */
	public void setCitationUri(String citationUri) {
		this.citationUri = citationUri;
	}


	/**
	 * @return the citationOrganization
	 */
	public String getCitationOrganization() {
		return citationOrganization;
	}


	/**
	 * @param citationOrganization the citationOrganization to set
	 */
	public void setCitationOrganization(String citationOrganization) {
		this.citationOrganization = citationOrganization;
	}


	/**
	 * @return the isAuthored
	 */
	public boolean isAuthored() {
		return isAuthored;
	}


	/**
	 * @param isAuthored the isAuthored to set
	 */
	public void setAuthored(boolean isAuthored) {
		this.isAuthored = isAuthored;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Thesaurus other = (Thesaurus) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Thesaurus [type=");
		builder.append(type);
		builder.append(", title=");
		builder.append(title);
		builder.append(", citationDate=");
		builder.append(citationDate);
		builder.append(", citationDescription=");
		builder.append(citationDescription);
		builder.append(", citationUri=");
		builder.append(citationUri);
		builder.append(", citationOrganization=");
		builder.append(citationOrganization);
		builder.append(", isAuthored=");
		builder.append(isAuthored);
		builder.append("]");
		return builder.toString();
	}
	
}
