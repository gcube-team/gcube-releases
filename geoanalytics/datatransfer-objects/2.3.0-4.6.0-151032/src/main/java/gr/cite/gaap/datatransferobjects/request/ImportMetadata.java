package gr.cite.gaap.datatransferobjects.request;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import gr.cite.geoanalytics.util.http.CustomException;

public class ImportMetadata {
	
	private static Logger logger=LoggerFactory.getLogger(ImportMetadata.class);
	
	private String user;
	private String title;

	private String description;
	private String purpose;	
	private String limitation;
	
	private List<String> keywords;

	private String distributorOrganisationName;
	private String distributorIndividualName;
	private String distributorOnlineResource;

	private String providerOrganisationName;
	private String providerIndividualName;
	private String providerOnlineResource;

	public ImportMetadata() {
		super();
		logger.trace("Initialized default contructor for ImportMetadata");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title=title;
	}

	public String getDescription() {
		return description;
	}

	public void setAbstractField(String abstractField) {
		this.description=abstractField;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose=purpose;
	}

	public String getLimitation() {
		return limitation;
	}

	public void setLimitation(String limitation) {
		this.limitation=limitation;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user=user;
	}

	public String getDistributorOrganisationName() {
		return distributorOrganisationName;
	}

	public void setDistributorOrganisationName(String distributorOrganisationName) {
		this.distributorOrganisationName=distributorOrganisationName;
	}

	public String getDistributorIndividualName() {
		return distributorIndividualName;
	}

	public void setDistributorIndividualName(String distributorIndividualName) {
		this.distributorIndividualName=distributorIndividualName;
	}

	public String getDistributorOnlineResource() {
		return distributorOnlineResource;
	}

	public void setDistributorOnlineResource(String distributorOnlineResource) {
		this.distributorOnlineResource=distributorOnlineResource;
	}

	public String getProviderOrganisationName() {
		return providerOrganisationName;
	}

	public void setProviderOrganisationName(String providerOrganisationName) {
		this.providerOrganisationName=providerOrganisationName;
	}

	public String getProviderIndividualName() {
		return providerIndividualName;
	}

	public void setProviderIndividualName(String providerIndividualName) {
		this.providerIndividualName=providerIndividualName;
	}

	public String getProviderOnlineResource() {
		return providerOnlineResource;
	}

	public void setProviderOnlineResource(String providerOnlineResource) {
		this.providerOnlineResource=providerOnlineResource;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords=keywords;
	}
	
	public void validate() throws Exception{
		try {
			Assert.notNull(user, "Auth. Organization cannot name be empty");
			Assert.notNull(title, "Layer name cannot be empty");
			Assert.notNull(description, "Layer description name cannot be empty");
			Assert.notNull(purpose, "Layer purpose name cannot be empty");
			Assert.notNull(limitation, "Layer limitation cannot be empty");
			Assert.notNull(distributorOrganisationName, "Distr. Organization cannot be empty");
			Assert.notNull(distributorIndividualName, "Distributor/s cannot be empty");
			Assert.notNull(distributorOnlineResource, "URL of distribution cannot be empty");
			Assert.notNull(providerOrganisationName, "Prov. Organization cannot be empty");
			Assert.notNull(providerIndividualName, "Providers cannot be empty");
			Assert.notNull(providerOnlineResource, "URL of provision cannot be empty");

			Assert.hasLength(user, "Auth. Organization name cannot be empty");
			Assert.hasLength(title, "Layer name cannot be empty");
			Assert.hasLength(description, "Layer description name cannot be empty");
			Assert.hasLength(purpose, "Layer purpose name cannot be empty");
			Assert.hasLength(limitation, "Layer limitation cannot be empty");
			Assert.hasLength(distributorOrganisationName, "Distr. Organization cannot be empty");
			Assert.hasLength(distributorIndividualName, "Distributor/s cannot be empty");
			Assert.hasLength(distributorOnlineResource, "URL of distribution cannot be empty");
			Assert.hasLength(providerOrganisationName, "Prov. Organization cannot be empty");
			Assert.hasLength(providerIndividualName, "Providers cannot be empty");
			Assert.hasLength(providerOnlineResource, "URL of provision cannot be empty");

			Assert.notEmpty(keywords, "Layer keywords cannot be empty");
		} catch (IllegalArgumentException e) {
			throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "ImportMetadata " +
		"[user=" + user + ", " +
		"title=" + title + ", " +
		"abstractField=" + description + ", " +
		"purpose=" + purpose + ", " +
		"keywords=" + keywords + ", " +
		"limitation=" + limitation + ", " +
		"distributorOrganisationName=" + distributorOrganisationName + ", " +
		"distributorIndividualName=" + distributorIndividualName + ", " +
		"distributorOnlineResource=" + distributorOnlineResource + ", " +
		"providerOrganisationName=" + providerOrganisationName + ", " +
		"providerIndividualName=" + providerIndividualName + ", " +
		"providerOnlineResource=" + providerOnlineResource + "]";
	}
}
