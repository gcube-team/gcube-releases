package gr.cite.gaap.datatransferobjects.request;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import gr.cite.geoanalytics.util.http.CustomException;

public class GeoNetworkMetadataDTO {

	private static Logger logger = LoggerFactory.getLogger(GeoNetworkMetadataDTO.class);

	private String title;

	private String description;
	private String purpose;
	private String limitation;

	private List<String> keywords;

	private Role author;
	private Role distributor;
	private Role provider;

	public GeoNetworkMetadataDTO() {
		super();
		this.author = new Role();
		this.distributor = new Role();
		this.provider = new Role();
		logger.trace("Initialized default contructor for ImportMetadata");
	}

	public static class Role {

		private String organisationName;
		private String individualName;
		private String onlineResource;

		public String getOrganisationName() {
			return organisationName;
		}

		public void setOrganisationName(String organisationName) {
			this.organisationName = organisationName;
		}

		public String getIndividualName() {
			return individualName;
		}

		public void setIndividualName(String individualName) {
			this.individualName = individualName;
		}

		public String getOnlineResource() {
			return onlineResource;
		}

		public void setOnlineResource(String onlineResource) {
			this.onlineResource = onlineResource;
		}

		public void validate(String errorMessagePrefix) throws Exception {
			try {
				Assert.hasLength(organisationName, errorMessagePrefix + " Organization name cannot be empty");
				Assert.hasLength(individualName, errorMessagePrefix + " Individual name cannot be empty");
				Assert.hasLength(onlineResource, errorMessagePrefix + " URL cannot be empty");
			} catch (IllegalArgumentException e) {
				throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
			}
		}
		
		public void validateOrganisationName(String errorMessagePrefix) throws Exception {
			try {
				Assert.hasLength(organisationName, errorMessagePrefix + " Organization name cannot be empty");
			} catch (IllegalArgumentException e) {
				throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
			}
		}
		
		@Override
		public String toString() {
			return "Role ["	+ "organisationName=" 	+ organisationName 	+ ", "
							+ "individualName=" 	+ individualName 	+ ", " 
							+ "onlineResource=" 	+ onlineResource 	+ "]";
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setAbstractField(String abstractField) {
		this.description = abstractField;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getLimitation() {
		return limitation;
	}

	public void setLimitation(String limitation) {
		this.limitation = limitation;
	}

	public Role getAuthor() {
		return author;
	}

	public void setAuthor(Role author) {
		this.author = author;
	}

	public Role getDistributor() {
		return distributor;
	}

	public void setDistributor(Role distributor) {
		this.distributor = distributor;
	}

	public Role getProvider() {
		return provider;
	}

	public void setProvider(Role provider) {
		this.provider = provider;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public void validate() throws Exception {
		try {
			Assert.hasLength(title, "Layer name cannot be empty");
			Assert.hasLength(description, "Layer description cannot be empty");
			Assert.hasLength(purpose, "Layer purpose cannot be empty");
			Assert.hasLength(limitation, "Layer limitation cannot be empty");
			Assert.notNull(author, "Author metadata cannot be empty");
			Assert.notNull(distributor, "Distributor metadata cannot be empty");
			Assert.notNull(provider, "Provider metadata cannot be empty");
			Assert.notEmpty(keywords, "Layer keywords cannot be empty");

			author.validateOrganisationName("Author");
			distributor.validate("Distributor");
			provider.validate("Provider");
		} catch (IllegalArgumentException e) {
			throw new CustomException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "GeoNetworkMetadataDTO " + "[title=" + title + ", " + "description=" + description + ", " + "purpose=" + purpose + ", " + "keywords=" + keywords + ", " + "limitation="
				+ limitation + ", " + "Author " + author + " Distributor " + distributor + " Provider " + provider + "]";
	}
}
