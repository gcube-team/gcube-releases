package gr.cite.regional.data.collection.application.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlAccessorType(XmlAccessType.FIELD)
public class MetadataDto {
	private static final String DC_NS = "http://purl.org/dc/elements/1.1/";
	private static final String DCTERMS_NS = "http://purl.org/dc/terms/";
	
	@JsonProperty("owner")
	@XmlElement(name = "rightsHolder", namespace = DCTERMS_NS)
	private String owner;
	
	@JsonProperty("context")
	@XmlElement(name = "collection", namespace = DCTERMS_NS)
	private String context;
	
	@JsonProperty("author")
	@XmlElement(name = "creator", namespace = DC_NS)
	private String author;
	
	@JsonProperty("title")
	@XmlElement(name = "title", namespace = DC_NS)
	private String title;
	
	@JsonProperty("publisher")
	@XmlElement(name = "publisher", namespace = DC_NS)
	private String publisher;
	
	@JsonProperty("creationDate")
	@XmlElement(name = "created", namespace = DCTERMS_NS)
	private String creationDate;
	
	@JsonProperty("lastUpdateDate")
	@XmlElement(name = "date", namespace = DC_NS)
	private String lastUpdateDate;
	
	@JsonProperty("expiryDate")
	@XmlElement(name = "valid", namespace = DCTERMS_NS)
	private String expiryDate;
	
	@JsonProperty("copyrightLicense")
	@XmlElement(name = "rights", namespace = DC_NS)
	private String copyrightLicense;
	
	@JsonProperty("spatialScale")
	@XmlElement(name = "spatial", namespace = DCTERMS_NS)
	private String spatialScale;
	
	@JsonProperty("language")
	@XmlElement(name = "language", namespace = DC_NS)
	private String language;
	
	@JsonProperty("identifier")
	@XmlElement(name = "identifier", namespace = DC_NS)
	private String identifier;
	
	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getContext() {
		return context;
	}
	
	public void setContext(String context) {
		this.context = context;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getPublisher() {
		return publisher;
	}
	
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	public String getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}
	
	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
	public String getExpiryDate() {
		return expiryDate;
	}
	
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	public String getCopyrightLicense() {
		return copyrightLicense;
	}
	
	public void setCopyrightLicense(String copyrightLicense) {
		this.copyrightLicense = copyrightLicense;
	}
	
	public String getSpatialScale() {
		return spatialScale;
	}
	
	public void setSpatialScale(String spatialScale) {
		this.spatialScale = spatialScale;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
